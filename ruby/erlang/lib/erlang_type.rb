
module Erlang

  # The tag used for small integers 
  SMALLINT =     97

  # The tag used for integers 
  INTEGER =          98

  # The tag used for floating point numbers 
  FLOAT =       99

  # The tag used for atoms 
  ATOM =        100

  # The tag used for old stype references 
  REF =         101

  # The tag used for ports 
  PORT =        102

  # The tag used for PIDs 
  PID =         103

  # The tag used for small tuples 
  #SMALLTUPLE =  [104,0]
  SMALLTUPLE =  104

  # The tag used for large tuples 
  LARGETUPLE =  105

  # The tag used for empty lists 
  NIL =         106

  # The tag used for strings and lists of small integers 
  STRING =      107

  # The tag used for non-empty lists 
  LIST =        108

  # The tag used for binaries 
  BIN =         109

  # The tag used for small bignums 
  SMALLBIG =    110

  # The tag used for large bignums 
  # not used ?
  #LARGEBIG =    111

  # The tag used for new style references 
  NEWREF =      114

  # The version number used to mark serialized Erlang terms 
  VERSION =     131

  # The largest value that can be encoded as an integer 
  ERLMAX = (1 << 27) -1

  # The smallest value that can be encoded as an integer 
  ERLMIN = -(1 << 27)

  # The longest allowed Erlang atom 
  MAXATOMLENGTH = 255
  
  def make_size size, num_bytes=2
    hi = (0xff00 & size) >> 8
    lo = (0x00ff & size)
    [hi,lo]
  end

  def base_encode tag, bytes
    size = bytes.size
    if tag[1] ==2
      hi, lo = make_size(size)
      [tag[0], hi, lo, bytes].pack("C1C2a#{size}")
    elsif tag[1] == 4
      # len == 4
      [tag[0], size, bytes].pack("C1Na#{size}")
    else 
      #len == 0
      [tag[0], bytes].pack("C1a#{bytes.size}")
    end
  end

class BaseType
  def self.parse io, parse_version=false
    io = StringIO.new(io) unless io.kind_of? StringIO
    class << io; include Erlang::Net; end
    if parse_version then
      ver = io.getc 
      raise "Protocol Error: unknown version (#{ver}), expected 131" unless ver == 131
    end
    tag = io.getc
    
    case tag
    when SMALLINT
      type = Number.decode io, tag
    when INTEGER
      type = Number.decode io, tag
    when SMALLBIG
      type = Number.parse io, tag
    when FLOAT
      type = Float.parse io
    when ATOM  # 100
      type = Atom.decode io
    when REF
      type = Ref.decode io, tag
    when NEWREF
      type = Ref.decode io, tag
    when PORT
      type = Port.decode io
    when PID
      type = Pid.decode io
    when SMALLTUPLE 
      type = Tuple.decode(io,tag)
    when LARGETUPLE
      type = Tuple.decode(io, tag)
    when NIL
      type = nil
    when STRING
      type = Erlang::String.parse io
    when LIST
      type = List.parse io
    when BIN
      type = BIN.parse io
    else
      raise "not supported! #{tag}"
    end
    type
  end

  def self.get_len io, num_bytes=2
    len = io.read(num_bytes)
    if num_bytes==2
      return d_two_bytes_big(len)
    elsif num_bytes==4
      return d_four_bytes_big(len)
    elsif num_bytes==1
      return len[0]
    else
      raise "TODO: can't read #{num_bytes} byte length"
    end
  end
end  
class Atom < BaseType
  include Erlang
  
  def initialize val
    if val.class != Symbol && val.size > 0 #TODO empty ATOM wtf?
      val = val.to_s.to_sym
    end
    @atom = val
  end

  def encode
    base_encode(ATOM, @atom.to_s)
  end
  
  def to_s
    "'#{@atom}'"
  end 
  #  1       2        Len
  #+-----+-------+-----------+
  #| 100 | Len   | Atom name |
  #+-----+-------+-----------+
  def self.decode io
    len = io.read_two_bytes_big 
    self.new io.read(len)
  end
end #Atom

class Binary
  include Erlang
  def initialize val
    @val = val
  end
  def encode
    base_encode(BIN, @val)
  end

  #   1      4           Len
  #+-----+-------+---------------------+
  #| 109 |  Len  |      Data           |
  #+-----+-------+---------------------+
  def self.decode io
    len = io.read_four_bytes_big
    self.new io.read(len) 
  end
end#Binary

#class Boolean
# boolean are atoms :true and :false in erlang
#end

#class Byte Char
#bytes, chars are Longs
#end

class Float
  def initialize val
    @val = val.to_f
  end

  def encode
    # -?\d{20}e(+-)\d\d <- total 31 bytes, 0 padded
    str = sprintf("%.20e", @val)
    while str.size < 31
      str += 0x00
    end
    base_encode(FLOAT, str)
  end

  #  1         31
  #+----+----------------+
  #| 99 |   Float String |
  #+----+----------------+
  def self.decode io
    f_str = io.read(31)
    self.new f_str.to_f
  end
end

class Number
  def initialize val
    @val = val.to_i
  end

  def encode
    if (@val & 0xff) == @val
      return base_encode(SMALLBIG, ""<<@val)
    elsif (@val<0 || @val < ERLMIN || @val > ERLMAX)
      #  signed SMALLBIG
      #  TODO
      #  tag : 1 byte len : 1 byte sign : len(!) bytes val little endian(!) 
    else
      return base_encode(INT, [@val].pack("N"))
    end
  end #encode

  def to_s
    @val.to_s
  end

  #  1    1
  #+----+-----+
  #| 97 | Int |
  #+----+-----+
  #
  #  1       4
  #+----+-----------+
  #| 98 |   Int     | #98
  #+----+-----------+
  def self.decode io, tag
    if tag == SMALLINT # 97
      val = io.getc
    elsif tag == INTEGER # 98
      val = io.read_four_bytes_big
    else
      raise "Protocol Error: unknown tag (#{tag}) for Number"
    end
    self.new val
  end
end#Number

class List
  def initialize arr
    @val = arr
  end

  def push val
    @val.push val
  end

  def encode
    list_head = @val.length==0 ? [[NIL[0]].pack("C")] : base_encode(LIST, [@val.size].pack("N"))
    elems = []
    @val.each {|elem|
      elems.push elem.encode
    }
    [list_head, elems].flatten
  end
  
  #   1      4             
  #+-----+--------+---------------------+
  #| 108 |   n    | Elem(1) ... Elem(n) |
  #+-----+--------+---------------------+
  def self.decode io
    len = io.read_four_bytes_big 
    list = self.new []
    if len>0 then
      1.upto(len){
        list.push(BaseType.parse(io))
      }
    end
    list  
  end

end # List

class Tuple
  def initialize val=nil
    @val = val
  end
  def encode 
    if @val.length < 0xff 
      tuple_head = base_encode(SMALLTUPLE, [@val].pack("C1")) 
    else
      tuple_head = base_encode(LARGETUPLE, [@val].pack("N"))
    end
    elems = []
    @val.each {|elem|
      elems.push elem.encode
    }
    [tuple_head, elems].flatten
  end
  
  def [] idx
    @val[idx]
  end

  def size
    @val.size
  end

  #  1       1        N
  #+-----+-------+--------------+
  #| 104 | Arity |    Elements  |
  #+-----+-------+--------------+
  #
  #  1       4        N
  #+-----+-------+--------------+
  #| 105 | Arity |    Elements  |
  #+-----+-------+--------------+

  def self.decode io, tag
    if tag == SMALLTUPLE
      arity = io.getc
    elsif tag == LARGETUPLE
      arity = io.read_four_bytes_big
    else
      raise "Protocol Error: unknown tag (#{tag}) for Tuple"
    end
    val = []
    1.upto(arity){ |i|
      type = BaseType.parse(io)
      val.push(type)
    }

    self.new(val)
  end

  def to_s
    str = '{'
      @val.each_with_index { |val,i|
        str += ',' unless i==0
        str += val.to_s
      }
    str += '}'
  end
end#tuple

class Pid
  attr_accessor :node, :id, :serial, :creation
  def initialize node, id, serial, creation
    @node=node
    @id=id
    @serial=serial
    @creation=creation
  end

  def encode
    @node = Erlang::Atom.new(@node) unless @atom.class == Erlang::Atom
    pay = [PID[0]].pack("C")
    pay += @node.encode
    pay += [@id & 0x7fff, @serial & 0x1fff, @creation & 0x3].pack("N2C1")
    pay
  end

  def to_s
    str = "<#{@node}.#{id}.#{creation}>"
  end

  #  1       N            4            4           1
  #+-----+-----------+-----------+-----------+----------+
  #| 103 |    Node   |    ID     |  Serial   | Creation |
  #+-----+-----------+-----------+-----------+----------+
  def self.decode io
    node = BaseType.parse io
    id = io.read_four_bytes_big & 0x7fff
    serial = io.read_four_bytes_big & 0x1fff
    creation = io.getc & 0x3
    self.new node, id, serial, creation
  end
end

class Port
  attr_accessor :node, :id, :creation
  def initialize node, id, creation
    @node=node
    @id=id
    @creation=creation
  end
  def encode
    @node = Erlang::Atom.new(@node) unless @atom.class == Erlang::Atom
    pay = [PORT[0]].pack("C")
    pay+= @node.encode
    pay+= [@id & 0xfffffff, @creation &0x3].pack("NC")
    pay
  end
  
  def to_s
    "#Port<#{@node}.#{@id}> creation: #{@creation}"
  end

  #  1       N            4           1
  #+-----+-----------+-----------+----------+
  #| 102 |    Node   |    ID     | Creation | # port
  #+-----+-----------+-----------+----------+
  def self.decode io
    node = BaseType.parse io
    id = io.read_four_bytes_big io & 0xfffffff # why different mask than PID? TODO
    creation = io.getc & 0x3
    self.new node, id, creation
  end
end # Port

class Ref
  attr_accessor :node, :id, :creation
  def initialize node, id, creation
    @node=node
    @id=id
    @creation=creation
  end
  def encode
    @node = Erlang::Atom.new(@node) unless @atom.class == Erlang::Atom
    old_style = @id.class != Array || @id.length==1
    if oldstyle
      pay = [REF[0]].pack("C")
      pay+= @node.encode
      pay+= [@id&0x3ffff, @creation&0x3].pack("NC")
    else
      arity = @id.length > 3 ? 3 : @id.length
      pay = [NEWREF[0]].pack("C")
      pay += [arity].pack("n")
      pay += @node.encode
      pay += [@creation & 0x3, @id.shift & 0x3ffff].pack("CN")
      @id.each {|i|
        pay+=[i].pack("N")
      }
    end
    pay
  end
  
  def to_s
    return "#Ref<#{@node}.#{@id}> #{@creation}|" unless @id.class.is_a? Array
    str = "#Ref<#{@node}"
    @id.each {|i|
      str += ".#{i}"
    }
    str += "> #{@creation}|"
  end

  # 1       N            4           1
  #+-----+-----------+-----------+----------+
  #| 101 |    Node   |     ID    | Creation |
  #+-----+-----------+-----------+----------+
  #  1      2       N            1           N'
  #+-----+-----+-----------+-----------+-----------+
  #| 114 | Len |    Node   | Creation  |   ID  ... |
  #+-----+-----+-----------+-----------+-----------+
  def self.decode io, tag
    if tag==REF
      puts "REF"
      node = BaseType.parse io
      id = io.read_four_bytes_big & 0x3ffff
      creation = io.getc
    elsif tag==NEWREF

      len = io.read_two_bytes_big 
      node = BaseType.parse io
      creation = io.getc
      id = []
      1.upto(len) {
        id.push(io.read_four_bytes_big)
      }
    else
      raise "Protocol Error: unknown tag (#{tag}) for Ref"
    end
    self.new node, id, creation
  end
end

class String
  def initialize val
    @val = val
  end

  def encode
    size = @val.size
    if size == 0
      return [[NIL[0]].pack("C")]
    elsif size <= 0xffff
      return base_encode(STRING, @val)
    else
      arr = @val.split(//)
      list = List.new(arr)
      return list.encode
    end
  end

  #   1     2        Len
  #+-----+-----+-----------------+
  #| 107 | Len |  Characters     |
  #+-----+-----+-----------------+
  def self.decode io,tag
    if tag == NIL
      val = ''
    elsif tag == STRING
      len = io.read_two_bytes_big
      val = io.read(len)
    end
    self.new val
  end
end




end #Erlang module

if $0==__FILE__
	a = Erlang::Atom.new(:test)
  puts a.encode
  a = Erlang::Binary.new("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD")
  puts a.encode
  Erlang::Tuple.decode a,a 
end

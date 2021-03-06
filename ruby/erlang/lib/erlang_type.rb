require 'stringio'
require 'erlang_connection'
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

  # NOT SUPPORTED other than parsing.
  NEW_CACHE =   78
  CACHED_ATOM = 67

  # The version number used to mark serialized Erlang terms 
  VERSION =     131


  # The largest value that can be encoded as an integer 
  ERLMAX = (1 << 27) -1

  # The smallest value that can be encoded as an integer 
  ERLMIN = -(1 << 27)

  # The longest allowed Erlang atom 
  MAXATOMLENGTH = 255
  


class BaseType
  include Erlang::Util
  attr_accessor :value

  def to_bytes
    e_byte(VERSION)+encode()
  end

  def == other
    return false unless other.is_a? BaseType
    other.value == self.value
  end

  alias eql? ==

  def hash 
    to_s.hash
  end

  def self.parse io, parse_version=false
    io = StringIO.new(io) unless io.kind_of? Erlang::Net
    class << io; include Erlang::Net; end
    if parse_version then
      ver = io.getc 
      raise "Protocol Error: unknown version (#{ver}), expected #{VERSION}" unless ver == VERSION
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
      type = Erlang::String.decode(io, tag)
    when LIST
      type = List.decode io
    when BIN
      type = Binary.decode io
    when NEW_CACHE
      puts "WARNING: NEW_CACHE experimental"
      type = NewCache.decode io
    when CACHED_ATOM
      puts "WARNING: CACHED_ATOM experimental"
      type = CachedAtom.decode io
    else
      raise "not supported! #{tag}"
    end
    type
  end #parse

  # Can't do Tuple or Binaries, Arrays are Lists.
  def self.erl val
    if val == nil
      return Nil.new
    elsif val.is_a? Symbol
      return Atom.new(val)
    elsif val.is_a? Integer
      return Number.new(val)
    elsif val.is_a? Array
      return List.new(val)
    elsif val.is_a? ::String
      return Erlang::String.new(val)
    elsif val.is_a? ::Float
      return Erlang::Float.new(val)
    elsif val.is_a? BaseType
      return val
    else
      raise "Protocol Error: can't encode class #{val.class}"
    end
  end
end  

class Nil < BaseType
  def to_s
    '#nil'
  end
  def encode
    e_byte(NIL)
  end
  def self.decode
    return self.new
  end
end
class Atom < BaseType
  include Erlang
  def initialize val
    if (!val.is_a? BaseType) && val.class != Symbol && val.size > 0 #TODO empty ATOM wtf?
      val = val.to_s.to_sym
    end
    @value = val
  end

  def encode
    val = @value.to_s
    len = e_two_bytes_big val.size
    e_byte(ATOM)+len+val   
  end
  
  def to_s
    "'#{@value}'"
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

class Binary < BaseType
  include Erlang
  def initialize val
    @value = check val
  end

  def check val
    return val if val.is_a? ::String
    if val.is_a? Enumerable
      str = ""
      val.each {|v|
        if v <= 0xff
          str << v
        else
          raise "Protocol Error: binary values must be bytes, offending value: #{v}"
        end
      }
    else
      raise "Protocol Error: value (#{@val}) must be String or Enumerable with numbers"
    end
    str
  end
  def encode
    len = e_four_bytes_big(@value.size)
    e_byte(BIN)+len+@value
  end

  #   1      4           Len
  #+-----+-------+---------------------+
  #| 109 |  Len  |      Data           |
  #+-----+-------+---------------------+
  def self.decode io
    len = io.read_four_bytes_big
    self.new io.read(len) 
  end

  def to_s
    str = '<<'
    vals = @value.split(//)
    vals.map! {|v| v[0]}
    str += vals.join(',')
    str +='>>'
    str
  end
end#Binary

#class Boolean
# boolean are atoms :true and :false in erlang
#end

#class Byte Char
#bytes, chars are Longs
#end

class Float < BaseType
  def initialize val
    @value = val.to_f
  end

  def encode
    # -?\d{20}e(+-)\d\d <- total 31 bytes, 0 padded
    str = sprintf("%.20e", @value)
    while str.size < 31
      str += 0x00
    end
    e_byte(FLOAT)+str
  end

  #  1         31
  #+----+----------------+
  #| 99 |   Float String |
  #+----+----------------+
  def self.decode io
    f_str = io.read(31)
    self.new f_str.to_f
  end

  def to_s
    @value.to_s
  end
end

class Number < BaseType
  def initialize val
    @value = val.to_i
  end

  def encode
    if (@value & 0xff) == @value
      return e_byte(SMALLINT)+e_byte(@value)
    elsif (@value >= ERLMIN && @value <= ERLMAX)
      return e_byte(INTEGER)+e_four_bytes_big(@value)
    else
      raise "TODO: Bigint"
    end
  end #encode

  def to_s
    @value.to_s
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

class List < BaseType
  def initialize arr
    @value = arr
  end

  def push val
    @value.push val
  end

  def encode
    list_head = @value.length==0 ? e_byte(NIL) : e_byte(LIST)+e_four_bytes_big(@value.size)
    elems = []
    @value.each {|elem|
      elem = BaseType.erl elem unless elem.is_a? BaseType # convert to erlang
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

  def to_s
    str = '['
      @value.each_with_index { |val,i|
        str += ',' unless i==0
        str += val.to_s
      }
    str += ']'
  end


end # List

class Tuple < BaseType
  def initialize val=nil
    @value = val
  end
  def encode 
    if @value.length < 0xff 
      tuple_head = e_byte(SMALLTUPLE)+e_byte(@value.size)
    else
      tuple_head = e_byte(LARGETUPLE)+e_four_bytes_big(@value.size)
    end
    elems = []
    @value.each {|elem|
      elem = BaseType.erl(elem) unless elem.is_a? BaseType #convert to erlang.
      elems.push elem.encode
    }
    [tuple_head, elems].to_s
  end
  
  def [] idx
    BaseType.erl @value[idx]
  end

  def size
    @value.size
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
      @value.each_with_index { |val,i|
        str += ',' unless i==0
        str += val.to_s
      }
    str += '}'
  end
end#tuple

class Pid < BaseType
  attr_accessor :node, :id, :serial, :creation
  def initialize node, id, serial, creation
    @node= node.is_a?(Atom) ? node : Atom.new(node)
    @id=id
    @serial=serial
    @creation=creation
    @value = to_s
  end

  def encode
    @node = Erlang::Atom.new(@node) unless @node.is_a? BaseType
    pay = e_byte(PID)
    pay += @node.encode
    pay += [@id & 0x7fff, @serial & 0x1fff, @creation & 0x3].pack("N2C1")
    pay
  end

  def to_s
    str = "#PID<#{@node}.#{id}.#{creation}>"
  end

  def == pid
    return false unless pid.is_a? Pid
    return pid.node == self.node && pid.id==self.id && pid.serial==self.serial && pid.creation == self.creation
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

class Port < BaseType
  attr_accessor :node, :id, :creation
  def initialize node, id, creation
    @node=node
    @id=id
    @creation=creation
    @value = to_s
  end
  def encode
    @node = Erlang::Atom.new(@node) unless @node.is_a? BaseType
    pay = e_byte(PORT) 
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

class Ref < BaseType
  attr_accessor :node, :id, :creation
  def initialize node, id, creation
    @node=node
    @id=id
    @creation=creation
    @value = to_s
  end
  def encode
    @node = Erlang::Atom.new(@node) unless @node.is_a? BaseType
    old_style = @id.class != Array || @id.length==1
    if old_style
      pay = e_byte(REF)
      pay+= @node.encode
      pay+= [@id&0x3ffff, @creation&0x3].pack("NC")
    else
      raise "Protocol Error: more than 12 ids" unless @id.length <= 12
      pay = e_byte(NEWREF)
      pay += e_two_bytes_big(@id.size) 
      pay += @node.encode
      pay += e_byte(@creation)
      @id.each {|i|
        pay+=e_four_bytes_big(i & 0x3ffff)
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

class String < BaseType
  def initialize val
    @value = val
  end

  def encode
    size = @value.size
    if size == 0
      return e_byte(NIL)
    elsif size <= 0xffff
      return e_byte(STRING)+e_two_bytes_big(@value.size)+@value
    else
      arr = @value.split(//)
      list = List.new(arr)
      return list.encode
    end
  end

  def to_s
    '"'+@value+'"'
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
class NewCache < BaseType
  def initialize idx, val
    @value = BaseType.erl(val)
    @idx = idx
  end
  #  1       1       2         Len
  #+----+-------+--------+-----------+
  #| 78 | index |  Len   | Atom name |
  #+----+-------+--------+-----------+
  def encode
    index = e_byte(@idx)
    e_byte(NEW_CACHE)+index+@value.encode
  end
  def self.decode io
    idx = io.getc
    value = Atom.decode(io) 
    self.new idx, value
  end
  def to_s
    "#{self.class} idx: #{@idx} val: #{@value}"
  end
end

class CachedAtom < BaseType
  def initialize value
    @value = value
  end
  #   1      1
  #+----+-------+
  #| 67 | index |
  #+----+-------+
  def self.decode io
    val = io.getc
    self.new(val)
  end
  def self.encode 
    e_byte(CACHED_ATOM)+e_byte(@value)
  end
  def to_s
    "#{self.class} idx: #{@value}"
  end
end # CachedAtom




end #Erlang module

if $0==__FILE__
#	a = Erlang::Atom.new(:test)
#  puts a.encode
#  a = Erlang::Binary.new("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD")
#  puts a.encode
#  Erlang::Tuple.decode a,a 

  
end

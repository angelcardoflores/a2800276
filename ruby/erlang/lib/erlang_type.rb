
module Erlang

  # The tag used for small integers 
  SMALLINT =     [97, 0]

  # The tag used for integers 
  INT =          [98,0]

  # The tag used for floating point numbers 
  FLOAT =       [99, 0]

  # The tag used for atoms 
  ATOM =        [100, 2]

  # The tag used for old stype references 
  REF =         [101,0]

  # The tag used for ports 
  PORT =        [102,0]

  # The tag used for PIDs 
  PID =         [103,0]

  # The tag used for small tuples 
  SMALLTUPLE =  [104,0]

  # The tag used for large tuples 
  LARGETUPLE =  [105, 0]

  # The tag used for empty lists 
  NIL =         [106, -1]

  # The tag used for strings and lists of small integers 
  STRING =      [107,2]

  # The tag used for non-empty lists 
  LIST =        [108,0]

  # The tag used for binaries 
  BIN =         [109,4]

  # The tag used for small bignums 
  SMALLBIG =    110

  # The tag used for large bignums 
  # not used
  #LARGEBIG =    111

  # The tag used for new style references 
  NEWREF =      [114,0]

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
	
class Atom
  include Erlang
  
  def initialize val
    if val.class != Symbol
      val = val.to_s.to_sym
    end
    @atom = val
  end

  def encode
    base_encode(ATOM, @atom.to_s)
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
end#Number

class List
  def initialize arr
    @val = arr
  end

  def encode
    list_head = @val.length==0 ? [[NIL[0]].pack("C")] : base_encode(LIST, [@val.size].pack("N"))
    elems = []
    @val.each {|elem|
      elems.push elem.encode
    }
    [list_head, elems].flatten
  end

end # List

class Tuple
  def initialize val
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
end#tuple

def Pid
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
end # Port

def Ref
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
      pay = [[NEWREF[0]].pack("C")
      pay += [arity].pack("n")
      pay += @node.encode
      pay += [@creation & 0x3, @id.shift & 0x3ffff].pack("CN")
      @id.each {|i|
        pay+=[i].pack("N")
      }
    end
    pay
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
end




end #Erlang

if $0==__FILE__
	a = Erlang::Atom.new(:test)
  puts a.encode
  a = Erlang::Binary.new("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD")
  puts a.encode
end

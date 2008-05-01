module Bytes

  # Utility method to converts a ruby numeric (255- -128) to it's
  # hex representation.
  def self.hex b
    raise "value too large #{b}" if b > 255 || b < -128
    "%02x" % b
  end
  
  # Utility method to converts a ruby numeric (255- -128) to it's
  # octal string representation.
  def self.octal b
    raise "value too large #{b}" if b > 255 || b < -128
    "%03o" % b
  end

  # Utility method to converts a ruby numeric (255- -128) to it's
  # 8 bit string representation.
  def self.bin b
    raise "value too large #{b}" if b > 255 || b < -128
    "%08b" % b
  end

  # Converts a binary String, e.g. "0101 1100" into a
  # Ruby numeric.
  def self.bin2b b
    b = b.gsub(/\s*/, "")
    raise "value too large #{b}" if b.size > 8
    [b].pack("B8")[0]
  end

  # Simple class example to access different parts of a bit String.
  # Byte is instantiated with a hash that describes the signifigance
  # of the individual bits in the byte. For example, a byte that has the 
  # following relevance in a protocol: 
  #
  #   |8|7|5|4|3|2|1|Desc
  #   ================================
  #   |1|-|-|-|-|-|-|Channel Encrypted
  #   |-|0|0|0|-|-|-|Method A
  #   |-|1|0|0|-|-|-|Method B
  #   |-|0|0|1|-|-|-|Method C
  #   |-|-|-|-|X|X|X|Channel Number
  #
  # can be represented as follows:
  #
  #   require "bytes"
  #   b = Bytes::Byte.new "1......." => :enc,
  #                       ".000...." => :a,
  #                       ".100...." => :b,
  #                       ".001...." => :c,
  #                       ".....vvv" => :channel
  #   b.value = 0xff
  #   b.enc?        # true 
  #   b.b?          # false
  #   b.b           # `b.value` is now 0xCF / "11001111"
  #   b.b?          # true 
  #   b.channel     # 7
  #   b.channel = 0 # `b.value` is now 0xC8 / "11001000"
  #
  # In order to be more legible, whitespace is allowed in the 
  # Strings describing the bits, e.g.:
  #
  #   "1000 ...."
  #
  # Is allowed. The "v" stands for 'value' and may be upper or lower case.
  # It's not allowed to mix "V" with 1 and 0 in a single pattern, but they
  # can appear in different patterns of the bit specification as in 
  # the above case.
  
class Byte
  attr_accessor :value
  # definition:
  #  "10......|desc" => :func_name,
  #  "01.. ....|desc" => :func_name2,
  #  "..vvvvvv|desc" => "func_name3
  def initialize definition
    @value = 0
    @defs = Bytes.check_definitions definition
    add_functionality
  end

  private
  def add_functionality
    @defs.each { |d|
      _d, definition, _desc, func = d
      Bytes.handle_state(self, definition, func, :value) if definition =~ /[.10]{8}/
      Bytes.handle_value(self, definition, func, :value) if definition =~ /[.v]{8}/
    }
  end



end
  
  private 

  # seperate definitions into def, desc, func arr,
  # remove spaces, normalize checks that the spec is ok.
  # An hash like this:
  #
  #   {"1... ....|comment" => :enc}
  #
  # would return an array:
  #
  #    [["1... ....","1.......", "comment", :enc]]  
  #
  # Checks:
  #   * definition is 8bit
  #   * contains only 1,0,v or "."
  #   * contains _either_ 1 and 0 _or_ v in a single pattern.
  #   * if values ("v") are specified, that they are all grouped together
  def self.check_definitions definition
    defs = []
    definition.each_pair { |key, value|
      definition, desc = key.split("|")
      def_normalized = definition.gsub(/\s+/, "")
      raise "Definition: #{definition} is not 8 bits" unless def_normalized.size == 8
      def_normalized.gsub!(/V/, "v")
      raise "Definition: #{definition} contains illegal values" if def_normalized =~ /[^.01v]/
      raise "#{value} is not a symbol" unless value.class == Symbol
      raise "#{definition} mixes values and states (use either `v` OR 1,0) not both" unless
        def_normalized =~ /[.10]{8}/ || def_normalized =~ /[.v]{8}/
      raise "value must be contiguous in definition: #{definition}" if def_normalized =~ /v\.+v/
      defs.push [definition, def_normalized, desc, value]
    }
    defs
  end
  
  # Assembles the functionality for patterns describing state, 
  # i.e patterns with 0's and 1's.
  #
  # Parameter:
  #   * instance: instance or class to attach functionality to.
  #   * d: definition of the bits, e.g. "11......"
  #   * f: symbol named like the function to access bit state
  #   * i: symbol named after the instance variable that contains the byte.
  #
  def self.handle_state instance, d, f, i
    mask = d.gsub(/[.]/, "0") # "10.. ...." => "1000 0000"
    mask = num_mask mask

    mask2 = d.gsub(/[10]/, "0") # "10.. ...." =? 0011 1111"
    mask2.gsub!(/[.]/, "1")
#mask2_s=mask2
    mask2 = num_mask mask2 
    # `mask` is to set the acutal value
    # `mask2` is to initialize the area to `0` before setting. 
    prefix = "@#{i.to_s} ||= 0;"
    body1 = "#{prefix}@#{i.to_s} = (@#{i.to_s} & #{mask2}) | #{mask}"

    mask3 = d.gsub(/[10]/,"1")
    mask3.gsub!(/[.]/, "0") # "10.. ...." => "1100 0000"
    mask3 = num_mask mask3 

    body2 = "#{prefix}(@#{i.to_s} & #{mask3}) == #{mask}"
   
    # differentiate whether we're being mixed into a class or an instance.
    # not pretty, not sure I understand it myself. 
    if instance.class == Class
      instance.instance_eval "define_method(:#{f.to_s}){instance_eval \"#{body1}\"}"
      instance.instance_eval "define_method(:#{f.to_s}?){instance_eval \"#{body2}\"}"
    else
      instance.instance_eval "def #{f.to_s}; #{body1} ; end"
      instance.instance_eval "def #{f.to_s}?; #{body2} ; end"
    end
  end

 # Assembles the functionality for patterns describing values, 
  # i.e patterns with "v"'s.
  #
  # Parameter:
  #   * instance: instance or class to attach functionality to.
  #   * d: definition of the bits, e.g. "11......"
  #   * f: symbol named like the function to access bit state
  #   * i: symbol named after the instance variable that contains the byte.
  #

  def self.handle_value instance, d, f, i
    #this method ends up adding a function like this example:
    # "vvvv ...." => :some
    # def some= v
    #   raise "v too large" unless v<2**n
    #   @value &= 0x0F # mask
    #   @value = @value & v<<shift
    # en
    #

    last = d.rindex('v')
    shift = 7 - last
    n = (last-d.index('v')) +1
    

    mask = d.gsub(/v/, "0") # "vv.. ...." =? 0011 1111"
    mask.gsub!(/[.]/, "1")
    mask = num_mask mask

    body1 =  "  raise \"value \#{v} too large\" unless v<2**#{n}\n"
    body1 << "  @#{i.to_s} ||= 0\n"
    body1 << "  @#{i.to_s} = (@#{i.to_s} & #{mask}) | (v << #{shift})\n"

    mask2 = d.gsub(/v/, "1")
    mask2.gsub!(/[.]/, "0")
    mask2 = num_mask mask2
    
    body2 =  "  @#{i.to_s} ||=0\n" 
    body2 << "  v = @#{i.to_s} & #{mask2}\n"
    body2 << "  v >> #{shift}\n"

    if instance.class == Class
      body1.gsub! /"/, '\"' # nested quotes in raise... 
      instance.instance_eval "define_method(:#{f.to_s}=){|v| instance_eval \"#{body1}\"}"
      instance.instance_eval "define_method(:#{f.to_s}){instance_eval \"#{body2}\"}"
    else
      instance.instance_eval "def #{f.to_s}= v; #{body1} ; end"
      instance.instance_eval "def #{f.to_s} ; #{body2} ; end"
    end

  end
  def self.num_mask bit_mask 
    [bit_mask].pack("B*")[0]
  end

end # module Bytes


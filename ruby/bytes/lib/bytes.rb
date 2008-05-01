module Bytes
  def self.hex b
    "%02x" % b
  end
  def self.octal b
    "%03o" % b
  end
  def self.bin b
    "%08b" % b
  end
  def self.bin2b b
    [b].pack("B8")[0]
  end

  # seperate definitions into def, desc, func arr,
  # remove spaces, normalize
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

  def self.handle_state instance, d, f, i
    mask = d.gsub(/[.]/, "0") # "10.. ...." => "1000 0000"
    mask = num_mask mask

    mask2 = d.gsub(/[10]/, "0") # "10.. ...." =? 0011 1111"
    mask2.gsub!(/[.]/, "1")
#mask2_s=mask2
    mask2 = num_mask mask2 
    
    prefix = "@#{i.to_s} ||= 0;"
    body1 = "#{prefix}@#{i.to_s} = (@#{i.to_s} & #{mask2}) | #{mask}"

    
    #instance_eval "def #{f.to_s}; puts \"mmms #{mask2_s}\"; @value = (@value & #{mask2}) | #{mask}; end"
    mask3 = mask2[0]

    mask3 = d.gsub(/[10]/,"1")
    mask3.gsub!(/[.]/, "0") # "10.. ...." => "1100 0000"
    mask3 = num_mask mask3 

    body2 = "#{prefix}(@#{i.to_s} & #{mask3}) == #{mask}"
    
    if instance.class == Class
      instance.instance_eval "define_method(:#{f.to_s}){instance_eval \"#{body1}\"}"
      instance.instance_eval "define_method(:#{f.to_s}?){instance_eval \"#{body2}\"}"
    else
      instance.instance_eval "def #{f.to_s}; #{body1} ; end"
      instance.instance_eval "def #{f.to_s}?; #{body2} ; end"
    end
  end


  def self.handle_value instance, d, f, i
    #this method ends up adding a function like this example:
    # "vvvv ...." => :some
    # def some= v
    #   raise "v too large" unless v<2**n
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
    body1 << "  @#{i.to_s} = @#{i.to_s} & #{mask}\n"
    body1 << "  @#{i.to_s} = @#{i.to_s} | (v << #{shift})\n"

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


  def add_functionality
    @defs.each { |d|
      _d, definition, _desc, func = d
      Bytes.handle_state(self, definition, func, :value) if definition =~ /[.10]{8}/
      Bytes.handle_value(self, definition, func, :value) if definition =~ /[.v]{8}/
    }
  end



end
end # module Bytes


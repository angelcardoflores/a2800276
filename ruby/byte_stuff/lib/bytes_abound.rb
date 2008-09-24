
module X

def X.included other
    other.module_eval {
     def method_missing m, *args
       X.send m, *args
     end
    }
end
class X < String
  
  def method_missing m, *args
    if (args.length == 0) && (val = X._get_val(m))
      X.new(self.concat( X.new(val) ))
    else
      super
    end
  end      
  def self.method_missing m, *args
          puts "here"
    # we'll handle two (three) cases:
    # .xabef01AF ...
    # (.o0123345671 ...)
    # .b01010101_01010101
    # underscores in the value are ignored, as are repititions
    # of the base id, e.g. .x31x32x33 is equivalent to .x313233
    # and .x31_32_33
    if (args.length == 0) && (val = _get_val(m))
      self.new val
    else
      super
    end
  end
  def self._get_val m
    str = m.to_s.gsub /_/,""
    return nil unless str =~ /^[xXoObB]/
    case str
    when /^[xX]([0-9a-fA-FxX]*)$/
      str.gsub!(/[xX]/,"")
      [str].pack("H*")
    when /^[bB]([01bB])*$/      
      str.gsub!(/[bB]/,"")
      [str].pack("B*")
    else
      return nil
    end
  end
end # class
end #module

#X::X.dingdong :a, :b, :c
#X::X.dingdong 

puts X::X._get_val("b0011_1000__0011_0001")
a= X::X.x3132
puts a
puts a.b0011_1000__0011_0001.x313233343536
puts X::X.x32.x31.x33

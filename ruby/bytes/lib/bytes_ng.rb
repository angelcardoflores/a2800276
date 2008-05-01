require "bytes.rb"

module Bytes
  
  # 
  # The `byte_accessor` method becomes available to your class by
  # including the `Bytes` module. The method is used for meta-programming,
  # it takes a symbol, which becomes the name of an instance variable and
  # accessors to store and access the byte value. The second parameter is 
  # a hash to describe 'sematics' of the byte. Example:
  #
  #  class SomeMixes
  #     include Bytes
  #     byte_accessor :my_byte,   "vv.. ...." => :two_bits,
  #                               "10.. ...." => :ten,
  #                               "...1 1..." => :two_middle,
  #                               "..vv vv.." => :four_middle
  #  end
  #
  #
  # Class `SomeMixes` included the module `Bytes`, therefore the
  # `byte_accessor` method becomes available to it. After the call to `byte_accessor`,
  # instances of `SomeMixes` have an instance 
  # variable named `@my_byte` and methods: `two_bits`, `two_bits=`, `ten`, 
  # `ten?`, `two_middle`, `two_middle?`, `four_middle` and `four_middle=`.
  #
  # These can be used as follows:
  #
  #    b = SomeMixes.new
  #    b.two_bits = 1000  # !! Overflow, raises an Error
  #    b.two_bits = 2     # b.my_byte == 1000 0000
  #    b.ten?             # true
  #    b.two_middle        # b.my_byte == 1001 1000
  #    b.four_middle      # 6
  #
 
  def byte_accessor instance_var, hash
    # This method is only here for documentation, it pops
    # into existance a few lines further down, marked #MAGIC...
  end

  private
  
  # `append_features` provides a hook into the include mechanism,
  # I don't want all (or actually any) of Bytes' instance methods
  # included. It just provides a hook to have the `byte_accessor` 
  # helper function.
  def self.append_features mod
    #mod.instance_eval {
      meta = class<<mod;self;end #meta class of whatever is including us
  #puts "@@@@ #{meta.class} = #{mod}"
      meta.instance_eval {
        define_method(:byte_accessor) {|instance_var, hash| # MAGIC
          attr_accessor instance_var
          Bytes.create(self, instance_var, hash)
        }
      }
    #}
  end

  def self.create (klass, instance_var, definition) 
    definitions = self.check_definitions(definition)
   # puts "!! #{klass.class}"
    definitions.each {|entry|
      _d, d, _desc, func = entry
      handle_state(klass, d, func, instance_var) if d =~ /[.01]{8}/
      handle_value(klass, d, func, instance_var) if d =~ /[.v]{8}/
    }
  end
end

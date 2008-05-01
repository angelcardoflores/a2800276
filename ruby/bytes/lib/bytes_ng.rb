require "bytes.rb"

module Bytes
  def self.append_features mod
    #mod.instance_eval {
      meta = class<<mod;self;end #meta class of whatever is including us
  #puts "@@@@ #{meta.class} = #{mod}"
      meta.instance_eval {
        define_method(:byte_accessor) {|instance_var, hash|
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

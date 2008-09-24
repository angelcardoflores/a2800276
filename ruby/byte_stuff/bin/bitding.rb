puts "module X"
puts "class X < String"
hex = %w{0 1 2 3 4 5 6 7 8 9 A B C D E F}
lettres = %w{A B C D E F}
hex.each { |a|
        hex.each {|b|
                byte = "#{a}#{b}"
                puts %Q{
def x#{byte}
  X.new(self + self.class.x#{byte})
end

def self.x#{byte}
  X.new "\\x#{byte}"
end
                }
        }
}

hex.each { |a|
  lettres.each {|b|
    byte = "#{a}#{b}"
    puts "alias_method :x#{byte.downcase}, :x#{byte}"
  }
}

puts "class << self"
hex.each { |a|
  lettres.each {|b|
    byte = "#{a}#{b}"
    puts "  alias_method :x#{byte.downcase}, :x#{byte}"
  }
}
puts "end"
puts "end #X"

hex.each { |a|
        hex.each {|b|
                byte = "#{a}#{b}"
                puts %Q{
def x#{byte}
  X.x#{byte}
end
                }
        }
}

hex.each { |a|
  lettres.each {|b|
    byte = "#{a}#{b}"
    puts "alias_method :x#{byte.downcase}, :x#{byte}"
  }
}


puts "end #module X"
#  
#






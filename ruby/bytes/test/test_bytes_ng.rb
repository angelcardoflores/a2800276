require 'test/unit'
require File.dirname(__FILE__) + '/../lib/bytes_ng.rb'


class Something
  include Bytes
  byte_accessor :something, "1... ...." => :bit8_s,
                            ".1.. ...." => :bit7_s,
                            "..vv vv.." => :value_s

  byte_accessor :the_other, "1... ...." => :bit8_o,
                            ".1.. ...." => :bit7_o,
                            "..vv vv.." => :value_o
 
  byte_accessor :value,     "11.. ...." => :bit8_7,
                            "..11 ...." => :bit6_5,
                            ".... 11.." => :bit4_3
end

class SomethingElse
  include Bytes
  byte_accessor :value,     "10.. ...." => :ten,
                            "11.. ...." => :eleven,
                            "01.. ...." => :o_one,
                            "00.. ...." => :o_o

end

class SomePattern
  include Bytes
  byte_accessor :value,     "11.. ..11" => :edges,
                            "1010 1010" => :this_that,
                            "0... ...0" => :no_edge,
                            "...1 1..." => :two_one_in_the_middle


end

class SomeValue
  include Bytes
  byte_accessor :value,     "vvvv ...." => :nib_one,
                            ".... vvvv" => :nib_two
end
class SomeMixes
  include Bytes
  byte_accessor :value,     "vv.. ...." => :two_bits,
                            "10.. ...." => :ten,
                            "...1 1..." => :two_middle,
                            "..vv vv.." => :four_middle

end

class TestByte < Test::Unit::TestCase
  def setup

  end

  def test_basic
    s = Something.new
    s.something=0
    assert_equal 0, s.something
    assert_equal nil, s.the_other
    assert !s.bit8_s?
    s.bit8_s
    assert  s.bit8_s?
  end

  def test_2_bits
    b = Something.new 
    b.bit8_7
    assert b.value == 192
    b.bit4_3
    assert b.value == 204
    b.value = 0xf0
    assert b.bit8_7?
    assert b.bit6_5?
    assert !b.bit4_3?

  end

  def test_oo_2_eleven
    b = SomethingElse.new

    b.value = 0xff
    assert b.eleven?
    assert !b.ten?

    b.o_o
    assert_equal 63, b.value # 63 = 0011 1111
  end
  
  def test_strange_patterns
    b = SomePattern.new

    b.edges
    assert_equal "11000011", Bytes.bin(b.value)
    b.this_that
    assert_equal "10101010", Bytes.bin(b.value)
    assert !b.no_edge?
    assert !b.two_one_in_the_middle?
    b.two_one_in_the_middle
    assert_equal "10111010", Bytes.bin(b.value)
  end
  
  def test_value
    b = SomeValue.new    
    assert_equal 0, b.nib_one
    assert_equal 0, b.nib_two

    b.nib_one=1
    assert_equal "00010000", Bytes.bin(b.value)
    b.nib_two=0x0f
    assert_equal "00011111", Bytes.bin(b.value)
    assert_equal 1, b.nib_one
    assert_equal 0x0f, b.nib_two 
  end

  def test_mixed
    b = SomeMixes.new 
    
    assert_raise (RuntimeError) {
      b.two_bits = 20
    }
    b.two_bits = 2
    assert_equal "10000000", Bytes.bin(b.value)
    assert b.ten?
    b.two_middle
    assert_equal "10011000", Bytes.bin(b.value)
    assert_equal 6, b.four_middle
    
  end
  
  
end

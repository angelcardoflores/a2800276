require 'test/unit'
require File.dirname(__FILE__) + '/../lib/bytes.rb'

class TestByte < Test::Unit::TestCase
  def setup

  end

  def test_8_bits
    
    b = Bytes::Byte.new   "1... ...." => :bit8,
                          ".1.. ...." => :bit7,
                          "..1. ...." => :bit6,
                          "...1 ...." => :bit5,
                          ".... 1..." => :bit4,
                          ".... .1.." => :bit3,
                          ".... ..1." => :bit2,
                          ".... ...1" => :bit1
    assert b.value = 0
    b.bit1
    assert b.value = 0x01
    b.bit4
    assert b.value = 9
    b.bit8
    assert b.value =137
  end

  def test_2_bits
    b = Bytes::Byte.new   "11.. ...." => :bit8_7,
                          "..11 ...." => :bit6_5,
                          ".... 11.." => :bit4_3

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
    b = Bytes::Byte.new  "10.. ...." => :ten,
                         "11.. ...." => :eleven,
                         "01.. ...." => :o_one,
                         "00.. ...." => :o_o

    b.value = 0xff
    assert b.eleven?
    assert !b.ten?

    b.o_o
    assert_equal 63, b.value # 63 = 0011 1111
  end
  
  def test_strange_patterns
    b = Bytes::Byte.new "11.. ..11" => :edges,
                        "1010 1010" => :this_that,
                        "0... ...0" => :no_edge,
                        "...1 1..." => :two_one_in_the_middle

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
    b = Bytes::Byte.new  "vvvv ...." => :nib_one,
                         ".... vvvv" => :nib_two
    
    assert_equal 0, b.nib_one
    assert_equal 0, b.nib_two

    b.nib_one=1
    assert_equal "00010000", Bytes.bin(b.value)
    b.nib_two=0x0f
    assert_equal "00011111", Bytes.bin(b.value)
    assert_equal 1, b.nib_one
    assert_equal 0x0f, b.nib_two 
  end

  def test_value_bug
    b = Bytes::Byte.new  "vvvv ...." => :nib_one,
                         ".... vvvv" => :nib_two
    b.value= 0xff
    b.nib_one = 0
    assert_equal "00001111", Bytes.bin(b.value)
  end

  def test_mixed
    b = Bytes::Byte.new "vv.. ...." => :two_bits,
                        "10.. ...." => :ten,
                        "...1 1..." => :two_middle,
                        "..vv vv.." => :four_middle
    
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

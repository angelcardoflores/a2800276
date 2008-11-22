require 'test/unit'
require 'bitstring'

class Test_Bitstring < Test::Unit::TestCase

B=Bitstring

def test_basics
  b = Bitstring.new("\x31"*8)
  assert_equal("11111111", b)
  
  b = B.new("\xF0"*8)
  assert_equal("1111000011110000111100001111000011110000111100001111000011110000", b.bits)

  b = B.new("\x0F"*8)
  assert_equal("0000111100001111000011110000111100001111000011110000111100001111", b.bits)

  i = 0
  expect = 0
  b.each_bit {|bit|
    assert_equal(expect, bit) 
    i+=1
    expect = (expect == 0 ? 1 : 0) if i%4 == 0
  }
  

  assert_equal(1, b.bit(-1) )
  assert_equal(0, b.bit(-(b.length)) )
  assert_nil(b.bit(64))
  assert_nil(b.bit(1000))
  
end

def test_complement
  [1,4,8,150].each { |i|
    [ ["\x00", "\xFF"], ["\xF0", "\x0F"], ["\x1d", "\xe2"], ["\xdb","\x24"] ].each{|pair|
      b = Bitstring.new(pair[0]*i)
      b2 = B.new(pair[1]*i)
      assert_equal(b, ~b2)
      assert_equal(b, ~(~b))
      assert_equal("\000"*i, (b & ~b))
      assert_equal("\000"*i, (b2 & ~b2))
    }
  }
end

def test_to_i
  b = B.new("\x31\x31")
  assert_equal(11, b.to_i(10))
  assert_equal(17, b.to_i(16))
  assert_equal(12593, b.to_i)
end

def test_shift
  b = B.new("\xFF")
  assert_equal("11111111", b.bits)
  assert_equal("11111110", (b<<1).bits)
  assert_equal("11111100", (b<<2).bits)
  assert_equal("00111111", (b>>2).bits)
  assert_equal("00000000", (b>>8).bits)
  assert_equal("00000000", (b<<8).bits)
  assert_equal("00000000", (b<<16).bits)
  assert_equal("11111111", b.bits)
end

def test_shift_assign
  b = B.new("\xFF\xFF")
  assert_equal("1111111111111111", b.bits)
  assert_equal("1111111111111110", (b<<=1).bits)
  assert_equal("1111111111111000", (b<<=2).bits)
  assert_equal("0011111111111110", (b>>=2).bits)
  assert_equal("0000000001111111", (b>>=7).bits)
  assert_equal("0111111100000000", (b<<=8).bits)
  assert_equal("0000000000000000", (b<<=16).bits)
  assert_equal("0000000000000000", b.bits)
end

def test_operators
  b= B.new  "\xff"
  b2= B.new "\x00"
  
  assert_equal(b2, (b & b2))
  assert_equal((b2 & b), (b & b2))
  assert_equal(b, (b | b2))
  assert_equal((b2 | b), (b | b2))
  assert_equal(b, (b ^ b2))
  assert_equal((b2^b), (b ^ b2))

  [1,4,7,23].each {|i|
    ["\x01", "\x10", "\xef", "\xdb", "\x00", "\xFF"].each {|pat| 
      b = B.new "\xff"*i
      b2 = B.new "\x00"*i
      b3 = B.new pat*i
      assert_equal(b3, b&b3)
      assert_equal(b3, b2|b3)
      assert_equal(b3, (b2^b3)^b2)
      assert_equal(b3, (b^b3)^b)
    }
  }
  
end

def test_set_clear
  b = B.new("\xff")
  assert_equal("11111111", b.bits)
  0.upto(7){|i|
    b.set(i)
    assert_equal("11111111", b.bits)
  }

  0.upto(7){|i|
    b.clear(i)
  }
  assert_equal("00000000", b.bits)

  [3,4,5].each {|i| b.set i}
  assert_equal("00011100", b.bits)

  b.clear 4
  assert_equal("00010100", b.bits)

  b = B.new("\x0f\xf0")
  assert_equal("0000111111110000", b.bits)
  b.set 0
  b.set b.length*8-1
  b.clear 7
  b.clear 8
  assert_equal("1000111001110001", b.bits)
  
  b.clear -1
  b.set -2
  assert_equal("1000111001110010", b.bits)
  
end



end

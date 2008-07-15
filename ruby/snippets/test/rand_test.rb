require 'test/unit'
require File.dirname(__FILE__) + '/../lib/random_bytes'

class RandomBytesTest < Test::Unit::TestCase

  def test_basics
    ["A", "Bloedsinn", Time.new, Dir].each {|thing|
      assert_raise(RuntimeError) {
        Bytes.check_byte thing
      }
    }
    assert_raise(RuntimeError) {
      Bytes.check_bytes [-1, 10000, 10*10, 256, -10000] 
    }

    arr = Bytes.sample "\x00", "\xff", 1000
    assert_equal 1000, arr.length


  end



end

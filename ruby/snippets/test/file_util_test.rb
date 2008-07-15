require 'test/unit'
require File.dirname(__FILE__) + '/../lib/file_utils'

class TestFileUtils < Test::Unit::TestCase

  def test_basics
    assert_raise(RuntimeError) {
      # only String or file
      check_valid_file self
    }
    
    file = File.open(File.dirname(__FILE__) + '/test_file.test')
    file2 = check_valid_file file, ".test"
    assert_equal file, file2

    file2 = check_valid_file file, "test"
    assert_equal file, file2

    file2 = check_valid_file file
    assert_equal file, file2

    assert_raise(RuntimeError) {
      # wrong extension
      check_valid_file file, ".txt"
    }
  end

  def test_string
    str = File.dirname(__FILE__) + '/test_file.test'

    file2 = check_valid_file str, ".test"
  
    file2 = check_valid_file str, "test"
  
    file2 = check_valid_file str
  
    assert_raise(RuntimeError) {
      # wrong extension
      check_valid_file str, ".txt"
    }

    # file doens't exist
    assert_raise(RuntimeError) { 
      file2 = check_valid_file "file_which_doesnt_exist.test" 
    }

    # file isn't readable i-> can't be added to svn... 
#    assert_raise (RuntimeError) { 
#      file2 = check_valid_file str+".test", ".test"
#    }
  end


end

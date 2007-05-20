require 'erlang_util'
require 'erlang_type'
require 'erlang_connection'

require 'stringio'
require 'cmdline'


#<<112,131,104,3,97,2,100,0,0,103,100,0,13,116,105,109,64,49,50,55,46,48,46,48,46,49,0,0,0,36,0,0,0,0,2,131,104,2,114,3,100,0,13,116,105,109,64,49,50,55,46,48,46,48,46,49,2,0,0,2,106,0,0,0,0,0,0,0,0,100,0,2,111,107>>

include Erlang::Util

if $0 == __FILE__

  file_readable=lambda{|f| File.readable? f}
  cmdline = CommandLine.new [
    ["-f", "--file", :file_name, "file_name", false, nil, "bytes to parse (def: STDIN)", file_readable],
    ["-c", "--contents", :content, "content", false, "raw", "content of file [raw|erl]", ["raw", "erl"]],
    ["-l", "--len", :parse_len, false, false, false, "parse 4 byte len marker, def: false"],
    ["-h", "--head", :parse_tag, false, false, false, "parse header (PASS_THROUGH)"]
  ]
  
  cmdline.parse
  io = cmdline.file_name ? File.new(cmdline.file_name) : STDIN
  if cmdline.content == "erl"
    str = io.read
    if str =~ /<<(.*)>>/
      nums=$1.split(',')
      str2 = ""
      nums.each {|n|
        str2 += e_byte(n.to_i)  
      }
      io = StringIO.new str2
    else
      raise "!? #{str}"
    end
  end
  class << io; include Erlang::Net; end
  
  if cmdline.parse_len
    puts "len: #{io.read_four_bytes_big}"
  end
  if cmdline.parse_tag
    puts "tag: #{io.getc}"
  end

  while type = Erlang::BaseType.parse(io, true)
    puts type
    break if io.eof?
  end

end

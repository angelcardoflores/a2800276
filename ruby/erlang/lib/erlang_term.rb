require 'stringio'
require 'erlang_type'

module Erlang

def Erlang.to_erl str, *vals
  t = TermHelper.new str, vals  
  t.parse
end

class TermHelper
  def initialize str, vals=[]
    io = StringIO.new(str)
    class << io;
      def read_compare s
        v = read(s.size)
        raise "Parse Error at : #{pos} expected: #{s} got: #{v}" unless s==v
        v
      end

      def peek
        c = getc
        ungetc(c)
        return [c].pack("C")
      end
    end

    @io = io
    @vals = vals
  end 

  def parse 
    return nil if @io.eof?

    consume_whitespace

    c = @io.peek
    case c
    when '['
      parse_list
      #LIST
    when '"'
      parse_string
      #STRING
    when '{'
      parse_tuple
    when '<'
      parse_pid_or_binary 
    when '#'
      parse_ref
    when /'|[a-z]/
      parse_atom
    when /\d/
      parse_number
    when '$'
      raise "Error: can't fill in value" unless @vals.size > 0
      @io.read_compare('$')
      @vals.shift
    else
      raise "Parse Error at: #{@io.pos}"
    end
  end # parse

  def parse_data start, stop, sep=',' 
    @io.read_compare start
    vals = []
    consume_whitespace
    
    while @io.peek != stop
      vals.push parse
      consume_whitespace
      if @io.peek == sep
        @io.read_compare sep
      elsif @io.peek != stop
        raise "Error: expected '#{sep}' or #{stop}, got: #{@io.peek}" 
      end
      consume_whitespace
    end
    @io.read_compare stop

    vals
  end

  def parse_list
    vals = parse_data '[', ']'
    List.new vals
  end

  def parse_tuple
    vals = parse_data '{', '}'
    Tuple.new vals
  end
  
  # these don't work, TODO
  #   a ref might be #Ref<1.2.3>
  #   the internal values get parsed as 1.2(float)
  def parse_ref
    vals = parse_data '#Ref<', '>', '.'
    puts vals
    Ref.new *vals
  end

  # these don't work, TODO
  #   a pid might be <1.2.3>
  #   the internal values get parsed as 1.2(float)
  def parse_pid
    vals == parse_data('<', '>', '.')
    Pid.new *vals
  end
  
  def parse_quoted quote
    @io.read_compare quote
    str = ""
    while true
      c = @io.read(1)
      break if c == quote
      
      if c == '\\' and @io.peek == quote
        str += @io.read(1)
      else
        str += c
      end
    end
    str
  end

  def parse_string
    Erlang::String.new parse_quoted('"')
  end

  def parse_pid_or_binary
    @io.read_compare '<'
    if @io.peek == '<'
      @io.ungetc('<'[0])
      parse_binary
    else
      parse_pid
    end
  end

  def parse_binary
    @io.read_compare '<<'
    vals = []
    consume_whitespace
    while @io.peek != '>'
      vals.push read_number.to_i
      consume_whitespace
      if @io.peek == ','
        @io.read_compare ',' 
      elsif @io.peek != '>'
        raise "Error: expected ',' or '>', got: #{@io.peek}" 
      end
      consume_whitespace
    end
    @io.read_compare '>>'
    Binary.new vals
  end # parse_binary

  def parse_atom
    # 'Complicated Atom'
    # easyAtom
    c = @io.peek
    return Atom.new(parse_quoted("'")) if c == "'"
    str = ''
    while c =~ /\w/
      str += @io.read(1)
      c = @io.peek
    end
    Atom.new str
  end

  def parse_number
    num = read_number /\d|\.|-/
    if num.include?('.')
      Float.new num
    else
      Number.new num
    end
  end

  def read_number check=/\d/
    str = ""
    c = @io.peek
    while c =~ check
      str << @io.read(1)
      c=@io.peek
    end
    str
  end





  def consume_whitespace
    while ((i=@io.read(1)) =~ /\s/) 
    end
    @io.ungetc(i[0])
  end

  def consume_whitespace_and_comma
    while (@io.read(1) =~ /[\s|,]/) 
    end
  end
    
end

end # module Erlang


if $0 == __FILE__

  str = '{"ding", "dong", "dang"}'
  a = Erlang.to_erl str
  str = '{"ding", "dong", <<1,2,3>>, $, test, $, \'EXIT\', 1.0, 2}'
  t = Erlang::TermHelper.new str, [a,a]
  puts t.parse

end

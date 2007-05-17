require 'socket'
require 'singleton'
require 'erlang_util'
require 'erlang_node'
require 'erlang_connection'

module Erlang

  # common values
   EPMDPORT = 4369
   DEBUG = true

  # Definitions of message codes */

   EPMD_ALIVE_REQ     = 'a'
   EPMD_ALIVE_OK_RESP = 'Y'
   EPMD_PORT_REQ      = 'p'
   EPMD_NAMES_REQ     = 'n'
   EPMD_DUMP_REQ      = 'd'
   EPMD_KILL_REQ      = 'k'
   EPMD_STOP_REQ      = 's'

  # New epmd messages 

   EPMD_ALIVE2_REQ    = 'x' # 120 x78
   EPMD_PORT2_REQ     = 'z' # 122
   EPMD_ALIVE2_RESP   = 'y' # 121
   EPMD_PORT2_RESP    = 'w' # 119 x77

class Epmd_Port2_Req
  include Erlang::Util
  attr_reader :node
  # node is a string 'alivename@hostname'
  def initialize node
    @node = Erlang::Node.get_node node
  end
  def encode
    #len = e_two_bytes_big(@node.node_name.size+1)
    req = EPMD_PORT2_REQ
    req+@node.node_name
  end
end # Epmd_Request

class Epmd_Alive2_Req
  include Erlang::Util
  attr_reader :node
  # node is an Erlang::LocalNode
  def initialize node
    @node = node
  end
  def encode
    #len = e_two_bytes_big(@node.node_name.size+13)
    req = EPMD_ALIVE2_REQ
    port = e_two_bytes_big(@node.port_no)
    type = e_byte(@node.type)
    protocol = e_byte(@node.protocol)
    hi, lo = @node.dist_range.map {|a|
      e_two_bytes_big(a)
    }
    nlen = e_two_bytes_big(@node.node_name.size)
    req+port+type+protocol+hi+lo+nlen+@node.node_name+"\0\0"
  end

end # Epmd_Alive2_Req
   

class Epmd_Stop_Req
  include Erlang::Util
  attr_reader :node
  def initialize node
    @node = Erlang::Node.get_node node
  end
  def encode
    #len = e_two_bytes_big(@node.node_name.size+1)
    req = EPMD_STOP_REQ
    req+@node.node_name
  end
end

class Epmd_Port2_Resp
  attr_reader :tag, :result, :node

  def initialize erl_io, node
    @io=erl_io
    @node=node
    decode
  end

  def decode
    @tag        = @io.getc
    @result     = @io.getc
    #@node       = Erlang::Node.new.decode @io if @result == 0
    
    port_no    = @io.read_two_bytes_big #d_two_bytes_big(erl_io.read(2))
    node_type  = @io.getc #erl_io.getc
    protocol   = @io.getc
    dist_range = [@io.read_two_bytes_big, @io.read_two_bytes_big]
    node_name  = @io.read_packet_2
    extra     = @io.read_packet_2
    
    @node.port_no   = port_no
    @node.type = node_type
    @node.protocol  = protocol
    @node.dist_range= dist_range
    @node.extra     = extra
  end

  def to_s
    str = "tag : #{@tag}\n"+
    "result : #{@result}\n"
    str += @node.to_s if @node
    str
  end

end # Epmd_Port2_Resp


class Epmd_Alive2_Resp
  include Erlang::Util
  attr_reader :tag, :result, :creation
  def initialize io
    @io=io
    decode
  end
  def decode
    @tag = @io.getc
    puts @io
    puts @tag
    @result = @io.getc
    puts @result
    @creation = d_two_bytes_big(@io.read(2))
  end
  def to_s
    "tag : #{@tag}\n"+
    "result : #{@result}\n"+
    "creation : #{@creation}\n"
  end
end

class Epmd_Stop_Resp
  attr_accessor :response
  def initialize io
    str = io.read(7)
    @response = str
  end
  def to_s
    @response
  end
end
 
class Epmd
  include Singleton 
  def send req, resp_class, 
    resp = nil
    TCPSocket.open(req.node.host,EPMDPORT) {|socket|
      class << socket; include Erlang::Net; end
      socket.write_packet_2(req.encode)
      resp = resp_class.new socket
    }
    resp
  end

  def lookup_port node
    node = Erlang::Node.get_node node
    req = Epmd_Port2_Req.new(node)

    resp = nil
        
    TCPSocket.open(req.node.host,EPMDPORT) {|socket|
      class << socket; include Erlang::Net; end
      socket.write_packet_2(req.encode)
      resp = Epmd_Port2_Resp.new(socket, node)
    }

    #send req, Epmd_Port2_Resp
    if DEBUG
      puts "Looked up:\n#{node}"
      puts "---"
      puts resp
      puts "---"
    end
    resp

  end

  def publish_port local_node
    req = Epmd_Alive2_Req.new local_node
    
    socket = TCPSocket.open(req.node.host,EPMDPORT)
    class << socket; include Erlang::Net; end
    socket.write_packet_2 req.encode
    resp = Epmd_Alive2_Resp.new socket
    
    if resp.result != 0
      puts "published failed for node: #{local_node}, response: #{resp}"
      socket.close
      socket = nil
      #TODO Exception
    end
    if DEBUG
      puts "Published:\n#{local_node}"
      puts "-!-"
      puts resp
      puts "---"
    end
    [resp, socket]
  end

  def unpublish_port local_node
    req = Epmd_Stop_Req.new local_node
    send req, Epmd_Stop_Resp
  end

end #Epmd
end #module

if $0 == __FILE__
  require 'cmdline'


  cmd = CommandLine.new [
    ["-n", "--node", :node, "node name", true, nil, "node to look up"],
    ["-p", "--epmd_port", :epmd_port, "port nr", false, 4369, "port number of EPMD", /^\d+$/]
  ]
  cmd.parse 

  pmd = Erlang::Epmd.instance
  puts pmd.lookup_port(cmd.node)
end

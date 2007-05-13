require 'erlang_util'
require 'erlang_epmd'
require 'erlang_process'
require 'socket'
require 'thread'
module Erlang

class Node
  include Erlang::Util
  attr_accessor :type, :protocol, :extra
  attr_writer :host, :dist_range, :cookie, :port_no, :type, :protocol
  attr_reader :node_name
  def decode erl_io
    @port_no    = d_two_bytes_big(erl_io.read(2))
    @node_type  = erl_io.getc
    @protocol   = erl_io.getc
    @dist_range = [d_two_bytes_big(erl_io.read(2)), d_two_bytes_big(erl_io.read(2))]
    @node_name  = erl_io.read_packet_2
    @extra      = erl_io.read_packet_2
    self
  end
  
  def host
    @host ||= 'localhost'
  end
  def dist_range
    @dist_range ||= [5,5]
  end
  def cookie
    @cookie ||= ""
  end
  def node_name= name
    if name.include? "@"
      name, host = name.split('@')
      @host= host
    end
    @node_name = name[0,0xff]
  end
  def port_no
    @port_no ||=0
  end
  def protocol
    @protocol ||=0 # 0 IPv4
  end
  def type
    @type ||= 77 # 77 normal 72 hidden
  end

  def full_name
    @node_name+'@'+@host
  end


  def to_s
    "port_no : #{@port_no}\n"+
    "node_type : #{@node_type}\n"+
    "protocol : #{@protocol}\n"+
    "dist_range : #{dist_range[0]}, #{dist_range[1]}\n"+
    "node_name : #{@node_name}\n"+
    "host : #{host}\n"+
    "extra : #{@extra}\n"
  end

  def self.make_node str
    return str if str.kind_of?  self
    node = self.new
    name, host = str.split('@') #TODO check
    node.node_name = name
    node.host = host
    node
  end

end #node

class LocalNode < Node
  attr_accessor :epmd_socket, :connections
  def initialize name, cookie="", port=0
    self.node_name= name
    self.cookie= cookie
    @port_no=port
    init
  end

  def init
    # from OtpLocalNode
    serial = 0
    pidCount = 1
    portCount = 1
    refId = []
    refId[0] = 1
    refId[1] = 0
    refId[2] = 0

    @lock = Mutex.new
    @pid_count = 0

    @net_kernel=Erlang::NetKernel.new(self) # process register themselves upon creation, don't need to do it.
    @net_kernel.receive_loop
    @connections = {}
    @acceptor = Acceptor.new self
    @acceptor.run


  end

  def generate_pid
    pid = 0
    @lock.synchronize {
      pid = @pid_count
      @pid_count+=1
    }
    Erlang::Pid.new self.full_name, pid, 0, 0
  end

  def add_connection socket
    @connections[socket.remote_node]=socket
  end 

  def send remote_node, msg
    # get connection from cache
    con = @connections[remote_node]
    # if not available, create connection
    con = Erlang::Connection.new(remote_node, self) unless con # Connection will register itself.
    # send msg
    con.write_packet_4 msg
  end


  def register_process process
    @procs ||= {}
    @procs[process.name]=process if process.name
    @procs[process.pid]=process if process.pid
  end

  def get_process process
    return nil unless @procs
    puts process
    @procs.keys.each{|key| puts "#{key} #{process} #{key==process}"}
    return @procs[process]
  end
end#class

class Acceptor
  IP6 = false
  def initialize node
    @node=node
  end

  def run 
    listen = IP6 ? '::' : '0.0.0.0'
    listener = Erlang::IncomingConnection.new(listen, @node)
    domain, port, name, addr = listener.addr
    puts listener.addr
    @node.port_no=port

    @t = Thread.new { # start listening
      while (session = listener.accept) # IncomingConnection does handshake 
        puts "received session!"
        @node.add_connection session    # publish connection at node
        session.read_loop               # connection starts reading msg in own thread.
      end
    }
    resp, socket = Erlang::Epmd.instance.publish_port(@node)
    @node.epmd_socket = socket
    #HANDLE resp.result != 0
  end

end #Acceptor
end #module

if $0 == __FILE__
  $DEBUG=true
   node = Erlang::LocalNode.new "test@127.0.0.1", "abc"
   Thread.abort_on_exception=true
   while true
     #resp = Erlang::Epmd.instance.lookup_port(node)
     puts "alive"
     sleep 20
   end

end


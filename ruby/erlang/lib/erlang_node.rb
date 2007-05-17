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

#  def decode erl_io
#       self
#  end
  
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
    puts @node_name
    puts @host
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
  
  def self.add_to_cache node
    @cache ||={}
    @cache[node.full_name]=node
  end
  
  def self.get_cached_node str
    @cache ||= {}
    @cache[str]
  end

  def self.get_node str
    
    # Only allow one instance per Node, if we're passed a node, see 
    # we should pass it back
    name = (str.is_a? Node) ? str.full_name : str.to_s
    node = get_cached_node name 
    unless node
      node = Node.new(name)
      add_to_cache node 
    end
    node
  end

  private

  def initialize node_name
    self.node_name=node_name
  end
  
end #node

class LocalNode < Node
  attr_accessor :epmd_socket, :connections

  def init

    @lock = Mutex.new # lock to generate pids
    @pid_count = 0 # unique pid counter

    @net_kernel=NetKernel.new(self) # process register themselves upon
                                    # creation, don't need to do it.
    @net_kernel.receive_loop

    @connections = {}               # stores connections to other nodes
    @acceptor = Acceptor.new self   # listens for connections from other nodes
    @acceptor.run


    resp, socket = Epmd.instance.publish_port(self) # register self with EPMD
    @epmd_socket = socket
    #HANDLE resp.result != 0


  end

  # generate new pid (Erlang::Pid) for this nodes processes
  def generate_pid
    pid = 0
    @lock.synchronize {
      pid = @pid_count
      @pid_count+=1
    }
    Erlang::Pid.new(self.full_name, pid, 0, 0)
  end
  
  # add a new connection to a remote_node
  def add_connection socket
    @connections[socket.remote_node]=socket
  end 

  # recipient = remote_node or pid
  # Send a protocol message to a recipient.
  # Recipient is either a Node, a node name (String, Symbol) 
  def send recipient, msg
    recipient = recipient.node.value if recipient.is_a? Pid
    recipient = Node.get_node recipient
    # get connection from cache
    con = @connections[recipient]
    # if not available, create connection
    puts recipient
    con = Erlang::Connection.new(recipient, self) unless con # Connection will register itself.
    # send msg
    puts "Sending #{msg} to #{recipient}"
    con.write_packet_4 msg.to_bytes
  end


  def register_process process
    @procs ||= {}
    @procs[process.name]=process if process.name
    @procs[process.pid]=process if process.pid
  end

  def get_process process
    unless @procs
      raise "Error: looking up #{process} no procs registered"
    end
    puts process
    @procs.keys.each{|key| puts "#{key} #{process} #{key==process} #{key.class} #{process.class}"}
    return @procs[process]
  end
  
  def self.get_node name, cookie="", port=0
    node= get_cached_node name
#    node = Node.get_node name
#puts node
#    if node && !node.is_a?(LocalNode)
#      raise "Protocol Error: node #{name} already instantiate as remote node" 
#    end

    unless node
      node = LocalNode.new(name, cookie, port)
      Node.add_to_cache node
    end
    node

  end

  private
  def initialize name, cookie="", port=0
    self.node_name= name
    self.cookie= cookie
    @port_no=port
    init
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
  end

end #Acceptor
end #module

if $0 == __FILE__
  $DEBUG=true
   node = Erlang::LocalNode.get_node "test@127.0.0.1", "abc"
   Thread.abort_on_exception=true
   while true
     #resp = Erlang::Epmd.instance.lookup_port(node)
     puts "alive"
     sleep 20
   end

end


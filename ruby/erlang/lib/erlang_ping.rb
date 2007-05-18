require 'erlang_node'
require 'erlang_message'
require 'erlang_type'


module Erlang

class Ping 
  
  def initialize local_node, remote_node
    @local_node = local_node
    @remote_node = remote_node
  end
  def ping
    process = Process.new @local_node
    ref = Ref.new(@local_node.full_name, [1,2,3],1)
    tuple = Tuple.new([process.pid,ref])
    ping = Tuple.new([:is_auth, @local_node.full_name.to_sym])
    msg = Tuple.new([:'$gen_call', tuple, ping])
    
    proto = RegSend.make(process.pid, Atom.new(''), :net_kernel, msg)

    @local_node.send @remote_node, proto
    return process.receive

  end
end

end #module Erlang

if $0 == __FILE__
  
  lnode = Erlang::LocalNode.get_node 'test@127.0.0.1', 'abc'
  rnode = Erlang::Node.get_node 'tim@127.0.0.1'
  p = Erlang::Ping.new lnode, rnode
  puts p.ping

  while true
    sleep 10
  end

end

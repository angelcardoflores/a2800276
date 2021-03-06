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
    #ref = Ref.new(@local_node.full_name, [1,2,3],1)
    #tuple = Tuple.new([process.pid,ref])
    #tupe = Erlang.to_erl("{$,#Ref<'test@127.0.0.1'.2>}", process.pid)
    #ping = Tuple.new([:is_auth, @local_node.full_name.to_sym])
    #ping = Erlang.to_erl("{is_auth, '#{local_node.full_name}'}")
    #msg = Tuple.new([:'$gen_call', tuple, ping])
    msg = Erlang.to_erl("{'$gen_call', {$,$}, {is_auth, '#{@local_node.full_name}'}}", process.pid, @local_node.make_ref)

    process.send_reg @remote_node, 'net_kernel', msg
    puts "HERE"
    return process.receive

  end
end

end #module Erlang

if $0 == __FILE__
  $DEBUG=true 
  lnode = Erlang::LocalNode.get_node 'test@127.0.0.1', 'abc'
  rnode = Erlang::Node.get_node 'tim@127.0.0.1'
  p = Erlang::Ping.new lnode, rnode
  10.times do
    puts p.ping
    puts "sent"
  end

  while true
    sleep 10
  end

end

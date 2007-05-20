require 'socket'
require 'digest/md5'
require 'stringio'
require 'erlang_util'
require 'erlang_node'
require 'erlang_epmd'
require 'erlang_type'
require 'erlang_message'

module Erlang


module Net
  include Erlang::Util

  DFLAG_PUBLISHED           =1
  DFLAG_ATOM_CACHE          =2
  DFLAG_EXTENDED_REFERENCES =4
  DFLAG_DIST_MONITOR        =8
  DFLAG_FUN_TAGS            =0x10
  DFLAG_DIST_MONITOR_NAME   =0x20
  DFLAG_HIDDEN_ATOM_CACHE   =0x40
  DFLAG_NEW_FUN_TAGS        =0x80
  DFLAG_EXTENDED_PIDS_PORTS =0x100
  DFLAG_EXPORT_PTR_TAG      =0x200
  
  attr_accessor :alive, :local_node, :remote_node

  # DEBUG
  def debug str
    STDOUT.puts str
  end

  # Erlang packages are formatted:
  # |2or4byte bigend len| data[len] ... |
  # this function returns just the data.
  def read_packet_2 tag=nil
    len = self.read_two_bytes_big
    read_len len, tag
  end

  def read_packet_4 tag=nil
    len = self.read_four_bytes_big
    read_len len, tag
  end

  def read_len len, tag=nil
    data = ''
    if len 
      data = self.read(len)
    end
    if tag
      rtag = data[0,1]
      raise "Protocol Error: expected tag '#{tag}', received '#{rtag}'" unless tag==rtag
      data = data[1,data.size-1] 
    end
    data
  end


  def read_four_bytes_big
    val = self.read(4)
    d_four_bytes_big(val)
  end
  
  def read_two_bytes_big
    val = self.read(2)
    d_two_bytes_big(val)
  end

  def write_packet_2 data
    len = e_two_bytes_big(data.size)
    self.write(len+data)
  end

  def write_packet_4 data
    len = e_four_bytes_big(data.size)
    self.write(len+data)
  end

  def read_loop 
    Thread.new {
      @alive=true
      while @alive
        data = read_packet_4
        debug "read packet (len: #{data.size}) from #{@remote_node.full_name}"
        if data.size==0
          # tick, tock
          write "\0\0\0\0"
        else
          msg = Erlang::Protocol.parse data
          process = @local_node.get_process msg.recipient
          debug "received message from #{@remote_node.full_name}"
          debug "  forwarding to #{msg.recipient} :: #{process}"
          process.add_message msg
        end
      end
    }
  end

  def gen_digest challenge, node
    Digest::MD5.digest(node.cookie+challenge.to_s)
  end
end

class Connection < TCPSocket
  include Erlang::Net


  def initialize remote_node, local_node
    @local_node=local_node
    @remote_node = Erlang::Epmd.instance.lookup_port(remote_node).node
    super(remote_node.host,remote_node.port_no)
    
    do_handshake
    debug "handshake completed: #{@remote_node.full_name}"
    @local_node.add_connection(self)

    read_loop
  end

  def do_handshake
    send_name
    #receive_status
    data = read_packet_2 's' #TODO check resp ok
    dist, peer_flags, challenge, rname = receive_challenge

    digest = gen_digest challenge, @local_node
    challenge = rand 2**32
    expected_digest = gen_digest challenge, @local_node

    send_challenge_reply challenge, digest
    #receive_challenge_ack
    
    chal_resp = read_packet_2 'a'
    debug "Received response, digest correct= #{chal_resp==expected_digest}"
  end # do_handshake

  private

  def send_name
    flags = DFLAG_EXTENDED_REFERENCES | DFLAG_EXTENDED_PIDS_PORTS
    flags = e_four_bytes_big(flags)
    write_packet_2 "n\5\5"+flags+@local_node.full_name
  end

  def receive_challenge
    # expecting n|\5\5|flags|challenge|remote_name
    data = read_packet_2 'n'
    dist = d_two_bytes_big(data[0,2]) # should be 5,5
    peer_flags = d_four_bytes_big(data[2,4])
    challenge = d_four_bytes_big(data[6,4])
    rname = data[10,data.size-10]
    [dist, peer_flags, challenge, rname]
  end

  def send_challenge_reply challenge, digest
    tag = 'r'
    challenge = e_four_bytes_big(challenge)
    write_packet_2(tag+challenge+digest)
  end

    


end#connection

class IncomingConnection < TCPServer
  include Erlang::Net

  def initialize host, local_node
    @local_node = local_node
    super host, @local_node.port_no
  end

  def accept
    session = super
    debug "accepted connection from: #{session.peeraddr.join(',')}"
    class << session; include Erlang::Net; end
    session.local_node=@local_node

    do_handshake session
    session
  end

  def do_handshake socket
    
    # RECEIVE_NAME
    dist, flags, node_name = receive_name(socket)
    @remote_node = Erlang::Node.get_node node_name
    remote_node.port_no = socket.peeraddr[1]
    socket.remote_node = remote_node
    
    # SEND_STATUS
    socket.write_packet_2 'sok' # take other possibilities into account TODO
   
    # SEND_CHALLENGE
    challenge = rand 2**32
    expected_digest = gen_digest challenge, @local_node
    send_challenge socket, challenge, dist

    challenge, digest = receive_challenge_reply socket
    debug "received digest, ok: #{expected_digest==digest}" #TODO

    digest = gen_digest challenge, @local_node
    send_challenge_ack socket, digest

    return true # TODO check handshake results 
  end

private
 
  #+---+--------+--------+-----+-----+-----+-----+-----+-----+-...-+-----+
  #|'n'|Version0|Version1|Flag0|Flag1|Flag2|Flag3|Name0|Name1| ... |NameN|
  #+---+--------+--------+-----+-----+-----+-----+-----+-----+-... +-----+
  def receive_name socket 
    data = socket.read_packet_2 'n'
    dist = data[0,2]  # 5,5
    flags = data[2,4] # flags
    node_name = data[6, data.length]
    [dist, flags, node_name]
  end


  #+---+--------+--------+-----+-----+-----+-----+-----+-----+-----+-----+---
  #|'n'|Version0|Version1|Flag0|Flag1|Flag2|Flag3|Chal0|Chal1|Chal2|Chal3|
  #+---+--------+--------+-----+-----+-----+-----+-----+-----+---- +-----+---
  #  ------+-----+-...-+-----+
  #   Name0|Name1| ... |NameN|
  #  ------+-----+-... +-----+
  def send_challenge socket, challenge, dist='\5\5'
    tag = 'n'
    flags = DFLAG_EXTENDED_REFERENCES | DFLAG_EXTENDED_PIDS_PORTS
    flags = e_four_bytes_big(flags)
    challenge = e_four_bytes_big(challenge)
    debug ("writing challenge: #{challenge} to #{@remote_node.full_name}")
    socket.write_packet_2(tag+dist+flags+challenge+@local_node.full_name)
  end

  def receive_challenge_reply socket
    data = socket.read_packet_2 'r'
    challenge = d_four_bytes_big(data[0,4])
    digest = data[4,data.length]
    [challenge, digest]
  end

  def send_challenge_ack socket, digest
    tag = 'a'
    socket.write_packet_2 tag+digest
  end

end



end

if $0 == __FILE__
  node = Erlang::LocalNode.get_node 'test@localhost', 'abc'
  puts node
  rnode = Erlang::Node.get_node 'tim@localhost'
  rnode.node_name='tim@localhost'
  con = Erlang::Connection.new rnode, node
  
    
end

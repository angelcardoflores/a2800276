require 'socket'
require 'digest/md5'
require 'erlang_util'
require 'erlang_node'
require 'erlang_epmd'
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

  # DEBUG
  def debug str
    STDOUT.puts str
  end

  # Erlang packages are formatted:
  # |2byte bigend len| data[len] ... |
  # this function returns just the data.
  def read_packet tag=nil
    debug "!!!!!!!! #{tag}"
    len = self.read(2)
    data = nil
    if len 
      len = d_two_bytes_big(len)
      data = self.read(len)
    end
    if tag
      rtag = data[0,1]
      raise "Protocol Error: expected tag '#{tag}', received '#{rtag}'" unless tag==rtag
      data = data[1,data.size-1] 
    end
    data
  end

  def write_packet data
    len = e_two_bytes_big(data.size)
    self.write(len+data)
  end

  def gen_digest challenge, node
    Digest::MD5.digest(node.cookie+challenge.to_s)
  end
end

class Connection < TCPSocket


  include Erlang::Net


  def initialize remote_node, local_node
    remote_node = Erlang::Epmd.instance.lookup_port(remote_node).node
    super(remote_node.host,remote_node.port_no)
    @node= local_node
    do_handshake
  end

  def do_handshake
    flags = DFLAG_EXTENDED_REFERENCES | DFLAG_EXTENDED_PIDS_PORTS
    flags = e_four_bytes_big(flags)
    payload = "n\5\5"+flags+@node.full_name
    
    write_packet(payload)

    # expecting: sok
    data = read_packet 's'
    debug "resp #{data}"
    #TODO check resp ok

    # expecting n|\5\5|flags|challenge|remote_name
    data = read_packet 'n'
    dist = d_two_bytes_big(data[0,2]) # should be 5,5
    peer_flags = d_four_bytes_big(data[2,4])
    challenge = d_four_bytes_big(data[6,4])
    rname = data[10,data.size-10]

    debug "dist: #{dist}"
    debug "peer_flags: #{peer_flags}"
    debug "challenge: #{challenge}"
    debug "rname: #{rname}"

    tag = 'r'
    digest = gen_digest challenge, @node
    
    my_challenge = rand 2**32
    my_digest = gen_digest my_challenge, @node

    chal = e_four_bytes_big(my_challenge)
    write_packet(tag+chal+digest)

    chal_resp = read_packet 'a'

    debug "chal: #{chal_resp==my_digest}"

  end # do_handshake


end#connection

class IncomingConnection < TCPServer
  include Erlang::Net
  def initialize host, local_node
    @local_node = local_node
    super host, @local_node.port_no
    
  end
  def accept
    session = super
    debug "accepted connection: #{session}"
    class << session; include Erlang::Net; end
    debug session.class
    do_handshake session
    session
  end
  def do_handshake socket
    data = socket.read_packet 'n'
    debug "read: #{data}"
    dist = data[0,2]  # 5,5
    flags = data[2,4] # flags
    node_name = data[6, data.length]
    #TODO checks
   
    debug "#{node_name} : connection"

    socket.write_packet 'sok'
    
    debug "wrote sok"

    tag = 'n'
    dist = '\5\5'
    flags = DFLAG_EXTENDED_REFERENCES | DFLAG_EXTENDED_PIDS_PORTS
    flags = e_four_bytes_big(flags)
    challenge = rand 2**32
    digest = gen_digest challenge, @local_node
    challenge = e_four_bytes_big(challenge)
    chal_mes = tag+"\5\5"+flags+challenge+@local_node.full_name
    debug "will write #{chal_mes.size}"
    socket.write_packet(chal_mes)
    debug "wrote...."


    data = socket.read_packet 'r'
    debug data
    re_challenge = d_four_bytes_big(data[0,4])
    re_digest = data[4,data.length]

    debug "rec chal: #{digest == re_digest}"

    tag = 'a'
    digest = gen_digest @local_node, re_challenge
    socket.write_packet tag+digest
    

  end
end



end

if $0 == __FILE__
  node = Erlang::LocalNode.new 'test@localhost', 'abc'
  rnode = Erlang::Node.new 
  rnode.node_name='tim@localhost'
  con = Erlang::Connection.new rnode, node
  
    
end

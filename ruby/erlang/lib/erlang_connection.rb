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
  # |2byte bigend len| data[len] ... |
  # this function returns just the data.
  def read_packet_2 tag=nil
    len = self.read_two_bytes_big
    data = nil
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

  def read_packet_4 tag=nil
    len = self.read_four_bytes_big
    debug "read_packet_4: #{len}"
    data = nil
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

  def read_loop 
    Thread.new {
      debug "started read loop!"
      @alive=true
      while @alive
        data = read_packet_4
        debug "read packet!"
        if data.size==0
          # tick, tock
          write '\0\0\0\0'
        else
          msg = Erlang::Message.parse data
          debug msg
          process = @local_node.get_process msg.recipient
          process.add_msg msg
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
    @remote_node = Erlang::Epmd.instance.lookup_port(remote_node).node
    super(remote_node.host,remote_node.port_no)
    @node= local_node
    do_handshake
  end

  def do_handshake
    flags = DFLAG_EXTENDED_REFERENCES | DFLAG_EXTENDED_PIDS_PORTS
    flags = e_four_bytes_big(flags)
    payload = "n\5\5"+flags+@node.full_name
    
    write_packet_2(payload)

    # expecting: sok
    data = read_packet_2 's'
    debug "resp #{data}"
    #TODO check resp ok

    # expecting n|\5\5|flags|challenge|remote_name
    data = read_packet_2 'n'
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
    write_packet_2(tag+chal+digest)

    chal_resp = read_packet_2 'a'

    debug "chal: #{chal_resp==my_digest}"

  end # do_handshake


end#connection

class IncomingConnection < TCPServer
  include Erlang::Net
  attr_accessor :remote_node

  def initialize host, local_node
    @local_node = local_node
    super host, @local_node.port_no
  end
  def accept
    session = super
    debug "accepted connection: #{session}"
    class << session; include Erlang::Net; end
    debug session.class
    session.local_node=@local_node
    do_handshake session
    session
#    data = session.read_packet_4 'p' # 112 'passthrough
#    debug "datasize: #{data.size}"
#    debug data.unpack("H*") 
#    io = StringIO.new data
#    class << io; include Erlang::Net; end 
#    control_msg = Erlang::BaseType.parse io, true
#    debug "ctrl_msg:#{control_msg.to_s}<" 
#    msg = Erlang::BaseType.parse io, true
#    debug "msg: #{msg}"

  end
  def do_handshake socket
    data = socket.read_packet_2 'n'
    debug "read: #{data}"
    dist = data[0,2]  # 5,5
    flags = data[2,4] # flags
    node_name = data[6, data.length]
    remote_node=Erlang::Node.new
    remote_node.node_name = node_name
    socket.remote_node = remote_node 
    #TODO checks
   
    debug "#{node_name} : connection"

    socket.write_packet_2 'sok' # take other possibilities into account TODO
    

    tag = 'n'
    #dist = "\0\5"
    flags = DFLAG_EXTENDED_REFERENCES | DFLAG_EXTENDED_PIDS_PORTS
    #flags = 0x03fe
    flags = e_four_bytes_big(flags)
    challenge = rand 2**32
    debug challenge
    digest = gen_digest challenge, @local_node
    challenge = e_four_bytes_big(challenge)
    chal_mes = tag+dist+flags+challenge+@local_node.full_name
    debug "will write #{chal_mes.size}"
    socket.write_packet_2(chal_mes)
    debug "wrote...."


    data = socket.read_packet_2 'r'
    re_challenge = d_four_bytes_big(data[0,4])
    re_digest = data[4,data.length]
    

    debug "rec chal: #{digest == re_digest} len #{digest.size} rlen #{re_digest.size}"
    tag = 'a'
    digest = gen_digest re_challenge, @local_node
    socket.write_packet_2 tag+digest
    return true # TODO check handshake results 
  end
end



end

if $0 == __FILE__
  node = Erlang::LocalNode.new 'test@localhost', 'abc'
  rnode = Erlang::Node.new 
  rnode.node_name='tim@localhost'
  con = Erlang::Connection.new rnode, node
  
    
end

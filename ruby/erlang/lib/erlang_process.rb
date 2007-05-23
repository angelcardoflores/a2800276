require 'synchronized_queue'
require 'erlang_term'

module Erlang

class Process
  attr_accessor :name, :pid, :alive, :local_node, :trap_exit

  def initialize local_node, name=nil
    @local_node=local_node
    @pid=@local_node.generate_pid
    @name=name.to_sym if name
    @local_node.register_process(self)
    @queue=SynchronizedQueue.new
    @alive=true
  end


  def add_message msg
    # TODO check msg type, link unlink...
    case msg
    when Link
      add_linked msg.from_pid
    when Unlink
      remove_linked msg.from_pid
    when Exit
      erl_exit msg.reason
    when Exit2
      erl_exit msg.reason
    else
      @queue.push msg
    end
  end

  #Retrieves the first available message from the mailbox, blocking
  #until either a message is received or after +timeout+ seconds if
  #a timeout parameter is provided.
  #If a code block is passed in, receive blocks until the block returns
  #a 'truish' (not null, false) value 
  def receive timeout=nil, &block
    unless block_given?
      return @queue.shift(timeout)
    end
    
    @queue.each timeout, &block
  end

  def send pid, msg
    protocol_msg = Send.make('', pid, msg)
    @local_node.send_internal pid, protocol_msg
  end

  def send_reg node, name, msg
    protocol_msg = RegSend.make(self.pid, '', name, msg)
    @local_node.send_internal node, protocol_msg
  end
  
  # link the specified process to this one.
  # equivalent to `link(Pid)` in erlang
  def link pid
    protocol_msg = Link.make(self.pid, pid)
    add_linked pid
    @local_node.send_internal pid, protocol_msg
  end

  # add the specifed pid to this process' list of linked
  # processes. This method doesn't send a link message to the
  # process and is intented for internal housekeeping.
  def add_linked pid
    #TODO safe, one? linke per pid
    @linked ||= []
    @linked.push pid
  end
  
  # Removes the specified pid from the list of linked processes
  # This method doesn't send an unlink message to the pid and is 
  # intended solely for internal housekeeping.
  def remove_linked pid
    #TODO: safe
    return unless @linked
    @linked.delete pid
  end
  
  # unlinks the specified pid. Sends an unlink message to
  # the pid and removes it from this process' list of linked pids.
  def unlink pid
    protocol_msg = Unlink.make(self.pid, pid)
    @local_node.send_internal pid, protocol_msg
    remove_linked pid
  end

  #
  def erl_exit reason, pid=nil
    if pid
      send_exit pid, reason
    else
      @linked.each {|p|
        send_exit p, reason
      }
      # TODO shutdown the process, unregister from node.
      # check trap_exit
    end
  end

  def send_exit pid, reason
    msg = Exit.make self.pid, pid, reason
    @local_node.send_internal pid, msg
  end
  

  def to_s
    "#Process {#{name}, #{pid}}"
  end

end#class Process

class NetKernel < Process
  def initialize local_node
    super local_node, :net_kernel 
  end
  
  def receive_loop
    Thread.new {
      while @alive
        puts "waiting on receive"
        msg = receive
        puts "got: #{msg}"
        handle_msg msg
      end
    }
  end

  def handle_msg msg
    puts "NetKernel, handling #{msg}, #{msg.class}\n"
    if msg.is_a? RegSend

    # {'$gen_call',{<'tim@localhost'.292.1>,#Ref<'tim@localhost'.266600> 1|},{'is_auth','tim@localhost'}}
      
      message = msg.msg[2]
      value = message[0]
      node = message[1]
      ref = msg.msg[1][1]

      puts "is_auth? #{value.value}"
      to_pid = msg.from_pid
      cookie = msg.cookie
     
      puts "Cookie: #{cookie.class}"
      response = Erlang.to_erl("{$, yes}", ref) 
      #response = Send.make(cookie, to_pid, tuple)
      puts "net_kernel sending #{response} to #{to_pid}"
      send to_pid, response
    else
      raise "Protocol Error: don't know how to handle: #{msg.class}"
    end 
  end

end
end # Erlang

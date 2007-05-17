require 'synchronized_queue'

module Erlang

class Process
  attr_accessor :name, :pid, :alive
  def initialize local_node, name=nil
    @local_node=local_node
    @pid=@local_node.generate_pid
    @name=name.to_sym if name
    @local_node.register_process(self)
    @queue=SynchronizedQueue.new
    @alive=true
  end


  def add_message msg
    @queue.push msg
  end

  #Retrieves the first available message from the mailbox, blocking
  #until either a message is received or after +timeout+ seconds if
  #a timeout parameter is provided.
  #If a code block is passed in, receive blocks until the block returns
  #a 'truish' (not null, false) value 
  def receive timeout=nil, &block
    unless block_given?
      return @queue.shift(timeout)
      puts "pro -> receiving"
    end
    
    @queue.each timeout, &block
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
      response = Send.make(cookie, to_pid, Tuple.new([ref, :ok]))
      puts "net_kernel sending #{response} to #{to_pid}"
      @local_node.send to_pid, response
    else
      raise "Protocol Error: don't know how to handle: #{msg.class}"
    end 
  end

end









end # Erlang

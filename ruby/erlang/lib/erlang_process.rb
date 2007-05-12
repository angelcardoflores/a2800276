require 'synchronized_queue'

module Erlang

class Process
  attr_accessor :name, :pid, :alive
  def initialize local_node, name=nil
    @local_node=local_node
    @pid=@local_node.generate_pid
    @name=name
    @local_node.register(self)
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
      return @queue.shift timeout
    end
    
    @queue.each timeout, &block
  end

end#class Process

class NetKernel < Process
  def initialize local_node
    super local_node, 'net_kernel'
  end
  
  def receive_loop
    Thread.new {
      while @alive
        handle_msg receive
      end
    }
  end

  def handle_msg msg
    puts msg
  end

end









end # Erlang

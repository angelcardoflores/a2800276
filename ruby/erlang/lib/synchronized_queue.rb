require 'thread'

module Erlang

class SynchronizedQueue
  def initialize
    @queue = []
    @lock = Mutex.new
    @t = Thread.new{Thread.stop}
  end
  def push val
    @lock.sychronize {
      @queue.push
      t = @t
      @t = Thread.new{Thread.stop}
      t.kill
    }
  end
  alias << push
  alias enqueue push

  # retrieve one message from the queue
  # blocks until a message is received
  # if timeout is set, +shift+ only blocks for 
  # +timeout+ seconds
  def shift timeout=nil
    msg = nil
    stop = Time.now+timeout if timeout
    while msg == nil
      @lock.sychronize {
        msg = @queue.shift
      }
      @t.join(timeout) unless msg
      break if timeout && Time.now >= stop 
    end
    msg
  end

  # applies each message in the queue to the block until the block
  # returns a truish value (not nil or false). The message the passed
  # block returns true for is removed from the queue. Blocks (for +timeout+
  # seconds if timeout is set, or until a suitable message is available
  # on the queue) if there are no messages currently pending.

  def each timeout=nil
    stop = Time.new+timeout if timeout
    found = false
    
    while ! found
      @lock.sychronize {
        @queue.each {|el|
          found = yield el
          if found
            @queue.delete el
            break
          end
        }
      }
      break if timeout && Time.now >=stop
      @t.join(timeout) unless found
    end
  end
end # SynchronizedQueue
end # module Erlang

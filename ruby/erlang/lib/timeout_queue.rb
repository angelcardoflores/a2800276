require 'thread'
require 'timeout'

module Erlang

class TimeoutQueue < Queue
  def initialize
    super
  end


  def pop timeout=nil
    val = nil
    begin
      Timeout::timeout(timeout) {
        val = super(false)
      }
    rescue Timeout::Error
    end
    val
  end

end

end #module Erlang

require 'erlang_type'
require 'stringio'

module Erlang
  class Message
    LINK = 1
    SEND = 2
    EXIT = 3
    UNLINK = 4
    NODE_LINK = 5
    REG_SEND = 6
    GROUP_LEADER = 7
    EXIT2 = 8
    SEND_TT = 12
    EXIT_TT = 13
    REG_SEND_TT = 16
    MONITOR_P = 19
    DEMONITOR_P = 20
    MONITOR_P_EXIT = 21

    attr_accessor :recipient, :control, :msg, :tag
    
    def initialize tag, control, msg
      @tag=tag
      @control=control
      @msg=msg
    end

    def self.parse str
      io = StringIO.new str
      tag = io.getc
      raise "Protocol Error: unexpected msg (#{tag}) expected 112" unless tag = 112
      cntrl = BaseType.parse io, true 
      tag = cntrl[0].value
      recipient = nil
      puts " 000 #{tag} #{tag.class} #{tag==REG_SEND}, #{REG_SEND}, #{REG_SEND.class}" 
      if [REG_SEND, REG_SEND_TT].include? tag
        recipient = cntrl[3]
      elsif [NODE_LINK].include? tag
        recipient = nil
      else
        recipient = cntrl[2]
      end

      msg = nil
      if [SEND, REG_SEND, SEND_TT, REG_SEND_TT].include? tag
        msg = BaseType.parse io, true
      end
      # todo get message for messages sending those along.
      m = self.new tag, cntrl, msg 
      m.recipient=recipient.value
      m
    end

    def to_s
      "#MSG{#{@tag}, #{@control}, #{@msg} rec: #{@recipient}}"
    end
  end
end # Erlang

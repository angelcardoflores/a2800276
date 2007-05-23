require 'erlang_type'
require 'stringio'

module Erlang
class Protocol
  include Erlang::Util
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

  PASS_THROUGH = 112

  attr_accessor :recipient, :control, :msg, :tag
  
  def initialize control, msg
    @tag=control[0].value
    @control=control
    @msg=msg

    if [REG_SEND, REG_SEND_TT].include? tag
      @recipient = control[3].value
    elsif [NODE_LINK].include? tag
      @recipient = nil
    else
      @recipient = control[2]
    end
  end


  def self.parse str
    io = StringIO.new str
    class << io; include Erlang::Net; end
    tag = io.getc
    raise "Protocol Error: unexpected msg (#{tag}) expected 112" unless tag == 112
    cntrl = BaseType.parse io, true 
    tag = cntrl[0].value
    recipient = nil

    msg = nil
    if [SEND, REG_SEND, SEND_TT, REG_SEND_TT].include? tag
      msg = BaseType.parse io, true
    end

    case tag
    when SEND
      return Send.new(cntrl, msg)
    when REG_SEND
      return RegSend.new(cntrl, msg)
    when LINK
      return Link.new(cntrl)
    when UNLINK
      return Unlink.new(cntrl)
    when EXIT
      return Exit.new cntrl
    when EXIT2
      return Exit2.new cntrl
    else
      return self.new(cntrl, msg)
    end
  end

  def type
    case tag 
    when 1: return "LINK"
    when 2: return "SEND"
    when 3: return "EXIT"
    when 4: return "UNLINK"
    when 5: return "NODE_LINK"
    when 6: return "REG_SEND"
    when 7: return "GROUP_LEADER"
    when 8: return "EXIT2"
    when 12: return "SEND_TT"
    when 13: return "EXIT_TT"
    when 16: return "REG_SEND_TT"
    when 19: return "MONITOR_P"
    when 20: return "DEMONITOR_P"
    when 21: return "MONITOR_P_EXIT"
    else
      raise "Protocol Error: unknown node type (#{tag})"
    end
  end

  def to_s
    "##{type}{#{@control}, #{@msg} rec: #{@recipient}}"
  end

  def to_bytes
    bytes = e_byte PASS_THROUGH
    bytes +=@control.to_bytes
    bytes +=@msg.to_bytes if @msg
  end

end # Protocol

class Send < Protocol
  attr_accessor :cookie, :to_pid
  def intialize control, msg
    super
    @cookie = control[1].value
    @to_pid = control[2].value
  end

  def self.make cookie, to_pid, msg
    cntrl = Erlang.to_erl("{#{SEND}, $, $}", cookie, to_pid)
    self.new(cntrl, msg)
  end
end

class RegSend < Protocol
  attr_accessor :from_pid, :cookie, :to_name
  def initialize cntrl, msg
    super
    @from_pid = @control[1]
    @cookie   = @control[2]
    @to_name  = @control[3].value
  end

  def self.make from_pid, cookie, to_name, msg
    #cntrl = Tuple.new([REG_SEND, from_pid, Atom.new(''), to_name])
    cntrl = Erlang.to_erl("{#{REG_SEND}, $, '', #{to_name}}", from_pid)
    self.new cntrl, msg
  end
end # regsend

class Link < Protocol
  attr_accessor :from_pid, :to_pid
  def initialize cntrl
    super
    @from_pid = cntrl[1]
    @to_pid = cntrl[2]
  end
  def self.make from_pid, to_pid
    cntrl = Tuple.new([LINK, from_pid, to_pid])
    self.new cntrl
  end
end # class Link

class Unlink < Link
  def self.make from_pid, to_pid
    cntrl = Tuple.new [UNLINK, from_pid, to_pid]
    self.new cntrl
  end
end

class Exit < Protocol
  attr_accessor :from_pid, :to_pid, :reason
  def initialize cntrl
    super
    @from_pid = cntrl[1]
    @to_pid = cntrl[2]
    @reason = cntrl[3]
  end
  def self.make from_pid, to_pid, reason
    cntrl = Tuple.new [EXIT, from_pid, to_pid, reason]
    self.new cntrl
  end
end

class Exit2 < Exit
  def self.make from_pid, to_pid, reason
    cntrl = Tuple.new [EXIT2, from_pid, to_pid, reason]
    self.new cntrl
  end
end


end # Erlang

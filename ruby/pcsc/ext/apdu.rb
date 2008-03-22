module ISO7816
        def b2s bytestr
          bytestr.unpack("H*")[0]
        end
module APDU
class APDU
  include ISO7816
  attr_accessor :cla, :ins, :p1, :p2, :data, :le

  def initialize
    @cla = @ins = @p1 = @p2 = "\x00"
  end

  def lc
    unless @lc
      @lc = @data ? [@data.length].pack("C") : nil
    end
    @lc   
  end 

  # normally don't need to set lc, because it's calculated from the
  # datas length, but for testing it may be necessary to set an 
  # correct value ...
  def lc= val
    if val.is_a? Numeric
      @lc = [val].pack("C")
    else
      @lc=val
    end
  end

  def to_s
    line1 = "|CLA|INS| P1| P2|"
    line2 = [@cla, @ins, @p1, @p2].map { |b|
            puts b
      "| "+ ( b.unpack("H*")[0] )
    }.join+"|"
    
    if @data
      if @data.length >=2
        pad0 = " "*((@data.length*2 - 4)/2) 
        pad1 = ""
      else
        pad0 = ""
        pad1 = " "   
      end
      line1 += "| LC|#{pad0}Data#{pad0}| LE|"      
      line2 += "| #{b2s(self.lc)}|#{pad1}#{b2s(@data)}#{pad1}| #{@le?b2s(@le):"   "}|"
    end
    "#{line1}\n#{line2}"
  end
end # class APDU 

class SELECT < APDU
  def initialize
    super
    @cla = "\x00"
    @ins = "\xA4"
    @p1 = 0x04
  end
end

end # module APDU

end # ISO7816

if __FILE__ == $0
        puts 0x04.class
  puts "here"
  a = ISO7816::APDU::APDU.new
  puts a
  a.data = "\xde\xad\xbe\xef"
  puts a
  a = ISO7816::APDU::APDU.new
  a.data = "\xde"
  a.le = "\x01"
  puts a
  a = ISO7816::APDU::SELECT.new
  puts a
end

module Erlang
module Util	
	# converts a numeric to 2 bytes (short) big endian
	def e_two_bytes_big num
		[num].pack("n")
	end

	def d_two_bytes_big str
		str.unpack("n")[0]
	end

  def e_four_bytes_big num
    [num].pack("N")
  end
	def d_four_bytes_big str
		str.unpack("N")[0]
	end
	
	# converts a numeric to a singel byte
	def e_byte num
		[num].pack("C")
	end


end #Util
end #Erlang

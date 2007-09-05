require 'socket'

serv = TCPServer.new(12345)
while sock = serv.accept 
i = 0
sock.each_byte {|b|
  puts "read: #{b}"
  break if (i+=1) == 10000
}
#sock.close
end


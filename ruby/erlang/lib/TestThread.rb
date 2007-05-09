require 'socket'
class Acceptor 

	def initialize
	       	server = TCPServer.open("", 0)	
		puts server.addr
		@t = Thread.new {
			puts "here"
			puts server.accept
			while (s = server.accept)
				puts "here"
				puts s
				puts s.gets
			end
		}
		@t.methods
	end

end
a = Acceptor.new

while true
	sleep 1
end

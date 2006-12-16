require 'webrick'

include WEBrick
class TestServer
	SUCCESS_TEXT_0 = "success_0"
	SUCCESS_TEXT_1 = "success_1"
	SUCCESS_TEXT_2 = "success_2"
	SUCCESS_TEXT_3 = "success_3"
	SUCCESS_TEXT_4 = "success_4"
	SUCCESS_TEXT_5 = "success_5"
	SUCCESS_TEXT_6 = "success_6"
	SUCCESS_TEXT_7 = "success_7"
	SUCCESS_TEXT_8 = "success_8"
	SUCCESS_TEXT_9 = "success_9"

	def initialize
		@server = HTTPServer.new(
			:Port => 12345
		)

		add_tests
	end
	
	def start
		@server.start
	end

	def shutdown
		@server.shutdown
	end
	
	def add_tests
		@server.mount_proc("/test1"){|req, res|
			res.body=SUCCESS_TEXT_0
		}
	end
end

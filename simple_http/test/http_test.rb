require 'simple_http.rb'
require 'http_test_server.rb'

require 'test/unit'
require 'uri'


class SimpleHttpTest < Test::Unit::TestCase
	def setup
		# create webbrick server in a separate thread
		Thread.new {
			TestServer.new.start
		}
		sleep 1 # webrick needs time to start up, I've not been able to 
			# determine when it's ready...
	end

	def teardown
		#@t.shutdown # webrick blocks in a weird way, can't
		#commnicate with it in a different thread. this, of
		#course, is really ugly.
	end

	def test_get
		ret = SimpleHttp.get "http://127.0.0.1:12345/test1"
		assert_equal(ret, TestServer::SUCCESS_TEXT_0, "basic GET test failed.");
		
		ret = SimpleHttp.get "http://127.0.0.1:12345/test1?query=true"
		assert_equal(ret, TestServer::SUCCESS_TEXT_0, "basic GET test failed.");
		
		uri = URI.parse "http://127.0.0.1:12345/test1?query=true"
		ret = SimpleHttp.get uri 
		assert_equal(ret, TestServer::SUCCESS_TEXT_0, "basic GET test failed.");
	end

	



end


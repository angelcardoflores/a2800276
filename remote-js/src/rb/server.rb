

#!/usr/local/bin/ruby
require 'webrick'
require 'event'

include WEBrick

s = HTTPServer.new(
  :Port            => 2000,
  :DocumentRoot    => Dir::pwd
)

$stderr.puts "here"	
s.mount_proc("/dump") { |req,res|

$stderr.puts "here"	
	puts "Class:"+ req.class.to_s
	puts "Query:"+ req.query.to_s
	puts "query_string:"+req.query_string.to_s
	puts "content_length:"+req.content_length.to_s
	puts "body:"+req.body.to_s
	puts "body.length:"+req.body.length.to_s
	puts "request_method:"+req.request_method.to_s
	
	ep = EventPackage.new(req.body, Time.new.to_i)
	puts "done"
}



trap("INT"){ s.shutdown }
s.start



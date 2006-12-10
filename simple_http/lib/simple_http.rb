require 'net/http'
require 'net/https'
require 'uri'
require 'cgi'


# Wrapper around ruby's standard net/http classes. Currently, only GET
# and POST https methods are supported. `SimpleHttp` provides class
# methods `get` and `post` to handle basic functionality. In case more
# complicated requests need to be made or default settings need to be
# overriden, it's possible to instantiate `SimpleHttp` and use instance
# methods `get` and `put`.
#  
#
# Features:
# 	* Handles Redirects automatically
# 	* Proxy used transparently if http_proxy environment variable is
# 	set.
# 	* SSL handled automatically
# 	* fault tolerant uri, e.g. all of these would work:
# 	"www.example.com", "www.example.com/", "http://www.example.com"
#
# Some usage examples:
#	<pre>
#		# plain GET (using class methods)
#		SimpleHttp.get "www.example.com"
#
#		# POST using the instance methods
#		uri = URI.parse "https://www.example.com/index.html"
#		sh = SimpleHttp uri
#		sh.setProxy "my.proxy", "8080"
#		sh.post {"query" => "query_data"}
#
#		# POST using class methods.
#		binaryData = getImage
#		SimpleData.post binaryData, "image/png"
#
#	</pre>
class SimpleHttp
	
	attr_accessor :proxy_host, :proxy_port, :proxy_user, :proxy_pwd, :uri
	
	def initialize uri
		set_proxy ENV['http_proxy'] if ENV['http_proxy']
						
		if uri.class == String

			unless uri =~ /^https?:\/\//
				uri = "http://#{uri}"
			end

			@uri = URI.parse uri

			if !@uri.path || "" == @uri.path.strip
				@uri.path="/"
			end
		end
	end

	#
	#	Set the proxy to use for the http request.
	# 	Not that you don't need to set the proxy in case the
	# 	`http_proxy` environment variable is set. To override 
	#	previous proxy settings and connect directly, call 
	#	`set_proxy nil`
	#
	#	usage:
	#		set_proxy <string>, e.g. set_proxy "http://proxy:8000"
	#	or:
	#		set_proxy <uri>, e.g.
	#		uri = URI.parse("http://proxy:8000")
	#		...set_proxy(uri)
	#	or:
	#		set_proxy <host>, <port>, <user>, <pwd>, e.g.
	#		...set_proxy 'proxy', '8000', 'my_user', 'secret'
	#	or:
	#		set_proxy nil # to override previous proxy
	#		settings and make the request directly.
			
	
	def set_proxy proxy, port=nil, user=nil, pwd=nil
		
		
		if !proxy	
			@proxy_host=@proxy_port=@proxy_user=@proxy_pwd=nil 
			return
		end

		if proxy.class == String 
			if !port && !user && !pwd
				proxy = URI.parse(proxy)
			else 
				@proxy_host= host
				@proxy_port= port
				@proxy_user= user
				@proxy_pwd = pwd
			end
		end
		
		if proxy.class == URI::HTTP 
			@proxy_host= proxy.host
			@proxy_port= proxy.port
			@proxy_user= proxy.user
			@proxy_pwd = proxy.password
		end
	end
	
	# internal
	def do_http request
		response = nil

		http = Net::HTTP.new(@uri.host, @uri.port, @proxy_host,
			@proxy_port, @proxy_user, @proxy_pwd)
		http.use_ssl = @uri.scheme == 'https'
		return http.request(request)
			
	end

	# internal
	def make_query query
		return query unless query && query.class == Hash
		str = ""
		query.collect { |key, value|
			str += CGI::escape(key) + "=" + CGI::escape(value)
		}
		str
	end

	def self.get uri, query=nil
		http = SimpleHttp.new uri
		http.get query	
	end

	def self.port uri, query=nil, content_type='application/x-www-form-urlencoded'
		http = SimpleHttp.new uri
		http.post query, content_type
	end
	
	def get query = nil
		if (query = make_query query)
			@uri.query = @uri.query ? @uri.query+"&"+query : query
		end
		full_path = @uri.path + (@uri.query ? "?#{@uri.query}" : "")
			
		req = Net::HTTP::Get.new(full_path)
		# puts Net::HTTP::Proxy(@proxy_host, @proxy_port, @proxy_user, @proxy_pwd).get(@uri)
		do_http req
	end

	#
	#	Post the query data to the url. 
	#	The body of the request remains empty if query=nil.
	#	In case `query` is a `Hash`, it's assumed that we are
	#	sending a form.
	#	In case `query` is a `String`, it's also assumed that a
	#	form is being sent, UNLESS the `content_type` parameter
	#	is set.
	#
	def post query=nil, content_type='application/x-www-form-urlencoded'
		req = Net::HTTP::Post.new(@uri.path)

		puts req.class
		req.methods.each {|meth|
			puts meth
		}
		req.body= make_query query if query
		req.content_type=content_type if query
		do_http req
	end
	
end

ht = SimpleHttp.new "http://www.google.de/search"
#puts(ht.get("q"=>"bla"))

puts (SimpleHttp.get "http://www.google.com").each_header {|head, val|
	puts "#{head} => #{val}" 
}

['http://www.google.com/', 'www.google.com', 'https://www.google.com'].each {|u|
	SimpleHttp.new u
}
#puts ht.post



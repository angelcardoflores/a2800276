#!/usr/bin/env ruby

require 'SimpleHttp'
require 'digest/sha1'

module Bulkupload


	module Protocol
	
		X_BULK_FUNCTION = 'X-BULK-FUNCTION'
		X_BULK_USER	= 'X-BULK-USER'
		X_BULK_PASSWORD	= 'X-BULK-PASSWORD'
		X_BULK_STATUS	= 'X-BULK-STATUS'
		X_BULK_SESSION	= 'X-BULK-SESSION'
		X_BULK_FILENAME	= 'X-BULK-FILENAME'
		X_BULK_LENGTH	= 'X-BULK-LENGTH'
		X_BULK_CHUNK	= 'X-BULK-CHUNK'
		X_BULK_HASH	= 'X-BULK-HASH'
		X_BULK_HOST	= 'X-BULK-HOST'

		STATUS_OK	= 'OK'
		STATUS_FAILED	= 'FAILED'
		STATUS_SESSION	= 'SESSION_EXPIRED'
		STATUS_STARTED	= 'IN_PROGRESS'
		STATUS_COMPLETE	= 'COMPLETE'

		FUNCTION_LOGIN	= 'LOGIN'
		FUNCTION_INIT	= 'INIT_UPLOAD'
		FUNCTION_QUERY	= 'QUERY'
		FUNCTION_UPLOAD	= 'UPLOAD'
		FUNCTION_CANCEL	= 'CANCEL'
		
		class Login 
			def initialize user, password, host_uri
				@user		= user
				@passwd		= password
				@host_uri	= host_uri
				@host_uri = host_uri
			end

			def session
				@session ||= do_login
				
			end

			def do_login
				http = SimpleHttp.new @host_uri

				http.request_headers[X_BULK_FUNCTION]	= FUNCTION_LOGIN
				http.request_headers[X_BULK_USER]	= @user
				http.request_headers[X_BULK_PASSWORD]	= @passwd
				http.register_response_handler(Net::HTTPSuccess) { |request, response, http|
					if response[X_BULK_STATUS] != STATUS_OK
					
						raise "Logging in to #{@host_uri} with: #{@user},#{@passwd}"
					end

					@session = response[X_BULK_SESSION]
					
					raise "No Session in response from: #{@host_uri}" unless @session
				}
				http.post
			end

			
		end # class Login

		class Init
			
			attr_reader :status
			
			#
			#	session		: session returned from Login
			#	file_name	: name of file to upload
			#	length		: number of bytes in file
			#	num_chunks	: number of chunks file is split up in
			#	hash		: SHA-1 hash of complete file
			#
			
			def initialize session, file_name, length, num_chunks, hash, host_uri
				@session = session
				@file_name = file_name
				@length = length
				@num_chunks = num_chunks
				@hash = hash
				@host_uri = host_uri
			end # initialize
			
			def response
				http = SimpleHttp.new @host_uri
				http.request_headers[X_BULK_FUNCTION]	= FUNCTION_INIT
				http.request_headers[X_BULK_SESSION]	= @session
				http.request_headers[X_BULK_FILENAME]	= @file_name
				http.request_headers[X_BULK_LENGTH]	= @length
				http.request_headers[X_BULK_CHUNK]	= @num_chunks
				http.request_headers[X_BULK_HASH]	= @hash

				http.register_response_handler(Net::HTTPSuccess){ |request, response, http|
					
					@status = response[X_BULK_STATUS]
					
					if STATUS_STARTED == @status
					
						# already have an upload started, check that chunksize, etc. are identical
						# if they are, change status to OK.
						# keeping the status set to STATUS_STARTED signalizes that the client
						# should adjust settings to mirror the settings returned from the server.
						
						if response[X_BULK_FILENAME] != @file_name
							raise "Filename for started upload differs. yours: #{@file_name} expected #{response[X_BULK_FILENAME]}"
						end
						
						if response[X_BULK_LENGTH].to_i != @length
							raise "Length for started upload differs. yours: #{@length} expected #{response[X_BULK_LENGTH]}"
						end	
						
						if response[X_BULK_CHUNK].to_i != @num_chunks
							raise "Num Chunks for started upload differs. yours: #{@num_chunks} expected #{response[X_BULK_CHUNK]}"
						end
						
						if response[X_BULK_HASH] != @hash
							raise "Hash for started upload differs. Yours: #{@hash} expected #{response[X_BULK_HASH]}"
						end

						@status = STATUS_OK

						
					elsif STATUS_OK != @status
						raise "Init upload for session #{session} failed with #{response[X_BULK_STATUS]}"
					end

				}

				http.post
				@status
			end
			
		end # class Init
		
		class Query
			
			attr_reader :status, :chunk, :upload_uri
			def initialize session, hash, host_uri
				@session = session
				@hash = hash
				@host_uri = host_uri
			end

			def response
				http = SimpleHttp.new @host_uri
				http.request_headers[X_BULK_FUNCTION]	= FUNCTION_QUERY
				http.request_headers[X_BULK_SESSION]	= @session
				http.request_headers[X_BULK_HASH]	= @hash
				
				http.register_response_handler(Net::HTTPSuccess){ |request, response, http|
					
					@status = response[X_BULK_STATUS]
					
					if STATUS_COMPLETE != @status && STATUS_OK != @status
						raise "Query for session #{session} failed with #{response[X_BULK_STATUS]}"
					end

					if STATUS_OK == @status
						@chunk = response[X_BULK_CHUNK]
						@upload_uri = response[X_BULK_HOST]
					end
				}
				http.post
				@status
			end
		end #class Query

		class Upload
			def initialize session, hash, chunk_nr, data, host_uri
				@session = session
				@hash = hash
				@chunk_nr = chunk_nr
				@data = data
				@host_uri = host_uri
			end

			def response
				http = SimpleHttp.new @host_uri
				http.request_headers[X_BULK_FUNCTION]	= FUNCTION_UPLOAD
				http.request_headers[X_BULK_SESSION]	= @session
				http.request_headers[X_BULK_HASH]	= @hash
				http.request_headers[X_BULK_CHUNK]	= @chunk_nr
				
				
				http.register_response_handler(Net::HTTPSuccess){ |request, response, http|
					
					@status = response[X_BULK_STATUS]
					if STATUS_OK != @status
						raise "Upload for chunk #{@chunk} session #{session} failed with #{@status}"
					end

					@status
				}
				http.post @data
			end
		end # class Upload

		class Cancel
			def initialize session, hash, chunk_nr, host_uri
				@session = session
				@hash = hash
				@chunk_nr = chunk_nr
				@host_uri = host_uri
			end
			
			def response
				http = SimpleHttp.new @host_uri
				http.request_headers[X_BULK_FUNCTION]	= FUNCTION_CANCEL
				http.request_headers[X_BULK_SESSION]	= @session
				http.request_headers[X_BULK_HASH]	= @hash
				http.request_headers[X_BULK_CHUNK]	= @chunk_nr
				
				
				http.register_response_handler(Net::HTTPSuccess){ |request, response, http|
					
					@status = response[X_BULK_STATUS]
					if STATUS_OK != @status
						raise "Cancel for chunk #{@chunk} session #{session} failed with #{@status}"
					end

					@status
				}
				http.post
			end

			
		end # Cancel
	end # module Protocol

class Bulkfile < File
	
	def initialize name
		self.class.check name
		super name
	end
	
	def self.check name
		if !self.exists?(name) || !self.file?(name) || !self.readable?(name)
			raise "#{name} is not a readable file"
		end
	end

	def size
		@size ||= self.class.size path
	end	

	def chunk_size		
		
		begin
			@chunk_size = 2**16
			begin
				@chunk_size *= 4
				@num_chunks = size / @chunk_size
				@num_chunks = (size % @chunk_size) == 0 ? @num_chunks : @num_chunks+1
			end while @num_chunks >= 2**10
			
		end unless @chunk_size	
		@chunk_size  
		
	end	

	def num_chunks
		chunk_size
		@num_chunks
	end

	# retrieve chunk i
	def chunk i
		raise "no such chunk: #{i} max: #{num_chunks-1}" unless i < num_chunks && i > -2
		
		if i == -1
			return all_chunk_hashes
		end

		
		offset = i*chunk_size
		seek offset
		read(chunk_size)		
	end
	
	#
	# returns the binary hashes of all chunks concatenated behind one another.
	# this is the "master" list of all chunks, sent to the server in response to
	# a request for chunk -1
	#
	def all_chunk_hashes
		
		begin
			@all_chunk_map = ""
			0.upto (num_chunks-1) { |i|
				@all_chunk_map += sha1_chunk(i)
			}

		end unless @all_chunk_map
		@all_chunk_map
	end
	
	def sha1_chunk i
		sha1 = Digest::SHA1.new(chunk(i))
		sha1.digest
	end

	def sha1
		begin
			sha1 = Digest::SHA1.new
			File.open(path, 'r') {|f|
				while (chunk = f.read(8*1024)) 
					sha1.update chunk
				end
			}
			@digest = sha1.hexdigest
		end unless @digest
		@digest
	end
end

class Client
	def initialize host_uri, user, password
		# determine size of file
		# determine chunk size/num chunks
		# calculate hashes
		
		@host_uri=host_uri
		login = Protocol::Login.new user, password, host_uri
		@session = login.session
		
	end

	def add_file file
		@files ||= []
		@files.push Bulkfile.new file
	end

	def do_upload
		@files.each { |file|
		#	def initialize session, file_name, length, num_chunks, hash, host_uri
			init = Protocol::Init.new @session, file.path, file.length, file.num_chunks, file.sha1, @host_uri
			init.response

			query = Protocol::Query.new @session, file.sha1, @host_uri
			while query.response != Protocol.STATUS_COMPLETE
				upload = Protocol::Upload.new @session, file.sha1, query.chunk, file.chunk[query.chunk.to_i], query.upload_uri 
				upload.response
			end
		}
	end
end # class Client

end # module Bulkupload

def log a
	$stdout.puts a
end

if $0 == __FILE__
	puts ARGV.length
	
	
	
	if (ARGV.length != 1 || !File.exists?(ARGV[0]) || !File.file?(ARGV[0]) || !File.readable?(ARGV[0])) 
		puts "usage: ... filename"
		puts "\t #{ARGV[0]} is not a readable file"
		exit 1
	end
	
	bf = Bulkupload::Bulkfile.new ARGV[0]	
	puts "sha1 hash : #{bf.sha1}"
	puts "filesize  : #{bf.size}"
	puts "chunk_size: #{bf.chunk_size}"

	0.upto(bf.num_chunks-1) {|i|
		puts i
		puts bf.sha1_chunk(i)
	}
end

# Chunk size.

# Possible chunnk sizes.
# 2^18 = 256 k
# 2^20 = 1 M
# 2^22 = 4 M
#    ...
#
# Chunk sizes are determined as follows:
# filesize / chunk_size[i] >= 2^10  ? => chunk_size[i]
# i+i

# 2^30 = 1 G

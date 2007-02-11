
class FileDigest
	def self.sha1 filename
		
		sha1 = Digest::SHA1.new()
		File.open(filename) { |file|
			bytes = nil
			while (bytes = f.read(1024))
				sha1.update(bytes)
			end
		}
		
		sha1.digest
	end

	def self.check filename, sha1
		return sha1(filename) == sha1
	end
end


module Bulkupload
module Protocol

	class LoginResponse

		def initialize user, password, client_ip, http_response
			@user		= user
			@password	= password
			@client_ip	= client_ip
			@http_reponse 	= http_response
		end

		def response 
			ses = nil
			begin
				ses = Session.new @user, @password, @client_ip
			rescue
				puts $!
				puts $!.backtrace
				puts "login failed"
				# login failed.
			end

			@http_reponse[X_BULK_STATUS] = ses ? STATUS_OK : STATUS_FAILED
			if ses
				@http_reponse[X_BULK_SESSION] = ses.session_token
			end
			@http_reponse
		end
	end

	class InitResponse
		def initialize header, http_response #session, filename, length, num_chunks, hash
			@session	= header[X_BULK_SESSION]
			@filename	= header[X_BULK_FILENAME]
			@length		= header[X_BULK_LENGTH]
			@num_chunks	= header[X_BULK_CHUNK]
			@chunk_size	= header[X_BULK_CHUNK_LEN]
			@hash		= header[X_BULK_HASH]
			@http_response	= http_response
		end

		def response
			ses = nil
			begin 
				ses = Session.retrieveSession @session
				raise "failed" unless ses.valid
				
				# check in progress
				upload = Upload.retrieveUpload ses.user_id, @hash

			puts "Upload: #{upload==nil}"
				if upload
					@http_response[X_BULK_STATUS]	= STATUS_STARTED
					@http_response[X_BULK_FILENAME]	= upload.filename
					@http_response[X_BULK_LENGTH]	= upload.length
					@http_response[X_BULK_CHUNK]	= upload.num_chunks
					@http_response[X_BULK_CHUNK_LEN]= upload.chunk_size
					@http_response[X_BULK_HASH]	= upload.hash
				else
					upload = Upload.new ses, @filename, @length, @num_chunks, @chunk_size, @hash
					@http_response[X_BULK_STATUS]=STATUS_OK
				end

			rescue
				puts $!
				puts $!.backtrace
				@http_response[X_BULK_STATUS]=STATUS_FAILED
			end
			puts "Status: #{@http_response[X_BULK_STATUS]}"
			@http_response			
		end
	end

	class QueryResponse
		def initialize header, http_response
			@session = header[X_BULK_SESSION]
			@hash	 = header[X_BULK_HASH]
			@http_response = http_response
		end

		def response
			ses = nil
			begin 
				ses = Session.retrieveSession @session
				raise "failed" unless ses.valid
				
				# check in progress
				upload = Upload.retrieveUpload ses.user_id, @hash
				if upload
					ret = {}

					if upload.complete?
						@http_response[X_BULK_STATUS]	= STATUS_COMPLETE
					else
						@http_response[X_BULK_STATUS]	= STATUS_OK
						@http_response[X_BULK_CHUNK]	= upload.next_chunk_number
						
					end
				end
			rescue
				puts $!
				puts $!.backtrace
				@http_response[X_BULK_STATUS]=STATUS_FAILED
			end
	puts "!!!!!!!!! #{@http_response[X_BULK_STATUS]}"	
			@http_response	
		end #response
	end # end query response

	class UploadResponse
		def initialize header, io
			@session	= header[X_BULK_SESSION]
			@hash		= header[X_BULK_HASH]
			@chunk_nr	= header[X_BULK_CHUNK]
		end

		def response

			ses = nil
			begin 
				ses = Session.retrieveSession @session
				raise "failed" unless ses.valid
				
				# check in progress
				upload = Upload.retrieveUpload ses.user, @hash
			
				if upload
					ret = {}

					if upload.complete?
						return {X_BULK_STATUS => STATUS_COMPLETE}
					else
						upload.save @chunk_nr, @io
						
						return {
							X_BULK_STATUS 	=> STATUS_OK
						}
					end
				end
			end
			
			return {
				X_BULK_STATUS => STATUS_FAILED
			}
		end

		
	end # UploadResponse

end # Protocol
end # Bulkupload

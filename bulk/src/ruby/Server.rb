
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

module Persistance

	PASSWD_SALT_SECRET = "secret"
	
	class SessionBase
		attr_accesor :session_id, :session_token, :user_name. :user_id
		
		def initialize user=nil, password=nil, client_ip=nil
			# check user password || exception
			# init session id
			@user_name=user
			@password=class.password password
			@client_ip=client_ip
		end

		def valid
			#check whether Session is valid.
			true
		end

		# remove this session from memory or persistance.
		def purge

		end

		def self.generateSessionToken user, ip
			if user && ip
				sha1 = Digest::SHA1.new(user+PASSWD_SALT_SECRET+ip+Time.now)
				tok=sha1.digest	
			end
			tok

		end

		def self.retrieveSession session
			# look up stored session.
			# throw excepion if no valid session is found.
		end

		def self.password user, passwd
			if passwd && user
				sha1 = Digest::SHA1.new(user+PASSWD_SALT_SECRET+passwd)
				passwd=sha1.digest	
			end
			passwd
		end

	end

	class SqliteSession < SessionBase
		def initialize user=nil, password=nil, client_ip=nil
			super
			if user && password && client_ip
				# Request to create a new Session.
				init
			end
		end

		def touch
			"UPDATE bulk_session SET last_seen = date('now') WHERE session_id=?", @session_id
		end

		def purge
			"DELETE FROM bulk_session WHERE session_id=?", @session_id
		end
		
		# loads an existing session from db or raises an exception.
		def load session_token
			if valid session_token
				@session_token=session_token
				@user_id, @user_name, @session_id = "SELECT user_id, user_name, session_id FROM bulk_session WHERE session_token=?", session_token 
			end
			# else exception
			self
			
		end
		
		# creates a new session.
		def init 
			@user_id, @user_name = "SELECT user_id, user_name FROM BULK_USER WHERE user_id=? AND user_pwd=?", @user_name, @password
			
			@session_token = class.generateSessionToken @user, @client_ip
			
			"INSERT INTO bulk_session (session_token, user_id, since, last_seen, ip_address)
				VALUES (?, ?, date('now'), date('now'), ?)", @session_token, user_id, @client_ip

			@session_id= 'SELECT last_inserted_value'
			
		end

		# retrieves a stored sesssion or raises an exception if none exist.
		def self.retrieveSession session_token
			ses = SqliteSession.new
			ses.load session_token
		end
	end # SqliteSession

	class UploadBase
		
		attr_accesor :filename, :length, :num_chunks, :hash, :status
		def initialize session=nil, filename=nil, length=nil, num_chunks=nil, hash=nil
			@session=session
			@filename=filename
			@length=length
			@num_chunks=num_chunks
			@hash=hash
		end
	
		# next chunk required for upload
		def next_chunk_number
			return -1
		end

		def complete?
			return false
		end

		def self.retrieveUpload user, hash

		end
		
		# raise exception if
		#
		#	- upload for chunk is already in progress.
		#	- upload's sha-hash doesn't match 
		#	- no such chunk is avail for upload
		def save chunk_nr, io
			# retrieve data from io and compare sig 
		end
	end

	class SqliteUpload < UploadBase
		TMP_FILE_DIR = "/tmp"
		attr_accesor :upload_id
		def initialize session=nil, filename=nil, length=nil, num_chunks=nil, hash=nil
			super session, filename, length, num_chunks, hash #?
			if session
				init	
			end
		end

		def touch chunk_nr=nil
			if chunk_nr
				"UPDATE bulk_chunks SET last_seen= date('now') where chunk_nr=? AND upload_id = ?", chunk_nr, @upload_id
			end
			"UPDATE bulk_upload SET last_seen = date('now') WHERE upload_id = ?", @upload_id
			
		end

		def complete?
			"COMPLETE" == @status	
		end

		def next_chunk_number
			count = "SELECT count(*) FROM bulk_chunks WHERE upload_id = ?", @upload_id
			
			if (count == 0)	# first chunk should be the hashes.
				chunk = -1
			else # else pick the next chunk. could of course also be a random chunk...
				chunk = "SELECT chunk_nr 
						FROM bulk_chunks 
						WHERE upload_id = ? 
						AND (status IS NULL OR status='CANCELED'
						ORDER BY chunk_nr LIMIT 1", @upload_id
			end
			
			if @num_chunks <= chunk
				raise "trying to go beyond last chunk!"
			end
			
			if -1 == chunk
				"INSERT INTO bulk_chunks (upload_id, session_id, status, chunk_nr, hash, since, last_seen)
					VALUES (?, ?, 'INPROGRESS', -1, '0', date('now'), date('now'))", @upload_id, @session.session_id
			else	
				"UPDATE bulk_chunks SET status = 'INPROGRESS', last_seen=date('now') WHERE chunk_nr = ? AND upload_id = ?", chunk, @upload_id
			end
			
			return chunk
		end

		def save chunk_nr, io
			if -1 == chunk_nr
				handle_hashes	
			else
			# check chunk_nr is inprogress
			chunk_hash, status = "SELECT hash, status FROM bulk_chunks WHERE chunk_nr=? AND @upload_id=?", chunk_nr, @upload_id
			# write io to tmp
			File.open ("#{TMP_FILE_DIR}/#{@hash}_chunk_nr.bulk", "w") {|f|
				data = nil
				while data=io.read(1024)
					touch chunk_nr
					f.write(data)
				end
			}
			
			#check hash
			

			
			
			end
		end

		def handle_hashes io
			status = "SELECT status FROM bulk_chunks WHERE upload_id=? AND chunk_nr=-1"
			if !status || "INPROGRESS" != status
				raise "upload not initialized or -1 already uploaded: #{status}"
			end

			chunk_hash=nil
			i=0
			
			while (chunk_hash=io.read(40)!=nil) 
				"INSERT INTO bulk_chunks (upload_id, session_id, status, chunk_nr, hash, since, last_seen)
					VALUES (?, ?, null, ?, ?, date('now'), date('now'))", @upload_id, @session.session_id, i, chunk_hash

				i+=1
			end
			
		end

		
		
		def load user_id, hash
						
			@upload_id, session_id, @filename, @length, @num_chunks, @hash, @status =
				"SELECT upload_id, session_token, filename, length, num_chunks, hash, status
					FROM bulk_upload up, bulk_session s
					WHERE up.session_id=s.session_id
					AND   s.user_id=?
					AND   up.hash=?
					AND", user_id, hash
			if !@upload_id
				raise "no such upload: #{user_id}, #{hash}"
			else
				touch
			end
			@session = SqliteSession.retrieveSession session_token
			self
		end
		
		def init 
			"INSERT INTO bulk_upload
				(session_id, since, last_seen, status, filename, num_chunks, length, hash)
				VALUES (?, date('now'), date('now'), 'INPROGRESS', ?, ?, ?,?)", ses.session_id, @filename, @num_chunks, @length, @hash
		end
		
		def self.retrieveUpload user_id, hash
			up = SqliteUpload.new
			up.load user_id, hash
			# TODO catch exception, return nil.
		end
	end
	
end # Persistance

module Protocol

	class LoginResponse

		def initialize user, password, client_ip
			@user		= user
			@password	= password
			@client_ip	= client_ip
		end

		def response
			ses = nil
			begin
				ses = Session.new @user, @password, @client_ip
			rescue
				# login failed.
			end

			response = {}
			response[X_BULK_STATUS] = ses ? STATUS_OK : STATUS_FAILED
			if ses
				response[X_BULK_SESSION] = ses.session_token
			end
			response
		end
	end

	class InitResponse
		def initialize header #session, filename, length, num_chunks, hash
			@session	= header[X_BULK_SESSION]
			@filename	= header[X_BULK_FILENAME]
			@length		= header[X_BULK_LENGTH]
			@num_chunks	= header[X_BULK_CHUNK]
			@hash		= header[X_BULK_HASH]
		end

		def response
			ses = nil
			begin 
				ses = Session.retrieveSession @session
				raise "failed" unless ses.valid
				
				# check in progress
				upload = Upload.retrieveUpload ses.user_id, @hash
			
				if upload
					return {
						X_BULK_STATUS 	=> STATUS_STARTED,
						X_BULK_FILENAME	=> upload.filename,
						X_BULK_LENGTH	=> upload.length,
						X_BULK_CHUNK	=> upload.num_chunks,
						X_BULK_HASH	=> upload.hash
					}
				else
					upload = Upload.new ses, @filename, @length, @num_chunks, @hash
				end

			rescue
				return {
					X_BULK_STATUS => STATUS_FAILED
				}
			end
			
			return {
				X_BULK_STATUS => STATUS_OK
			}
		end
	end

	class QueryResponse
		def initialize header
			@session = header[X_BULK_SESSION]
			@hash	 = header[X_BULK_HASH]
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
						return {
							X_BULK_STATUS 	=> STATUS_OK,
							X_BULK_CHUNK	=> upload.next_chunk_number
						}
					end
				end
			end
			
			return {
				X_BULK_STATUS => STATUS_FAILED
			}
		end
	end

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

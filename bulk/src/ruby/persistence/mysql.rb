require 'DB'
require 'persistence/persistence'

module Bulkupload
module Persistence
module Mysql
	
	def self.set_db instance, user, passwd
		@driver="DBI:Mysql:#{instance}"
		@user=user
		@passwd=passwd
	end

	def self.db
		@db ||= DBrb.new @driver, @user, @passwd	
	end

	def self.destroy_db 
		@db.close if @db
	end

	def self.add_user user, password
		pass = SessionBase.password user, password
		db.sql("INSERT INTO bulk_user (user_name, user_pwd) VALUES (?,?)", user, pass)
	end

	class MysqlSession < SessionBase
		def initialize user=nil, password=nil, client_ip=nil
			super
			if user && password && client_ip
				# Request to create a new Session.
				init
			end
		end

		def db
			Bulkupload::Persistence::Mysql.db	
		end

		def touch
			db.sql "UPDATE bulk_session SET last_seen = date('now') WHERE session_id=?", @session_id
		end

		def purge
			db.sql "DELETE FROM bulk_session WHERE session_id=?", @session_id
		end
		
		# loads an existing session from db or raises an exception.
		def load session_token
			@session_token=session_token
			@user_id,  @session_id = db.sql "SELECT user_id, session_id FROM bulk_session WHERE session_token=?", session_token 
			
			raise "No matching session: #{session_token}" unless @user_id	
			self
			
		end
		
		# creates a new session.
		def init 
			@user_id = db.sql "SELECT user_id FROM bulk_user WHERE user_name=? AND user_pwd=?", @user_name, @password
			if !@user_id || !user_name
				raise "login denied"
			end
			
			@session_token = SessionBase.generateSessionToken @user_name, @client_ip
			db.sql "INSERT INTO bulk_session (session_token, user_id, since, last_seen, ip_address)
				VALUES (?, ?, NOW(), NOW(), ?)", @session_token, user_id, @client_ip

			@session_id= db.sql 'SELECT LAST_INSERT_ID()'
			
		end

		# retrieves a stored sesssion or raises an exception if none exist.
		def self.retrieveSession session_token
			ses = MysqlSession.new
			ses.load session_token
		end
	end # MysqlSession

	class MysqlUpload < UploadBase
		TMP_FILE_DIR = "/tmp"
		attr_accessor :upload_id
		def initialize session=nil, filename=nil, length=nil, num_chunks=nil, chunk_size=nil, hash=nil
			super session, filename, length, num_chunks, chunk_size, hash #?
			if session
				init	
			end
		end
		
		def db
			Bulkupload::Persistence::Mysql.db	
		end

		def touch chunk_nr=nil
			if chunk_nr
				db.sql "UPDATE bulk_chunks SET last_seen= date('now') where chunk_nr=? AND upload_id = ?", chunk_nr, @upload_id
			end
			db.sql "UPDATE bulk_upload SET last_seen = date('now') WHERE upload_id = ?", @upload_id
			
		end

		def complete?
			"COMPLETE" == @status	
		end

		def next_chunk_number
			puts ">> next_chunk_number"
			count = db.sql "SELECT count(*) FROM bulk_chunks WHERE upload_id = ?", @upload_id
		puts "next: #{count}"	
			if (count < 2)	# first chunk should be the hashes.
				chunk = -1
			else # else pick the next chunk. could of course also be a random chunk...
				chunk = db.sql "SELECT chunk_nr 
						FROM bulk_chunks 
						WHERE upload_id = ? 
						AND (status IS NULL OR status='CANCELED')
						ORDER BY chunk_nr LIMIT 1", @upload_id
			end
		puts "nc #{@num_chunks} c #{chunk}"	
			if @num_chunks <= chunk
				raise "trying to go beyond last chunk!"
			end
			
			if -1 == chunk
				db.sql "INSERT INTO bulk_chunks (upload_id, session_id, status, chunk_nr, hash, since, last_seen)
					VALUES (?, ?, 'INPROGRESS', -1, '0', date('now'), date('now'))", @upload_id, @session.session_id
			else	
				db.sql "UPDATE bulk_chunks SET status = 'INPROGRESS', last_seen=date('now') WHERE chunk_nr = ? AND upload_id = ?", chunk, @upload_id
			end
			
			return chunk
		end

		def save chunk_nr, io
			puts ">>save"
			if -1 == chunk_nr
				handle_hashes	
			else
			# check chunk_nr is inprogress
			chunk_hash, status = db.sql "SELECT hash, status FROM bulk_chunks WHERE chunk_nr=? AND @upload_id=?", chunk_nr, @upload_id
			# write io to tmp
			File.open("#{TMP_FILE_DIR}/#{@hash}_chunk_nr.bulk", "w") {|f|
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
			puts ">> handle_hashes"
			status = db.sql "SELECT status FROM bulk_chunks WHERE upload_id=? AND chunk_nr=-1"
			if !status || "INPROGRESS" != status
				raise "upload not initialized or -1 already uploaded: #{status}"
			end

			chunk_hash=nil
			i=0
			
			while (chunk_hash=io.read(40)!=nil) 
				db.sql "INSERT INTO bulk_chunks (upload_id, session_id, status, chunk_nr, hash, since, last_seen)
					VALUES (?, ?, null, ?, ?, date('now'), date('now'))", @upload_id, @session.session_id, i, chunk_hash

				i+=1
			end
			
		end

		
		
		def load user_id, hash
			puts ">>load"			
			@upload_id, session_token, @filename, @length, @num_chunks, @hash, @chunk_size, @status =
				db.sql "SELECT upload_id, session_token, filename, length, num_chunks, hash, chunk_size, status
					FROM bulk_upload up, bulk_session s
					WHERE up.session_id=s.session_id
					AND   s.user_id=?
					AND   up.hash=?", user_id, hash
			if !@upload_id
				raise "no such upload: #{user_id}, #{hash}"
			else
				touch
			end
			@session = MysqlSession.retrieveSession session_token
			self
		end
		
		def init 
			puts ">>init"
			db.sql "INSERT INTO bulk_upload
				(session_id, since, last_seen, status, filename, num_chunks, length, chunk_size, hash)
				VALUES (?, date('now'), date('now'), 'INPROGRESS', ?, ?, ?,?, ?)", @session.session_id, @filename, @num_chunks, @length, @chunk_size, @hash
		end
		
		def self.retrieveUpload user_id, hash
			puts ">>retrieveUpload"
			up=nil
			begin
				up = MysqlUpload.new
				up.load user_id, hash
			rescue
				# TODO better exception structure, differentiate between
				# "no such upload" and errors.
				up = nil
			end
			up
		end
	end






end #Mysql
end #Persistence
end #Bulkupload


if $0 == __FILE__
	user = ARGV[0]
	passwd = ARGV[1]
	puts "adding user: #{user} with password #{passwd}"

	Bulkupload::Persistence::Mysql.set_db 'test', 'test', 'test'
	Bulkupload::Persistence::Mysql.add_user	user, passwd 


end


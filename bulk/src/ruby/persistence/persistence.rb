require 'digest/sha1'
module Bulkupload

module Persistence

	PASSWD_SALT_SECRET = "secret" #TODO externalize
	
	
	
		
	class SessionBase
		attr_accessor :session_id, :session_token, :user_name, :user_id
		
		def initialize user=nil, password=nil, client_ip=nil
			# check user password || exception
			# init session id
			@user_name=user
			@password=SessionBase.password user, password
			@client_ip=client_ip
		end

		def valid
			#check whether Session is valid.
			true
		end

		# remove this session from memory or persistance.
		def purge

		end

		def self.generate_session_token user, ip
			if user && ip
				sha1 = Digest::SHA1.new(user+PASSWD_SALT_SECRET+ip+Time.now.to_i.to_s)
				tok=sha1.to_s	
			end
			tok

		end

		def self.retrieve_session session
			# look up stored session.
			# throw excepion if no valid session is found.
		end

		def self.password user, passwd
			if passwd && user
				sha1 = Digest::SHA1.new(user+PASSWD_SALT_SECRET+passwd)
				passwd=sha1.to_s	
			end
			passwd
		end

	end


	class UploadBase
		
		attr_accessor :filename, :length, :num_chunks, :chunk_size, :hash, :status
		def initialize session=nil, filename=nil, length=nil, num_chunks=nil, chunk_size=nil, hash=nil
			@session=session
			@filename=filename
			@length=length
			@num_chunks=num_chunks
			@chunk_size=chunk_size
			@hash=hash
		end
	
		# next chunk required for upload
		def next_chunk_number
			return -1
		end

		def complete?
			return false
		end

		def self.retrieve_upload user_id, hash

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

	
end # Persistence
end # Bulkupload


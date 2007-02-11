
require 'constants'
require 'Server'
require 'persistence/mysql'

require 'webrick'

include WEBrick
include Bulkupload::Protocol

module Bulkupload

module Protocol
	Session=Bulkupload::Persistence::Mysql::MysqlSession
	Upload=Bulkupload::Persistence::Mysql::MysqlUpload
	Bulkupload::Persistence::Mysql.set_db 'test', 'test', 'test'
end 
module Server
	
	class BulkServer
		def initialize 

			@server = HTTPServer.new(
				:Port => 8000
			)
			trap('INT'){
				@server.shutdown
			}
			add_services 

			@server.start

		end

		def add_services
			@server.mount_proc("/upload") { |req, res|

				function = req[X_BULK_FUNCTION]
				case function
					when FUNCTION_LOGIN
						handle_login req, res
					when FUNCTION_INIT
						handle_init req, res
					when FUNCTION_QUERY
						handle_query req, res
					when FUNCTION_UPLOAD
						handle_upload req, res
					when FUNCTION_CANCEL
						handle_cancel req, res
					else
						handle_error req, res
				end

					
			}

		end # add_services

		def handle_query req, res
			query = QueryResponse.new req, res
			query.response
		end
		
		def handle_init req, res
			init_resp = InitResponse.new req, res
			init_resp.response
		end # handle_init


		def handle_login req, res
			user = req[X_BULK_USER]
			passwd = req[X_BULK_PASSWORD]
			ip = req.peeraddr[3]

			if !user || !passwd || !ip
				handle_error req, res
			end

			lr = LoginResponse.new user, passwd, ip, res
			lr.response 
		end

		def handle_error req, res
			res[X_BULK_STATUS] = STATUS_FAILED
		end



	end # BulkServer
	
end # Server
end # Bulkupload

if $0 == __FILE__
	s = Bulkupload::Server::BulkServer.new
end




class GeometryPacket
	attr_accessor  :time, :user_agent, :url, :session, :ip
	def initialize geom, ip, session=nil
		self.time=		geom["time"]
		self.user_agent=	geom["useragent"]
		self.ip= 		ip

		if !session && time == 0
			initialize_session
		elsif !session
			return
		else
			return unless check_session
		end

		generate_screen geom
	end

	def check_session
		require 'sqlite3'
		retVal = false
		db = SQLite3::Database.new( "sql/remote.sqllite.db" )
		db.execute("select count (*)from SESSIONS where SES=?", session) { |count|
			retVal = count.to_i!=0	
		}
		db.close
		retVal
		
	end




	def generate_session 
		require 'digest/md5'
		self.session= Digest::MD5.new(Time.new.to_s + "SECRET" + ip.to_s)
	end

	def initialize_session
		generate_session
		require 'sqlite3'
		db = SQLite3::Database.new( "sql/remote.sqllite.db" )
		db.execute("insert into SESSIONS (SES, IP, USER_AGENT, START_TIME) values (?,?,?,?)", session, ip, user_agent, Time.new.to_i)
		db.close
		
	end

	IMAGE_BASE_DIR = 'TODO'
	def generate_screen geom
		nth_screen = Dir.glob("#{IMAGE_BASE_DIR}/#{session}*.png").length
		file_name = "#{IMAGE_BASE_DIR}/#{session}_#{nth_screen}.png"
		#
	end


end

g = GeometryPacket.new({"package" => 0, "time" => 0}, "127.0.0.1")
puts g.session

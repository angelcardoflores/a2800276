require 'base64'

# The server side of the event.
# TODO: session handling, which event from which client,
#       user agent.
#

class Event

	MOUSE_MOVE	= 0x01;		   # just a standard move event, could be 0x00 if we run out of room ...
	MOUSE_DOWN	= MOUSE_MOVE << 1  # mouse down event
	MOUSE_UP	= MOUSE_DOWN << 1  # mouse up event
	TIME_RESET	= MOUSE_UP   << 1  # 18 bit time counter overflowed, starting over at zero 
	GEOMETRY	= TIME_RESET << 1  # geometry of the screen has changed, will send new geom per xmlrpc
	BYE		= GEOMETRY   << 1  # signalises end of connection.



	attr_accessor :type, :time, :x, :y, :package, :session
	
	def initialize()
	end
	
	# initialize this Event object from the (currently) six byte wire format:
	# 	6 bits type, 18 bits time, 12 bits x, 12 bits y = 6 bytes
	# and save the packet to the database.
	#
	def initialize_from_bytes six_byte_arr
		# wire protocol is:
		# 6 bits type, 18 bits time, 12 bits x, 12 bits y = 6 bytes
		raise "six_byte_arr not 6 bytes! :"+six_byte_arr unless six_byte_arr && six_byte_arr.length == 6
		@type = six_byte_arr[0] >> 2
		@time = (six_byte_arr[0] & 0x03) << 16
		@time|= six_byte_arr[1] << 8
		@time|= six_byte_arr[2]

		@x = six_byte_arr[3] << 4
		@x|= six_byte_arr[4] >> 4
		@y = (six_byte_arr[4] & 0x0F) << 4
		@y|= six_byte_arr[5]

		save_to_db
		self
	end
		
	# set values in case we're generating the events on the server.
	def set_values (type, time, x, y)
		@type=type
		@time=time
		@x=x
		@y=y
		self
	end

	def to_s
		return "-#{type}:#{time}::#{x}:#{y}-"
	end

	# TEMPORARY
	# stuck on a train, no standard impl.
	def to_json
		"[#{type}, #{time}, #{x}, #{y}]"
	end


	# TEMPORARY
	# save packet to sqlite
	def save_to_db
		require 'sqlite3'
		db = SQLite3::Database.new( "sql/remote.sqllite.db" )
puts "insert into EVENTS (SES,PACKAGE, TYP, TIME, X, Y) values (#{session}, #{package}, #{@type}, #{@time}, #{x}, #{y} )"
  		db.execute( "insert into EVENTS (SES,PACKAGE, TYP, TIME, X, Y) values (#{session}, #{package}, #{@type}, #{@time}, #{x}, #{y} )" )
	       	
		db.close	
		self
	end
end

# Event packet coming from client...
class EventPackage
	attr_accessor :seq, :events
	def initialize bytes, session
		@events = []
		@seq, base64 = bytes.split(":")
		
		bytes = Base64.decode64(base64)

		0.step(bytes.length-6, 6) { |i|
			event = Event.new
			event.package=seq
			event.session=session
			event.initialize_from_bytes(bytes[i,6])
#puts event
			if event.type && Event::BYE
				require 'sqlite3'
				db = SQLite3::Database.new( "sql/remote.sqllite.db" )
				db.execute("update SESSIONS set END_TIME=#{Time.new.to_i}")	
				db.close
			end
			@events.push(event)
		}
	end

	def to_s
		str = "#{@seq}->"	
		@events.each {|event| str += event.to_s}
		str
	end
end

# Playback client requests a new packet. must keep track of which packets the playback client
# session has already received.
class EventPackagePlayback

	NUM_EVENTS_PLAYED_BACK = 20
	
	attr_accessor :events

	# pb_session playback session
	# orig_session session being played back
		
	def initialize pb_session, orig_session

		require 'sqlite3'
		@events= []

		db = SQLite3::Database.new( "sql/remote.sqllite.db" )
		# check that the session we're trying to play back actually exists.
		db.execute("select count(*) from SESSIONS where SES = ?", orig_session) { |result|
			return unless result[0].to_i==1
		}
	
		# see how many packets we've already played back, initialize 
		# playback session if we've just started. 

		num_played = -1		
		db.execute("select NUM_PLAYED from PLAYBACK_SESSIONS where PB_SES = ?", pb_session) { |rows|
			num_played=rows[0]
		}

		# if this is the first playback event in this playback session, init the PLAYBACK_SESSIONS table.
		if num_played == -1
			db.execute("insert into PLAYBACK_SESSIONS (PB_SES, ORIG_SES, NUM_PLAYED) values (?,?,0)", pb_session, orig_session)
			num_played=0
		end
	
		count = 0
		
		sql = "select 
				TYP, TIME, X, Y 
			 from 
				EVENTS
			 where
				SES = ?
			 order by EVENT_ID 
			 limit #{num_played}, #{NUM_EVENTS_PLAYED_BACK}"
		
		db.execute(sql, orig_session) {|row|
			
			count+=1
			#create packets from rows
			@events.push(Event.new.set_values(*row))
		}
		db.execute("update PLAYBACK_SESSIONS set NUM_PLAYED = NUM_PLAYED+#{count} where PB_SES = ?", pb_session)
	
		db.close
		# JSONify packets, return.
	end
end

#1.upto(3) { |i|
#	puts i
#	e = EventPackagePlayback.new 'pb_session_1', '1162761460'
#	puts e
#	e.events.each { |p|
#		puts p
#	}
#}

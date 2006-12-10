 require 'sqlite3'

  db = SQLite3::Database.new( "remote.sqllite.db" )

  db.execute( "select * from events" ) do |row|
    puts row.class
  end

  db.close

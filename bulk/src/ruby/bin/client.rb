require 'Client'


if $0 == __FILE__
	
	
	
	if (ARGV.length != 4 || !File.exists?(ARGV[0]) || !File.file?(ARGV[0]) || !File.readable?(ARGV[0])) 
		puts "usage: ... filename, uri, user, passwd"
		puts "\t #{ARGV[0]} is not a readable file"
		exit 1
	end
	
	uri = ARGV[1]
	user = ARGV[2]
	passwd = ARGV[3]

	bf = Bulkupload::Bulkfile.new ARGV[0]	
	puts "sha1 hash : #{bf.sha1}"
	puts "filesize  : #{bf.size}"
	puts "chunk_size: #{bf.chunk_size}"

	0.upto(bf.num_chunks-1) {|i|
		puts i
		puts bf.sha1_chunk(i)
	}
	
	puts "Initializing Client"
	cl = Bulkupload::Client.new uri, user, passwd
	cl.add_file ARGV[0]
	cl.do_upload

end




def check_valid_file file_or_file_name, extension=:ignore
  raise "#{file_or_file_name} is not a file or a file name" unless (file_or_file_name.is_a?(File) || file_or_file_name.is_a?(String))
  file = nil 
  if file_or_file_name.is_a? String
    raise "#{file_or_file_name} is not readable" unless File.readable? file_or_file_name
    file_name = File.basename file_or_file_name
    file = File.new file_or_file_name
  elsif
    file_name = File.basename( file_or_file_name.path )
    file = file_or_file_name
  end
 
  unless extension == :ignore 
    extension = extension.gsub /^\./, ""
    r = Regexp.new ".*\.#{extension}" 
  
    unless r.match(file_name) 
      file.close
      raise "#{file_or_file_name} is not a valid .#{extension} file"
    end
  end

 file
end

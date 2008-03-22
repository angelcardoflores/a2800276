

# retrieve list of known atrs from
ATR_LIST = "http://ludovic.rousseau.free.fr/softwares/pcsc-tools/smartcard_list.txt"
# and turn them into a `hash` of the form:
# {"3B02......" => "decription", ... } 
# which gets written into: 
OUT_FILE = "atr_generated.rb"
# for use by the atr routines in `iso7816.rb`
require "open-uri"


all_atrs = Hash.new("")
prev_atr = nil
open ATR_LIST do |f|

  f.each_line {|line|
    next if line =~ /^#/ || line.strip == ""
  
    if line =~ /^\t/ 
      raise "No ATR to attache description to!" unless prev_atr
      all_atrs[prev_atr] += line.strip << "\n"
    else
      all_atrs[prev_atr].strip! if prev_atr 
      prev_atr = line.gsub(/\s+/,'')
    end
  }
end

File.open(OUT_FILE, 'w') {|f|
f.puts "# THIS IS A GENERATED FILE! YOU SHOULDN'T EDIT IT MANUALLY. PLEASE CHECK prepare.rb"
f.puts "module ISO7816"
f.puts "  class ATR"
f.puts "    ATR_HASH = {"
for key, value in all_atrs 
  f.puts %Q-      "#{key}" => "#{value.gsub(/\n/,"\\n").gsub(/"/,"\\\"")}",-
end
f.puts "    }#ATR_HASH"
f.puts "  end #class ATR"
f.puts "end #module ISO7816"
}

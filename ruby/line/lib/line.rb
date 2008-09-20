

class Line
  # options are:
  #  :textwidth default 72
  #  :prefix    default ""   
  #  :nl        default "\n"   
  def Line.break line, options={}
    nl = options[:nl] || "\n"
    r  = Regexp.new(nl)
    line.gsub!(r,"")
    _break line, "", options
  end

  def Line._break line, collect, options
    textwidth = options[:textwidth] || 72
    prefix    = options[:prefix]    || ""
    nl = options[:nl] || "\n"
    
    def Line.sp (line, tw)
      r  = Regexp.new("(.{#{tw}})(.*)")
      md = line.match(r)
      md ? [md[1],md[2]] : [line,""]
    end

    l_line, rest = sp(line, textwidth)

    if rest.length > 0
      collect += (l_line+nl)
      return _break(prefix+rest, collect, options)
    else
      return collect += l_line
    end
  end
end

if $0 == __FILE__
  arg = ARGV.join(" ")
  puts "="*72
  puts Line.break(arg)
end


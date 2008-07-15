module Bytes
def Bytes.check_byte b
  raise "not in byte range: #{b}" unless (b.is_a?(Numeric) && b>-1 && b<256)
end

def Bytes.check_bytes arr
  arr.each {|val|
    check_byte val 
  }
end

def Bytes.bytestr2numeric bs
  bs.unpack("C")[0]
end 

def Bytes.numeric2bytestr num
  [num].pack("C")
end

def Bytes.sample from, to, count, seed=0
  srand seed
  f = bytestr2numeric from 
  t = bytestr2numeric to 
  
  check_bytes([t,f])

  f,t = t,f unless f<t

  raise "don't be silly" if t==f

  range = (t-f)+1
  arr = []
  1.upto(count) {
    arr.push f+rand(range)
  }
  arr.map {|val|
    numeric2bytestr val 
  }
end
end #module

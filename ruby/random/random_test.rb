

class RandomTest

end

class PokerTest < RandomTest
  def test spec=:fips_140_2
    raise "no block" unless block_given?
    # according to FIPS 140-1 (http://csrc.nist.gov/publications/fips/fips140-1/fips1401.pdf)
    #
    # Divide the 20,000 bit stream into 5,000 contiguous 4 bit segments. Count 
    # and store the number of occurrences of each of the 16 possible  4 bit 
    # values.  Denote f(i) as the number of each 4 bit value i where  
    # 0 ≤ i ≤ 15.
    
    sample = Hash.new(0)
    
    1.upto(5000) {
      curr = yield
      sample[curr] += 1 
    }

    # Evaluate the following:
    # (16/5000) * sum(i = 0..15){ f(i)^2 } - 5000
    
    sum = sample.values.inject(0) {|sum, fi|
      sum += fi**2
    }

    # The test is passed if 1.03 < X < 57.4. 
    #
    # In FIPS 140-2 (http://csrc.nist.gov/publications/fips/fips140-2/fips1402.pdf)
    # The requirement for Poker test is removed, but in the removed description, the
    # pass criteria are stricter:
    #
    # The test is passed if 2.16 < X < 46.17

    pass_criteria = case spec
                    when :fips_140_1  : (1.03..57.4)
                    when :fips_140_2  : (2.16..46.17)
                    when Range        : spec
                    else
                            raise "not a valid spec '#{spec}'"
                    end

    result = ((16.to_f/5000) * sum) - 5000
    pass   = pass_criteria.include? result
    [pass, result]

  end
end

if $0 == __FILE__
  pt = PokerTest.new

  [:fips_140_1, :fips_140_2, (0..0.1), "boing"].each { |spec|
    pass, result = pt.test(spec) { rand(16) }
    puts pass
    puts result
  }
end

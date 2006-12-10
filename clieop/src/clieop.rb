


class ClieOp03

	attr_accessor :fileheader, :batches, :items
end # CliepOp03

class ClieOpRecord

	def initialize
		self.class.addMethods
	end	

	# CliepRecords are composed of a number of fields that are defined as follows:
	# [name, :alpha|:numeric, length, describtion, content?]
	# Definition contains an array of all the record describtions

	def self.DEFINITION
		[]
	end
	
	# creates accessors for each method in the definition
	# record fields with constant values only receive getters
	def self.addMethods
		return if @initialized
		self.definitions.each {|definition|
			# accessor methods are named like in the field definition,
			# but lowercase and spaces substituded with underscores.
			meth = definition[0].gsub(/ /,'_').downcase
			
			definition.push meth.to_sym
			if definition[4] # constant value
				define_method(meth) {
					definition[4]
				}
			else
				definition.push((meth+"=").to_sym)
				attr_accessor meth.to_sym
			end
		}
		@initialized ||= true

	end

	def self.parse str
		record = self.new
		self.definitions.each {|definition|
			len = definition[2]
			curr = str[0,len]
			puts str
			str = str[len, str.length-len]
			curr = definition[1] == :numeric ? curr.to_i : curr.strip
			if (definition[4]) # constant value
				if (curr != definition[4])
					raise "Error: '#{definition[0]}' expected #{definition[4]}, got '#{curr}'"
				end
				next	
			end
			record.method(definition[6]).call(curr)
		}	
		record
	end

	def to_clieop
		str = ""
		self.class.definitions.each {|definition|
			fmt = "%"
			fmt += definition[1]==:numeric ? '0' : '-'
			fmt += definition[2].to_s
			fmt += definition[1]==:numeric ? 'd' : 's'
		puts fmt
		puts self.duplicate_code.class
			str += sprintf(fmt, self.method(definition[5]).call)
		}
		str
	end
end

class Fileheader < ClieOpRecord
	def initialize
		super
	end
	

	class << self
		def definitions
			@definition ||= [
				["Record Code", :numeric, 4, "", 1],
				["Variant Code", :alpha, 1, "", "A"],
				["File creation date", :numeric, 6, "ddmmyy", nil],
				["File name", :alpha, 8, "", nil],
				["Sender identification", :alpha, 5, "", nil],
				["File identification", :alpha, 4, "", nil],
				["Duplicate code", :numeric, 1, "", nil],
				["Filler", :alpha, 21, "filler", nil]
			] 
			@definition
		end	
	end	
end

class Filetrailer < ClieOpRecord
	def initialize
		super
	end

	class << self
		def definitions
			@definition ||= [
				["Record Code", :numeric, 4, "", 9999],
				["Variant Code", :alpha, 1, "", "A"],
				["Filler", :alpha, 45, "filler", nil]
			] 
			@definition
		end	
	end
end

class Batchheader < ClieOpRecord
	def initialize
		super
	end
	

	class << self
		def definitions
			@definition ||= [
				["Record Code", :numeric, 4, "", 10],
				["Variant Code", :alpha, 1, "", "B"],
				["Transactiongroup", :alpha, 2, "", nil],
				["Account Number Ordering Party", :numeric, 10, "", nil],
				["Batch sequence number", :numeric, 4, "", nil],
				["Delivery currency", :alpha, 3, "", nil],
				["Filler", :alpha, 26, "filler", nil]
			] 
			@definition
		end	
	end	
end

#reserved for future use
class BatchheaderVariantC < ClieOpRecord
	def initialize
		super
	end
	

	class << self
		def definitions
			@definition ||= [
				["Record Code", :numeric, 4, "", 10],
				["Variant Code", :alpha, 1, "", "C"],
				["Transactiongroup", :alpha, 2, "", nil],
				["Account Number Ordering Party", :numeric, 10, "", nil],
				["Batch sequence number", :numeric, 4, "", nil],
				["Delivery currency", :alpha, 3, "", nil],
				["Batch Identification", :alpha, 16, "", nil],
				["Filler", :alpha, 10, "filler", nil]
			] 
			@definition
		end	
	end	
end

class FixedDescriptionRecord < ClieOpRecord
	def initialize
		super
	end

	class << self
		def definitions
			@definition ||= [
				["Record Code", :numeric, 4, "", 20],
				["Variant Code", :alpha, 1, "", "A"],
				["Fixed Description", :alpha, 32, "", nil],
				["Filler", :alpha, 13, "filler", nil]
			] 
			@definition
		end	
	end	
end


class OrderingPartyRecord < ClieOpRecord
	def initialize
		super
	end

	class << self
		def definitions
			@definition ||= [
				["Record Code", :numeric, 4, "", 30],
				["Variant Code", :alpha, 1, "", "B"],
				["Name code", :numeric, 1, "", nil],
				["Desired processing date", :numeric, 6, "ddmmyy", nil],
				["Name ordering party", :alpha, 35, "", nil],
				["Test code", :alpha, 1, "", nil],
				["Filler", :alpha, 2, "filler", nil]
			] 
			@definition
		end	
	end	
end


class BatchTrailer < ClieOpRecord
	def initialize
		super
	end

	class << self
		def definitions
			@definition ||= [
				["Record Code", :numeric, 4, "", 9990],
				["Variant Code", :alpha, 1, "", "A"],
				["Total amount", :numeric, 18, "", nil],
				["Total amount numbers", :numeric, 10, "", nil],
				["Number of items", :numeric, 7, "", nil],
				["Filler", :alpha, 10, "filler", nil]
			] 
			@definition
		end	
	end	
end


class Transaction < ClieOpRecord
	def initialize
		super
	end

	class << self
		def definitions
			@definition ||= [
				["Record Code", :numeric, 4, "", 100],
				["Variant Code", :alpha, 1, "", "A"],
				["Transactiontype", :alpha, 4, "", nil],
				["Amount", :numeric, 12, "", nil],
				["Account number payer", :numeric, 10, "", nil],
				["Account number beneficiary", :numeric, 10, "", nil],
				["Filler", :alpha, 9, "filler", nil]
			] 
			@definition
		end	
	end	
end

class NamePayer < ClieOpRecord
	def initialize
		super
	end

	class << self
		def definitions
			@definition ||= [
				["Record Code", :numeric, 4, "", 110],
				["Variant Code", :alpha, 1, "", "B"],
				["Name payer", :alpha, 35, "", nil],
				["Filler", :alpha, 10, "filler", nil]
			] 
			@definition
		end	
	end	
end

# City payer record is ignored by Interpay. You can remove it from your software. 
class CityPayer < ClieOpRecord
	def initialize
		super
	end

	class << self
		def definitions
			@definition ||= [
				["Record Code", :numeric, 4, "", 113],
				["Variant Code", :alpha, 1, "", "B"],
				["Filler", :alpha, 45, "filler", nil]
			] 
			@definition
		end	
	end	
end


class PaymentReference < ClieOpRecord
	def initialize
		super
	end

	class << self
		def definitions
			@definition ||= [
				["Record Code", :numeric, 4, "", 150],
				["Variant Code", :alpha, 1, "", "A"],
				["Payment reference", :alpha, 16, "", nil],
				["Filler", :alpha, 29, "filler", nil]
			] 
			@definition
		end	
	end	
end


class DescriptionRecord < ClieOpRecord
	def initialize
		super
	end

	class << self
		def definitions
			@definition ||= [
				["Record Code", :numeric, 4, "", 160],
				["Variant Code", :alpha, 1, "", "A"],
				["Description", :alpha, 32, "", nil],
				["Filler", :alpha, 13, "filler", nil]
			] 
			@definition
		end	
	end	
end

class NameBeneficiary < ClieOpRecord
	def initialize
		super
	end

	class << self
		def definitions
			@definition ||= [
				["Record Code", :numeric, 4, "", 170],
				["Variant Code", :alpha, 1, "", "B"],
				["Name beneficiary", :alpha, 35, "", nil],
				["Filler", :alpha, 10, "filler", nil]
			] 
			@definition
		end	
	end	
end

#City beneficiary  record is ignored by Interpay. You can remove it from your software. 
class CityBeneficiary < ClieOpRecord
	def initialize
		super
	end

	class << self
		def definitions
			@definition ||= [
				["Record Code", :numeric, 4, "", 173],
				["Variant Code", :alpha, 1, "", "B"],
				["Filler", :alpha, 45, "filler", nil]
			] 
			@definition
		end	
	end	
end
fh = Fileheader.new
puts Fileheader.instance_variables
fh.file_creation_date = '060612'
fh.file_name = "test"
fh.sender_identification = "abc"
fh.file_identification = "f_id"
fh.duplicate_code = 0
puts fh.to_clieop+"-"

fh = Fileheader.parse "0001A024970test    abc  f_id0                     "
puts fh.to_clieop


$:.unshift(File.dirname(__FILE__)) unless
  $:.include?(File.dirname(__FILE__)) || $:.include?(File.expand_path(File.dirname(__FILE__)))

require 'card'
require 'atr'
require 'apdu'
require 'iso_apdu'
require 'pcsc_helper'


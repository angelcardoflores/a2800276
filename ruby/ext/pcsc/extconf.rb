require 'mkmf'

PROJECT_NAME="PCSC"
PCSC_HEADER="winscard.h"
HEADER_PATHS=[
"/System/Library/Frameworks/PCSC.framework/Versions/A/Headers/"
]

LIB_PATHS=[
"/System/Library/Frameworks/PCSC.framework/Versions/A/"
]

dir_config(PROJECT_NAME)
dir_config(PROJECT_NAME)

find_header(PCSC_HEADER, HEADER_PATHS)
$LDFLAGS += ' -framework PCSC'
create_makefile(PROJECT_NAME)



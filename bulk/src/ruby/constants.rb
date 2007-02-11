
module Bulkupload
	module Protocol
		# HTTP Header fields
		X_BULK_FUNCTION = 'x-bulk-function'
		X_BULK_USER	= 'x-bulk-user'
		X_BULK_PASSWORD	= 'x-bulk-password'
		X_BULK_STATUS	= 'x-bulk-status'
		X_BULK_SESSION	= 'x-bulk-session'
		X_BULK_FILENAME	= 'x-bulk-filename'
		X_BULK_LENGTH	= 'x-bulk-length'
		X_BULK_CHUNK	= 'x-bulk-chunk'
		X_BULK_CHUNK_LEN= 'x-bulk-chunk-length'
		X_BULK_HASH	= 'x-bulk-hash'
		X_BULK_HOST	= 'x-bulk-host'

		# Return values in X_BULK_STATUS header
		STATUS_OK	= 'OK'
		STATUS_FAILED	= 'FAILED'
		STATUS_SESSION	= 'SESSION_EXPIRED'
		STATUS_STARTED	= 'IN_PROGRESS'
		STATUS_COMPLETE	= 'COMPLETE'

		#FUNCTION ids in X_BULK_FUNCTION
		FUNCTION_LOGIN	= 'LOGIN'
		FUNCTION_INIT	= 'INIT_UPLOAD'
		FUNCTION_QUERY	= 'QUERY'
		FUNCTION_UPLOAD	= 'UPLOAD'
		FUNCTION_CANCEL	= 'CANCEL'

	end # Protocol
end # Bulkupload

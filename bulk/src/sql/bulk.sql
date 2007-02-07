

--- 
--- 	User information. Basic stuff.
---
DROP TABLE bulk_user;
CREATE   TABLE bulk_user (
user_id		INTEGER PRIMARY KEY,			-- numeric id
user_name	VARCHAR(256) NOT NULL UNIQUE,		-- self picked
user_pwd	VARCHAR(40)				-- sha1 (md5?) hash of passwd + salt
);


---
---	Session of the user.
---
DROP TABLE bulk_session;
CREATE  TABLE bulk_session (
session_id	INTEGER PRIMARY KEY,
session_token	VARCHAR(40) NOT NULL UNIQUE,
user_id		INTEGER NOT NULL,	
since		DATE,
last_seen	DATE,					-- used to calculate expiry
ip_address	VARCHAR(15),
FOREIGN KEY (user_id) REFERENCES bulk_user(user_id)
);

---
---	Contains information about the entire upload process, 
---	general information about the file being uploaded.
---
DROP TABLE bulk_upload;
CREATE  TABLE bulk_upload (
upload_id	INTEGER PRIMARY KEY,
session_id	INTEGER,
since		DATE,
last_seen	DATE,
status		VARCHAR(16),			-- null, INPROGRESS, CANCELED, COMPLETE
filename	VARCHAR(1024),
num_chunks	INTEGER NOT NULL,
chunk_size	INTEGER NOT NULL,
hash		VARCAHR (40),
FOREIGN KEY (session_id) REFERENCES bulk_session(session_id)
);


---
---	Table to register information about the individual chunks 
---	compromising the upload. Stores information about the 
---	hash values and upload status of each chunk	
---
DROP TABLE bulk_chunks;
CREATE  TABLE bulk_chunks (
chunk_id	INTEGER PRIMARY KEY,
upload_id	INTEGER,
session_id	INTEGER,
status		VARCHAR(16),			-- null, INPROGRESS, CANCELED, COMPLETE
chunk_nr	INTEGER NOT NULL,
hash		VARCHAR(40) NOT NULL,
since		DATE,
last_seen 	DATE,
FOREIGN KEY (upload_id) REFERENCES bulk_upload(upload_id),
FOREIGN KEY (session_id) REFERENCES bulk_session(session_id)
);



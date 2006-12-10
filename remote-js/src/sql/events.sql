drop table EVENTS;
create table EVENTS (
	EVENT_ID integer primary key autoincrement, -- need this to determine order
	SES int,
	PACKAGE int, -- need this to determine order also
	TYP int,
	TIME int,	
	X int,
	Y int	
);
drop table SESSIONS;

-- collect once per session on intialization.
create table SESSIONS (
	SES int,
	IP int, -- ip of client
	USER_AGENT text, 
	START_TIME date,
	END_TIME date
);

drop table PLAYBACK_SESSIONS;
create table PLAYBACK_SESSIONS (
	PB_SES int,
	ORIG_SES int,
	NUM_PLAYED int
);


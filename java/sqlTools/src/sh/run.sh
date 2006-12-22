CLASSPATH=./classes:../function/classes/:../utils/classes/:/home/tim/tools/oracle_jdbc/jdbc816classes12.zip
MAIN=sqlTools.DumpMetaData
DRIVER=oracle.jdbc.driver.OracleDriver
HOST=bambi
DB=tstnet
URL=jdbc:oracle:thin:@${HOST}:1521:${DB}
#URL='jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=${HOST})(PORT=1521)))(CONNECT_DATA=(SID=${DB})(SERVER=DEDICATED)))'
USER=atosccr
PASSWD=zvOltp

java -cp $CLASSPATH \
$MAIN \
-driver $DRIVER \
-url $URL \
-user $USER \
-password $PASSWD

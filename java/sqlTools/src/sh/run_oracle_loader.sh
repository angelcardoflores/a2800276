
CLASSPATH=./classes:../function/classes/:../utils/classes/:../dot/classes/:/home/tim/tools/oracle_jdbc/ojdbc14.jar
MAIN=sqlTools.OracleLoader
DRIVER=oracle.jdbc.driver.OracleDriver
#HOST=eric.devel.pago.de
#HOST=bambi
HOST=wendy.devel.pago.de
DB=devnet
#DB=tstnet
URL=jdbc:oracle:thin:@${HOST}:1521:${DB}
#URL='jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=${HOST})(PORT=1521)))(CONNECT_DATA=(SID=${DB})(SERVER=DEDICATED)))'
USER=bob
#USER=atosccr
PASSWD=rodel
#PASSWD=zvOltp

java -cp $CLASSPATH \
$MAIN \
-driver $DRIVER \
-url $URL \
-user $USER \
-password $PASSWD

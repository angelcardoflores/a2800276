CLASSPATH=./classes:../function/classes/:../utils/classes/:/home/tim/tools/AtosGUImod/jar/weblogic61sp4.jar
for jar in `ls /home/tim/tools/AtosGUI/jar/*.jar`; do
	CLASSPATH=$CLASSPATH:$jar
done
echo $CLASSPATH
MAIN=jndi.WlpGuiHack
#DRIVER=oracle.jdbc.driver.OracleDriver
#HOST=eric.devel.pago.de
#DB=devnet
#URL=jdbc:oracle:thin:@${HOST}:1521:${DB}
#USER=twbrokat
#PASSWD=twdev
DATASOURCE_NAME=CustomerTxDataSource
#DATASOURCE_NAME=BaseDataSource
#DATASOURCE_NAME=logservice.LogServiceDS




java -cp $CLASSPATH \
$MAIN \
-initialFactory weblogic.jndi.WLInitialContextFactory \
-url t3://bambi:16509 \
-userName atosccr

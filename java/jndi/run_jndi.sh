CLASSPATH=./classes:../function/classes/:../utils/classes/:/home/tim/tools/AtosGUImod/jar/weblogic61sp4.jar
MAIN=jndi.DumpContext
#DRIVER=oracle.jdbc.driver.OracleDriver
#HOST=eric.devel.pago.de
#DB=devnet
#URL=jdbc:oracle:thin:@${HOST}:1521:${DB}
#USER=twbrokat
#PASSWD=twdev
DATASOURCE_NAME=CustomerTxDataSource
#DATASOURCE_NAME=BaseDataSource
#DATASOURCE_NAME=logservice.LogServiceDS

#URL=t3://wlpgui.rz-intern.pago.de:16509
URL=t3://bambi:16509

#-url t3://bambi:16509 

java -cp $CLASSPATH \
$MAIN \
-initialFactory weblogic.jndi.WLInitialContextFactory \
-url t3://bambi:16509

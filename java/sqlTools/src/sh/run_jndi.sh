CLASSPATH=./classes:../function/classes/:../utils/classes/:/home/tim/tools/AtosGUImod/jar/weblogic61sp4.jar
MAIN=sqlTools.DumpMetaData
#DRIVER=oracle.jdbc.driver.OracleDriver
#HOST=eric.devel.pago.de
#DB=devnet
#URL=jdbc:oracle:thin:@${HOST}:1521:${DB}
#USER=twbrokat
#PASSWD=twdev
#DATASOURCE_NAME=CustomerTxDataSource
#DATASOURCE_NAME=BaseDataSource
#DATASOURCE_NAME=logservice.LogServiceDS
DATASOURCE_NAME=com_atos_poseidon_base_usermgmt_maint_UserMaintHome_EO



java -cp $CLASSPATH \
-Djava.naming.factory.initial=weblogic.jndi.WLInitialContextFactory \
-Djava.naming.provider.url=t3://bambi:16509 \
$MAIN \
-jndi $DATASOURCE_NAME

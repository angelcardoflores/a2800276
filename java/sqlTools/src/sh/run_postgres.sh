CLASSPATH=./classes:../function/classes/:../utils/classes/:/home/tim/tools/postgres/postgresql-7.4.2/src/interfaces/jdbc/jars/postgresql.jar
#CLASSPATH=./classes:../function/classes/:../utils/classes/:usr/local/pgsql/share/java/postgresql.jar
echo $CLASSPATH

cmd="java -cp $CLASSPATH  
	sqlTools.DumpMetaDataGUI 
	-driver org.postgresql.Driver 
	-url jdbc:postgresql:testdb 
	-user postgres -password postgres"
echo $cmd
$cmd

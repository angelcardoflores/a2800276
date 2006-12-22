CLASSPATH=./classes:../function/classes/:../utils/classes/:/home/tim/tools/mysql/mysql-connector-java-3.0.11-stable/mysql-connector-java-3.0.11-stable-bin.jar
#CLASSPATH=./classes:../function/classes/:../utils/classes/:usr/local/pgsql/share/java/postgresql.jar
echo $CLASSPATH

cmd="java -cp $CLASSPATH  
	sqlTools.DumpMetaData 
	-driver com.mysql.jdbc.Driver
	-url jdbc:mysql://localhost/test
	-user root -password root"
echo $cmd
$cmd

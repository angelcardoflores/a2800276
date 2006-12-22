banner `date`
JIKESPATH=$JIKESPATH:./classes:../function/classes/:../utils/classes/:../dot/classes:../sql/classes
for jar in `ls /home/tim/tools/AtosGUI/jar/*.jar`; do
	JIKESPATH=$JIKESPATH:$jar
done
echo $JIKESPATH

jikes -nowarn -classpath $JIKESPATH -d classes *java 

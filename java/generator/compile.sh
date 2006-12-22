banner `date`
cd ../function
#./compile.sh
cd -

jikes -nowarn -classpath $JIKESPATH:./classes:../function/classes/:../utils/classes/:../dot/classes -d classes *java 
#javac -nowarn -classpath $JIKESPATH:./classes:../function/classes/:../utils/classes/:../dot/classes -d classes *java 




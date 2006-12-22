banner `date`
cd ../function
#./compile.sh
cd -

jikes -nowarn -classpath $JIKESPATH:./classes:../function/classes/:../utils/classes/:../dot/classes:../generator/classes -d classes *java functions/*java collection/*java dot/*java orm/*java orm/generator/*.java sample/*.java
#javac -nowarn -classpath $JIKESPATH:./classes:../function/classes/:../utils/classes/:../dot/classes -d classes *java functions/*java collection/*java dot/*java orm/*java sample/*.java





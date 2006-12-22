CLASSPATH=$JIKESPATH:../gradient/classes:../function/classes:./classes:../utils/classes
jikes -classpath $CLASSPATH  -d classes *.java parse/*.java

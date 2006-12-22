OLD_CP=$CLASSPATH
CLASSPATH=$CLASSPATH:./classes/:../function/classes:../utils/classes/ 
CLASS_NAME=html.parse.HtmlParser 
OPTIONS="-Dhttp.proxyHost=proxy.dmz.pago.de -Dhttp.proxyPort=8000"
java -cp ${CLASSPATH} $OPTIONS $CLASS_NAME $1

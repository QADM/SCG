#!/bin/sh

# If you find that your system encoding isn't UTF-8, but the platform
# web.xml is set for UTF-8, you need to add an additional -D parameter to the java line below:
# -Dfile.encoding="UTF-8"

DIR_REL=`dirname $0`
cd $DIR_REL
DIR=`pwd`
cd -

. "$DIR/set-pentaho-env.sh"

setPentahoEnv "$DIR/../biserver-ce/jre"

CLASSPATH="$DIR_REL:resource/config:"
files=`ls $DIR_REL/jdbc/*.jar $DIR_REL/lib/*.jar`

for i in $files
do
  CLASSPATH="$CLASSPATH:$i"
done

"$_PENTAHO_JAVA" -Xmx512M -XX:PermSize=64M -XX:MaxPermSize=128M  -DCONSOLE_HOME=$DIR_REL -Dlog4j.configuration=resource/config/log4j.xml -cp $CLASSPATH  org.pentaho.pac.server.JettyServer

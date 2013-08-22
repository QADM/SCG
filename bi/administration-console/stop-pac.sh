#!/bin/sh

DIR_REL=`dirname $0`
cd $DIR_REL
DIR=`pwd`
cd -

. "$DIR/set-pentaho-env.sh"

setPentahoEnv "$DIR/../biserver-ce/jre"

#---------------------------------#
# dynamically build the classpath #
#---------------------------------#

THE_CLASSPATH="$DIR_REL:$DIR_REL/resource:$DIR_REL/bin:$DIR_REL/classes:$DIR_REL/lib"
files=`ls $DIR_REL/lib/*.jar`

for i in $files
do
  THE_CLASSPATH="$THE_CLASSPATH:$i"
done

"$_PENTAHO_JAVA" -Djava.io.tmpdir=/tmp/ -cp $THE_CLASSPATH org.pentaho.pac.server.StopJettyServer
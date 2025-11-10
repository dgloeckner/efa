#!/bin/sh
 
# ##########################################
# Check for updated JAR files and install  #
# ##########################################
check_online_update()
{
  for fnew in `find program -name '*.jar.new'`
  do
    echo "[`date +%Y-%m-%d_%H:%M:%S` $PROG] found updated JAR file: $fnew"
    forg=`echo "$fnew" | sed "s/.jar.new/.jar/"`
    echo "[`date +%Y-%m-%d_%H:%M:%S` $PROG] copying $fnew to $forg"
    cp ${fnew:?} ${forg:?}
    rm ${fnew:?}
  done
}

# ##########################################
# Main                                     #
# ##########################################

# change to efa directory
cd `dirname $0`
PROG=$0

# ##########################################
# Get Arguments                            #
# ##########################################
if [ $# -eq 0 ] ; then
  echo "usage: $PROG <mainclass> [arguments]"
  exit 1
fi
CLASSNAME=$1
shift

# ##########################################
# JVM Settings                             #
# ##########################################

# Java Heap
# Include File expected in efa installation directory
if [ -f java.heap ] ; then
  . ./java.heap
fi
if [ "$EFA_JAVA_HEAP" = "" ] ; then
# A higher Java Heaps helps to speed up efa on slower computers
# As garbage collection needs to run at lower frequencies
  EFA_JAVA_HEAP=192m
fi
if [ "$EFA_NEW_SIZE" = "" ] ; then
  EFA_NEW_SIZE=32m
fi

# JVM-Optionen
JVMOPTIONS="-Xmx$EFA_JAVA_HEAP -XX:NewSize=$EFA_NEW_SIZE -XX:MaxNewSize=$EFA_NEW_SIZE"
# Enable remote debugging if requested
if [ "$EFA_JVM_DEBUG" = "1" ] ; then
  JVMOPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005 $JVMOPTIONS"
fi


# ##########################################
# Run Program                              #
# ##########################################

# Java Arguments
EFA_JAVA_ARGUMENTS="$JVMOPTIONS -cp program/efa.jar $CLASSNAME"

# Run Program
if [ $EFA_VERBOSE ] ; then
  echo "[`date +%Y-%m-%d_%H:%M:%S` $PROG] script running ..."
fi
RC=99
while [ $RC -ge 99 ]
do
  check_online_update
  if [ $EFA_VERBOSE ] ; then
    echo "[`date +%Y-%m-%d_%H:%M:%S` $PROG] starting $CLASSNAME ..."
  fi
  java $EFA_JAVA_ARGUMENTS "$@"
  RC=$?
  if [ $EFA_VERBOSE ] ; then
    echo "[`date +%Y-%m-%d_%H:%M:%S` $PROG] efa exit code: $RC"
  fi
done

if [ $EFA_VERBOSE ] ; then
  echo "[`date +%Y-%m-%d_%H:%M:%S` $PROG] script finished."
fi
exit $RC

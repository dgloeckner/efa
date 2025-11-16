#!/bin/sh
 
# ##########################################
# Main                                     #
# ##########################################

# change to efa directory
cd "$(dirname "$0")"
PROG=$0

# ##########################################
# Get Arguments                            #
# ##########################################
if [ $# -eq 0 ] ; then
  echo "usage: $PROG <appid> [arguments]"
  exit 1
fi
APP_ID=$1


# ##########################################
# Run Launcher                             #
# ##########################################

LAUNCHER_JVM_OPTS="-Xmx96m -XX:NewSize=32m -XX:MaxNewSize=32m"

# Run Launcher
if [ $EFA_VERBOSE ] ; then
  echo "[`date +%Y-%m-%d_%H:%M:%S` $PROG] script running ..."
fi
RC=99
while [ $RC -ge 99 ]
do
  if [ $EFA_VERBOSE ] ; then
    echo "[`date +%Y-%m-%d_%H:%M:%S` $PROG] starting $APP_ID ..."
  fi
  java $LAUNCHER_JVM_OPTS -cp "program/*" de.nmichael.efa.EfaGetdownLauncher program "$@"
  RC=$?
  if [ $EFA_VERBOSE ] ; then
    echo "[`date +%Y-%m-%d_%H:%M:%S` $PROG] efa exit code: $RC"
  fi
done

if [ $EFA_VERBOSE ] ; then
  echo "[`date +%Y-%m-%d_%H:%M:%S` $PROG] script finished."
fi
exit $RC

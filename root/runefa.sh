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
  echo "usage: $PROG <mainclass> [arguments]"
  exit 1
fi
CLASSNAME=$1

exec java -Dgetdown.appid=$CLASSNAME -cp "program/*" de.nmichael.efa.EfaGetdownLauncher program "$@"

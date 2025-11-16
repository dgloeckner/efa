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

exec java -Dappid=$APP_ID -cp "program/*" de.nmichael.efa.EfaGetdownLauncher program "$@"

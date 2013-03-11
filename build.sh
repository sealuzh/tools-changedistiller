#!/bin/sh

P1="-i"
LOCALREPO=/tmp/seal.localrepo

if [ $# -ne 0 ] ; then
	if [ $1 = $P1 ] ; then
		echo "Installing to ~/.m2"
		mvn clean install
	else
		echo "unrecognized parameter: $1"
	fi
else
	echo "Installing to $LOCALREPO"
	
	mvn clean install -Dmaven.repo.local=$LOCALREPO
fi
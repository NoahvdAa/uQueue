#!/bin/bash

echo New javadocs should only be generated every release! If this isn\'t a new release, exit now!
echo Sleeping for 5 seconds...

sleep 5

mvn javadoc:javadoc
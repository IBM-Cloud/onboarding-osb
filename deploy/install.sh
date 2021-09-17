#!/bin/bash

echo ""
echo "---------- Building maven application ----------"
echo "This may take a while. don't terminate process..."

mvn -q clean install

echo "Done."
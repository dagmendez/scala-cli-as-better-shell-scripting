#!/bin/bash

set -e

# Create, or if it exists already, clear and recreate the output dir
DESTINATION="./output"

if [ -d "$DESTINATION" ]; then
  rm -rf $DESTINATION
fi

mkdir $DESTINATION

# Read the file
while read -r line
do
  # Request the data
  PLANET=$(curl -s $line) # -s tells curl to work silently

  # Extract the name
  NAME=$(echo $PLANET | jq -r .name) # -r discards the quotes, so you get Yavin, not "Yavin".

  # Output the data to a json file named after the planet in the output directory
  echo $PLANET > "$DESTINATION/$NAME.json"

  # Print the name for some user feedback
  echo $NAME
done < "planets.txt"
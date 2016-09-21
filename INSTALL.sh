#!/usr/bin/env bash

if [ $UID -ne 0 ]; then
    echo $0 must be run as root!
    echo 'Try "sudo !!", right now!'
    exit 1
fi

mvn clean package
cp target/godoggy-1.0-SNAPSHOT.one-jar.jar /usr/local/share/java/
cp src/main/scripts/*.sh /usr/local/bin
mysql -u root - < src/main/scripts/nextepisode.ddl
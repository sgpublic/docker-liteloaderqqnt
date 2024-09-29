#!/bin/bash

set -e
set -u

if [ ! -d "$LITELOADERQQNT_HOME" ]; then
  mkdir -p $(dirname "$LITELOADERQQNT_HOME")
  cp -a /opt/LiteLoaderQQNT "$LITELOADERQQNT_HOME"
  chown 1000:1000 -R "$LITELOADERQQNT_HOME"
fi

if [ ! -d "$LITELOADERQQNT_PROFILE" ]; then
  mkdir -p "$LITELOADERQQNT_PROFILE"
  chown 1000:1000 -R "$LITELOADERQQNT_PROFILE"
fi

QQ_APP_HOME=$QQ_HOME/resources/app
LITELOADER_IS=$QQ_APP_HOME/app_launcher/liteloader.js
rm -f $LITELOADER_IS
echo "require(String.raw\`$LITELOADERQQNT_HOME\`);" > $LITELOADER_IS
sed -i "s|.*\"main\".*|\"main\": \"\./app_launcher/liteloader\.js\",|" $QQ_APP_HOME/package.json

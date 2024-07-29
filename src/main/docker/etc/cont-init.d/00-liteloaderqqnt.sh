#!/usr/bin/env sh

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

if [ -f $QQ_APP_HOME/application/preload.js ]; then
  rm -f $QQ_APP_HOME/application/preload.js
fi

ln -s $LITELOADERQQNT_HOME/src/preload.js $QQ_APP_HOME/application/preload.js
sed -i "/\/\/ add for LiteLoaderQQNT/d" $QQ_APP_HOME/app_launcher/index.js
sed -i "1i\require('$LITELOADERQQNT_HOME'); \/\/ add for LiteLoaderQQNT" $QQ_APP_HOME/app_launcher/index.js

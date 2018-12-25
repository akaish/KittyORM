#!/bin/bash
CONFIG_FILE='config.toml'
ANDROID_ASSET='"file:///android_asset/inapptut/"'
#ANDROID_ASSET='http://example.com'
BASE_URL='baseURL'
EXAMPLE_URL='"http://example.com"'
ANDROID_PROJECT_ASSET_DIR='/media/akaish/C65E2FD25E2FBA57/AndroidStudioProjects/WST-Project/KittyORMDemo/app/src/main/assets/inapptut'
echo "s#^\($BASE_URL\s*=\s*\).*\$#\1$ANDROID_ASSET#"
sed -i "s#^\($BASE_URL\s*=\s*\).*\$#\1$ANDROID_ASSET#" $CONFIG_FILE
#sed -i "s/\($BASE_URL *= *\).*/\1$ANDROID_ASSET/" $CONFIG_FILE
hugo
rm -rf $ANDROID_PROJECT_ASSET_DIR
mkdir $ANDROID_PROJECT_ASSET_DIR
cp -R public/. $ANDROID_PROJECT_ASSET_DIR
sed -i "s#^\($BASE_URL\s*=\s*\).*\$#\1$EXAMPLE_URL#" $CONFIG_FILE


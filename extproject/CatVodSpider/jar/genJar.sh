#!/bin/bash

# 删除 custom_spider.jar
rm -f "$(dirname "$0")/custom_spider.jar"

# 删除 Smali_classes 目录
rm -rf "$(dirname "$0")/Smali_classes"

mkdir -p "$(dirname "$0")/Smali_classes/com/github/catvod/spider"
mkdir -p "$(dirname "$0")/Smali_classes/com/github/catvod/parser"
mkdir -p "$(dirname "$0")/Smali_classes/com/github/catvod/js"


# 反编译 dex 文件
java -jar "$(dirname "$0")/3rd/baksmali-2.5.2.jar" d "$(dirname "$0")/../app/build/intermediates/dex/release/mergeDexRelease/classes.dex" -o "$(dirname "$0")/Smali_classes"

# 删除 spider.jar 目录中的旧文件
rm -rf "$(dirname "$0")/spider.jar/smali/com/github/catvod/spider"
rm -rf "$(dirname "$0")/spider.jar/smali/com/github/catvod/parser"
rm -rf "$(dirname "$0")/spider.jar/smali/com/github/catvod/js"

# 创建必要的目录
mkdir -p "$(dirname "$0")/spider.jar/smali/com/github/catvod/"

# 移动新反编译出来的文件到 spider.jar 目录中
mv "$(dirname "$0")/Smali_classes/com/github/catvod/spider" "$(dirname "$0")/spider.jar/smali/com/github/catvod/"
mv "$(dirname "$0")/Smali_classes/com/github/catvod/parser" "$(dirname "$0")/spider.jar/smali/com/github/catvod/"
mv "$(dirname "$0")/Smali_classes/com/github/catvod/js" "$(dirname "$0")/spider.jar/smali/com/github/catvod/"
mv "$(dirname "$0")/Smali_classes/com/github/catvod/api" "$(dirname "$0")/spider.jar/smali/com/github/catvod/"
mv "$(dirname "$0")/Smali_classes/com/github/catvod/bean" "$(dirname "$0")/spider.jar/smali/com/github/catvod/"
mv "$(dirname "$0")/Smali_classes/com/github/catvod/utils" "$(dirname "$0")/spider.jar/smali/com/github/catvod/"
mv "$(dirname "$0")/Smali_classes/com/github/catvod/net" "$(dirname "$0")/spider.jar/smali/com/github/catvod/"

# 使用 apktool 重编译 spider.jar
java -jar "$(dirname "$0")/3rd/apktool_2.4.1.jar" b "$(dirname "$0")/spider.jar" -c

# 移动生成的 dex.jar 到 custom_spider.jar
mv "$(dirname "$0")/spider.jar/dist/dex.jar" "$(dirname "$0")/custom_spider.jar"

# 计算 custom_spider.jar 的 MD5 哈希并输出到 custom_spider.jar.md5 文件中
md5sum "$(dirname "$0")/custom_spider.jar" | awk '{ print $1 }' > "$(dirname "$0")/custom_spider.jar.md5"

# 删除临时生成的目录和文件
#rm -rf "$(dirname "$0")/spider.jar/build"
#rm -rf "$(dirname "$0")/spider.jar/smali"
#rm -rf "$(dirname "$0")/spider.jar/dist"
#rm -rf "$(dirname "$0")/Smali_classes"

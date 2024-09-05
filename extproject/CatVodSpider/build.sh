#!/bin/bash

# 获取脚本所在目录
script_dir=$(dirname "$0")

# 执行 gradlew assembleRelease
"$script_dir/gradlew" assembleRelease --no-daemon

# 执行 genJar.bat 脚本并传递参数
"$script_dir/jar/genJar.sh" "$1"

# 等待用户输入，防止窗口关闭
read -p "Press any key to continue..."

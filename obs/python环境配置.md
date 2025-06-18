# Python 的版本选择

按照obs官方说明，Python 支持的version有限制，并不是越高越好。https://obsproject.com/kb/scripting-guide 这里说明支持的版本是 3.6~3.10。

## 安装 

使用 brew 安装3.10 的python
>  brew install python@3.10

## obs 配置 python 环境

打开obs -> 工具 -> 脚本 -> Python 设置  -> 浏览（选择安装路径）

目录大概是:
> usr/local/Framworks

关闭 obs ,然后重新打开

显示 “已加载Python版本 3.10”  证明 Python 已经配置成功
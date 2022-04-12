# “zsh: command not found: adb” 的解决方法：

终端分别输入
echo 'export ANDROID_HOME=/Users/$USER/Library/Android/sdk' >> ~/.zshrc
echo 'export ANDROID_HOME=/Users/$USER/Library/Android/sdk' >> ~/.zshrc
然后
source ~/.zshrc

# brew 安装失败

显示的错误如下：
unable to access 'https://github.com/Homebrew/brew/': LibreSSL SSL_read: Operation timed out, errno 60

解决方案：配置 git 的代理

https://juejin.cn/post/6987392117236564005
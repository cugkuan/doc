# 概述

Harmony的官方 地址为：https://developer.harmonyos.com/cn/docs/documentation/doc-references/js-apis-fileio-0000001333640945


使用起来，还是有点啥，功能非常的弱，因为没有流，所以很多操作非常繁琐，甚至功能不支持。


# zlib.decompressFile 无法解压

错误信息是：BusinessError 17700101: Bundle manager service is excepted.


给华为提了一个工单，等华为的回复。


# 关于文件下载


文件下载是每一个APP必备的功能，文件下载说简单也简单，和文件服务器建立连接，然后读取文件流，写入本地即可。

复杂一点的，下载需要支持断点续传，断点续传需要处理 416， 200,206等问题。


## Harmony 自带的 request.downloadFile

这个使用简单，但是因为完全是一个黑盒，内部是否支持断点续传等都不是很清楚。在熟悉其api后，做了一个demo。

但是因为不清楚内部是否支持断点续传，如果支持，应该如何操作等都是一个未知数，于是思考要自己封装一个库。


## Http 库的问题

harmony sdk 中 http 提供的功能很弱，在了解完所有的 api后，开始进行开发。开发到一半，发现流的读取支持非常的弱，无法实时计算已经下载的文件大小，心想着，先不管这些，把流程走完，然后....


http 抛出的异常是：

```js

{
    code:2300023,
    message:‘Failed writing received data to disk/application’
}
```

通过查询资料可知，http 无法下载 文件大小超过 5M 的文件。

这真的令我非常的诧异，还有这种操作？在群里问了下别人，别人这样说：“在 api10 和 api 11之后，就能正常的使用了”













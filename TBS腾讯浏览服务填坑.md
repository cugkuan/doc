
# 概述

https://x5.tencent.com/docs/access.html

使用腾讯的TbsReaderView，浏览 pdf，doc等文件，碰到了下面的一些问题

- 用户第一次安装几乎不能正确的预览文件
- tbs官网上相关的资料很少，也没有清晰的文档。

# 问题分析与解决
第一个问题的原因：
> TBS 的内核非常大，第一次使用的时候会从微信，qq或者qq浏览器中copy内核文件，如果都没有则从服务器下发内核，这这个过程导致耗费时间，当内核没有准备完成时，使用tbs，就使用切换到系统的内核。

所以再内核没有准备好的时候，当然无法使用。

于是，再使用前需要判断内核是否准备完成，如果内核还没有准备完成则需要


## 内核初始化和下载过程的处理

根据官网的说法，tbs内核有初始化过程，再第一次使用的时候，有下载的过程。
```
  map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
            map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
            QbSdk.initTbsSettings(map)
            val cb = object : QbSdk.PreInitCallback {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                override fun onViewInitFinished(arg0: Boolean) {
                  
                }
                // webView 验证完毕
                override fun onCoreInitFinished() {
        
                }
            }
            QbSdk.setTbsListener(object : TbsListener {
                override fun onDownloadFinish(erroerCode: Int) {
        
                }

                override fun onInstallFinish(errorCode: Int) {
                
                }

                override fun onDownloadProgress(p0: Int) {
                
                }
            })
            //x5内核初始化接口
            //X5初始化耗时将近1300ms
            QbSdk.setDownloadWithoutWifi(true)
            QbSdk.initX5Environment(application, cb)
```

于是需要记录 tbs的初始化和下载的整个过程状态，在使用前需要判断其状态过程，如果处于下载中，或者还没有准备好，那么就不能使用内核，需要等待内核下载完成

```
/**
 * tbs 内核初始化过程的监听
 */
interface OnTbsInitListener {
    fun stateChange(code: Int)
}

var isX5WebCoreInit: Boolean = false


/**
 * 浏览器内核初始化完成
 */
const val TSB_CORE_INIT_COMPLETE = 300


const val TBS_CORE_INIT_NOT_COMPLETE = 301


/**
 * 内核安装成功 200,其它的都表示失败
 */
const val TSB_CORE_INSTALL_FINISH = 200

/**
 * 内核安装失败
 */
const val TBS_CORE_INSTALL_FAILURE = 201

/**
 * 内核下载完成的标志
 */
const val TBS_CORE_DOWNLOAD_FINISH = 100

/**
 * 内核下载失败
 */
const val TSB_CORE_DOWNLOAD_FAILURE = 101

/**
 * 请慎重使用，谁使用谁释放，不负责清理,不释放一定造成内存泄露
 */
var tbsInitListener: OnTbsInitListener? = null
    set(value) {
        field = value
        if (isX5WebCoreInit) {
            value?.stateChange(tbsCoreInitStatus)
        } else {
            value?.stateChange(tbsDownloadState)
        }
    }

/**
 *  tbs 内核的初始化情况
 *  0 - 还未初始化
 *  1-  初始化完成

 */
@Volatile
var tbsCoreInitStatus = TBS_CORE_INIT_NOT_COMPLETE
    set(value) {
        field = value
        tbsInitListener?.stateChange(value)
    }

/**
 * tbs 内核的下载进度[0~100]
 */
@Volatile
var tbsDownloadState = -1
    set(value) {
        field = value
        tbsInitListener?.stateChange(value)
    }

```

于是在使用的时候，先判断 tbs的内核有没有准备好

```
   if (WebViewHelperKt.getTbsCoreInitStatus() == TBS_CORE_INIT_NOT_COMPLETE
                || TbsDownloader.isDownloading()
                || TbsDownloader.isDownloadForeground()
                || TbsDownloader.needDownload(getApplicationContext(), TbsDownloader.DOWNLOAD_OVERSEA_TBS)
        ) {
            // 表明内核还没有准备好，应该显示内核准备的界面
        
        }
```

下面是内核还没有准备好的一段伪代码，表示当Tbs 内核还没有准备好时的处理


```

 tbsInitListener = object : OnTbsInitListener {
            override fun stateChange(code: Int) {
                // 注意
                view?.post {
                    when (code) {
                        TBS_CORE_INIT_NOT_COMPLETE -> {
                            tvText.text = "浏览内核正在初始化中..."
                            progress.visibility = View.VISIBLE
                        }
                        TSB_CORE_INIT_COMPLETE -> {
                            if (TbsDownloader.isDownloading()
                                || TbsDownloader.isDownloadForeground()
                                || TbsDownloader.needDownload(
                                    requireContext().applicationContext,
                                    TbsDownloader.DOWNLOAD_OVERSEA_TBS
                                )
                            ) {
                                tvText.text = "浏览内核初始化..."
                                progress.visibility = View.VISIBLE
                            } else {
                                progress.visibility = View.GONE
                                tvText.text = "浏览内核初始化完成"
                                if (tbsCoreNotInstall.not()) {
                                    tbsInitFinish()
                                }
                            }
                        }
                        TSB_CORE_INSTALL_FINISH -> {
                            progress.visibility = View.GONE
                            tvText.text = "浏览内核安装完成"
                            tbsCoreNotInstall = false
                            tbsInitFinish()
                        }
                        TBS_CORE_INSTALL_FAILURE -> {
                            progress.visibility = View.GONE
                            tvText.text = "浏览内核安装失败"
                            tbsCoreNotInstall = false
                            tbsInitFinish()
                        }
                        TBS_CORE_DOWNLOAD_FINISH -> {
                            progress.visibility = View.GONE
                            tvText.text = "浏览内核下载完成"
                            tbsCoreNotInstall = true
                        }
                        TSB_CORE_DOWNLOAD_FAILURE -> {
                            progress.visibility = View.GONE
                            tvText.text = "浏览内核下载失败"
                            tbsCoreNotInstall = true
                        }
                        in 0 until TBS_CORE_DOWNLOAD_FINISH -> {
                            tbsCoreNotInstall = true
                            progress.visibility = View.VISIBLE
                            tvText.text = "浏览内核下载进度$code%"
                        }
                        // 不清楚什么情况
                        else -> {
                            tbsInitFinish()
                        }
                    }
                }
            }
        }
        if (TbsDownloader.needDownload(
                requireActivity().applicationContext,
                TbsDownloader.DOWNLOAD_OVERSEA_TBS
            )
        ) {
            tbsCoreNotInstall = true
            TbsDownloader.startDownload(requireContext().applicationContext)
        }
```


## TbsReaderView 使用的坑

解决内核初始化问题，以为就完了？ too yong too simple 。TbsReadView 除了在快速接入的文档中提到后就再也没有官方资料了，包括demo 和 Api 文档。


TbsReadView  相关回调必须再构造函数中传入，这决定了 这个对象不能通过 xml 去写，只能是代码加入。


```
  tbsReaderView = TbsReaderView(requireActivity()) { code, p1, p2 ->

        }
```

在回调给的 code 中，杂乱无章，例如   NOTIFY_CANDISPLAY = 12 表示能预览，但是这后面又有其他错误码的回到。另外，5045 这个code 没有任何解释， TbsReaderView 上显示一个正在加载的动画,长时间都不能显示出内容

经过多次测试，一种解决的办法代码如下：

```java 

 val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == TBS_SEND_ERROR_MSG) {
                notOpenFileModel()
            }
        }
    }
 val   tbsReaderView = TbsReaderView(requireActivity()) { code, p1, p2 ->
            when (code) {
                TbsReaderView.ReaderCallback.NOTIFY_CANDISPLAY -> {
                    isNotifyCanDisplay = true
                    handler.removeMessages(TBS_SEND_ERROR_MSG)
                }
                TbsReaderView.ReaderCallback.NOTIFY_ERRORCODE , 5045 -> {
                    if (isNotifyCanDisplay.not()) {
                        // 我也不知道为什么这样,反正这样做能缓解问题，tbs 不透明，也没有文档，只能这样测试出来
                        handler.removeMessages(TBS_SEND_ERROR_MSG)
                        handler.sendEmptyMessageDelayed(TBS_SEND_ERROR_MSG, 3 * 1000)
                    }
                }
            }
        }
```

# 总结


使用腾讯的tbs 服务，总的来说，确实可以加快渲染速度，但是有着自身无法克服的困难，特别是文档不完整和其他一些奇怪的问题，诸如，再内核切换过程中，可能导致 cookie 丢失等情况。

上面的是针对项目中的问题，给出一种解决方案。

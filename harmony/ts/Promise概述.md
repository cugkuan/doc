
# 概述


Promise 是什么？**Promise 对象表示异步操作最终的完成（或失败）以及其结果值。**

请注意，异步的代码是在 new Promise的回调之前的，then 和 catch 的代码，是同步代码，下面的例子，说明了异步代码，同步代码分别运行在哪里

```js

       let resolve = function (result:number){
          // 这里是同步代码
          console.log('lmk','输出结果：'+result)
        }
        let reject = function(error:number){
          // 这里是同步代码
        }
        new  Promise<number>(function (reslove,error) {
          // 这是是异步代码
          reslove(1)
        }).then(resolve,reject)

```

第二个例子
```js

         new  Promise<number>(function (reslove, error) {
            // 这是是异步代码
            reslove(1)
          }).then(() => {
            //同步代码
            return 0
          }).then(() => {
            return new Promise(function (reslove, error) {
              // 异步代码
              reslove(3)
            })
          })
```


如果没有Promise，那么我们以回调的方式去处理；下面的一个例子解释 Promise的方便性


下面的代码要完成的功能如下：
- 下载一个文件
- 下载完成后，更改文件的名字。
- 对文件进行MD5 校验
- 解压

因为每一步都是异步的，于是回调地狱的写法如下：

```js
    let downloadUrl = 'https://cm-test.qizhidao.com/lp/qzd-h5-content-lp.799658.test.zip'
    let fileMd5 = 'dbefa27dcad3a1b035dc4763ec093e37'
    let downloadFile = context.getApplicationContext().filesDir + '/testa.temp'
    let resultFile = context.getApplicationContext().filesDir + '/testa.zip'
    let unzipFile = context.getApplicationContext().filesDir + '/unzip'

    request.downloadFile(context, {
      url: downloadUrl,
      filePath: downloadFile
    }, (error, task: request.DownloadTask) => {
      if (error) {
        console.log('lmk', '任务创建失败' + error)
        return
      }
      this.downloadTask = task
      task.on('progress', (downloadSize, totalSize) => {
        console.log('lmk', '下载中：' + downloadSize + ' -' + totalSize)
      })
      task.on('fail', (error) => {
        console.log('lmk', '下载错误：' + error)
        task.off('fail')
      })
      task.on('complete', () => {
        task.off('complete')
        task.off('progress')
        fs.rename(downloadFile, resultFile, () => {
          hash.hash(resultFile, 'md5', (error, md5) => {
            if (error) {
              console.log('lmk', 'md5检验失败' + error)
              return
            }
            if (fileMd5.toLowerCase() == md5.toLowerCase()) {
              let options = {
                level: zlib.CompressLevel.COMPRESS_LEVEL_DEFAULT_COMPRESSION,
                memLevel: zlib.MemLevel.MEM_LEVEL_DEFAULT,
                strategy: zlib.CompressStrategy.COMPRESS_STRATEGY_DEFAULT_STRATEGY
              };
              try {
                fs.mkdirSync(unzipFile)
              } catch (error) {
              }
              zlib.decompressFile(resultFile, unzipFile, options, (error, data) => {  
                if (error) {
                  console.log('lmk', error)
                  return
                }
                console.log('lmk', '解压成功')
              })
            }
          })
        })
      })
    })
```

这个也太难看了，如果用Promiss呢

```js
  private downloadProcess(task: request.DownloadTask): Promise<void> {
    return new Promise(function (resolve, reject) {
      task.on('progress', (downloadSize, totalSize) => {
        console.log('lmk', '下载中：' + downloadSize + ' -' + totalSize)
      })
      task.on('fail', (error) => {
        console.log('lmk', '下载错误：' + error)
        task.off('fail')
        reject(new Error('下载失败'))
      })
      task.on('complete', () => {
        task.off('complete')
        task.off('progress')
        resolve()
      })
    })
  }

  test(context: Context) {
    let downloadUrl = 'https://cm-test.qizhidao.com/lp/qzd-h5-content-lp.799658.test.zip'
    let fileMd5 = 'dbefa27dcad3a1b035dc4763ec093e37'
    let downloadFile = context.getApplicationContext().filesDir + '/testa.temp'
    let resultFile = context.getApplicationContext().filesDir + '/testa.zip'
    let unzipFile = context.getApplicationContext().filesDir + '/unzip'
    request.downloadFile(context, {
      url: downloadUrl,
      filePath: downloadFile,
      header: { 'User-Agent': HttpConfig.USER_AGENT }
    })
      .then((downloadTask) => {
        return this.downloadProcess(downloadTask)
      })
      .then(() => fs.rename(downloadFile, resultFile))
      .then(() => hash.hash(resultFile, 'md5'))
      .then((md5) => {
        if (md5.toLowerCase() == fileMd5) {
          return true
        } else {
          throw new Error('md5校验失败了')
        }
      })
      .then(() => {
        let options = {
          level: zlib.CompressLevel.COMPRESS_LEVEL_DEFAULT_COMPRESSION,
          memLevel: zlib.MemLevel.MEM_LEVEL_DEFAULT,
          strategy: zlib.CompressStrategy.COMPRESS_STRATEGY_DEFAULT_STRATEGY
        };
        try {
          fs.mkdirSync(unzipFile)
        } catch (error) {
        }
        return zlib.decompressFile(resultFile, unzipFile, options)
      })
      .catch((error) => {
        console.log('lmk', '任务失败：' + error)
      })
  }

```

这就好看了。一个完整的链式调用，是标准的响应式编程。逻辑也变得非常的清晰。


从流的观点看，Promise 是一种热流


# Promise  使用

我们简单的创建一个 Promise 可以是：

```js
  new  Promise<number>(function (reslove,error) {
        if(succeed){
            reslove(‘成功’)
        }else{
            error(‘失败’)
        }
        })
```

不用多说了，非常的简单。

对于 then ,最终也是返回一个Promise，下面的二种写法是等价的，即使 return 的不是 Promise 内部也是封装成一个Promise。

```js
.then(() => '下一步')
.then(() =>{

   return new Promise<string>(function (resolve) {
      resolve('下一步')

   })
})
```

等等，对于第一段代码中 error ，只能是 catch 捕获吗？，不是的。看下面的代码

```js
then((data)=>{
          return '下一个成功处理逻辑'
        },(error)=>{
          return '下一个失败逻辑'
        })
```

也就是说，可以在 then中分别处理对应的成功失败逻辑，如果没有处理失败逻辑，那么 最终的 catch 去处理。

这个时候，我们再来看下面这张图就很清晰明了

![image](assets/promises.png)

# Promise 的问题

如果使用过 Rx 和 koltin flow ,那么他对流的操作都非常的简单。但是 对于 Promise，他的情况比较特殊，只能发送一次。这个特点导致了，很多的操作没法进行。如流的辗平等。
例如，我没法将 [1,2,3,4]分拆一个个的发出去。


Promise 的设计之初，是为异步操作准备的，和 Rx,flow 这种响应式的编程规范出发点不同。




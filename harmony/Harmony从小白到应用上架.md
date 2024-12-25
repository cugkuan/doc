# 概述

四月底确定要开发Harmony next 版本，5月6日提交了第一行代码，8.22发布第一个测试版本，9.25成功上架。整个开发过程历时5个月。而且功能几乎是全量上。回顾整个过程，感受很多。


# 准备篇

## 开发语言

  Harmony Next 的开发语言是 arkts（仓颉还没发布）,arkts 是 Ts 的超集，因此学会 Ts 即可。因为有Java，KT的基础，学习Ts 非常的容易。

  - 基本的Ts 语法

 - 异步，并发任务
   
   Promise  asyn wait   TaskPool,Worker



**特别注意**

arkTs 是动态和静态混用的，这导致数不清的坑，看下面最典型的例子：

```ts

class Car{

    run(){

    }
}

class People{
   
}

// code 
let car = new Car()
car.run()  // 这样做OK

let people = new People()
let car = people as Car // 不会报错,不会崩溃
car.run()// 直接崩溃

```

如果是 kt，或者Java 你觉得不可思议，在动态语言中 as 这是数据形状的改变，跟静态语言的as 不是一个东西。这就要命了，无数的坑都来自这里，出现 bug,首先怀疑是不是这里出了问题。其实约定大于规范让维护成了最大的问题。


## 架构问题

项目到一定的规模，必须考虑架构设计。宏观上，模块话，动态化；比如Android 的组件化和动态分发。如果是组件化则要考虑组件化框架，是自研还是用成熟的？动态化也同样考虑这样的问题。对于Harmony，问题就简单了很多，华为已经帮助你处理了这些问题，多hap类似于Android的组件化。


- 路由框架，从刚开始的 router 到  透明化的 Navigation 框架
- 模块的通信能力，过于孱弱，自研了一套，用于组件通信


总的来说，华为已经贴心的准备好了组件化，插件化。但组件通信能力有点弱。这是最大的败笔。


回到具体的一个页面，arkui 就是 响应式，典型的mvvm ；所以arkui 开发起来就简单了很多。明白一点，ViewModel 就是界面数据的描述。换个说法，viewModel 就是界面的数据描述。在Android 上，mvvm 有个致命的缺陷，数据的生命周期超过界面的生命周期，如果希望数据用完即扔，那么mvvm不适合了；在横竖屏切换的时候，更为明显，当然在一些情况下，这个是一个优势，但更多的对开发者造成困扰，mvvm 主张的是数据驱动，mvi，i Intent  翻译成意图，其实就是事件驱动模型。说到这里，arkui 是一个数据驱动框架。


# 开发中


四月底开始看相关的文档。看了二周文档，下载几个demo后，5.8日写下第一行代码。

分三个阶段


## 总体架构

因为有Android的成熟经验，Harmony APP 直接照抄，对Android 的不足之处得以修复。下面是一张非常简陋的图：


![App架构图](./assets/harmony_qzd_app%20copy.png)



其中 CS（自研） 是一个组件通信的框架，弥补自带通信能力孱弱的问题。




## 关于Arkui


这里不去讨论UI范式，状态管理。下面是一个简单的UI界面

```ts
@Component
export struct TestView {
build(){
   Text('测试')
}

}
```

上面代码，本质上是一段描述界面的脚本，不是 Ts ；因为不是一个具体的类型，没有办法跨组件传输共享。

那怎么办？总不能到处复制吧！arkui 应该意识到和这个问题，留了一个口子，就是wrapBuilder，把 UI描述的脚本可以包装成一个可复用，传输的对象。

具体的步骤如下：

1. 通过 @Builder 先构建复用UI的函数
```ts

@Builder
function TestBuilder(data:string){
  TestView()
}
```
2. wrapBuilder 封装 @Builder 函数

```ts 
const testBuilder:WrapBuilder<string> = wrapBuilder(TestBuilder)
```

3. 现在可以使用 testBuilder 。

例如：信息流里面有不同的数据类型对应着不同的UI表现。我们抽象出 一个 Feed类， Feed类封装了数据和UI构建规则。

```ts
class Feed<T>{
  viewBuilder:WrappedBuilder<T>
  data:T|null
}
```

于是，我们可以这样构建完整的界面


``` ts
Component
struct Page{
 build(){

  List(){
     LazyForEach(this.viewModel.viewDataSource, (item: ViewData<Object>, index) => {
        ListItem() {
           Column() {
      item.viewBuilder.builder(item.data)
        }
        }
      })
  }
 }
}
```
这样，界面的构建规则进行了进一步抽象。对复杂业务有利于更高层次的抽象。





**arkUI 是一个描述界面的脚本，本身不属于语言本身，无法继承，为了UI的复用，搞出了一堆东西，比如为了属性的复用，弄出@Style，为了能够继承（阉割版本）弄出了@Extend**，唉，我认为这种是头痛医头，脚痛医脚的，不解决根本问题。


## 关于Http

如果直接用socket 编程，代价太大，除非有 okHttp 那样成熟的网络框架，Harmony提供的HTTP 框架过于逆天，单次支持的数据传输最大为5M，最多100个请求，最开始还不支持流传输。

在HTTP 上面在进行一层业务封装，添加拦截器，日志打印，数据加密解密等。其中加解密可能是最难的，没有华为的帮助，很难去弄。


另外就是下载问题，一声叹息


## 混乱的单位


vp 是 ui 默认的尺寸，但是 获取屏幕的宽高单位又是 px ,图片读取的尺寸也是px。在实际开发中，为了还原UI图，使用了一个lpx 单位；这导致了在某些情况下需要不停的转换单位。

> 有一个功能模块，类似于淘宝的以图搜图，选中一张图片，压缩到指定的大小，然后上传到服务器，服务器分析图片，并返回图片标记中的区域，客户端根据图片区域进行裁剪，标记，然后将裁剪的图片上传到服务器根据图片搜索出结果。

上面这个功能，涉及到下面的技术：
- 图片压缩
- 图片拆解
- 图片标记，也就是在图片显示的对应区域绘制标记点

不讨论具体的技术，看下这个过程单位要转变多少次。

- 图片拆解，单位是 px,
- 图片绘制指示区域，px 转 vp ,vp 转lpx


这些单位的转换，需要详细的注释，不然过一段时间不知所云。


## 关于流

arkTs  的流跟没有差不多，不过也理解，arkts 看成是脚本语言，并不直接跟底层的操作系统打交道。简化了流的操作，但是也让人非常的费解。

比如下面是一个文件转换为ArrayBuffer

```ts
 private readLocalFileWithStream(file: string): ArrayBuffer | null {
    let inputStream: fs.Stream | null = null
    try {
      // 存储每次读取的结果
      let buffers: buffer.Buffer[] = [];
      // 打开文件流
      inputStream = fs.createStreamSync(file, 'r');
      // 以流的形式读取源文件内容
      let bufSize = 4096;
      let readSize = 0;
      let buf = new ArrayBuffer(bufSize);
      let option: ReadOptions = {
        offset: readSize,
        length: bufSize
      }
      option.offset = readSize;
      let readLen = inputStream.readSync(buf, option);
      // 存储当前读取结果
      buffers.push(buffer.from(buf.slice(0, readLen)))
      readSize += readLen;
      while (readLen > 0) {
        option.offset = readSize;
        readLen = inputStream.readSync(buf, option);
        // 存储当前读取结果
        buffers.push(buffer.from(buf.slice(0, readLen)))
        readSize += readLen;
      }
      let finalBuf: ArrayBuffer = buffer.concat(buffers).buffer
      return finalBuf
    } catch (error) {
      log.info(TAG, JSON.stringify(error))
      return null
    } finally {
      if (inputStream) {
        try {
          inputStream.closeSync()
        } catch (e) {
          log.info(TAG, JSON.stringify(e))
        }
      }
    }
  }
  ```

在我们看来，这是把整个文件放入内存，内存是要炸的，这属于脑残的设计，但是，这个是web组件 WebResourceResponse 接受的 ResponseData 。



# 完成上架


二周的学习时间，一个月的底层框架搭建；然后同学参与业务开发，三个月完成全量功能开发。








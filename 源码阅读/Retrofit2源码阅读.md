
# 概述

Retrofit 的设计总体来说比较简单。

- 动态代理，解析interface的 方法的相关参数，生成对应的 HttpServiceMethod，当然 HttpServiceMethod 有一个缓存，提高效率。生成的过程中，确定了 CallAdapter，Converter等信息。
  
- 执行请求任务；通过 CallAdapter，将请求结果转变为接受的结果，这个是适配器设计模式；真正进行网络请求任务的是 Call。请注意，retrofit的Call 看做是一个代理，它的实现类是OkHttpCall;网络请求任务是 okhttp3.Call，就是我们设置的OkHttp.
  


**Retrofit的核心类是 ServiceMethod**

``` mermaid
classDiagram

ServiceMethod <|-- HttpServiceMethod 
HttpServiceMethod <|-- CallAdapted
HttpServiceMethod <|-- SuspendForBody
HttpServiceMethod <|-- SuspendForResponse

```

SuspendForResponse 和 SuspendForBody 属于协程的内容，可以不用看。重点看


# HttpServiceMethod 

HttpServiceMethod 文件中代码比较多，删除无关紧要的，核心的代码如下：

``` java 
 @Override
  final @Nullable ReturnT invoke(Object[] args) {
    Call<ResponseT> call = new OkHttpCall<>(requestFactory, args, callFactory, responseConverter);
    return adapt(call, args);
  }

  protected abstract @Nullable ReturnT adapt(Call<ResponseT> call, Object[] args);

````
发现还是一个抽象类。

我们看CallAdapted  ；HttpServiceMethod具体子类，代码很短

```java
  static final class CallAdapted<ResponseT, ReturnT> extends HttpServiceMethod<ResponseT, ReturnT> {
    private final CallAdapter<ResponseT, ReturnT> callAdapter;

    CallAdapted(
        RequestFactory requestFactory,
        okhttp3.Call.Factory callFactory,
        Converter<ResponseBody, ResponseT> responseConverter,
        CallAdapter<ResponseT, ReturnT> callAdapter) {
      super(requestFactory, callFactory, responseConverter);
      this.callAdapter = callAdapter;
    }

    @Override
    protected ReturnT adapt(Call<ResponseT> call, Object[] args) {
      return callAdapter.adapt(call);
    }
  }
```

而  CallAdapter 就是真正的业务逻辑执行地方。接下来说说 CallAdapter


下面是一个我们自定义的CallAdapter，用于适配 Flow

```java
class FlowCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<T, Flow<T>> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): Flow<T> {
        val cloneCall = call.clone()
        return flow {
            emit(suspendCancellableCoroutine { continuation ->
                continuation.invokeOnCancellation { cloneCall.cancel() }
                if (continuation.isCancelled) return@suspendCancellableCoroutine
                try {
                    val response = cloneCall.execute()
                    if (continuation.isCancelled) return@suspendCancellableCoroutine
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    } else {
                        throw HttpException(response)
                    }
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            })
        }
    }
}
```

核心的代码就是
```
  val response = cloneCall.execute()
```
这行代码才是真正的网络请求，然后将请求的结果包装成Flow。



对于对注解和参数的解析这些不是重点，就不说了。这些不是重点，只有无穷无尽的细节。具体的可以看下RequestFactory中根据注解生成的过程。



# Retrofit 工作过程。

下面是一个请求的规则配置

```
public interface GitHubService {
  @GET("users/{user}/repos")
  Flow<String>> listRepos(@Path("user") String user);
}
```


Retrofit.create(GitHubService.class).listRepos("name")

上面这行代码会动态生成 CallAdapter.


CallAdapter.invoke 最终 是 FlowCallAdapter.adapt 方法被调用。


# 关于 Call

这个网络真正的请求。它的具体实现类OkHttpCall 是具体的网络请求过程。


# 总结

Retrofit 框架作为一个入门级的阅读，非常合适的。整个非常的简单。涉及到的设计模式

-  动态代理
-  适配器模式
-  工厂方法



  
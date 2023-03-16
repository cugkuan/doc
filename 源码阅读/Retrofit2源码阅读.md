
# 概述

Retrofit 的设计总体来说比较简单。

- 动态代理，解析interface的 方法的相关参数，生成对应的 HttpServiceMethod，当然 HttpServiceMethod 有一个缓存，提高效率。生成的过程中，确定了 CallAdapter，Converter等信息。
  
- 执行请求任务；通过 CallAdapter，将请求结果转变为接受的结果，这个是适配器设计模式；真正进行网络请求任务的是 Call。请注意，retrofit的Call 看做是一个代理，它的实现类是OkHttpCall;网络请求任务是 okhttp3.Call，就是我们设置的OkHttp.
  


在看Retrofit 代码的时候，关注核心类，不要纠结代码



# HttpServiceMethod 的生成

```
public interface GitHubService {
  @GET("users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);
}
```



这里的代码逻辑很简单，使用的是动态代理；生成 HtptServiceMethod

- HttpService.invoke 方法 其 CallAdapter 开始工作。
-  CallAdapter.adapt(Call) 进行真正的网络请求。


关于根据 interface 的 method 生成对应的 HttpServiceMethod 的代码，这里就不贴出了。





# CallAdapter

这里就是典型的适配器设计模式，目的 是将 Call 得到的结果，经过适配，返回给 调用者期待的结果，这个期待的结果就是  interface 的 Method 的返回类型。请注意，CallAdapter 中没有任何的业务逻辑，只是起一个适配作用，真正的网络请求构建，结果转换都是在 Call 中进行的。



 ## Call 是干什么的？

  Call 是设计为执行真正网络请求的，其具体的实现 是  OkHttpCall ；但是 OkHttpCall 也只是一个起代理作用的，真正执行网络请求的是  okhttp3.Call,也就是我们设置的OkHttpClient。但是Call集成了 Request的构建，返回结果的转换等，是真正的业务逻辑中心。


### Converter
 
 这个其实真的没什么可以说的，其作用的就两处。请求的时候，参数转换，call 的返回结果进行转换，就这两个作用。

 ###  RequestFactory

这个最后说，因为这个真的简单，作用就是根据配置和传入的参数，生成 Requset

# 简单的类图


``` mermaid
classDiagram


HttpServiceMethod --> RequestFactory
HttpServiceMethod --> okhttp3_Call_Factory
HttpServiceMethod --> CallAdapter
HttpServiceMethod --> Converter

CallAdapter --> Call
Call ..|> OkHttpCall

OkHttpCall --> RequestFactory
OkHttpCall --> okhttp3_Call_Factory
OkHttpCall --> Converter


class HttpServiceMethod

class  CallAdapter{
T adapt(Call<R> call)
}


class Call
class OkHttpCall{
  RequestFactory requestFactory
  khttp3.Call.Factory callFactory;
  Converter<ResponseBody, T> responseConverter;
}


class RequestFactory{
  okhttp3.Request create(Object[] args)
}

class okhttp3_Call_Factory {
  + Call newCall(Request request);
}

class Converter
```


















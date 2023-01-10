
# 概述

EventBus 基本上每一个APP都会使用的总线通信库。它的特征有

- 能够指定消息发送和接收的线程模式(POSITION;MIAN;MAIN_ORDEGRED;BACKGROUND;ASYNC)
- 可以指定订阅的优先级

https://greenrobot.org/eventbus/documentation/delivery-threads-threadmode/

# 工作原理

EventBus 的工作原理很简单，核心代码也没有几行

## 注册（EventBus.register）

```java
public void register(Object subscriber) {

····
        Class<?> subscriberClass = subscriber.getClass();
        List<SubscriberMethod> subscriberMethods = subscriberMethodFinder.findSubscriberMethods(subscriberClass);
        synchronized (this) {
            for (SubscriberMethod subscriberMethod : subscriberMethods) {
                subscribe(subscriber, subscriberMethod);
            }
        }
    }
```

1. 通过反射，找到 @Subscribe 注解的方法。然后生成 SubscriberMethod。

2. subsribe，根据订阅事件的类型，将订阅方法放到对应的类型的list中



- 由于可能多个地方订阅了同一个事件，最终 生成 Subscription 包含了订阅者所在的 class 和 订阅方法。

- 当然 subscribe 方法中还有其它的处理，比如 sticky 事件。
- 事件的优先级排序


Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType;


## 发送消息（EventBus.post）

这里就没啥说的了，通过消息的 classType ，找到对应的订阅者，然后根据订阅者的 ThreadMode。分别采用不用的策略。

这里的几个Poster 是非常值得阅读源代码的

- AsyncPoster
- BackgroundPoster
- HandlerPoster 对应 MAIN_ORDERED 

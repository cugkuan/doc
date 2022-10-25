Handler 原理很多公司都会问，关于Handler 的原理，用下面的代码就可以看清楚Handler 是如何工作的。



# Handler 简单分析

 
 几个核心的类：Handler,Looper,Message,MessageQueue


- MessageQuenu 

 这个不用怎么看，一个消息队列。


核心代码 Looper


Looper.prepare() 创建 Looper 对象，Lopper对象中包括了最重要的 MessageQueue创建。

请注意！！
> Looper 创建在哪个线程。消息就在那个线程执行。使用 ThreadLocal 来维持每个线程的 Looper 副本。


Looper.loop() 启动循环，不断地从 MessageQueue 中拿取消息，Handle 去分发

核心的代码其实就是

```
 msg.target.dispatchMessage(msg);
```
其中的 target 就是Hanlder.

## 主线程中Looper 的启动

ActivityThread 的 main 中。


```java
public static void main(String[] args) {
    
    ....
        Looper.prepareMainLooper();

.....
        Looper.loop();

        throw new RuntimeException("Main thread loop unexpectedly exited");
    }
```

# 关于同步屏障

这里有一篇文章解释的很好 
https://juejin.cn/post/6844903910113705998

同步屏障干什么用的？ 对于消息队列中的消息，同步屏障可以让异步消息优先得到执行。

## 什么是同步消息，什么是异步消息

Handler 中有这样的一个属性

```java 

  final boolean mAsynchronous;  

```

这个值 为 true 就是异常消息

通过这个静态方法去构造

```
   @NonNull
    public static Handler createAsync(@NonNull Looper looper) {
        if (looper == null) throw new NullPointerException("looper must not be null");
        return new Handler(looper, null, true);
    }
```

## 如何开启同步屏障

```java
//开启同步屏障
mHandler.getLooper().getQueue().postSyncBarrier();
// 关闭同步屏障
mHandler.getLooper().getQueue().removeSyncBarrier(mTraversalBarrier);
```

请注意，如果不关闭同步屏障，同步消息永远得不到执行

哪里使用了同步屏障？

View的绘制等。

# IdleHandler

看到官方的解释

```

    /**
     * Callback interface for discovering when a thread is going to block
     * waiting for more messages.
     */
    public static interface IdleHandler {
        /**
         * Called when the message queue has run out of messages and will now
         * wait for more.  Return true to keep your idle handler active, false
         * to have it removed.  This may be called if there are still messages
         * pending in the queue, but they are all scheduled to be dispatched
         * after the current time.
         */
        boolean queueIdle();
    }

```

当消息队空闲的时候，执行IdleHanlder


IdleHanler 执行的具体时间不定。

# 关于Message 的设计


应用中的任何交互，包括点击事件，手势滑动，更新UI等，都是通过 Message ，Handler机制去更新，那岂不是需要创建无数个Meassge?

Android 的设计者们早就考虑到了这个问题，如果你通过

> Message.obtain()

获取的消息可能来自缓存池的消息。

当一个消息被分发完毕后，会调用消息的 recycleUnchecked 方法进行消息的清理；该方法会被标识为 FLAG_IN_USE，如果 消息的缓存池中小于 50个，此消息会进行缓存池，而不会被回收，等待下次被复用。




# 下面是Handler 的一个简化版本：

```java
public class Handler {
    private Looper looper;
    public Handler(){
        looper =  Looper.looperThreadLocal.get();
    }
    void sendMsg(Message message) throws InterruptedException {
        message.target = this;
        looper.q.put(message);
    }
    public void handMsg(Message msg){
        System.out.println(Thread.currentThread().getName());
    }
}

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Looper {
    static ThreadLocal<Looper> looperThreadLocal = new ThreadLocal<>();
    BlockingQueue<Message> q = new LinkedBlockingDeque<>();
    public Looper() {
        looperThreadLocal.set(this);
    }
    public void prepare() {
        while (true) {
            try {
                Message message = q.take();
                message.target.handMsg(message);
            } catch (InterruptedException e) {
            }
        }
    }
}

public class Message {
    public Handler target;
    public String msg;
    public Message(String msg){
        this.msg = msg;
    }
}
```
测试代码：
```java
public class Ttest {
    public static void main(String[] args)  {
        Looper looper = new Looper();
        Handler handler = new Handler(){
            @Override
            public void handMsg(Message msg) {
                super.handMsg(msg);
                System.out.println(msg.msg + Thread.currentThread().getName());
            }
        };
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName());
                Message msg = new Message("xxxxxx");
                try {
                    handler.sendMsg(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
        looper.prepare();
    }

```

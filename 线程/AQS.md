# 概述

AQS  AbstractQueuedSynchronizer  队列同步器。是构建锁的基础。

- 独占锁的获取和释放
- 共享锁的获取和释放
- 获取状态

AQS 只是提供一个同步的框架。


这篇文章对这个讲解的非常详细，非常值得看。

https://cloud.tencent.com/developer/article/1749371


但是，请注意，上面的文章基于Java 20 以下的，Java 20  源码有变化。

## 关于Node 的源码

```java 
   abstract static class Node {
        volatile Node prev;       // initially attached via casTail
        volatile Node next;       // visibly nonnull when signallable
        Thread waiter;            // visibly nonnull when enqueued
        volatile int status;      // written by owner, atomic bit ops by others

   }
```


## 公平锁和非公平锁

这个是针对ReentrantLock 而言的。

公平锁和非公平锁的含义这里就不说了。ReetrantLock 实现公平锁，就是获取锁之前如果队列中有待排队的线程，就让出资源。

下面是 FairSync的 tryAcquire的代码和 NonfairSync的不同点是多了 hasQueuedPredecessors

```java 
   protected final boolean tryAcquire(int acquires) {
            if (getState() == 0 && !hasQueuedPredecessors() &&
                compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }
    }

```

对的锁的释放，是不存在所谓的公平和非公平的，直接取 头部节点，然后唤醒线程。

## 关于 Condition 

- 首先，Condition 的 await,signal,signalAll 对应着 Object 的 wait,notify,notifyAll；
- Object的 notify 是随机唤起一个线程。 Condition 可以精确的唤起一个线程。

- Conddition 是和 Lock 配合一起使用，不能单独使用。



### 为什么 Condition 能精确的唤起一个线程？


Condition 的实现类，ConditionObject 维护着一个 将当前线程等信息构建成一个Node 节点的队列。当唤醒的时候，唤醒头部Node相关的Thread.


# CyclicBarrier


使用场景，https://www.jianshu.com/p/4ef4bbf01811

# ForkJoinPool

对线程池的一个补充，支持将一个任务拆分成多个“小任务”并行计算，再把多个“小任务”的结果合并成总的计算结果。

请注意，ExecutorCompletionService 也可以，只是对于结果，需要自己去合并处理。

# Phaser
https://juejin.cn/post/6978733791938363405

是CyclicBarrier 的升级版本，分阶段任务场景。

# Exchange 

用于二个线程之间交换对象。


# Semaphore

Semaphore主要用于管理信号量，同样在创建Semaphore对象实例的时候通过传入构造参数设定可供管理的信号量的数值。简单说，信号量管理的信号就好比令牌，构造时传入令牌数量，也就是Semaphore控制并发的数量。线程在执行并发的代码前要先获取信号（通过aquire函数获取信号许可），执行后归还信号（通过release方法归还），每次acquire信号成功后，Semaphore可用的信号量就会减一，同样release成功之后，Semaphore可用信号量的数目会加一，如果信号量的数量减为0，acquire调用就会阻塞，直到release调用释放信号后，aquire才会获得信号返回。
一个例子：一个应用程序要读取几万个文件的数据，由于都是IO密集型任务，所以可以启动几十个（例如10）个线程并发地读取。读取到内存中后还要将数据存储到数据库，但是数据库的连接只有3个。此时就必须设置策略控制只有10个线程同时获取数据库连接并保存数据，否则会报错，无法连接到数据库，这时Semaphore就派上用场了，用它来做流量控制，即连接到数据库的线程数。






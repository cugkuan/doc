# 概述

LockSupport 这是一个知识盲区，和 notify,wait 有着不同。LockSupport 有许可的概念。


LockSupport中的park() 和 unpark() 的作用分别是阻塞线程和解除阻塞线程，而且park()和unpark()不会遇到“Thread.suspend 和 Thread.resume所可能引发的死锁”问题。


- park：阻塞当前线程(Block current thread),字面理解park，就算占住，停车的时候不就把这个车位给占住了么？起这个名字还是很形象的。
- unpark: 使给定的线程停止阻塞(Unblock the given thread blocked )。

> 相比于 notify 和 notifyAll,notify 是随机唤醒一个线程，notifyAll 是唤醒所有线程；unpark可以精确的唤醒一个线程。

# 关于 permit （许可）

- pack时：如果线程的permit存在，那么线程不会被挂起，立即返回；如果线程的permit不存在，认为线程缺少permit，所以需要挂起等待permit。
- unpack时：如果线程的permit不存在，那么释放一个permit。因为有permit了，所以如果线程处于挂起状态，那么此线程会被线程调度器唤醒。如果线程的permit存在，permit也不会累加，看起来想什么事都没做一样。注意这一点和Semaphore是不同的。




这个 permit 到底是什么？

- 其实park/unpark的设计原理核心是“许可”。park是等待一个许可。unpark是为某线程提供一个许可。如果某线程A调用park，那么除非另外一个线程调用unpark(A)给A一个许可，否则线程A将阻塞在park操作上。
- 有一点比较难理解的，是unpark操作可以再park操作之前。也就是说，先提供许可。当某线程调用park时，已经有许可了，它就消费这个许可，然后可以继续运行。这其实是必须的。考虑最简单的生产者(Producer)消费者(Consumer)模型：Consumer需要消费一个资源，于是调用park操作等待；Producer则生产资源，然后调用unpark给予Consumer使用的许可。非常有可能的一种情况是，Producer先生产，这时候Consumer可能还没有构造好（比如线程还没启动，或者还没切换到该线程）。那么等Consumer准备好要消费时，显然这时候资源已经生产好了，可以直接用，那么park操作当然可以直接运行下去。如果没有这个语义，那将非常难以操作。
- 但是这个“许可”是不能叠加的，“许可”是一次性的。比如线程B连续调用了三次unpark函数，当线程A调用park函数就使用掉这个“许可”，如果线程A再次调用park，则进入等待状态。


**park和unpark的灵活之处**

上面已经提到，unpark函数可以先于park调用，这个正是它们的灵活之处。

一个线程它有可能在别的线程unPark之前，或者之后，或者同时调用了park，那么因为park的特性，它可以不用担心自己的park的时序问题，否则，如果park必须要在unpark之前，那么给编程带来很大的麻烦！！

考虑一下，两个线程同步，要如何处理？

在Java5里是用wait/notify/notifyAll来同步的。wait/notify机制有个很蛋疼的地方是，比如线程B要用notify通知线程A，那么线程B要确保线程A已经在wait调用上等待了，否则线程A可能永远都在等待。 编程的时候就会很蛋疼。

是调用notify，还是notifyAll？

notify只会唤醒一个线程，如果错误地有两个线程在同一个对象上wait等待，那么又悲剧了。为了安全起见，貌似只能调用notifyAll了。而unpark可以指定到特定线程。
park/unpark模型真正解耦了线程之间的同步，线程之间不再需要一个Object或者其它变量来存储状态，不再需要关心对方的状态。


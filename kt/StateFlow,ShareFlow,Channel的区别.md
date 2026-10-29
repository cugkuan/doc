
# StateFlow，ShareFlow,Channel的区别是什么？


- ShareFlow 和 Channel可以归为一类
- StateFlow 每一次订阅 都会发送最新的值，而ShareFlow不会
  > StateFlow常用于UI，ShareFlow可以作为事件使用。

 

- ShareFlow和Channel的区别在于，Channel 默认行为是不会丢失事件的。

> 比如 发送了三个事件，分别是1,2,3;当ShareFlow 的订阅者正在消费事件 1，而2,3 发送过来了，那么2,3 会被丢弃，无法被消费。而Channel 则不会，甚至可能挂起发送者典型的生产，消费者模型。

- StateFlow 始终保留最后一个值，只有值覆盖的概念，内部也没有维护任何队列等，永远只有一个值。



**StateFlow就是值更新的概念，ShareFlow是一个事件的概念***
# 概述

比起原生的事件体系，Flutter 的事件体系则是非常简单。请注意，事件和手势是两件不同的事情。手势是对事件的进一步封装。


**事件**

- Listener


**手势**

- GestureDetector  对 Listener 的封装,使用 GestureRecognizer 识别手势

# 事件机制

跟原生的大同小异，但是它有自己的特点


- 命中测试：当手指按下时，触发 PointerDownEvent 事件，按照深度优先遍历当前渲染（render object）树，对每一个渲染对象进行“命中测试”（hit test），如果命中测试通过，则该渲染对象会被添加到一个 HitTestResult 列表当中。
- 事件分发：命中测试完毕后，会遍历 HitTestResult 列表，调用每一个渲染对象的事件处理方法（handleEvent）来处理 PointerDownEvent 事件，该过程称为“事件分发”（event dispatch）。随后当手指移动时，便会分发 PointerMoveEvent 事件。
 - 事件清理：当手指抬（ PointerUpEvent ）起或事件取消时（PointerCancelEvent），会先对相应的事件进行分发，分发完毕后会清空 HitTestResult 列表。

**补充**
- 命中测试采用深度优先的方式遍历。
- 子节点通过命中测试那么父也通过，当所有的子没有通过命中测试后，父自己检查自己的命中测试。
- 只要有一个Child 通过，那么立刻停止其兄弟的命中测试，



 ## 关于HitTestBehavior

我们前面阐释了命中测试的正常流程，那么能否改变这个，命中流程呢，是可以的，就是 HitTestBehavior

# 前言

我们以Java和koltin语言的规则去学习 typeScrip。个人觉得及其的啰嗦。


# 函数

函数是一等公民。

下面是一些注意项目

1.=> 进行函数的定义

=> 用来表示函数的定义。左边是输入类型，用（），右边是输出类型

```js
let mySum = (a:number,y:number)=> number = function(x:number,y:number):number {return x+y}
```
2.接口定义函数样式

这个确实没在其它的语音中见过。类似于 => 的作用。对函数进行定义

```js
interface MyFun{
    (a:number,b:number):number;
}

let my:MyFunc;
my = function(a:number,b:number) {
    return a+b;
}
```
其余的函数参数可选，参数默认值，剩余参数等，重载等，koltin都有，就不用说了。

# interface 的理解

从官方的文档上看，interface被称为 Object Types,是规范对象样子的。也就是说，interface是一种规范行为。其实跟Java的interface 没啥区别

# 联合类型

这个有点骚气，在别的语音还没有见到。

下面是一个简单的例子：

```ts
let my :String|number
my = "fuck"
my = 1
```

# Class 

跟Java差不多。需要注意的的是：

- readOnly 

# Module 的概念
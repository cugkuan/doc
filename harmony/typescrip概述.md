# 前言

我们以Java和koltin语言的规则去学习 typeScrip。个人觉得及其的啰嗦。


**理解1**

不能使用静态语言的方式去理解TS。这个语言很诡异。刚开始理解很有难度。甚至其中 new  和Java 的new不一样。ts中。new 就是一个方法而已。

```ts
interface NumberConstructor {
    new(value?: any): Number;
    (value?: any): number;
    readonly prototype: Number;

    /** The largest number that can be represented in JavaScript. Equal to approximately 1.79E+308. */
    readonly MAX_VALUE: number;

    /** The closest number to zero that can be represented in JavaScript. Equal to approximately 5.00E-324. */
    readonly MIN_VALUE: number;

    /**
     * A value that is not a number.
     * In equality comparisons, NaN does not equal any value, including itself. To test whether a value is equivalent to NaN, use the isNaN function.
     */
    readonly NaN: number;

    /**
     * A value that is less than the largest negative number that can be represented in JavaScript.
     * JavaScript displays NEGATIVE_INFINITY values as -infinity.
     */
    readonly NEGATIVE_INFINITY: number;

    /**
     * A value greater than the largest number that can be represented in JavaScript.
     * JavaScript displays POSITIVE_INFINITY values as infinity.
     */
    readonly POSITIVE_INFINITY: number;
}

/** An object that represents a number of any kind. All JavaScript numbers are 64-bit floating-point numbers. */
declare var Number: NumberConstructor;
```


**理解2**

Number，String等被声明 在lib.es5.d.ts中，由运行环境或者编译环境去做具体的实现。但我们看不到他们是怎么具体实现的，甚至都没有像 Java，c,c++那样的标准库。这个和静态语言有着很大的区别。


**理解3**

ts 提供了一种 声明合并的功能，这个功能很屌，类似于Class 的继承，比继承更牛逼。简单的说，如果是同名声明，那么就进行合并。这个是ts的一种扩展方式。


**理解3**

Class 的使用和 Java 的Class 类似。但是请注意，private ，public ，protected 访问控制，只存在 Class 中，只存在Class中，只存在Class中，这个和 kt 不一样。甚至你不能 private class 


**理解4**

因为没有了访问控制，因此就引入了作用域(namespace)和export,import 用来控制暴露程度。namespace 用来组织代码，消除冲突，本质上还是因为控制作用域的问题。

**理解5**
因为没有Java静态语言的特征，为了减少编译期间的困扰，然后搞出了 declare 这种东西。

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

从官方的文档上看，interface被称为 Object Types,是规范对象样子的。也就是说，interface是一种规范行为。但是跟Java的interface 还是有点不一样。

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

- get 和 set 

# Module 和 namespace

为什么有模块这个概念。下面这个文章其实写的很好的
https://jkchao.github.io/typescript-book-chinese/project/modules.html#%E5%85%A8%E5%B1%80%E6%A8%A1%E5%9D%97


默认情况下，是文件作用域，就是全局作用域。有了 export 和 import 之后，变成了本地作用域。


## namespace 

为了解决重名问题，本质上是一个类。


# 声明合并

这个特征，目前只在 ts上发现有。指的是，编译器将针对同一个名字的二个独立声明合并为单一声明。合并后的声明同时拥有原来两个声明的特征。

# declare 的意思

# 前言

我们以Java和koltin语言的规则去学习 typeScrip。个人觉得及其的啰嗦。



# Ts 没有方法重载

因为没有方法重载，现在终于知道了为什么有一个联合类型了,因为没有方法重载麻，引入 namespace 也是为了解决这个问题。

它的重载只是方法的签名重载，这种重载在我看来就不算是重载。


# 函数

一个简单的函数

```js
function sum(a:number,b:number):number{
    return a+b
}
sum(a,b)
```
函数本身也是一种类型,可以写成这样

```js
let sum = function(a:number,b:number):number{
    return a+b
}
sum(a,b)
```

我们也可以先定义一个函数的类型，使用 => ， => 的左边是函数的输入，=> 的右边是输出

```js
(a:number,b:number) => number
```
于是下面的代码看的就比较明白了
```js
let sum:(a:number,b:number)=>number = function(a:number,b:number):number{
    return a+b
}
```

当然，我们觉得这样写太啰嗦了，我们能不能不用function去实现函数？我能不能把函数的定义和函数的实现放在一起写呢?,当然可以


```js

let sum = (a:number,b:number):number => {
    return a+b
}
```
因为函数的返回类型是可以被推断的，我们甚至可以简化为：

```js
let sum = (a:number,b:number) =>{
    return a+b
}
```

上面的写法，=> 作用还是左边输入，右边输出； => 的本质没有变化。


函数的类型还是太长了，可以使用 type 对类型进行重命名，于是

```js
type Add = (a:number,b:number) =>number
let sum:Add = (a:number,b:number):number{
    retrun a+b
}
```

当然，我们也可以使用 interface来定义函数的形状。但请注意 interface的内容中只能有这么一个函数。

```js
  interface Sum{
      (a:number,b:number):number
    }
    let a:Sum = function (a,b):number{
      return a+b;
    }
    a(1,2)
```

## 高阶函数

就是把函数作为参数和返回值的，这个很简单，不多说了。


## 函数合并，就是签名重载的问题

```js
function reverse(x: number): number;
function reverse(x: string): string;
function reverse(x: number | string): number | string {
    if (typeof x === 'number') {
        return Number(x.toString().split('').reverse().join(''));
    } else if (typeof x === 'string') {
        return x.split('').reverse().join('');
    }
}
```


# interface 的理解

从官方的文档上看，interface被称为 Object Types,是用来规范对象形状的。也就是说，interface是一种规范行为


# Index Signatures （索引签名）

这个特征，我在Java，koltin上没有看到此特征。这个不好用言语去描述，举个例子：

```js
   interface C{
      [index:number]:string
    }
    let c :C ={
      2:'2'
    }
    c[0] = '0'
    c[1] = '1'
    console.log('lmk',c[0])
    console.log('lmk',c[1])
    console.log('lmk',c[2])
```
但是 js 是动态语言，我们可以这样写

```js
  let a = {
      c:function(){
        return 'xxxxx'
      }
    }
    a[0] = 'a'
    a[1] = 'b'
    console.log('lmk',a[0])
    console.log('lmk',a[1])
    console.log('lmk',a.c())

```

这样一对比，签名索引似乎没啥必要了？，ts创建一个对象是如此的简单。但是进一步理解，interface 和 classs 更多的是规范定义。interface 被归纳到 Object Types

# 联合类型

这个有点骚气，在别的语音还没有见到。本质上解决的是重载的问题

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


## module 


我们使用能够用Java 的 import 的思想去理解 export 和 import？


**什么是Module?**

一个文件就是一个Module。


使java 编程的时候，class 文件就是一个独立的编译单元，但是 class 有 public ，protected ,private ,internal 等访问权限的控制。

那么对于 typescript ；

- 默认的，函数，变量，类只在模块内访问。
- export 对外暴露函数，变量，类的访问。
- import 等同于Java的import


理解了上面的知识。那么模块的其它特征就很容易了。比如 export和import as 进行重命名.

另一个注意是是，import（）函数实现动态加载的目的。


## namespace 

组织管理代码的两种方式分别是 module 和 namesapce。


namespace 可以将分散在几个文件中的代码组织起来，但这个能力非常的鸡肋。


更多的是下面的应用

### 方便使用
a.ts
```js
export namespace Shape{
  export class Cire{}
  export class Rectangle{  }
  
}
```

使用的使用我们只需要这样引入

```js
import Shape from './a.ts'

let circle = new Shape.Cire()
let rectange = new Shape.Rectange()
```

### 暴露和封装

比如下面的代码

```js
export namespace Bird{

   class Egg {
     size:number
   }

   export egg；Egg = new Egg()

}
```

我们在另外的地方使用

```js
import Bird from './Bird.ts'

Bird.egg.size = 10
```

我们并没有导出 Egg ，但是在外面，我们仍然能访问 egge的对应属性。这个特性可以用于更好的设计封装和相关的暴露。


### 方法和Class 的重名问题

```js
function init(){

}
namespace Bird{
    function init(){
        
    }
}
```

# 声明合并

这个特征，目前只在 ts上发现有。指的是，编译器将针对同一个名字的二个独立声明合并为单一声明。合并后的声明同时拥有原来两个声明的特征。

# declare 的意思

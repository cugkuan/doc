#  概述


Dart 语言给人很怪的感觉；特别是写习惯了koltin这种感觉更怪。给我感觉，koltin 才是未来的语言，dart 语言太啰嗦了；从函数上就看出来。


- dart 语言的可见性只有二种，public 和 private ，其中默认的是 public ；私有的是以 _开头
- Typedefs  类型的别名

- required  关键字 表示该参数是必传的
  

# 关于扩展函数和扩展类的说明使用。

koltin 也有扩展方法，扩展类，但是显然，dart 比 koltin 的难用多了。

```
extension NumberParsing on String {
  int parseInt() {
    return int.parse(this);
  }
  // ···
}

// ···
print('42'.padLeft(5)); // Use a String method.
print('42'.parseInt()); // Use an extension method.
```

还有下面的方式
```
extension on String {
  bool get isBlank => trim().isEmpty;
}
```

# 函数

函数也是一个类，一个函数就是一个 Function 对象

## 函数的参数


必要参数和可选参数。


### 命名参数

函数的参数部分是这样的， {参数1，参数2}  这个就是命名参数

```
void test({int a = 0, required int b}){

}
```

命名参数在使用该函数的时候，需要指定参数对应的值
```
test(a:1,b:2)
```
### 可选的位置参数

这个没啥可说的。

**下面代码基本上包含了函数的的使用**

```
void main() {
  print(f1(1, 2));
  print(f2(1, 2));

  print(f3(c: 3));
  print(f3(a: 2, c: 3));
  
  print(f4(1, 2));
  print(f4(1, 2,3));

  B(a:3,b: 2);
  C(a: 4);
}
int f1(a, b) => a + b;
int f2(int a, int b) => a + b;
// 命名参数
int f3({a = 1, b = 2, required int c}) => a + b + c;
// 可选参数
int f4(int a, int b, [int? c]) {
  if (c == null) {
    return a + b;
  } else {
    return a + b + c;
  }
}
class A{
  A({ a = 1}){
    print("构造函数${a}");
  }
}
class B extends A{
  B({a,b = 2}):super(a: a){
    print(b);
  }
}
/// 可以进行简写,{super.a}

class C extends A{
  C({super.a});
}


```

### 匿名函数

([参数类型] 参数,....){

}

# class 

这个玩意竟然可以多继承的。

- final 这个关键字，有用来修饰内部变量的
- 并没有 final class ；这种限制类继承的方式，但是有 enum 又不能被继承；这个语言真是分裂
> package:meta provides a @nonVirtual annotation to disallow overriding methods and a @sealed annotation to disallow derived classes entirely.

> Note that these annotations just provides hints to dartanalyzer. They won't actually prevent anything from violating the annotations, and they instead will cause warnings to be printed when analysis is performed.



## Getter 和 Setter

这个感觉有点傻屌啊；并没有 koltin 这种幕后字段 filed ;感觉怪怪的

```
class Rectangle {
  double left, top, width, height;

  double get right => left + width;
  set right(double value) => left = value - width;
  double get bottom => top + height;
  set bottom(double value) => top = value - height;
}

```

这东西真的是鸡肋


## 构造函数


构造函数中有一个命名式构造函数；这个语言特征在别处是没有的。

```
const double xOrigin = 0;
const double yOrigin = 0;

class Point {
  final double x;
  final double y;

  Point(this.x, this.y);

  // Named constructor
  Point.origin()
      : x = xOrigin,
        y = yOrigin;
}
```

**超类参数**


抽象方法；Dart 没有接口的概念。

### 初始化列表

这个是 Dart 特有的；为了是子类中的某些东西先于父类初始化

```
import 'dart:math';

class Point {
  final double x;
  final double y;
  final double distanceFromOrigin;

  Point(double x, double y)
      : x = x,
        y = y,
        distanceFromOrigin = sqrt(x * x + y * y);

Point.withAssert(this.x, this.y) : assert(x >= 0) {
  print('In Point.withAssert(): ($x, $y)');
}
}


void main() {
  var p = Point(2, 3);
  print(p.distanceFromOrigin);
}
``` 

特别有意思

# mixin

字面意思是混合；这种不能看成是多继承，它的作用是代码复用。

```
mixin  A{
  int f = 1;
  void f(){
    print(f);
  }
}
```

A 不能被实例化。

```
class Test with A{

}
```

下面的代码这样做是合法的

```
Test t = Test();
A a = Test();
/// 下面的代码无法被编译
A a1 = A();
```

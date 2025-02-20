
记录一个ksp问题。该问题防不胜防,关于内部类

```java

class A {

        class B{
        public void print(){
            System.out.print("======>");
        }
    }
}

```

ksp 通过 KSVisitorVoid 得到的KSClassDeclaration 获取的类名；对于上面的 B类，获取的类名 是 A.B.但是正确的应该是 A$B。


如何解决？

没办法解决！！尽量不要使用内部类和嵌套类。
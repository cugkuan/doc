
# 概述
该算法十分巧妙；真不知道是谁想出来的。
关于该算法的详细讲解，见下面这篇博客：https://www.ruanyifeng.com/blog/2013/05/Knuth%E2%80%93Morris%E2%80%93Pratt_algorithm.html


这篇博客讲课的非常透彻了；

 - 求解 模式串的 next 数组，这数组代表着当匹配不上时，模式串指针退回的位置。
 - 匹配，匹配的字符串只前进不后退。

## 匹配表是什么

关于部分匹配表的生成，这个才是KMP最难的部分；

什么是前缀，后缀，下面的例子说明：

"部分匹配值"就是"前缀"和"后缀"的最长的共有元素的长度。以"ABCDABD"为例，

　　－　"A"的前缀和后缀都为空集，共有元素的长度为0；

　　－　"AB"的前缀为[A]，后缀为[B]，共有元素的长度为0；

　　－　"ABC"的前缀为[A, AB]，后缀为[BC, C]，共有元素的长度0；

　　－　"ABCD"的前缀为[A, AB, ABC]，后缀为[BCD, CD, D]，共有元素的长度为0；

　　－　"ABCDA"的前缀为[A, AB, ABC, ABCD]，后缀为[BCDA, CDA, DA, A]，共有元素为"A"，长度为1；

　　－　"ABCDAB"的前缀为[A, AB, ABC, ABCD, ABCDA]，后缀为[BCDAB, CDAB, DAB, AB, B]，共有元素为"AB"，长度为2；

　　－　"ABCDABD"的前缀为[A, AB, ABC, ABCD, ABCDA, ABCDAB]，后缀为[BCDABD, CDABD, DABD, ABD, BD, D]，共有元素的长度为0。

**求解思路**
通过观察，发现，前缀，是不包含最后一个字符的 所有顺序组合，后缀是不包含第一个字符的所有顺序组合，怎么理解顺序组合；

ABCDA 的前缀的顺序组合 是 A,AB,ABC,ABCDA;  后缀的顺序组合 是BCDA, BCDA,CDA,DA,A


那么怎样求解 前缀后缀共有元素的最大长度？

我们观察后缀，发现，如果 前缀和后缀有共有元素，那么后缀的第一个 元素一定能匹配前缀的第一个元素，如果匹配上，对于下一个元素，如果匹配上，那么长度是前一个+1.匹配不上，后面的匹配从 第一个开始。

怎么理解；

AB ,后缀是 B 和 A 匹配不上，一直到 ABCD 都是这样。到了ABCDA,后缀 A 能匹配上前缀A,于是该位置的共有最大元素长度为1，对于ABCDAB, 后缀B能匹配上B,于是在前有的基础上+1；于是该位置最大长度为 2，到下一个 ABCDABD ，其后缀D匹配不上C了，于是该位置的最长共有元素为0；


求解 模式串 前缀后缀的最大长度的代码如下：

```
public int[] getNext( String p){
  int[] next = new int[p.length()];
        int j = 0;
        for (int i = 1;i < p.length();i++){
            if (p.charAt(i) == p.charAt(j)){
                j++;
            }else {
                j= 0;
            }
            next[i] = j;
        }
        return next;
}
```

KMP 算法核心是，其 对比的字符串 只前进不后退，模式串的指针来回移动，Next 代码，模式串 要后退到哪个位置。
那么 KMP 算法为：

```
 public int kmp(String s,String p){
        int i = 0;
        int j = 0;
        int[] next = getNext(p);
        while (i < s.length() && j < p.length()){
            if (s.charAt(i) == p.charAt(j)){
                i++;
                j++;
            }else {
                if (j > 0){
                    j = next[ j-1]; // 模式串后退的位置
                }else {
                    i++;
                }
            }
        }
        if (j == p.length()){
            return i-p.length();
        }else {
            return  -1;
        }
    }
```



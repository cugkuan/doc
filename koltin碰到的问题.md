
# interface 实现 Cloneable 问题


 当 kotlin 版本升级后，同事反馈崩溃问题:

```
  java.lang.IllegalAccessError: Method 'java.lang.Object java.lang.Object.clone()' is inaccessible to class 'com.qizhidao.clientapp.common.widget.filterview.IFilterViewData' (declaration of 'com.qizhidao.clientapp.common.widget.filterview.IFilterViewData' appears in /data/app/com.qizhidao.clientapp-5yjcyjUvCGgLvgSrq1EKow==/base.apk!classes8.dex)
```
 当定位到出问题的代码时，惊呆了，简化的代码大概是这样的
 ```
interface IFilterViewData : Cloneable {

    var filterGroupList: List<IFilterViewGroupData>
    var bottomOpBean:BottomOpBean?
     override fun clone(): Any {
        var clone = super.clone() as IFilterViewData
        val list = arrayListOf<IFilterViewGroupData>()
        for (item in filterGroupList) {
            list.add(item.clone())
        }
        clone.filterGroupList = list
        return clone
    }

 ```

clone 方法是 Object的，而且 是 protected，问题大概也就解决了。

诡异的还 在 kotlin  1.3.5 版本是没问题的，而到了 1.5 后面的版本就有问题了，暂时不清楚是 kotlin的版本问题还是java 版本问题。



岗位职责

1. 对Android，Harmony 整理质量负直接责任。
2. 负责Android,Harmony总体架构设计与维护；基础组件，重要业务组件开发，维护。
3. 保持技术敏感度，跟踪前沿技术，定期分享，并将一些优秀技术方案应用到工程中，提升APP的使用体验。
4. review同事提交代码，规范同事技术行为。

一季度工作成果。

Android

1.重构组件化框架CS，采用Ksp+AsM 方式，将编译效率提升10倍。
2.升级版本，Android版本适配 Android 15。编译工具版本升级，适配最新特征，提升APP整体稳定度。
3.业务需求（AI，综合搜索，安全校验，审核合规等）


Harmony

1.综合搜索业务收尾，综合搜索所用功能可用。
2.新的业务需求开发（AI，综合搜索，审核合规）


KMP(跨平台)

1.跨平台技术方案选型，KMP技术方案的探索，完成最小demo
2.KMP 整体框架搭建，完成kmp组件化框架 Bridge开发。
3.Android 现有项目和KMP结合方案落地，综合搜索和登录二个模块为第一阶段迁移工作。


个人评价

1. 具备较扎实的技术基础，，能胜任日常开发中的复杂技术挑战；
2. 高效准确完成产品需求。
3. 积极追踪行业技术发展趋势，对新技术保持敏锐洞察，能够结合实际场景探索应用落地
4. 技术迭代应用过程中，对APP某些场景测试不到位，造成一些问题，影响部分功能使用体验，在今后需要注意







 
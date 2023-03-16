# 概述

Frgament 本身很简单，复杂的是 FragmentTransaction 涉及到的一系列的操作。

事务的概念，这里就不说了。


那么为什么有 FragmentTransaction 的概念：一个场景就是 我们需要移除一个 FragmentA然后 添加 FragmentB,这整个过程是不能中断的。




## 几个关键的类

- FragmentTransaction

只是作为一个行为记录。一次操作，被抽象为一个

- BackStackRecord  

FragmentTransaction 的唯一子类，实现了 commit 的相关逻辑

- FragmentManager

很多的逻辑都是通过 FragmentManger 进行转接的。我们也是通过FramentManger 去控制Fragment，

- FragmentStateManager 

Frgament 对应状态的具体处理类，这里面的代码值得阅读，明白一个Fragment 的创建过程，销毁过程。这个类非常的重要。

> 通过这个类的阅读，你会发现。FragmentTransaction 的 hide 和 show 操作对应着 View 的 setVisibility 的 GONE 和 VISIBLE 操作，replace 对应着 containerView 的remove 操作。
> 底层的代码都这么朴实无华。

## 关于干涉 Fragment 的生命周期

在之前的版本。不直接提供对 Fragment的生命周期的控制，这导致了一些场景需要非常复杂的处理。后来提供了setMaxLifecycle 来控制 Fragment的生命周期，要理解其工作原理，需要明白以下要点：

-  Lifecycle.State 其生命状态的排序是这样的：DESTROYED，INITIALIZED，CREATED，STARTED，RESUMED 也就是 说 RESUME 最大，DESTROYE最小。
-  在计算 Fragment 的生命周期时候， 其 newState  = min(maxLifecycle,currentState)
-  FragmentManger 的moveToState（） 中对状态进行矫正。当 Frgment 的 state  > newState 时，矫正的逻辑的伪代码（f代表Fragment）如下
  
  ```java
   if (f.mState > newState) {
            switch (f.mState) {
                case Fragment.RESUMED:
                    if (newState < Fragment.RESUMED) {
                        fragmentStateManager.pause();
                    }
            
                case Fragment.STARTED:
                    if (newState < Fragment.STARTED) {
                        fragmentStateManager.stop();
                    }
            
                case Fragment.ACTIVITY_CREATED:
                    if (newState < Fragment.ACTIVITY_CREATED) {
                        if (isLoggingEnabled(Log.DEBUG)) {
                            Log.d(TAG, "movefrom ACTIVITY_CREATED: " + f);
                        }
                        if (f.mView != null) {
                            // Need to save the current view state if not
                            // done already.
                            if (mHost.onShouldSaveFragmentState(f) && f.mSavedViewState == null) {
                                fragmentStateManager.saveViewState();
                            }
                        }
                    }
                    // fall through
                case Fragment.VIEW_CREATED:
                    if (newState < Fragment.VIEW_CREATED) {
                
                        if (f.mView != null && f.mContainer != null) {
                            // Stop any current animations:
                            f.mContainer.endViewTransition(f.mView);
                            f.mView.clearAnimation();
                            // If parent is being removed, no need to handle child animations.
                            if (!f.isRemovingParent()) {
                                if (mCurState > Fragment.INITIALIZING && !mDestroyed
                                        && f.mView.getVisibility() == View.VISIBLE
                                        && f.mPostponedAlpha >= 0) {
                                    anim = FragmentAnim.loadAnimation(mHost.getContext(),
                                            f, false, f.getPopDirection());
                                }
                                f.mPostponedAlpha = 0;
                                // Robolectric tests do not post the animation like a real device
                                // so we should keep up with the container and view in case the
                                // fragment view is destroyed before we can remove it.
                                ViewGroup container = f.mContainer;
                                View view = f.mView;
                                if (anim != null) {
                                    FragmentAnim.animateRemoveFragment(f, anim,
                                            mFragmentTransitionCallback);
                                }
                                container.removeView(view);
                                if (FragmentManager.isLoggingEnabled(Log.VERBOSE)) {
                                    Log.v(FragmentManager.TAG, "Removing view " + view + " for "
                                            + "fragment " + f + " from container " + container);
                                }
                                // If the local container is different from the fragment
                                // container, that means onAnimationEnd was called, onDestroyView
                                // was dispatched and the fragment was already moved to state, so
                                // we should early return here instead of attempting to move to
                                // state again.
                                if (container != f.mContainer) {
                                    return;
                                }
                            }
                        }
                        // If a fragment has an exit animation (or transition), do not destroy
                        // its view immediately and set the state after animating
                        if (mExitAnimationCancellationSignals.get(f) == null) {
                            fragmentStateManager.destroyFragmentView();
                        }
                    }
                    // fall through
                case Fragment.CREATED:
                    if (newState < Fragment.CREATED) {
                        if (mExitAnimationCancellationSignals.get(f) != null) {
                            // We are waiting for the fragment's view to finish animating away.
                            newState = Fragment.CREATED;
                        } else {
                            fragmentStateManager.destroy();
                        }
                    }
                    // fall through
                case Fragment.ATTACHED:
                    if (newState < Fragment.ATTACHED) {
                        fragmentStateManager.detach();
                    }
            }
  ```
  这样大概理解了 其控制生命周期的逻辑了。

  
## 类图

``` mermaid
classDiagram




class FragmentManager

class FragmentTransaction

class BackStackRecord

class FragmentStateManager

```
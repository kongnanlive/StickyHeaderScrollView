# StickyHeaderScrollView
## 简介

StickyHeaderScrollView是Android下支持吸顶Sticky，滚动布局NestedScrollView嵌套RecyclerView，解决CoordinatorLayout、AppBarLayout快速滑动fling停住卡住回弹抖动等bug。

StickyHeaderScrollView支持NestedScrolling机制。

## 效果图

滚动布局NestedScrollView嵌套RecyclerView\
NestedScrollView嵌套ViewPager2\
![device-2021-06-03-175146.gif](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7abdfe5dd71d45c9af5832d3bfc60978~tplv-k3u1fbpfcp-watermark.image)

支持SwipeRefreshLayout下拉刷新\
动态改变布局不影响吸顶\
![device-2021-06-03-175146 (1).gif](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ae8f778f0afd448286be8a2f40778710~tplv-k3u1fbpfcp-watermark.image)


## 使用文档

```js
<?xml version="1.0" encoding="utf-8"?>
<com.kongnan.headerscrollview.HeaderScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <com.kongnan.headerscrollview.StickyLinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <!-- 布局省略 -->

            <TextView
                android:id="@+id/remove_view"
                android:layout_width="match_parent"
                android:layout_height="80dp"
                android:background="@android:color/background_dark"
                android:text="吸顶View-1"
                android:textColor="@color/purple_200"
                app:isSticky="true" />

           <!-- 布局省略 -->

        </com.kongnan.headerscrollview.StickyLinearLayout>

        <androidx.recyclerview.widget.RecyclerView
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </LinearLayout>
</com.kongnan.headerscrollview.HeaderScrollView>

```
就是如此简单，详细使用请看Demo
## LICENSE

没有使用协议，两个类HeaderScrollView和StickyLinearLayout拿去随便用。

## 掘金

欢迎评论区讨论
https://juejin.cn/post/6969759148862865415/
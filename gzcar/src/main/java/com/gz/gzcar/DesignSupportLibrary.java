package com.gz.gzcar;

/**
 * Created by Endeavor on 2016/9/5.
 *
 * 一、FloatingActionButton
 *  简介 FloatingActionButton 就是浮动按钮，是兼容包中提供的一个控件， 继承自 ImageView，那么好办
 *  ImageView 所拥有的属性 FloatingActionButton 应该都有（是不是都可以用那就有待考验了）。

    FloatingActionButton 提供了几个特有的属性：

         app:fabSize：控制 FloatingActionButton 的大小（PS：可以试一下设置 layout_width 的大小是否有作用），有两个取值
         app:backgroundTint：设置 FloatingActionButton 的背景色，默认为 Theme 主题中的 “colorAccent”（PS：可以试一下设置 background 是否有作用）
         app:elevation ：设置 FloatingActionButton 阴影的深度，默认有阴影

         对应的 Java 方法：
         --（fabSize 没找到）
         setBackgroundTintList()
         setElevation()

         简单使用
         1、导入兼容库

         compile 'com.android.support:design:23.3.0'
         2、在 Layout 中使用

         <android.support.design.widget.FloatingActionButton
             android:id="@+id/fabtn_test2"
             android:layout_width="100dp"
             android:layout_height="100dp"
             android:layout_alignBottom="@+id/fabtn_test1"
             android:src="@mipmap/ic_alarm_add_black"
             app:fabSize="normal"
             app:backgroundTint="@color/colorPrimary"
             app:elevation="4dp"
             android:layout_centerHorizontal="true" />
         3、代码中使用
         除了那几个特有的属性，可以直接把 FloatingActionButton 当作 ImageView 用即可。

         4、注意事项
         主要是属性设置方法的：
         android:background 设置背景不管用，得用 app:backgroundTint
         大小是 app:fabSize 控制的，layout_width 和 layout_height 不起作用

    二  TextInputLayout
        简介:
         在 EditView 获取焦点的时候，EditView 上的 hint 提示文本通过一个动画显示到了 EditView 的左上角。
         其实该控件就是用于配合 EditView 的，主要为了解决 EditView 获得焦点后 hint 提示文本消失的问题。
         继承自 LinearLayout，
     简单使用
         1、导入兼容包
         和上面的一样

         2、在 Layout 中使用

         <android.support.design.widget.TextInputLayout
             android:id="@+id/til_test1"
             android:orientation="vertical"
             android:layout_width="match_parent"
             android:layout_height="wrap_content"
             android:layout_below="@+id/tv_textinputlayout_tip"
             android:layout_alignParentStart="true">
         <EditText
             android:id="@+id/et_test1"
             android:layout_width="match_parent"
             android:layout_height="wrap_content" />
         </android.support.design.widget.TextInputLayout>
         “注意：”这里没有在 Layout 中使用 TextInputLayout 中所特有的属性，是在代码中设置的。

         3、在代码中使用

         private void initData() {
                 // 设置提示文本
                 mTestTIL.setHint("请输入你的邮箱：");
                 mTestET1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 10){
                            // 设置错误提示
                            mTestTIL.setErrorEnabled(true);
                            mTestTIL.setError("邮箱名过长！");
                        }else{
                            mTestTIL.setErrorEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
         }
         4、常用属性
         Layout 中：

             android:hint="Hint" ：设置提示文本
             app:hintAnimationEnabled="true" ：设置是否动画显示提示文本
             app:hintEnabled="true" ：设置提示是否可用
             app:errorEnabled="true" ：设置错误提示是否可用

         Java 代码中：
             setHint()：设置提示文本
             setHintAnimationEnabled()：设置是否动画显示提示文本
             setHintEnabled()：设置提示是否可用
             setErrorEnabled()：设置错误提示是否可用
             setError()：设置错误信息
             getEditText()：获取 TextInputLayout 中的 EditView

         注意事项
             TextInputLayout 是为了配合 EditText 来使用的，也就是说单独使用是没什么意义的
             TextInputLayout 可以设置错误提示，EditText 也可以设置错误提示，两者并不冲突，建议只选择设置其中一种错误提示（TextInputLayout 的要漂亮，不是吗）

    四 Snackbar
         简介
             Snackbar 是 design support library 中的一个组件，使用 Snackbar 可以在屏幕底部(大多时候)快速弹出消息，它与 Toast 非常相似，但是更灵活一些。

             介于Toast和AlertDialog之间轻量级控件
             可以很方便的提供消息的提示和动作反馈
             当它显示一段时间后或用户与屏幕交互时它会自动消失
             它是 context sensitive message，显示在所在屏幕最顶层

             类似升级版的 Toast，性能更高

        简单使用
             1、导入兼容包
                    和上面一样

             2、显示普通的 Snackbar

                 private void showCommonSB() {
                     Snackbar snackbar = Snackbar.make(mRootRL,
                     "我是普通 Snackbar", Snackbar.LENGTH_LONG);
                     snackbar.show();
                 }
             3、显示带 Action 的 Snackbar

                 private void showWithActionSB() {
                     final Snackbar snackbar = Snackbar.make(mRootRL,
                     "我是带 Action 的 Snackbar", Snackbar.LENGTH_LONG);
                     snackbar.setAction("撤销", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(SnackBarActivity.this, "撤销成功", Toast.LENGTH_SHORT).show();
                            snackbar.dismiss();
                        }
                    });
                     snackbar.show();
                 }
             4、使用 Snackbar 的 Callback

                     private void setCallbackTest() {
                         final Snackbar snackbar = Snackbar.make(mRootRL,
                         "我是带 Action 的 Snackbar", Snackbar.LENGTH_LONG);
                             snackbar.setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(SnackBarActivity.this, "撤销成功", Toast.LENGTH_SHORT).show();
                                snackbar.dismiss();
                            }
                        });
                         snackbar.setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                mShowResultTV.setText("Snackbar - onDismissed()");
                            }
                            @Override
                            public void onShown(Snackbar snackbar) {
                                super.onShown(snackbar);
                                mShowResultTV.setText("Snackbar - onShown()");
                            }
                        });
                         snackbar.show();
                     }
             5、设置背景色

                 private void changeBackground() {
                     Snackbar snackbar = Snackbar.make(mRootRL,
                     "我是普通 Snackbar", Snackbar.LENGTH_LONG);
                     snackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                     snackbar.show();
                 }
             6、添加 CoordinatiorLayout
                 Layout 中：

                     <android.support.design.widget.CoordinatorLayout
                         android:id="@+id/cl_test"
                         android:layout_width="match_parent"
                         android:layout_height="match_parent"
                         android:layout_alignParentTop="true"
                         android:layout_alignParentStart="true">
                     <android.support.design.widget.FloatingActionButton
                         android:id="@+id/fabtn_test"
                         android:layout_width="wrap_content"
                         android:layout_height="wrap_content"
                         android:layout_gravity="bottom|right"
                         android:src="@mipmap/ic_alarm_add_black"
                         app:fabSize="normal"
                         app:borderWidth="0dp"
                         android:layout_marginBottom="@dimen/activity_vertical_margin"
                         android:layout_marginRight="@dimen/activity_vertical_margin"/>
                     </android.support.design.widget.CoordinatorLayout>
                Java 代码中：

                     private void addCoordinatiorLayout() {
                         mTestCL.setVisibility(View.VISIBLE);
                         Snackbar snackbar = Snackbar.make(mTestCL,
                         "我是普通 Snackbar", Snackbar.LENGTH_LONG);
                         snackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                         snackbar.show();
                     }
                使用很简单，虽然没有使用 Builder 内部类，但是和使用 Buidler 的调用方式一样，这也是一个 Builder 模式的使用。

         常用方法简介
             make() 方法：生成Snackbar消息，第一个参数是一个 View, Snackbar 会试着寻找一个父 View 来 hold 这个 View。Snackbar 将遍历整个 View tree 来寻找一个合适的父 View，它可能是一个 coordinatorLayout 也可能是 window decor’s content view，随便哪一个都行。
             setDuration() 方法：设置显示持续时间
             setAction() 方法：设置 Action，第一个参数会作为按钮（Actiong）的文本，第二个参数是按钮的点击事件
             setCallback() 方法：Snackbar 的显示和消失会回调 Snackbar.Callback的 onDismissed()和 onShown()方法
             getView()：获取 Snackbar 的 View
         注意事项
             利用好 getView() 方法来定制 Snackbar
             要支持 Snackbar 的更多特性（例如：向右滑动退出，适配 FloatingActionButton），那么需要使用 CoordinatorLayout

 五、TabLayout
    简介
         继承自 HorizontalScrollView，这个很好理解，Tab 的数量一多，那就需要横向滑动，直接继承自 HorizontalScrollView 来实现会省事很多
         TabLayout 就是用来做选项卡切换这一类效果的控件，在兼容包中提供。对于 Tab 的实现 Google 上提供过不少方案，先有 TabHost；然后在 Android 3.x 上又出现了 ActionBar 提供的 Tab；现在有了更好的实现，那就是使用用 TabLayout。

         当然，还有非常多的各种第三方开源库来实现 Tab，例如：JakeWharton 大神开源的组件 ViewPagerIndicator 相信很多人都用过。

         Tabs 选项卡，效果类似网易新闻客户端的 Tab
         Google 官方实现的 TabPageIndicator
     简单使用
         1、导入兼容包

         compile 'com.android.support:design:23.3.0'
         2、直接使用示例
         TabLayout 继承自 HorizontalScrollView，那先当作 HorizontalScrollView 试试：
         在 Layout 中简单使用，没有使用 TabLayout 特有的属性：

             <android.support.design.widget.TabLayout
                 android:id="@+id/tl_test"
                 android:layout_width="match_parent"
                 android:layout_height="wrap_content" />
         在 Java 代码中使用：

                 private void addTab() {
                     mTabsNum += 1;
                     TabLayout.Tab tab = mTestTL.newTab().setText("TAB" + mTabsNum);
                     mTestTL.addTab(tab);
                 }

                 private void removeFirstTab() {
                     int count = mTestTL.getTabCount();
                     if (count <= 0) return;
                     mTestTL.removeTabAt(0);
                 }

                 private void removeLastTab() {
                     int count = mTestTL.getTabCount();
                     if (count <= 0) return;
                     mTestTL.removeTabAt(count - 1);
                 }

                 private void removeAllTabs() {
                     int count = mTestTL.getTabCount();
                     if (count <= 0) return;
                     mTabsNum = 0;
                     mTestTL.removeAllTabs();
                 }
    上面示例代码演示了 Tab 的添加和删除，简单介绍下 Tab 类和 addTab() 方法：
        Tab 类：这是 TabLayout 中的静态内部类，看下源码，构造方法是 private 的，不能直接 new 对象，注释中提示可以使用 TabLayout 的 newTab() 方法来创建实例。
     addTab() 方法：addTab() 有四个重载方法，那就看参数最多的那个吧！
     第一个参数 tab，就是 Tab 类的实例；第二个参数 position，用于指定加入的 Tab 需要插入的位置，这里需注意，调用该方法之前最好通过 getTabCount() 方法获取当前 TabLayout 中所拥有的 Tab 总数，如果 position + 1 大于总 TabLayout 中的 Tab 总数会导致数组越界（这个应该不难理解）；第三个参数 setSelected，用于指定新加入的 Tab 是否为选中状态。

     3、配合 ViewPager 使用
         TabLayout 是个指示器，确切的说是 ViewPager 的指示器，所以更常见的使用场景是配合 ViewPager 来使用。
         在 Layout 中使用：

             <?xml version="1.0" encoding="utf-8"?>
                 <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                     xmlns:tools="http://schemas.android.com/tools"
                     android:layout_width="match_parent"
                     android:layout_height="match_parent"
                     tools:context="com.diygreen.android6new.newwidget2.TabLayoutDemo3Activity">
                     <android.support.design.widget.TabLayout
                     android:id="@+id/tl_title"
                     android:layout_width="match_parent"
                     android:layout_height="wrap_content"
                     android:scrollbars="none"/>
                     <android.support.v4.view.ViewPager
                     android:id="@+id/vp_content"
                     android:layout_width="match_parent"
                     android:layout_height="match_parent"
                     android:layout_above="@+id/tl_bottom"
                     android:scrollbars="none" />
                     </RelativeLayout>
         将把 TabLayout 当作普通的布局使用即可。

             在 Java 代码中：

                 private void initData() {
                     int size = mTitleArr.length;
                     mViewList = new ArrayList<>(size);
                     // 添加 Tab，初始化 View 列表
                     for (int i = 0; i < size; i++) {
                     mTitleTL.addTab(mTitleTL.newTab().setText(mTitleArr[i]));
                     View view = LayoutInflater.from(this).inflate(R.layout.item_tablayoutdemo2_vp, null);
                     TextView tv = (TextView) view.findViewById(R.id.tv_text);
                     tv.setText(mTitleArr[i]);
                     mViewList.add(view);
                 }
                 mAdapter = new TabLayoutDemo2PagerAdapter(mViewList);

                 // 填充 ViewPager
                         mContentVP.setAdapter(mAdapter);
                 // 给ViewPager添加监听
                         mContentVP.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTitleTL));
                 // 设置setOnTabSelectedListener
                         mTitleTL.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        // 切换到指定的 item
                        mContentVP.setCurrentItem(tab.getPosition());
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });
                 }
            要实现 TabLayout 与 ViewPager 的联动，有两个关键方法：
                 // 给 ViewPager 添加页面改变监听
                         addOnPageChangeListener()
                 // 设置 Tab 选择监听
                         setOnTabSelectedListener()
                 上面代码中都做了示例，使用也很简单。

                 说明：还有另一种方式来实现联动，使用的是 setupWithViewPager() 方法
                 // 设置 TabLayout 与 ViewPager 联动
                    setupWithViewPager()：该方法在 TabLayoutDemo4Activity.java 中使用了，会在下面做说明。

            4、实现底部 Tab

                 这个很简单，直接看布局文件就可以了。

                     <?xml version="1.0" encoding="utf-8"?>
                     <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                         xmlns:tools="http://schemas.android.com/tools"
                         android:layout_width="match_parent"
                         android:layout_height="match_parent"
                         tools:context="com.diygreen.android6new.newwidget2.TabLayoutDemo3Activity">
                         <android.support.design.widget.TabLayout
                             android:id="@+id/tl_bottom"
                             style="@style/DIYTabLayout"
                             android:layout_width="match_parent"
                             android:layout_height="wrap_content"
                             android:layout_alignParentBottom="true"/>
                         <android.support.v4.view.ViewPager
                             android:id="@+id/vp_content"
                             android:layout_width="match_parent"
                             android:layout_height="match_parent"
                             android:layout_above="@+id/tl_bottom"
                             android:scrollbars="none" />
                     </RelativeLayout>
                 -_-^，把 TabLayout 放到 ViewPager 下面即可...

                 5、自定义 TabLayout 样式

                 首先，在 style 文件中定义：

                 <!-- 自定义 TabLayout 样式 -->
                     <style name="DIY.TabLayout" parent="Widget.Design.TabLayout">
                         <item name="paddingEnd">10dp</item>
                         <item name="paddingStart">10dp</item>
                         <item name="tabBackground">@color/colorPrimaryDark</item>
                         <item name="tabContentStart">10dp</item>
                         <item name="tabGravity">center</item>
                         <item name="tabIndicatorColor">#999900</item>
                         <item name="tabIndicatorHeight">6dp</item>
                         <item name="tabMaxWidth">@dimen/tab_max_width</item>
                         <item name="tabMinWidth">@dimen/tab_min_width</item>
                         <item name="tabMode">scrollable</item>
                         <item name="tabPaddingBottom">2dp</item>
                         <item name="tabPaddingEnd">10dp</item>
                         <item name="tabPaddingStart">10dp</item>
                         <item name="tabPaddingTop">15dp</item>
                         <item name="tabSelectedTextColor">#ffcc00</item>
                         <item name="tabTextAppearance">@style/DIY.TabTextAppearance</item>
                         <item name="tabTextColor">#000066</item>
                    </style>
                 <!-- 自定义 TabText 的外观 -->
                 <style name="DIY.TabTextAppearance" parent="TextAppearance.Design.Tab">
                     <item name="android:textSize">14sp</item>
                     <item name="android:textColor">#006600</item>
                     <item name="textAllCaps">false</item>
                 </style>
                 然后，在 Layout 中使用样式

                 <android.support.design.widget.TabLayout
                     android:id="@+id/tl_title"
                     android:layout_width="match_parent"
                     android:layout_height="wrap_content"
                     style="@style/DIY.TabLayout"/>
                 有几个属性要解释一下：

                     tabGravity：用来设置 TabLayout 的布局方式，只有两个可选值fill 和 center，默认值是 fill
                     tabIndicatorColor：用来设置底部指示器的颜色
                     tabIndicatorHeight：用来设置底部指示器的高度
                     tabMode：用来设置 TabLayout 的布局模式，有两个取值
                     fixed：固定不可滚动
                     scrollable: 可滚动的
                 对应的 Java 方法：

                     setTabGravity()
                     setSelectedTabIndicatorColor()
                     setSelectedTabIndicatorHeight()
                     setTabMode():MODE_FIXED 或 MODE_SCROLLABLE
                 其他的属性都是用于设置 Tab 中文本的属性，这基本上可以参考 TextView。

            6、给 TabLayout 加角标

                其实就是利用 TabLayout.Tab 的 setCustomView() 方法，最好自己维护好为 Tab 设置的 CustomView 列表，以便更新数据。
                 简单的示例：

                 private void initData() {
                     int size = mTitleArr.length;
                     mTabItemViewList = new ArrayList<>(size);
                     mViewPagerViewList = new ArrayList<>(size);
                     // 添加 Tab，初始化 View 列表
                     for (int i = 0; i < size; i++) {
                         View tabView = LayoutInflater.from(this).inflate(R.layout.item_tablayout_item, null);
                         TextView tabViewTV = (TextView) tabView.findViewById(R.id.tv_tab);
                         tabViewTV.setText(mTitleArr[i]);
                         mTabItemViewList.add(tabView);

                         View vpView = LayoutInflater.from(this).inflate(R.layout.item_tablayoutdemo2_vp, null);
                         TextView viViewTV = (TextView) vpView.findViewById(R.id.tv_text);
                         viViewTV.setText(mTitleArr[i] + i);
                         mViewPagerViewList.add(vpView);
                     }
                     mAdapter = new TabLayoutDemo2PagerAdapter(mViewPagerViewList);

                     // 填充 ViewPager
                     mContentVP.setAdapter(mAdapter);
                     mTitleTL.setupWithViewPager(mContentVP);
                     size = mTitleTL.getTabCount();
                     for (int i = 0; i < size; i++) {
                         TabLayout.Tab tab = mTitleTL.getTabAt(i);
                         if (tab != null) {
                            tab.setCustomView(mTabItemViewList.get(i));
                         }
                     }
                     mContentVP.setCurrentItem(1);
                 }
            注意事项
                 1、关于 TabMode
                 如果你发现你的 TabLayout 不能滚动，那么可能是没有设置 TabMode 为 MODE_SCROLLABLE 的缘故

                 2、关于 remove 方法
                 TabLayout 的 removeXxx 方法没有什么问题，这里要说的是当 TabLayout 与 ViewPager 一起使用的时候，ViewPager 不能同步刷新的问题。设想一下你在删除 Tab 的同时希望删除该 Tab 下 ViewPager 中的相应页面，这时你修改 ViewPager 的数据源，然后调用 ViewPager Adapter 的 notifyDataSetChanged()（ListView 中是这个思路，并且没什么问题），你会发现结果并不是你料想的那般。

                 对于该问题，不是一两句话可以说清楚的，你可以 Google 一下“ViewPager notifyDataSetChanged 无效”，有一大堆文章在讨论这个问题，但是都不能覆盖所有场景。所以，打算专门写一篇文章来讨论这个问题。

                 3、关于 setupWithViewPager 方法
                 在设置 TabLayout 与 ViewPager 联动的时候，有一种方式是，分别调用 ViewPager 的 addOnPageChangeListener() 方法关联 TabLayout，和 TabLayout 的 setOnTabSelectedListener() 方法关联 ViewPager；这样才能达到联动的目的。

                 还有一种方式是通过 TabLayout 的 setupWithViewPager() 方法，刚开始以为直接调用该方法即可完成联动（就是第一种方式的其他代码都不动，只是删除关联用到的两个方法）。试了一下，结果发现 Tab 的文本没有显示出来，下面的线条有了，而且可以联动。坑！有没有，我没打算去分析里面的原因了，直接给出正确的添加 Tab 的方式，注意是调用 setupWithViewPager() 方法的时候：

                 private void initData() {
                     int size = mTitleArr.length;
                     mViewList = new ArrayList<>(size);
                     // 初始化 View 列表
                     for (int i = 0; i < size; i++) {
                     View view = LayoutInflater.from(this).inflate(R.layout.item_tablayoutdemo2_vp, null);
                     TextView tv = (TextView) view.findViewById(R.id.tv_text);
                     tv.setText(mTitleArr[i]);
                     mViewList.add(view);
                 }
                 mAdapter = new TabLayoutDemo2PagerAdapter(mViewList);

                 // 填充 ViewPager
                 mContentVP.setAdapter(mAdapter);
                 mTitleTL.setupWithViewPager(mContentVP);
                 size = mTitleTL.getTabCount();
                 for (int i = 0; i < size; i++) {
                     TabLayout.Tab tab = mTitleTL.getTabAt(i);
                     if (tab != null) {
                        tab.setText(mTitleArr[i]);
                     }
                 }
                     mContentVP.setCurrentItem(1);
                 }
            4、关于 setScrollPosition 方法
                 该方法是用来设置 Tab 下那条线的位置的，来看下方法签名：

                 public void setScrollPosition(int position, float positionOffset, boolean updateSelectedText)
                 这里想说明的问题是，该方法使用的时候容易引起误会，对第二个参数不理解。

                 第一个参数表示当前 Tab 的位置，第二个参数是偏移值，该值的取值范围是0到1的一个半开区间，最后一个参数表示是否置移动后位置所对应的Tab为选中状态。这里要说明的是 positionOffset 这个参数是一个倍数值，这里不打算分析其实现，有兴趣的可以去看看这篇文章TabLayout：另一种Tab的实现方式，里面解释得不错。

            5、setTabGravity 方法
                 上面介绍了该方法的作用，要注意的是 setTabGravity 方法只有在 Mode 为 MODE_FIXED 的情况下才有用。

            6、setTabTextColors 方法无效？
                 setTabTextColors 当然不是说真的无效，场景是这样的，如果你在 addTab 方法之后，再来调用该方法，这时会看到，该方法没有起作用（TabLayout 的 Bug 有没有？）。

                 当然这也不是什么无解的问题，我们完全可以通过使用自定义 View 的来作为 TabLayout 的 Item，那么这个控制权就全部在我们手上了。

            小结
                 TabLayout 的介绍就到这里了，使用比较简单。

五、NavigationView
六、CoordinatorLayout
     简介
         CoordinatorLayout 命名中带有协调的意思，它的作用正是协调(Coordinate)其他组件, 实现联动，CoordinatorLayout 实现了多种 Material Design 中提到的滚动效果。
         超级 FrameLayout
         CoordinatorLayout 使用新的思路通过协调调度子布局的形式实现触摸影响布局的形式产生动画效果
      提供了几种不用写动画代码就能工作的动画效果，如下：
         1.让浮动操作按钮上下滑动，为Snackbar留出空间
         2.扩展或者缩小 Toolbar 或者头部，让主内容区域有更多的空间
         3.控制哪个 View 应该扩展还是收缩，以及其显示大小比例，包括视差滚动效果动画

         CoordinatorLayout 继承自 ViewGroup，官方介绍的第一句话就是：“CoordinatorLayout is a super-powered FrameLayout .”。从继承结构来看，收获不大，不过有个地方要注意，那就是它实现了一个接口：NestedScrollingParent。

      简单介绍下 NestedScrolling：

            在 Android L 中提供了一套 API 来支持嵌套（或者说嵌入）的滑动效果（Support V 4 提供了兼容 API），可以称之为嵌套滑动机制（NestedScrolling）。
        通过 NestedScrolling，能实现很多很复杂的滑动效果。
            NestedScrolling 提供了一套父 View 和子 View 滑动交互机制。要完成这样的交互，父 View 需要实现 NestedScrollingParent 接口，而子 View 需要实现 NestedScrollingChild 接口。
            关于 NestedScrolling，有兴趣的可以去看看这篇文章：Android NestedScrolling 实战，这里就不展开了。
            CoordinatorLayout 实现了 NestedScrollingParent 接口，其包裹的子控件如果要想能更好的配合 CoordinatorLayout，
        就需要实现 NestedScrollingChild 接口。

     简单使用
        1、Snackbar 与 FAB 浮动效果

            Snackbar 一般出现在屏幕的底部，这容易覆盖住靠近底部的 FAB（FloatingActionButton）。为了给 Snackbar 留出空间，FAB 需要向上移动。

            实现这种效果很简单（其实在 Snackbar 中已经用了，这里再稍微说两句），只要将 FAB 放到 CoordinatorLayout 布局中，FAB 就将自动产生向上移动的动画。FAB 有一个默认的 behavior来检测 Snackbar 的添加并让按钮在 Snackbar 之上呈现上移与 Snackbar 等高的动画。

               看代码：

                 <?xml version="1.0" encoding="utf-8"?>
                     <android.support.design.widget.CoordinatorLayout
                     xmlns:android="http://schemas.android.com/apk/res/android"
                     xmlns:app="http://schemas.android.com/apk/res-auto"
                     android:id="@+id/cl_root"
                     android:layout_width="match_parent"
                     android:layout_height="match_parent">
                 <!-- 任何其他布局 -->
                 <android.support.design.widget.FloatingActionButton
                     android:layout_width="wrap_content"
                     android:layout_height="wrap_content"
                     android:layout_gravity="bottom|right"
                     android:layout_margin="16dp"
                     android:clickable="true"
                     android:onClick="onClick"
                     android:src="@mipmap/ic_check_white"
                     app:layout_anchorGravity="bottom|right|end"/>
                 </android.support.design.widget.CoordinatorLayout>

                 // 点击回调
                     public void onClick(View v) {
                         Snackbar snackbar = Snackbar.make(mRootCL,
                         "我是普通 Snackbar", Snackbar.LENGTH_SHORT);
                         snackbar.show();
                     }
          2、Toolbar 的显示与隐藏

                     为了让 Toolbar 能够响应滚动事件，这里要用到一个新控件 AppBarLayout，该控件会在下面介绍，这里先不讨论，直接用。

                 来看 Layout 中是如何使用的：

                 <?xml version="1.0" encoding="utf-8"?>
                 <android.support.design.widget.CoordinatorLayout
                     xmlns:android="http://schemas.android.com/apk/res/android"
                     xmlns:app="http://schemas.android.com/apk/res-auto"
                     android:id="@+id/cl_root"
                     android:layout_width="match_parent"
                     android:layout_height="match_parent">
                     <android.support.design.widget.AppBarLayout
                         android:id="@+id/appbar"
                         android:layout_width="match_parent"
                         android:layout_height="wrap_content"
                         android:theme="@style/ThemeOverlay.AppCompat">
                     <android.support.v7.widget.Toolbar
                         android:id="@+id/tb_title"
                         android:layout_width="match_parent"
                         android:layout_height="wrap_content"
                         android:minHeight="?attr/actionBarSize"
                         android:background="@color/colorPrimary"
                         app:layout_scrollFlags="scroll|enterAlways" />
                     </android.support.design.widget.AppBarLayout>
                     <android.support.v7.widget.RecyclerView
                         android:id="@+id/rv_content"
                         android:layout_width="match_parent"
                         android:layout_height="match_parent"
                         app:layout_behavior="@string/appbar_scrolling_view_behavior"/>
                     <android.support.design.widget.FloatingActionButton
                         android:layout_width="wrap_content"
                         android:layout_height="wrap_content"
                         android:layout_gravity="bottom|right"
                         android:layout_margin="16dp"
                         android:clickable="true"
                         android:onClick="onClick"
                         android:src="@mipmap/ic_check_white"
                         app:layout_anchor="@id/rv_content"
                         app:layout_anchorGravity="bottom|right|end"/>
                 </android.support.design.widget.CoordinatorLayout>
            首先，让 AppBarLayout 作为 CoordinatorLayout 的第一个子 View，将 Toolbar 包裹在其中。
             AppBarLayout 里面定义的 Toolbar 需要设置 app:layout_scrollFlags 属性，
             app:layout_scrollFlags 属性里面必须至少启用scroll 这个 flag，这样这个 View 才会滚动出屏幕，否则它将一直固定在顶部。

           app:layout_scrollFlags 属性可以使用如下 flag：

             scroll：所有想滚动出屏幕的 View 都需要设置这个 flag，没有设置这个 flag 的 View 将被固定在屏幕顶部
             enterAlways：一旦向上滚动这个 View 就可见，这个 flag 让任意向下的滚动都会导致该 View 变为可见，启用快速“返回模式”
             enterAlwaysCollapsed：顾名思义，这个 flag 定义的是何时进入（已经消失之后何时再次显示）。假设你定义了一个最小高度（minHeight）同时 enterAlways 也定义了，那么 View 将在到达这个最小高度的时候开始显示，并且从这个时候开始慢慢展开，当滚动到顶部的时候展开完
             exitUntilCollapsed：同样顾名思义，这个 flag 定义何时退出，当你定义了一个 minHeight，这个 View 将在滚动到达这个最小高度的时候消失。
             特别注意：所有使用 scroll flag 的 View 都必须定义在没有使用 scroll flag 的 View 前面，这样才能确保所有的 View 从顶部退出，留下固定的元素。

             PS：CoordinatorLayout 还提供了 layout_anchor 和 layout_anchorGravity 属性一起配合使用，可以用于放置 floating view，比如FloatingActionButton 与其他 View 的相对位置。
             （参考自：Android应用Design Support Library完全使用实例）

             然后，定义 AppBarLayout 与 RecyclerView 之间的联系（可以使用任意支持嵌套滚动的 View 都可以，在这里使用了 RecyclerView 来演示）。在 RecyclerView 中添加属性 app:layout_behavior。

             support library 包含了一个特殊的字符串资源 @string/appbar_scrolling_view_behavior，它和 AppBarLayout.ScrollingViewBehavior 相匹配，用来通知 AppBarLayout 这个特殊的 View 何时发生了滚动事件，这个 Behavior 需要设置在触发事件（滚动）的 View 之上。
             （参考自：Android应用Design Support Library完全使用实例）

             最后，其实在完成上面两步之后，就已经完成了 Toolbar 随着 RecyclerView 显示隐藏的功能，剩下的就只需要为 RecyclerView 填充数据即可：

                 private void initData() {
                     LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                     layoutManager.setOrientation(OrientationHelper.VERTICAL);
                     // 设置布局管理器
                     mContentRV.setLayoutManager(layoutManager);
                     ArrayList dataList = new ArrayList<>(100);
                     for (int i = 0; i < 100; i++) {
                         dataList.add("DIY-ITEM:" + i);
                     }
                     RecyclerAdapter adapter = new RecyclerAdapter(dataList);
                     mContentRV.setAdapter(adapter);
                 }
        为了使 Toolbar 有滑动效果，必须做到如下四点：

                 CoordinatorLayout 作为布局的父布局容器
                 添加 AppBarLayout
                 给需要滑动的组件设置 app:layout_scrollFlags=”scroll|enterAlways” 属性
                 给滑动的组件设置 app:layout_behavior 属性
                 （参考自：android CoordinatorLayout使用）

        小结
             CoordinatorLayout 还可以结合其他控件实现很多很炫的效果，接下来要介绍的这两个控件就需要和 CoordinatorLayout，所以，这里不继续展开 CoordinatorLayout 的相关内容了。

七、AppBarLayout

     简介
         AppBarLayout 是 Design Support Library 中提供的一个容器控件，是为了 Material Design 设计的 App Bar。

         MD风格的滑动Layout
         把容器内的组件全部作为 AppBar
         就是一个 纯容器类，配合 ToolBar 与 CollapsingToolbarLayout 等使用

         继承自 LinearLayout，并且默认是垂直方向的 —— “ AppBarLayout is a vertical LinearLayout ...”。
         从官方的示例代码中可以看到，它的使用方式和 LinearLayout 没什么区别。
         当 RecyclerView 滑动的时候，Toolbar 并没有任何反应。AppBarLayout 与 LinearLayout 在这里没什么区别，所以一般也不会单独使用 AppBarLayout。

     上代码：

         <?xml version="1.0" encoding="utf-8"?>
         <RelativeLayout
             xmlns:android="http://schemas.android.com/apk/res/android"
             xmlns:app="http://schemas.android.com/apk/res-auto"
             android:id="@+id/rl_root"
             android:layout_width="match_parent"
             android:layout_height="match_parent">
             <android.support.design.widget.AppBarLayout
                 android:id="@+id/appbar"
                 android:layout_width="match_parent"
                 android:layout_height="wrap_content"
                 android:theme="@style/ThemeOverlay.AppCompat">
                 <android.support.v7.widget.Toolbar
                     android:id="@+id/tb_title"
                     app:layout_scrollFlags="scroll|enterAlways"
                     android:layout_width="match_parent"
                     android:layout_height="wrap_content"
                     android:background="@color/colorPrimary"
                     android:minHeight="?attr/actionBarSize" />
                 <android.support.design.widget.TabLayout
                     android:id="@+id/tl_tabs"
                     android:layout_width="match_parent"
                     android:layout_height="wrap_content" />
             </android.support.design.widget.AppBarLayout>
             <android.support.v7.widget.RecyclerView
                 android:id="@+id/rv_content"
                 app:layout_behavior="@string/appbar_scrolling_view_behavior"
                 android:layout_width="match_parent"
                 android:layout_height="match_parent"
                 android:layout_below="@+id/appbar" />
             <android.support.design.widget.FloatingActionButton
                 app:layout_anchor="@id/rv_content"
                 app:layout_anchorGravity="bottom|right|end"
                 android:layout_width="wrap_content"
                 android:layout_height="wrap_content"
                 android:layout_alignParentBottom="true"
                 android:layout_alignParentRight="true"
                 android:layout_margin="16dp"
                 android:clickable="true"
                 android:onClick="onClick"
                 android:src="@mipmap/ic_check_white" />
         </RelativeLayout>
    2、CoordinatorLayout 与 AppBarLayout配合

         与 1、 中的效果对比，可以发现 AppBarLayout 中的控件可以随着 RecyclerView 的滑动而显示或隐藏。代码和简单：

         <?xml version="1.0" encoding="utf-8"?>
         android.support.design.widget.CoordinatorLayout
             xmlns:android="http://schemas.android.com/apk/res/android"
             xmlns:app="http://schemas.android.com/apk/res-auto"
             android:id="@+id/cl_root"
             android:layout_width="match_parent"
             android:layout_height="match_parent">
             <android.support.design.widget.AppBarLayout
                 android:id="@+id/appbar"
                 android:layout_width="match_parent"
                 android:layout_height="wrap_content"
                 android:theme="@style/ThemeOverlay.AppCompat">
                 <android.support.v7.widget.Toolbar
                     android:id="@+id/tb_title"
                     android:layout_width="match_parent"
                     android:layout_height="wrap_content"
                     android:minHeight="?attr/actionBarSize"
                     android:background="@color/colorPrimary"
                     app:layout_scrollFlags="scroll|enterAlways" />
                 <android.support.design.widget.TabLayout
                     android:id="@+id/tl_tabs"
                     android:layout_width="match_parent"
                     android:layout_height="wrap_content" />
             </android.support.design.widget.AppBarLayout>
             <android.support.v7.widget.RecyclerView
                 android:id="@+id/rv_content"
                 android:layout_width="match_parent"
                 android:layout_height="match_parent"
                 app:layout_behavior="@string/appbar_scrolling_view_behavior"/>
             <android.support.design.widget.FloatingActionButton
                 android:layout_width="wrap_content"
                 android:layout_height="wrap_content"
                 android:layout_gravity="bottom|right"
                 android:layout_margin="16dp"
                 android:clickable="true"
                 android:onClick="onClick"
                 android:src="@mipmap/ic_check_white"
                 app:layout_anchor="@id/rv_content"
                 app:layout_anchorGravity="bottom|right|end"/>
         </android.support.design.widget.CoordinatorLayout>
         在 Java 代码里面没什么可说的，这里就不列代码了，有需要的可从文末链接自行下载 Demo。

         注意： AppBarLayout 中的子控件必须设置了 app:layout_scrollFlags 属性，而且该属性至少设置了 ”scroll“（原因前面说过），
                才有可能实现随着 RecyclerView 的滑动而显示隐藏。

    注意事项
         注意 AppBarLayout 的 expanded 属性的使用特性
         就是 ”六、“ 中所讲的，为了使 Toolbar 有滑动效果，必须做到那三点。这里就不再赘述，该控件的使用还是很简单的。
八、CollapsingToolbarLayout

    简介
         CollapsingToolbarLayout，从名称上来看这是一个可折叠的 Toolbar 布局，确实名副其实。它可以控制包含在其内部的控件(如：ImageView、Toolbar)在响应 layout_behavior 事件时作出相应的 scrollFlags 滚动事件(移除屏幕或固定在屏幕顶端)，形成各种视觉效果。

         可折叠MD风格ToolbarLayout
         可以折叠的Toolbar

         继承自 Framelayout，这个哥们最近出镜率很高啊！文档上说，CollapsingToolbarLayout 是 Toolbar 的一个”包装“。不翻译文档了，Framelayout 很熟悉了，但作用不大，来看看 CollapsingToolbarLayout 提供的属性。

    简单使用
         Toolbar 的折叠效果
         要实现上述效果比较简单，主要是在布局中设置：

         <?xml version="1.0" encoding="utf-8"?>
         <android.support.design.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
             xmlns:app="http://schemas.android.com/apk/res-auto"
             android:id="@+id/cl_root"
             android:layout_width="match_parent"
             android:layout_height="match_parent">
             <android.support.design.widget.AppBarLayout
                 android:id="@+id/appbar"
                 android:layout_width="match_parent"
                 android:layout_height="150dp"
                 android:fitsSystemWindows="true"
                 android:theme="@style/ThemeOverlay.AppCompat">
                 <android.support.design.widget.CollapsingToolbarLayout
                     android:id="@+id/ctl_title"
                     android:layout_width="match_parent"
                     android:layout_height="match_parent"
                     android:fitsSystemWindows="true"
                     app:title="DIY-Green"
                     app:contentScrim="?attr/colorPrimary"
                     app:expandedTitleMarginEnd="64dp"
                     app:expandedTitleMarginStart="46dp"
                     app:layout_scrollFlags="scroll|exitUntilCollapsed">
                 <android.support.v7.widget.Toolbar
                     android:id="@+id/tb_title"
                     android:layout_width="match_parent"
                     android:layout_height="?attr/actionBarSize"
                     app:layout_scrollFlags="scroll|enterAlways"/>
                 </android.support.design.widget.CollapsingToolbarLayout>
             </android.support.design.widget.AppBarLayout>
             <android.support.v7.widget.RecyclerView
                 android:id="@+id/rv_content"
                 android:layout_width="match_parent"
                 android:layout_height="match_parent"
                 app:layout_behavior="@string/appbar_scrolling_view_behavior" />
         </android.support.design.widget.CoordinatorLayout>

         首先，使用 CollapsingToolbarLayout 包裹 Toolbar。
         然后，设置 CollapsingToolbarLayout 的app:layout_scrollFlags 属性，如 "scroll|exitUntilCollapsed"。

         注意，Title 要在 CollapsingToolbarLayout 上设置，而不能在 Toolbar 中设置了。
         这个效果看起挺炫的，其实也不难，看代码就明白了：

         <?xml version="1.0" encoding="utf-8"?>
         <android.support.design.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
             xmlns:app="http://schemas.android.com/apk/res-auto"
             android:id="@+id/cl_root"
             android:layout_width="match_parent"
             android:layout_height="match_parent">
             <android.support.design.widget.AppBarLayout
                 android:id="@+id/appbar"
                 android:layout_width="match_parent"
                 android:layout_height="240dp"
                 android:fitsSystemWindows="true"
                 android:theme="@style/ThemeOverlay.AppCompat">
                 <android.support.design.widget.CollapsingToolbarLayout
                     android:id="@+id/ctl_title"
                     android:layout_width="match_parent"
                     android:layout_height="match_parent"
                     android:fitsSystemWindows="true"
                     app:contentScrim="?attr/colorPrimary"
                     app:expandedTitleMarginEnd="64dp"
                     app:expandedTitleMarginStart="46dp"
                     app:layout_scrollFlags="scroll|exitUntilCollapsed">
                 <ImageView
                     android:layout_width="match_parent"
                     android:layout_height="match_parent"
                     android:scaleType="centerCrop"
                     android:fitsSystemWindows="true"
                     android:src="@mipmap/bg_drawer_header"
                     app:layout_collapseMode="parallax" />
                 <android.support.v7.widget.Toolbar
                     android:id="@+id/tb_title"
                     android:layout_width="match_parent"
                     android:layout_height="?attr/actionBarSize"
                     app:layout_collapseMode="pin"
                     app:popupTheme="@style/ThemeOverlay.AppCompat.Light" />
                 </android.support.design.widget.CollapsingToolbarLayout>
             </android.support.design.widget.AppBarLayout>
             <android.support.v7.widget.RecyclerView
                 android:id="@+id/rv_content"
                 android:layout_width="match_parent"
                 android:layout_height="match_parent"
                 app:layout_behavior="@string/appbar_scrolling_view_behavior" />
             <android.support.design.widget.FloatingActionButton
                 android:layout_height="wrap_content"
                 android:layout_width="wrap_content"
                 app:layout_anchor="@id/appbar"
                 app:layout_anchorGravity="bottom|right|end"
                 android:src="@mipmap/ic_check_white"
                 android:layout_margin="@dimen/activity_horizontal_margin"
                 android:clickable="true"/>
         </android.support.design.widget.CoordinatorLayout>
     如示例中所示，为了图片在折叠的时候淡入淡出的效果，需要设置 app:layout_collapseMode 属性为 "parallax"。

     CoordinatorLayout 还提供了一个 layout_anchor 的属性，连同 layout_anchorGravity 一起，可以用来放置与其他视图关联在一起的悬浮视图（如 FloatingActionButton）。（参考自：android CoordinatorLayout使用）

     小结
     CoordinatorLayout 还有很多很炫的功能有待挖掘，这里不打算深入探讨了。还有一个比较重要的类没有介绍，那就是 Behavior，系统提供了一些 Behavior，我们也可以自定义。打算在讨论动画的时候再好好介绍下 Behavior，CoordinatorLayout 的介绍就到这里了。

     对 Android 6.x 的新控件介绍就到这里了。关于有些控件可能不是 Android 6.x 提供这个问题，我这里想稍微说一下。请大家不要在意这些细节，写这一系列文章，我的初衷是想提供一个简明的示例供大家参考，能达到这个目的就足够了。本人能力和时间有限，不足和疏漏之处在所难免，请见谅。

     文／diygreen（简书作者）
     原文链接：http://www.jianshu.com/p/b93ea2312dda
     著作权归作者所有，转载请联系作者获得授权，并标注“简书作者”。
 */
public class DesignSupportLibrary {
}

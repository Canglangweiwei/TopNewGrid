package com.example.topnewgrid;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.topnewgrid.adapter.DragAdapter;
import com.example.topnewgrid.adapter.OtherAdapter;
import com.example.topnewgrid.app.AppApplication;
import com.example.topnewgrid.bean.ChannelItem;
import com.example.topnewgrid.bean.ChannelManager;
import com.example.topnewgrid.view.DragGrid;
import com.example.topnewgrid.view.OtherGridView;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * 频道管理
 */
@SuppressWarnings("ALL")
public class ChannelActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.root_view)
    RelativeLayout drawerLayout;

    /**
     * 用户栏目的GRIDVIEW
     */
    @Bind(R.id.userGridView)
    DragGrid userGridView;

    /**
     * 其它栏目的GRIDVIEW
     */
    @Bind(R.id.otherGridView)
    OtherGridView otherGridView;

    /**
     * 用户栏目对应的适配器，可以拖动
     */
    private DragAdapter userAdapter;

    /**
     * 其它栏目对应的适配器
     */
    private OtherAdapter otherAdapter;

    /**
     * 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。
     */
    private boolean isMove = false;

    @Override
    protected int initContentView() {
        return R.layout.subscribe_activity;
    }

    @Override
    protected void initUi() {
        userAdapter = new DragAdapter(this);
        userGridView.setAdapter(userAdapter);

        otherAdapter = new OtherAdapter(this);
        otherGridView.setAdapter(otherAdapter);
    }

    @Override
    protected void initDatas() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 用户栏目列表
                ArrayList<ChannelItem> userChannelList = ((ArrayList<ChannelItem>) ChannelManager.getManage(
                        AppApplication.get().getSQLHelper()).getUserChannel());

                // 其它栏目列表
                ArrayList<ChannelItem> otherChannelList = ((ArrayList<ChannelItem>) ChannelManager.getManage(
                        AppApplication.get().getSQLHelper()).getOtherChannel());

                userAdapter.setData(userChannelList);
                otherAdapter.setData(otherChannelList);
            }
        }, 1200);
    }

    @Override
    protected void initListener() {
        // 设置GRIDVIEW的ITEM的点击监听
        otherGridView.setOnItemClickListener(this);
        userGridView.setOnItemClickListener(this);
    }

    /**
     * GRIDVIEW对应的ITEM点击监听接口
     */
    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
        // 如果点击的时候，之前动画还没结束，那么就让点击事件无效
        if (isMove) {
            return;
        }
        switch (parent.getId()) {
            case R.id.userGridView:
                // position为 0，1 的不可以进行任何操作
                if (position != 0 && position != 1) {
                    final ImageView moveImageView = getView(view);
                    if (moveImageView != null) {
                        TextView newTextView = (TextView) view
                                .findViewById(R.id.text_item);
                        final int[] startLocation = new int[2];
                        newTextView.getLocationInWindow(startLocation);
                        final ChannelItem channel = ((DragAdapter) parent
                                .getAdapter()).getItem(position);// 获取点击的频道内容
                        otherAdapter.setVisible(false);
                        // 添加到最后一个
                        otherAdapter.addItem(channel);
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    int[] endLocation = new int[2];
                                    // 获取终点的坐标
                                    otherGridView.getChildAt(
                                            otherGridView.getLastVisiblePosition())
                                            .getLocationInWindow(endLocation);
                                    MoveAnim(moveImageView, startLocation, endLocation, userGridView);
                                    userAdapter.setRemove(position);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 50L);
                    }
                }
                break;
            case R.id.otherGridView:
                final ImageView moveImageView = getView(view);
                if (moveImageView != null) {
                    TextView newTextView = (TextView) view
                            .findViewById(R.id.text_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final ChannelItem channel = ((OtherAdapter) parent.getAdapter())
                            .getItem(position);
                    userAdapter.setVisible(false);
                    // 添加到最后一个
                    userAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                // 获取终点的坐标
                                userGridView.getChildAt(
                                        userGridView.getLastVisiblePosition())
                                        .getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation, endLocation, otherGridView);
                                otherAdapter.setRemove(position);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 50L);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 点击ITEM移动动画
     */
    private void MoveAnim(View moveView, int[] startLocation, int[] endLocation, final GridView clickGridView) {
        int[] initLocation = new int[2];
        // 获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        // 得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView,
                initLocation);
        // 创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);                    // 动画时间
        // 动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);               // 动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
                if (clickGridView instanceof DragGrid) {
                    otherAdapter.setVisible(true);
                    otherAdapter.notifyDataSetChanged();
                    userAdapter.remove();
                } else {
                    userAdapter.setVisible(true);
                    userAdapter.notifyDataSetChanged();
                    otherAdapter.remove();
                }
                isMove = false;
            }
        });
    }

    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     */
    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        moveLinearLayout.setLayoutParams(params);
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    /**
     * 获取点击的Item的对应View，
     */
    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    /**
     * 退出时候保存选择后数据库的设置
     */
    private void saveChannel() {
        ChannelManager.getManage(AppApplication.get().getSQLHelper())
                .deleteAllChannel();
        ChannelManager.getManage(AppApplication.get().getSQLHelper())
                .saveUserChannel(userAdapter.getChannnelLst());
        ChannelManager.getManage(AppApplication.get().getSQLHelper())
                .saveOtherChannel(otherAdapter.getChannelLst());
    }

    @Override
    protected void onDestroy() {
        saveChannel();
        super.onDestroy();
    }

    private long newTime;

    /**
     * 监听返回键
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - newTime > 2000) {
            newTime = System.currentTimeMillis();
            Snackbar snackbar = Snackbar.make(drawerLayout, getString(R.string.press_twice_exit), Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.setAction(R.string.exit_directly, new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    AppApplication.get(getApplicationContext()).finishAllActivity();
                }
            });
            snackbar.show();
        } else {
            moveTaskToBack(true);
        }
    }
}
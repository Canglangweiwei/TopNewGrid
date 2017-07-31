package com.example.topnewgrid.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.example.topnewgrid.db.SQLHelper;

import java.util.Stack;

@SuppressWarnings("ALL")
public class AppApplication extends Application {

    private static AppApplication mAppApplication;

    private SQLHelper sqlHelper;

    private Stack<Activity> activityStack;// activity栈

    @Override
    public void onCreate() {
        super.onCreate();
        mAppApplication = this;
    }

    /**
     * 获取Application
     */
    public static AppApplication get() {
        return mAppApplication;
    }

    public static AppApplication get(Context context) {
        return (AppApplication) context.getApplicationContext();
    }

    /**
     * 获取数据库Helper
     */
    public SQLHelper getSQLHelper() {
        if (sqlHelper == null)
            sqlHelper = new SQLHelper(mAppApplication);
        return sqlHelper;
    }

    /**
     * 摧毁应用进程时候调用
     */
    public void onTerminate() {
        if (sqlHelper != null)
            sqlHelper.close();
        super.onTerminate();
    }

    /**
     * 把一个activity压入栈列中
     */
    public void pushActivityToStack(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取栈顶的activity，先进后出原则
     */
    public Activity getLastActivityFromStack() {
        return activityStack.lastElement();
    }

    /**
     * 从栈列中移除一个activity
     */
    public void popActivityFromStack(Activity activity) {
        if (activityStack != null && activityStack.size() > 0) {
            if (activity != null) {
                activity.finish();
                activityStack.remove(activity);
            }
        }
    }

    /**
     * 退出所有activity
     */
    public void finishAllActivity() {
        if (activityStack != null) {
            while (activityStack.size() > 0) {
                Activity activity = getLastActivityFromStack();
                if (activity == null) {
                    break;
                }
                popActivityFromStack(activity);
            }
        }
    }
}

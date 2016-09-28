package com.sunc.controller;

import android.os.AsyncTask;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by suncheng on 2016/9/6.
 */
public class BaseController {
    private ConcurrentHashMap<String, AsyncTask> asyncTaskMap = new ConcurrentHashMap<String, AsyncTask>();

    protected <Param, Progress, Result> void doAsyncTask(
            final String taskKey,
            final UpdateViewAsyncCallback<Result> updateViewAsyncCallback,
            final DoAsyncTaskCallback<Param, Result> doAsyncTaskCallback,
            Param... param) {
        if (null == updateViewAsyncCallback || taskKey == null) {
            return;
        }

        AsyncTask<Param, Void, Result> asyncTask = new AsyncTask<Param, Void, Result>() {
            private Exception ie = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                updateViewAsyncCallback.onPreExecute();
            }

            @Override
            protected Result doInBackground(Param... params) {
                Result result = null;
                try {
                    result = doAsyncTaskCallback.doAsyncTask(params);
                } catch (Exception ie) {
                    this.ie = ie;
                }
                return result;
            }

            @Override
            protected void onPostExecute(Result result) {
                super.onPostExecute(result);
                if (null == ie) {
                    updateViewAsyncCallback.onPostExecute(result);
                } else {
                    updateViewAsyncCallback.onException(ie);
                    ie = null;
                }
                asyncTaskMap.remove(taskKey);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                updateViewAsyncCallback.onCancelled();
            }

        };
        cancel(taskKey);
        asyncTaskMap.put(String.valueOf(taskKey), asyncTask);
        asyncTask.execute(param);
    }

    public void cancel(String asyncTaskKey) {
        if (asyncTaskMap.containsKey(asyncTaskKey)) {
            asyncTaskMap.get(asyncTaskKey).cancel(true);
            asyncTaskMap.remove(asyncTaskKey);
        }
    }

    public void cancelAllTasks() {
        Set<Map.Entry<String, AsyncTask>> entrySet= asyncTaskMap.entrySet();
        Iterator<Map.Entry<String, AsyncTask>> it = entrySet.iterator();
        while(it.hasNext())
        {
            Map.Entry<String, AsyncTask> entry = it.next();
            AsyncTask task = entry.getValue();
            if(task != null)
            {
                task.cancel(true);
            }
            it.remove();
        }
    }

    /**
     * 需要controller层实现的线程执行事务的回调接口
     *
     * @author Bob
     *
     * @param <Param>
     * @param <Result>
     */
    public interface DoAsyncTaskCallback<Param, Result> {
        public abstract Result doAsyncTask(Param... params) throws Exception;
    }

    /**
     * 全功能线程处理View层Controller参数中实现该接口
     *
     * @author Bob
     *
     * @param <Result>
     */
    public interface UpdateViewAsyncCallback<Result> {
        public abstract void onPreExecute();
        public abstract void onPostExecute(Result result);
        public abstract void onCancelled();
        public abstract void onException(Exception ie);
    }

    /**
     * 常规线程处理时View层Controller参数中实现该抽象类 只需实现onPostExecute和onException
     *
     * @author Bob
     *
     */
    public abstract static class CommonUpdateViewAsyncCallback<Result>
            implements UpdateViewAsyncCallback<Result> {
        @Override
        public void onPreExecute() {
        };

        @Override
        public void onCancelled() {
        };
    }

    public class NullCallback extends CommonUpdateViewAsyncCallback<Void> {
        @Override
        public void onPostExecute(Void articles) {
        }

        @Override
        public void onException(Exception ie) {
        }
    }

}

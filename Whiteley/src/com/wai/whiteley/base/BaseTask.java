package com.wai.whiteley.base;

import android.os.AsyncTask;

public class BaseTask extends AsyncTask<Integer, Object, Object> {
	
	protected TaskListener mListener = null;
	protected int mTaskId = 0;
	private boolean mIsRunning = false;
	private Object mData = null;

	public BaseTask() {
		super();
	}

	public BaseTask(int taskId) {
		super();
		this.mTaskId = taskId;
	}

	public BaseTask(int taskId, Object data) {
		super();
		this.mTaskId = taskId;
		this.mData = data;
	}

	public static void run(TaskListener listener) {
		run(listener, null);
	}

	public static void run(TaskListener listener, Object data) {
		BaseTask task = new BaseTask();
		task.setListener(listener);
		task.setData(data);
		task.execute();
	}

	public void setListener(TaskListener listener) {
		mListener = listener;
	}

	public void setData(Object data) {
		mData = data;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mListener != null) {
			mListener.onTaskPrepare(mTaskId, mData);
		}
	}

	@Override
	protected Object doInBackground(Integer... params) {
		mIsRunning = true;
		Object result = doRunning();
		mIsRunning = false;
		return result;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if (mListener != null) {
			mListener.onTaskCancelled(mTaskId);
			mListener = null;
		}
		mIsRunning = false;
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);

		if (mListener != null) {
			mListener.onTaskResult(mTaskId, result);
			mListener = null;
		}

		mIsRunning = false;
	}

	@Override
	protected void onProgressUpdate(Object... values) {
		super.onProgressUpdate(values);
		if (mListener != null) {
			mListener.onTaskProgress(mTaskId, values);
		}
	}

	public void progress(Object... values) {
		publishProgress(values);
	}

	public void release() {
		if (mIsRunning) {
			this.cancel(false);
			mIsRunning = false;
		}
	}

	protected Object doRunning() {
		if (mListener != null) {
			return mListener.onTaskRunning(mTaskId, mData);
		}
		return null;
	}

	public static interface TaskListener {
		public void onTaskPrepare(int taskId, Object data);
		public Object onTaskRunning(int taskId, Object data);
		public void onTaskProgress(int taskId, Object... values);
		public void onTaskResult(int taskId, Object result);
		public void onTaskCancelled(int taskId);
	}

	public static class TaskAdapter implements TaskListener {
		public void onTaskPrepare(int taskId, Object data) {}
		public Object onTaskRunning(int taskId, Object data) { return null; }
		public void onTaskProgress(int taskId, Object... values) {}
		public void onTaskResult(int taskId, Object result) {}
		public void onTaskCancelled(int taskId) {}
	}

}

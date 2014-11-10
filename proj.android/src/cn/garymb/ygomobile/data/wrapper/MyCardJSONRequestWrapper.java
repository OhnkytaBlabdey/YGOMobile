package cn.garymb.ygomobile.data.wrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import cn.garymb.ygomobile.common.Constants;

public abstract class MyCardJSONRequestWrapper extends BaseRequestWrapper {

	private static final String TAG = "CardImageRequestWrapper";

	public MyCardJSONRequestWrapper(int requestType) {
		super(requestType);
	}

	@Override
	public int parse(Object in) {
		int result = TASK_STATUS_FAILED;
		if (in instanceof InputStream) {
			InputStream data = (InputStream) in;
			result = TASK_STATUS_SUCCESS;
			StringBuilder out = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(data));
			int len = -1;
			char[] buffer = new char[Constants.IO_BUFFER_SIZE];
			try {
				while ((!Thread.currentThread().isInterrupted() && (len = reader
						.read(buffer, 0, Constants.IO_BUFFER_SIZE)) != -1))
					out.append(buffer, 0, len);
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException();
				}
				Log.d(TAG, out.toString());
				handleJSONResult(new JSONObject(out.toString()));
			} catch (IOException e) {
				e.printStackTrace();
				result = TASK_STATUS_FAILED;
			} catch (InterruptedException e) {
				e.printStackTrace();
				result = TASK_STATUS_CANCELED;
			} catch (JSONException e) {
				e.printStackTrace();
				result = TASK_STATUS_FAILED;
			} finally {
				buffer = null;
				if (data != null) {
					try {
						data.close();
					} catch (IOException e) {
					}
				}
				out.delete(0, out.length());
				out = null;
				System.gc();
			}
		}
		return result;
	}
	
	protected abstract void handleJSONResult(JSONObject object) throws JSONException;
}

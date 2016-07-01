package com.weather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import android.util.Log;

public class HttpUtil {
	private static final String TAG = "HttpUtil";
	
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection = null;
				try{
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream inputStream = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					StringBuilder response = new StringBuilder();
					String line ;
					//Log.d(TAG,response.toString());
					while((line = reader.readLine()) != null){
						response.append(line);
					}
					if (listener != null){
						listener.onFinish(response.toString());
					}
				}catch(Exception e){
					if (listener != null){
						Log.e(TAG, e.toString());
						//Log.e(TAG, e.getMessage());
						listener.onError(e);
					}
				}finally{
					if (connection != null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}

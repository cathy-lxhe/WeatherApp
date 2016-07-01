package com.weather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.weather.model.City;
import com.weather.model.County;
import com.weather.model.MyWeatherDB;
import com.weather.model.Province;

public class Utility {
	
	private final static String TAG = "Utility";
	/**
	 * �����ʹ�����������ص�ʡ������
	 * �������򣺰�","�ָȻ���ٰ�"|"�ָ���Ž������������������õ�ʵ�����У�������myWeatherDB�е�����save()���������ݴ洢�����ݱ���
	 */
	public synchronized static boolean handleProvincesResponse(MyWeatherDB myWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			if(allProvinces != null && allProvinces.length>0){
				for(String p: allProvinces){
					String[] array = p.split("\\|");
					Log.d("province", p+ " "+array[0]);
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					myWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �����ʹ�����������ص��м�����
	 */
	public static boolean handleCitiesResponse(MyWeatherDB myWeatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			
			if(allCities != null && allCities.length >0){
				for(String c:allCities){
					String[] arrayStrings = c.split("\\|");
					Log.d("city", c+ " "+arrayStrings[0]);
					City city = new City();
					city.setCityCode(arrayStrings[0]);
					city.setCityName(arrayStrings[1]);
					city.setProvinceId(provinceId);
					Log.d("SetCityCode", city.getCityCode());
					myWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �����ʹ�����������ص��м�����
	 */
	public static boolean handleCountiesResponse(MyWeatherDB myWeatherDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			Log.d("county:", response);
			if(allCounties != null && allCounties.length >0){
				for(String c:allCounties){
					String[] arrayStrings = c.split("\\|");
					County county = new County();
					county.setCountyCode(arrayStrings[0]);
					county.setCountyName(arrayStrings[1]);
					county.setCityId(cityId);
					myWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �������������ص�JSON���ݣ����������������ݴ洢�����ء�
	 */
	public static void handleWeatherResponse(Context context, String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			/**
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			*/
			JSONObject weatherInfo = jsonObject.getJSONArray("HeWeather data service 3.0").getJSONObject(0);
			JSONObject basicInfo = weatherInfo.getJSONObject("basic");
			String cityName = basicInfo.getString("city");
			String weatherCode = basicInfo.getString("id").substring(2);
			Log.d(TAG,"weatherCode:"+weatherCode);
			
			JSONArray desArray = weatherInfo.getJSONArray("daily_forecast");
			JSONObject tempInfo = desArray.getJSONObject(0).getJSONObject("tmp");
			String temp1 = tempInfo.getString("min")+"��";
			String temp2 = tempInfo.getString("max")+"��";
			Log.d(TAG,"tmp:"+temp1);
			
			JSONObject despInfo = desArray.getJSONObject(0).getJSONObject("cond");
			String weatherDesp = despInfo.getString("txt_d");
			JSONObject updataInfo = basicInfo.getJSONObject("update");
			
			String publishTime =updataInfo.getString("loc").substring(11);
			
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
					weatherDesp, publishTime);
			Log.d(TAG, "city:"+cityName+" weather:"+weatherDesp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �����������ص�����������Ϣ�洢��SharedPreferences�ļ��С�
	 */
	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}

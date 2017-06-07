package com.quanjiakanuser.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.quanjiakanuser.util.GsonParseUtil;
import com.androidquanjiakan.util.SignatureUtil;

import android.content.Context;
import android.util.Log;


/**
 * @author Administrator
 *
 */
public class CacheManager {
	
	private static CacheManager cacheManager;
	private static Map<String, SoftReference<String>> map;
	private static Context context;
	
	static final String TAG_STRING = CacheManager.class.getSimpleName();
	
	
	public static CacheManager getInstance(){
		if(cacheManager == null || map == null){
			synchronized (CacheManager.class) {
				if(cacheManager == null || map == null){
					cacheManager = new CacheManager();
				}
			}
		}
		return cacheManager;
	}
	
	public static void init(Context _context){
		context = _context;
	}
	
	private CacheManager(){
		if(map == null){
			map = new HashMap<String, SoftReference<String>>();
		}
	}
	
	private CacheManager(Context _context){
		if(context == null ){
			context = _context;
			if(map == null){
				map = new HashMap<String, SoftReference<String>>();
			}
		}
	}
	
	public static CacheManager getInstance(Context _context){
		if(cacheManager == null || context == null){
			synchronized (CacheManager.class) {
				if(cacheManager == null || context == null){
					cacheManager = new CacheManager(_context);
				}
			}
		}
		return cacheManager;
	}
	
	
	
	/**
	 * @param path
	 * @param value
	 * ���뻺�����?
	 */
	public void put2SF(String path,String value){
		if(value == null || value.length() == 0)
			return;
		String key = SignatureUtil.getMD5String(path);
		SoftReference<String> sf = new SoftReference<String>(value);
		map.put(key, sf);
//		System.out.println("putsoftreference:"+path+"\nresult:"+value);
	}
	
	
	/**
	 * @param path
	 * @return
	 * ��ȡ�������?
	 */
	public String getBySF(String path){
		String key = SignatureUtil.getMD5String(path);
		SoftReference<String> sf = map.get(key);
		if(sf != null){
			String result = sf.get();
//			System.out.println("softreference:"+path+"\nresult:"+result);
			if(result == null){
				return "";
			}else{
				return result;
			}
		}else {
			return "";
		}
	}	
	
	
	
	
	/**
	 * ����ȡ����������ݱ��浽�����ļ���?
	 * @param url
	 * @param result
	 */
	public void putStringCache(String url,String result){
		if(result == null || result.length() == 0)
			return;
		String filename = SignatureUtil.getMD5String(url);
		try {
			FileOutputStream ous = context.openFileOutput(filename, Context.MODE_PRIVATE);
			ous.write(result.getBytes());
			ous.flush();
			ous.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			Log.e(TAG_STRING, e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			Log.e(TAG_STRING, e.toString());
		}
		
	}
	
	/**
	 * jsonlist 2 string
	 * @param key
	 * @param jsons
	 */
	public void putArrayListJson(String key,List<JsonObject> jsons){
		JsonArray array = new JsonArray();
		for (int i = 0; i < jsons.size(); i++) {
			array.add(jsons.get(i));
		}
		if(array.size() > 0){
			putStringCache("qjk_problem_id_"+key, array+"");
		}
	}
	
	/**
	 * string 2 jsonlist
	 * @param key
	 */
	public List<JsonObject> getArrayListJson(String key){
		String cache = getStringCache("qjk_problem_id_"+key);
		if(cache.equals("")){
			return new ArrayList<JsonObject>();
		}else {
			JsonArray array = new GsonParseUtil(cache).getJsonArray();
			List<JsonObject> objects = new ArrayList<>();
			for (int i = 0; i < array.size(); i++) {
				objects.add(array.get(i).getAsJsonObject());
			}
			return objects;
		}
	}
	
	
	/**
	 * ��ȡ�����б�������
	 * @param url
	 * @return
	 * str
	 */
	public String getStringCache(String url){
		if(context == null){
			return "";
		}
		String filename = SignatureUtil.getMD5String(url);
		try {
			String filepath = "data/data/" + context.getPackageName() + "/files/"+filename;
			if(!new File(filepath).exists()){
				return "";
			}
			FileInputStream ins = context.openFileInput(filename);
			byte[] bytes = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while(ins.read(bytes) != -1){
				baos.write(bytes, 0, bytes.length);
			}
			ins.close();
			baos.close();
			String str = new String(baos.toByteArray());
			return str;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e(TAG_STRING, e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG_STRING, e.toString());
		}
		return "";		
	}
	
	
	public String get(String URL){
		String valueOfSF = getBySF(URL);
		if(valueOfSF.length() == 0){
			return getStringCache(URL);
		}else {
			return valueOfSF;
		}
	}
	
	
}

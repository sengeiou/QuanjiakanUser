package com.quanjiakanuser.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class GsonParseUtil {

	JsonElement element = null;
	
	public GsonParseUtil(String json){
		JsonReader reader = new JsonReader(new StringReader(json));
		reader.setLenient(true);
		element = new JsonParser().parse(reader);
	}
	
	
	public JsonObject getJsonObject(){
		if(element != null && element.isJsonObject()){
			return element.getAsJsonObject();
		}else {
			return new JsonObject();
		}
	}
	
	public JsonArray getJsonArray(){
		if(element != null && element.isJsonArray()){
			return element.getAsJsonArray();
		}else {
			return new JsonArray();
		}
	}
	
	
	public JsonObject getJsonObject(String key){
		if(element != null && element.isJsonObject()){
			JsonObject object = element.getAsJsonObject();
			return object.get(key).getAsJsonObject();
		}else {
			return new JsonObject();
		}
	}
	
	public JsonArray getJsonArray(String key){
		if(element != null && element.isJsonObject()){
			JsonObject object = element.getAsJsonObject();
			return object.get(key).getAsJsonArray();
		}else {
			return new JsonArray();
		}
	}
	
	public boolean hasKey(String key){
		if(element != null && element.isJsonObject()){
			JsonObject object = element.getAsJsonObject();
			return object.has(key);
		}else {
			return false;
		}
	}
	
	public int getAsInt(String key){
		JsonObject object = element.getAsJsonObject();
		return object.get(key).getAsInt();
	}
	
	public String getAsString(String key){
		JsonObject object = element.getAsJsonObject();
		return object.get(key).getAsString();
	}
	
	public long getAsLong(String key){
		JsonObject object = element.getAsJsonObject();
		return object.get(key).getAsLong();
	}

	public List<JsonObject> getJsonList() {
		List<JsonObject> mList = new ArrayList<>();
		JsonArray array = getJsonArray();
		for (int i = 0; i < array.size(); i++) {
			mList.add(array.get(i).getAsJsonObject());
		}
		return mList;
	}
	
}

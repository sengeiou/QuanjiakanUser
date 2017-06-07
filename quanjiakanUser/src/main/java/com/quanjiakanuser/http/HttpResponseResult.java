package com.quanjiakanuser.http;

import java.io.StringReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.androidquanjiakan.util.LogUtil;

public class HttpResponseResult {

	private String code;
	private JsonElement data;
	private String sign;
	private String version;
	private String token;
	private String message;
	private JsonObject object;
	
	public HttpResponseResult(String val){
//		LogUtil.e("HttpResult:val-"+val);
		if(!val.equals("")){
			JsonReader reader = new JsonReader(new StringReader(val));
			reader.setLenient(true);
			if(!isJson(val)){
				setCode("500");
			}else {
				JsonObject object = new JsonParser().parse(reader).getAsJsonObject();
				setObject(object);
				setCode(object.get("code").getAsString());
				if(object.has("data")){
					setData(object.get("data"));
				}
				if(object.has("message")){
					this.message = object.get("message").getAsString();
				}
				if(object.has("object")){
					setObject(object.get("object").getAsJsonObject());
				}
			}
		}else {
			setCode("500");
		}
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public JsonElement getData() {
		return data;
	}


	public void setData(JsonElement data) {
		this.data = data;
	}


	public String getSign() {
		return sign;
	}


	public void setSign(String sign) {
		this.sign = sign;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}
	
	public JsonObject getDataAsJsonObject(){
		if(data == null){
			return new JsonObject();
		}else {
			return data.getAsJsonObject();
		}
	}
	
	public String getDataAsString(){
		if(data == null){
			return "";
		}else {
			return data.getAsString();
		}
	}
	
	public JsonArray getDataAsJsonArray(){
		if(data == null){
			return new JsonArray();
		}else {
			return data.getAsJsonArray();
		}
	}
	
	public String getMessage(){
		return this.message;
	}
	
	private boolean isJson(String string){
		if (string.equals("")) {
			return false;
		}
		try {
			JsonReader reader = new JsonReader(new StringReader(string));
			reader.setLenient(true);
			JsonElement element = new JsonParser().parse(reader);
			if(element.isJsonArray() || element.isJsonObject()){
				return true;
			}else {
				return false;
			}
		} catch (JsonParseException e) {
			return false;
		}
	}
	
	
	
	public JsonObject getObject() {
		return object;
	}


	public void setObject(JsonObject object) {
		this.object = object;
	}


	public boolean isResultOk(){
		return getCode().equals("200");
	}
	
}

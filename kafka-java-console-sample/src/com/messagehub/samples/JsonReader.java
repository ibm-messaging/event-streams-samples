package com.messagehub.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {

	  private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }

	  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      JSONObject json;
	      
	      if(jsonText.startsWith("{")) json = new JSONObject(jsonText);
	      else if(jsonText.startsWith("[")) {
	    	  JSONArray jsonArray = new JSONArray(jsonText);
	    	  json = jsonArray.getJSONObject(0);
	      }
	      else json = null;
	      
	      return json;
	    } finally {
	      is.close();
	    }
	  }
	}
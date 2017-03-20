package com.github.jptx1234.loginHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Config {
	public static volatile long secondBeforeReconnect = 5;
	private static HashMap<String, List<String[]>> msgListMap = new HashMap<String, List<String[]>>();
	private static File configFile =new File("config/LoginHelper.json"); 
	
	
	public static List<String[]> getMsgList(String serverIP){
		readConfig();
		List<String[]> list =msgListMap.get(serverIP);
		return list == null ? new ArrayList<String[]>() : list;
	}
	
	public static void setMsgList(String serverIP,List<String[]> msgList){
		msgListMap.put(serverIP, msgList);
		saveConfig();
	}
	
	
	private static void saveConfig(){
		HashMap<String, HashMap<String, List<String[]>>> configMap = new HashMap<String, HashMap<String,List<String[]>>>();
		HashMap<String, List<String[]>> secondBeforeReconnectMap = new HashMap<String, List<String[]>>();
		ArrayList<String[]> secondBeforeReconnectList = new ArrayList<String[]>();
		secondBeforeReconnectList.add(new String[]{String.valueOf(secondBeforeReconnect)});
		secondBeforeReconnectMap.put("global", secondBeforeReconnectList);
		configMap.put("countDown", secondBeforeReconnectMap);
		configMap.put("msg", msgListMap);
		Gson gson = new Gson();
		String jsonString = gson.toJson(configMap);
		BufferedWriter bw = null;
		try {
			if (!configFile.exists()) {
				configFile.createNewFile();
			}
			bw = new BufferedWriter(new FileWriter(configFile));
			bw.write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void readConfig(){
		Gson gson = new Gson();
		String jsonString = "";
		if (configFile.exists()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(configFile));
				StringBuilder sb = new StringBuilder();
				String readed;
				while ((readed = br.readLine()) != null) {
					sb.append(readed);
					sb.append("\n");
				}
				jsonString = sb.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		HashMap<String, HashMap<String, List<String[]>>> configMap = gson.fromJson(jsonString,new TypeToken<HashMap<String, HashMap<String, List<String[]>>>>(){}.getType() );
		try {
			secondBeforeReconnect = Long.valueOf(configMap.get("countDown").get("Global").get(0)[0]);
		} catch (Exception e) {
			secondBeforeReconnect = 5;
		}
		try {
			msgListMap = configMap.get("msg");
		} catch (Exception e) {
			msgListMap = new HashMap<String, List<String[]>>();
		}
	}
	
	
	
}

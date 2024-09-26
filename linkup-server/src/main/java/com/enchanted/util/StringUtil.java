package com.enchanted.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StringUtil {

	public static boolean isEmpty(String str){
		if(str==null||"".equals(str.trim())){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isNotEmpty(String str){
		if((str!=null)&&!"".equals(str.trim())){
			return true;
		}else{
			return false;
		}
	}
	
	public static String formatLike(String str){
		if(isNotEmpty(str)){
			return "%"+str+"%";
		}else{
			return null;
		}
	}
	
	public static List<String> filterWhite(List<String> list){
		List<String> resultList=new ArrayList<String>();
		for(String l:list){
			if(isNotEmpty(l)){
				resultList.add(l);
			}
		}
		return resultList;
	}
	
	public static String stripHtml(String content) {
	    content = content.replaceAll("<p .*?>", "\r\n");
	    content = content.replaceAll("<br\\s*/?>", "\r\n");
	    content = content.replaceAll("\\<.*?>", "");
	    content = content.replaceAll(" ", "");
	    return content;   
	}
	
	public static String genSixRandomNum(){
		Random random = new Random();
		String result="";
		for (int i=0;i<6;i++)
		{
			result+=random.nextInt(10);
		}
		return result;
	}

	public static String getRandomString(int length){
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < length; ++i){
			int number = random.nextInt(2);
			long result = 0;
			switch(number){
				case 0:
					result = Math.round(Math.random() * 25 + 65);
					sb.append(String.valueOf((char)result));
					break;
				case 1:
					sb.append(String.valueOf(new Random().nextInt(10)));
					break;
			}
		}
		return sb.toString();
	}


}

package com.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

public class HttpUtils {
	private static final Logger LOG = Logger.getLogger(HttpUtils.class.getName());

	public static String request(String url, Map<String, String> hear,
			String body, String method) {
		String result = "";
		OutputStream out = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		BufferedReader reader = null;
		try {
			URL url2 = new URL(url);
			LOG.info("========================================================error000000111");
			if (url.startsWith("https")) {
				conn = (HttpsURLConnection) url2.openConnection();
			} else {
				conn = (HttpURLConnection) url2.openConnection();
			}
			if (hear != null && !hear.isEmpty()) {
				for (String key : hear.keySet()) {
					conn.setRequestProperty(key, hear.get(key));
				}
			}
			LOG.info("========================================================error0000000222");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(30000);
			conn.setRequestMethod(method.toUpperCase());
//			conn.setRequestProperty("Accept", "*/*");
//			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; CIBA)");//模拟ie浏览器
//			conn.setRequestProperty("Accept-Language", "zh-cn");

//			// 设置请求头信息
//			conn.setRequestProperty("Connection", "Keep-Alive");
//			conn.setRequestProperty("Charset", "UTF-8");
//			// 设置边界
//			String BOUNDARY = "----------" + System.currentTimeMillis();
//			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; boundary="
//			+ BOUNDARY);
//			conn.connect();
			if (body != null) {
				// 设置边界
//				conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="
//				+ BOUNDARY);
				out = conn.getOutputStream();
				out.write(body.getBytes("UTF-8"));
			}
			int code = conn.getResponseCode();
//			int code = 200;
			if (code == 200) {
				is = conn.getInputStream();
				reader = new BufferedReader(new InputStreamReader(is));
				String line = "";
				while ((line = reader.readLine()) != null) {
					result += line;
				}
				LOG.info("========================================================error000000333");
			} else {
				JSONObject j = new JSONObject();
				j.put("errCode", code);
				j.put("msg", conn.getResponseMessage());
				result = j.toString();
				conn.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("========================================================error000000444");
		} finally {
			LOG.info("========================================================error000000011111");
			conn.disconnect();
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
				if (reader!=null) {
					reader.close();
				}
				if (is != null) {
					is.close();
				}
				LOG.info("========================================================error000000022222");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static void main(String[] args) {
		
		System.out.println(request("https://xueqiu.com", null, null, "GET"));
	}
}

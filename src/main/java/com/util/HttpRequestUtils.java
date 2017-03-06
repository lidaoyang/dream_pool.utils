package com.util;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.net.URLDecoder;
import java.util.*;

/**
 * HTTP工具类
 * Created by andreww on 15/11/8.
 */
public class HttpRequestUtils {

    private static final String EQUALS_SIGN = "=";
    private static final String AND_SIGN    = "&";

    private static Log LOG = LogFactory.getLog(HttpRequestUtils.class);

    /**
     * post请求,无header参数
     * @param url
     * @param body
     * @return
     */
    public static String httpPost(String url, Map<String, Object> body){
        return httpPost(url, null, body);
    }

    /**
     * post请求,含header参数
     * @param url
     * @param header
     * @param body
     * @return
     */
    public static String httpPost(String url, Map<String, Object> header, Map<String, Object> body){
        //post请求返回结果
        String strResult = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            //发送post请求
            HttpPost method = new HttpPost(url);
            url = URLDecoder.decode(url, "UTF-8");

            if (MapUtils.isNotEmpty(header)) {
                Header[] headers = new Header[header.size()];
                int num = 0;
                for (Map.Entry<String, Object> item : header.entrySet()) {
                    String name = item.getKey();
                    String value = (String)item.getValue();
                    Header h = new BasicHeader(name , value);
                    headers[num++] = h;
                }

                method.setHeaders(headers);
            }

            if (MapUtils.isNotEmpty(body)) {
                String formBody = generateRequestBody(body);
                if (!StringUtils.isEmpty(formBody)) {
                    //解决中文乱码问题
                    StringEntity entity = new StringEntity(formBody, "utf-8");
                    entity.setContentEncoding("UTF-8");
                    entity.setContentType("application/x-www-form-urlencoded");
                    method.setEntity(entity);
                }
            }

            HttpResponse response = httpClient.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            strResult = EntityUtils.toString(response.getEntity());

            /**请求发送成功，并得到响应**/
            if (statusCode != HttpStatus.SC_OK) {
                LOG.warn("Http Status Error Of Post Method!" + ", url:" + url + ", status code:" + statusCode + ", error msg:" + strResult);
            }
        } catch (Exception e) {
            LOG.error("Http Error of Post Method:" + ", url:" + url, e);
        }

        return strResult;
    }

    /**
     * 发送get请求,无header参数
     * @param url
     * @return
     */
    public static String httpGet(String url) {
        return httpGet(url, null);
    }


    /**
     * 发送get请求,含header参数
     * @param url
     * @param header
     * @return
     */
    public static String httpGet(String url, Map<String, Object> header){
        //get请求返回结果
        String strResult = null;
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            //发送get请求
            HttpGet method = new HttpGet(url);
            url = URLDecoder.decode(url, "UTF-8");

            if (MapUtils.isNotEmpty(header)) {
                List<Header> headers = new ArrayList<Header>();
                for (Map.Entry<String, Object> item : header.entrySet()) {
                    String name = item.getKey();
                    String value = (String)item.getValue();
                    Header h = new BasicHeader(name , value);
                    headers.add(h);
                }

                method.setHeaders((Header[]) headers.toArray());
            }

            HttpResponse response = client.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();

            /**请求发送成功，并得到响应**/
            if (statusCode == HttpStatus.SC_OK) {
                /**读取服务器返回过来的数据**/
                strResult = EntityUtils.toString(response.getEntity());
            } else {
                LOG.warn("Http Status Error Of Get Method!" + ", url:" + url + ", status code:" + statusCode);
            }
        } catch (Exception e) {
            LOG.error("Http Error of Get Method:" + ", url:" + url, e);
        }

        return strResult;
    }

    /**
     * 拼接请求的body，为form表单格式
     * @param body
     * @return
     */
	public static String generateRequestBody(Map<String, Object> body) {
		StringBuffer buffer = new StringBuffer();
		Iterator<String> it = body.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object value = body.get(key);
			if ("toUserIds".equals(key)) {
				List<String> toUserIds = (List<String>) value;
				for (int i = 0; i < toUserIds.size(); i++) {
					if (!it.hasNext()&&i==toUserIds.size()-1) {
						buffer.append("toUserId").append(EQUALS_SIGN).append(toUserIds.get(i));
					}else {
						buffer.append("toUserId").append(EQUALS_SIGN).append(toUserIds.get(i)).append(AND_SIGN);
					}
				}
			} else if ("userIds".equals(key)) {
				List<String> userIds = (List<String>) value;
				for (int i = 0; i < userIds.size(); i++) {
					if (!it.hasNext()&&i==userIds.size()-1) {
						buffer.append("userId").append(EQUALS_SIGN).append(userIds.get(i));
					}else {
						buffer.append("userId").append(EQUALS_SIGN).append(userIds.get(i)).append(AND_SIGN);
					}
				}
			} else {
				if (it.hasNext()) {
					buffer.append(key).append(EQUALS_SIGN).append(value).append(AND_SIGN);
				} else {
					buffer.append(key).append(EQUALS_SIGN).append(value);
				}
			}
		}

		return buffer.toString();
	}

}

package com.util.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.web.multipart.MultipartFile;

import redis.clients.jedis.Jedis;
import sun.misc.BASE64Encoder;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.FileProgressMonitor;
import com.jcraft.jsch.SFTPChannel;
import com.jcraft.jsch.SFTPConstants;
import com.jcraft.jsch.SftpException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.redis.RedisClient;
import com.util.DateUtils;
import com.util.HttpRequestUtil;
import com.util.HttpUtils;
import com.util.NumberUtils;
import com.util.StrUtils;

public class Common {
	private static final Logger LOG = Logger.getLogger(Common.class.getName());

	public static String getStock(String url) {
		String res = "";
		try {
			res = HttpRequestUtil.request(url, null,
					HttpRequestUtil.REQUEST_TYPE_GET, "GBK");
		} catch (IOException e) {
			e.printStackTrace();
			LOG.info("=======================================获取数据失败");
		}
		return res;
	}

	public static JSONObject get_latest_info(String symbol) {
		Jedis jedis = RedisClient.getJedis();
		JSONObject jsonObj = null;
		if (jedis!=null) {
			try {
				String value = jedis.get(symbol + ":latest");
				jsonObj = JSONObject.fromObject(value);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		return jsonObj;
	}
	public static JSONObject get_us_latest_info(String symbol) {
		Jedis jedis = RedisClient.getJedis();
		JSONObject jsonObj = null;
		if (jedis!=null) {
			try {
				symbol = symbol.replace(".", "");
				String value = jedis.get(symbol + ":us_latest");
				jsonObj = JSONObject.fromObject(value);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		return jsonObj;
	}
	public static JSONObject get_latest_info_list(ArrayList<HashMap<String, Object>> symbols) {
		Jedis jedis = RedisClient.getJedis();
		JSONObject jsonObj = null;
		if (jedis!=null&&symbols!=null&&symbols.size()>0) {
			try {
				jsonObj = new JSONObject();
				for (HashMap<String, Object> symbolMap : symbols) {
					String symbol = symbolMap.get("symbol").toString().toUpperCase();
					String value = jedis.get(symbol + ":latest");
					JSONObject jo = JSONObject.fromObject(value);
					jsonObj.put(symbol, jo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		return jsonObj;
	}
	public static JSONObject get_user_ranking_info(String user_id,String flag) {
		Jedis jedis = RedisClient.getJedis();
		JSONObject jsonObj = null;
		if (jedis!=null) {
			try {
				String value = jedis.get(user_id + ":ranking:"+flag);
				jsonObj = JSONObject.fromObject(value);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		return jsonObj;
	}
	public static JSONArray get_user_ranking_list(String flag) {
		Jedis jedis = RedisClient.getJedis();
		JSONArray jarr = null;
		if (jedis!=null) {
			try {
				String value = jedis.get("rankinglist:"+flag);
				if (value==null) {
					return null;
				}
				jarr = JSONArray.fromObject(value);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		return jarr;
	}
	public static JSONObject get_point_in_minutes(String symbol,
			List<String> minutes, double price) {
		Jedis jedis = RedisClient.getJedis();
		JSONArray time_share_points = null;
		if (jedis!=null) {
			try {
				time_share_points = new JSONArray();
				JSONObject temp_point = null;
				for (int i = 0; i < minutes.size(); i++) {
					String minute = minutes.get(i);
					String point_info_string = jedis.get(symbol + ":points:"+ minute);
					JSONObject jsonmap = new JSONObject();
					jsonmap.put("time", minute);
					String now_str = DateUtils.DateToStr(new Date(), "yyyy-MM-dd");
					Date time_11_30 = DateUtils.StrToDate(now_str+" 11:30", "yyyy-MM-dd HH:mm");
					Date time_15_00 = DateUtils.StrToDate(now_str+" 15:00", "yyyy-MM-dd HH:mm");
					String long_time1130 = (time_11_30.getTime()/1000)+"";
					String long_time1500 = (time_15_00.getTime()/1000)+"";
					if (minute.equals(long_time1130)||minute.equals(long_time1500)||(point_info_string == null&&time_share_points.size()>0)) {
						JSONObject jo = time_share_points.getJSONObject(time_share_points.size()-1);
						jsonmap.put("price", jo.get("price"));
						jsonmap.put("total_volume", jo.get("total_volume"));
						jsonmap.put("total_amount", jo.get("total_amount"));
						time_share_points.add(jsonmap);
						continue;
					}
					if (point_info_string == null) {// 当日还没有行情
						double total_volume = 0;
						double total_amount = 0;
						if (temp_point != null && !temp_point.isEmpty()) {
							price = temp_point.getDouble("price");
							total_volume = temp_point.getDouble("total_volume");
							total_amount = price*total_volume;
						}else {
							String value = jedis.get(symbol + ":latest");
							JSONObject latest_info = JSONObject.fromObject(value);
							if (latest_info != null && !latest_info.isEmpty()) {
								price = latest_info.getDouble("price");
//								total_volume = latest_info.getDouble("total_volume");
//								total_amount = price*total_volume;
							}
						}
						jsonmap.put("price", price);
						jsonmap.put("total_volume", total_volume);
						jsonmap.put("total_amount", NumberUtils.formatDouble(total_amount, 2));
					} else {
						JSONObject point_info = JSONObject.fromObject(point_info_string);
						jsonmap.put("price", point_info.get("price"));
						jsonmap.put("total_volume", point_info.get("total_volume"));
						jsonmap.put("total_amount", point_info.get("total_amount"));
						temp_point = point_info;
					}
					time_share_points.add(jsonmap);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("points", time_share_points);

		return jsonObj;
	}

	public static JSONObject get_point_in_minutes(String symbol, String[] keys) {
		List<String> values = get_redis_value(keys);
		JSONArray jarr = JSONArray.fromObject(values);
		JSONArray time_share_points = new JSONArray();
		JSONObject temp_point = null;
		JSONObject share_point = new JSONObject();
		for (int i = 0; i < keys.length; i++) {
			String minute = keys[i].split("[:]")[2];
			share_point = getPointObj(jarr, minute);
			if (share_point == null) {
				if (temp_point==null) {
					share_point = new JSONObject();
					String cn = "latest";
					String key = symbol.replace(".", "") + ":us_" + cn;
					if (symbol.length() == 8 && (symbol.startsWith("SH") || symbol.startsWith("SZ"))) {
						key = symbol + ":" + cn;
					}
					String value = get_redis_value(key);
					double total_volume = 0;
					double total_amount = 0;
					double price = 0;
					if (value != null) {
						JSONObject jo = JSONObject.fromObject(value);
						price = jo.getDouble("price");
						/*total_volume = jo.getDouble("total_volume");
						total_amount = jo.getDouble("total_amount");
						if (total_amount == 0) {
							total_amount = NumberUtils.formatDouble(price* total_volume, 2);
						}*/
					}
					share_point.put("time", minute);
					share_point.put("price", price);
					share_point.put("total_volume", total_volume);
					share_point.put("total_amount", total_amount);
				}else {
					temp_point.put("time", minute);
					share_point = temp_point;
				}
			}else {
				double price = share_point.getDouble("price");
				double total_volume = share_point.getDouble("total_volume");
				double total_amount = share_point.getDouble("total_amount");
				if (total_amount == 0) {
					total_amount = NumberUtils.formatDouble(price* total_volume, 2);
					share_point.put("total_amount", total_amount);
				}
				temp_point = share_point;
			}
			time_share_points.add(share_point);
		}
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("points", time_share_points);

		return jsonObj;
	}

	private static JSONObject getPointObj(JSONArray jarr, String minute) {
		JSONObject temp_point = null;
		for (int j = 0; j < jarr.size(); j++) {
			JSONObject jo = jarr.getJSONObject(j);
			if (jo != null && !jo.isNullObject()) {
				String mine = jo.getString("time");
				long time = Long.valueOf(mine);
				long m_time = Long.valueOf(minute);
				if (time - m_time < 60 && time - m_time >= 0) {
					jo.put("time", minute);
					temp_point = jo;
					break;
				}
			}
		}
		return temp_point;
	}
	public static JSONObject get_us_point_in_minutes(String symbol,
			List<String> minutes) {
		Jedis jedis = RedisClient.getJedis();
		double price = 0;
		JSONArray time_share_points = null;
		if (jedis!=null) {
			try {
				time_share_points = new JSONArray();
				symbol = symbol.replace(".", "");
				JSONObject temp_point = null;
				for (int i = 0; i < minutes.size(); i++) {
					String minute = minutes.get(i);
					String point_info_string = jedis.get(symbol + ":us_points:"+ minute);
					JSONObject jsonmap = new JSONObject();
					jsonmap.put("time", minute);
					if (point_info_string == null) {// 当日还没有行情
						double total_volume = 0;
						double total_amount = 0;
						if (temp_point != null && !temp_point.isEmpty()) {
							price = temp_point.getDouble("price");
							total_volume = temp_point.getDouble("total_volume");
							total_amount = price*total_volume;
						}else {
							String value = jedis.get(symbol + ":us_latest");
							JSONObject latest_info = JSONObject.fromObject(value);
							if (latest_info != null && !latest_info.isEmpty()) {
								price = latest_info.getDouble("price");
								total_volume = latest_info.getDouble("total_volume");
								total_amount = price*total_volume;
							}
						}
						jsonmap.put("price", price);
						jsonmap.put("total_volume", total_volume);
						jsonmap.put("total_amount", NumberUtils.formatDouble(total_amount, 2));
					} else {
						JSONObject point_info = JSONObject.fromObject(point_info_string);
						double total_amount = point_info.getDouble("price")*point_info.getDouble("total_volume");
						jsonmap.put("price", point_info.get("price"));
						jsonmap.put("total_volume", point_info.get("total_volume"));
						jsonmap.put("total_amount", NumberUtils.formatDouble(total_amount, 2));
						temp_point = point_info;
					}
					time_share_points.add(jsonmap);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("points", time_share_points);

		return jsonObj;
	}
	public static JSONObject get_5_level_volumes(String symbol) {
		Jedis jedis = RedisClient.getJedis();
		String value = null;
		if (jedis!=null) {
			try {
				value = jedis.get(symbol + ":volumes");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		JSONObject jsonObj = new JSONObject();
		if (value != null) {
			JSONArray jsonArr = JSONArray.fromObject(value);
			if (jsonArr.size() >= 5) {
				jsonObj.put("sell_volumes", jsonArr.subList(0, 5));
			}
			if (jsonArr.size() >= 10) {
				jsonObj.put("buy_volumes", jsonArr.subList(5, 10));
			}
		}else {
			jsonObj.put("sell_volumes", new JSONArray());
			jsonObj.put("buy_volumes", new JSONArray());
		}

		return jsonObj;
	}
	public static int set_redis_value(byte[] key,Object obj) {
		Jedis jedis = RedisClient.getJedis();
		int ret =0;
		if (jedis != null) {
			try {
				jedis.set(key, serialize(obj));
				ret = 1;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		return ret;
	}
	public static Object get_redis_value(byte[] key) {
		Jedis jedis = RedisClient.getJedis();
		Object result = null;
		if (jedis!=null) {
			try {
				byte[] in = jedis.get(key);
				result = deserialize(in);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		return result;
	}
	public static long del_redis_value(byte[] key) {
		Jedis jedis = RedisClient.getJedis();
		long ret=0;
		if (jedis != null) {
			try {
				ret = jedis.del(key);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		return ret;
	}
	public static int set_redis_value(String key,String json) {
		Jedis jedis = RedisClient.getJedis();
		int ret =0;
		if (jedis != null) {
			try {
				jedis.set(key, json);
				ret = 1;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		return ret;
	}
	public static String get_redis_value(String key) {
		Jedis jedis = RedisClient.getJedis();
		String verify_code="";
		if (jedis!=null) {
			try {
				verify_code = jedis.get(key);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		return verify_code;
	}
	public static List<String> get_redis_value(String[] keys) {
		Jedis jedis = RedisClient.getJedis();
		List<String> values=null;
		if (jedis!=null) {
			try {
				values = jedis.mget(keys);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		return values;
	}
	public static long del_redis_value(String key) {
		Jedis jedis = RedisClient.getJedis();
		long ret=0;
		if (jedis != null) {
			try {
				ret = jedis.del(key);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		return ret;
	}
	public static byte[] serialize(Object value) {  
        if (value == null) {  
            throw new NullPointerException("Can't serialize null");  
        }  
        byte[] rv=null;  
        ByteArrayOutputStream bos = null;  
        ObjectOutputStream os = null;  
        try {  
            bos = new ByteArrayOutputStream();  
            os = new ObjectOutputStream(bos);  
            os.writeObject(value);  
            os.close();  
            bos.close();  
            rv = bos.toByteArray();  
        } catch (IOException e) {  
            throw new IllegalArgumentException("Non-serializable object", e);  
        } finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        }
        return rv;  
    } 
	public static Object deserialize(byte[] in) {
        Object rv=null;  
        ByteArrayInputStream bis = null;  
        ObjectInputStream is = null;  
        try {  
            if(in != null) {  
                bis=new ByteArrayInputStream(in);  
                is=new ObjectInputStream(bis);  
                rv=is.readObject();  
                is.close();  
                bis.close();  
            }  
        } catch (IOException e) {  
        	e.printStackTrace();
        } catch (ClassNotFoundException e) {  
            e.printStackTrace();
        } finally {  
        	if (is!=null) {
        		 try {
     				is.close();
     			} catch (IOException e) {
     				e.printStackTrace();
     			}
			}
        	if (bis!=null) {
        		 try {
     				bis.close();
     			} catch (IOException e) {
     				e.printStackTrace();
     			}
			}
        }  
        return rv;  
    }  
	public static JSONObject get_trade_details(String symbol) {
		Jedis jedis = RedisClient.getJedis();
		List<String> listValue = null;
		if (jedis != null) {
			try {
				listValue = jedis.lrange(symbol + ":details", 0, -1);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				RedisClient.returnRes(jedis);
			}
		}
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		if (listValue != null && listValue.size() > 0) {
			jsonArr = JSONArray.fromObject(listValue);
//			jsonObj.put("details", jsonArr);
		}
		jsonObj.put("details", jsonArr);
		return jsonObj;
	}
	//2016-12-02 18:59更改
	public static List<String> time_sharing_minutes_between(Date start_time,
			Date end_time,int minute) {
		long time1 = start_time.getTime();
		long time2 = end_time.getTime();
		long total_minute = (time2 - time1) / 1000 / 60;
		long count = total_minute%minute==0?total_minute/minute:total_minute/minute+1;
		long time = start_time.getTime() / 1000;
		List<String> minutes = new ArrayList<String>();
		if (minute==1) {
			minutes.add(String.valueOf(time));
		}
		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < count; i++) {
			cal.setTime(start_time);
			int surplus = (int) ((end_time.getTime()/1000-time)/60);
			if (surplus<minute) {
				cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + surplus);
			}else {
				cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + minute);
			}
			Date d = cal.getTime();
			time = d.getTime() / 1000;
			minutes.add(String.valueOf(time));
			start_time = d;
		}

		return minutes;
	}
	public static List<String> time_sharing_us_minutes_between(Date start_time,
			Date end_time,int minute) {
		long time1 = start_time.getTime();
		long time2 = end_time.getTime();
		long count = (time2 - time1) / 1000 / 60/minute;
		Date ch_date = DateUtils.convertTimeZoneToCH(start_time);//转成北京时间
		long time = ch_date.getTime() / 1000;
		List<String> minutes = new ArrayList<String>();
		minutes.add(String.valueOf(time));

		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < count; i++) {
			cal.setTime(ch_date);
			cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + minute);
			Date d = cal.getTime();
			time = d.getTime() / 1000;
			minutes.add(String.valueOf(time));
			ch_date = d;
		}

		return minutes;
	}
	public static List<String> time_sharing_minutes_in_date(Date time,int minute) {
		String strdate = DateUtils.DateToStr(time, "yyyy-MM-dd");
		Date start_time = DateUtils.StrToDate(strdate + " "
				+ Constant.MORNING_START_TIME, "yyyy-MM-dd HH:mm");
		Date end_time = DateUtils.StrToDate(strdate + " "
				+ Constant.MORNING_END_TIME, "yyyy-MM-dd HH:mm");
		Date pmstart_time = DateUtils.StrToDate(strdate + " "
				+ Constant.AFTERNOON_START_TIME, "yyyy-MM-dd HH:mm");
		Date pmend_time = DateUtils.StrToDate(strdate + " "
				+ Constant.AFTERNOON_END_TIME, "yyyy-MM-dd HH:mm");

		List<String> minutes = time_sharing_minutes_between(start_time,
				end_time,minute);
		minutes.addAll(time_sharing_minutes_between(pmstart_time, pmend_time,minute));
		return minutes;
	}
	public static boolean now_in_data_reset_period(int min) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int start_minute = 9 * 60 + 10;
		int end_minute = 9 * 60 + min;
		int now_minute = hour * 60 + minute;
		if (now_minute >= start_minute && now_minute < end_minute) {
			return true;
		}
		return false;
	}

	public static boolean now_in_data_reset_period(String symbol) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int start_minute = 9 * 60 + 10;
		int end_minute = 9 * 60 + 16;
		int now_minute = hour * 60 + minute;
		if (now_minute < start_minute) {
			return false;
		}
		/*JSONObject j = get_latest_info(symbol);
		long time = 0;
		if (j != null && !j.isEmpty()) {
			time = j.getLong("time");
			String date = DateUtils.DateToStr(new Date(time * 1000),
					"yyyy-MM-dd");
			if (!DateUtils.isToday(date)) {
				return false;
			}
		}*/
		if (now_minute > end_minute) {
			return false;
		}
		return true;
	}

	public static boolean valid_in_stock_time(Date time) {
		String strnow = DateUtils.DateToStr(new Date(), "yyyy-MM-dd");
		Date now_start = DateUtils.StrToDate(strnow + " 09:30","yyyy-MM-dd HH:mm");
		Date now_end = DateUtils.StrToDate(strnow + " 11:30","yyyy-MM-dd HH:mm");
		Date now_pm_start = DateUtils.StrToDate(strnow + " 13:00","yyyy-MM-dd HH:mm");
		Date now_pm_end = DateUtils.StrToDate(strnow + " 15:00","yyyy-MM-dd HH:mm");
		if ((time.getTime() >= now_start.getTime() && time.getTime() <= now_end.getTime())
				|| (time.getTime() >= now_pm_start.getTime() && time.getTime() <= now_pm_end.getTime())) {
			return true;
		}
		return false;
	}
	public static boolean valid_in_us_stock_time(Date date) {
 		SimpleDateFormat sdfe = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdfe.setTimeZone(TimeZone.getTimeZone("America/New_York"));//转换美国纽约时间
		date = DateUtils.StrToDate(sdfe.format(date), "yyyy-MM-dd HH:mm:ss");
		String strdate = DateUtils.DateToStr(date, "yyyy-MM-dd");
		Date now_start = DateUtils.StrToDate(strdate + " 09:30","yyyy-MM-dd HH:mm");
		Date now_end = DateUtils.StrToDate(strdate + " 16:00","yyyy-MM-dd HH:mm");
		if ((date.getTime() >= now_start.getTime() && date.getTime() <= now_end.getTime())) {
			return true;
		}
		return false;
	}
	public static boolean dv_valid_in_us_stock_time(Date date) {
 		SimpleDateFormat sdfe = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdfe.setTimeZone(TimeZone.getTimeZone("America/New_York"));//转换美国纽约时间
		date = DateUtils.StrToDate(sdfe.format(date), "yyyy-MM-dd HH:mm:ss");
		String strdate = DateUtils.DateToStr(date, "yyyy-MM-dd");
		Date now_start = DateUtils.StrToDate(strdate + " 09:32","yyyy-MM-dd HH:mm");
		Date now_end = DateUtils.StrToDate(strdate + " 16:00","yyyy-MM-dd HH:mm");
		if ((date.getTime() >= now_start.getTime() && date.getTime() <= now_end.getTime())) {
			return true;
		}
		return false;
	}
	public static boolean valid_in_hk_stock_time(Date time) {
		String strnow = DateUtils.DateToStr(new Date(), "yyyy-MM-dd");
		Date now_start = DateUtils.StrToDate(strnow + " 09:30","yyyy-MM-dd HH:mm");
		Date now_end = DateUtils.StrToDate(strnow + " 12:00","yyyy-MM-dd HH:mm");
		Date now_pm_start = DateUtils.StrToDate(strnow + " 13:00","yyyy-MM-dd HH:mm");
		Date now_pm_end = DateUtils.StrToDate(strnow + " 16:00","yyyy-MM-dd HH:mm");
		if ((time.getTime() >= now_start.getTime() && time.getTime() <= now_end.getTime())
				|| (time.getTime() >= now_pm_start.getTime() && time.getTime() <= now_pm_end.getTime())) {
			return true;
		}
		return false;
	}
	public static boolean isExistFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	public static Json upLoadWebFile(String url, String path, String fname) {
		Json j = new Json();
		HttpResponse<InputStream> httpIS = null;
		try {
			httpIS = Unirest.get(url).asBinary();
		} catch (UnirestException e1) {
			j.setSuccess(false);
			e1.printStackTrace();
			return j;
		}
		String base = Constant.IMAGE_BASE_PATH;
		String filepath = base + "/" + path;
		File file = new File(filepath);
		if (!file.exists()) {
			file.mkdirs();
		}
		ByteArrayInputStream bais = (ByteArrayInputStream) httpIS.getBody();
		String dst = base + "/" + path + "/" + fname; // 目标文件名
		try {
			FileOutputStream os = new FileOutputStream(dst);
			int b = 0;
			while ((b = bais.read()) != -1) {
				os.write(b);
			}
			os.flush();
			os.close();
			bais.close();
			j.setSuccess(true);
		} catch (IOException e) {
			j.setSuccess(false);
			e.printStackTrace();
		}
		return j;
	}

	public static Json LocalUpLoadWebFileWeb(String url, String path,
			String fname) {
		Map<String, String> sftpDetails = setSftpDetail();
		Json j = new Json();
		HttpResponse<InputStream> httpIS = null;
		try {
			httpIS = Unirest.get(url).asBinary();
		} catch (UnirestException e1) {
			j.setSuccess(false);
			e1.printStackTrace();
			return j;
		}
		ByteArrayInputStream bais = (ByteArrayInputStream) httpIS.getBody();
		SFTPChannel channel = new SFTPChannel();
		try {
			ChannelSftp chSftp = channel.getChannel(sftpDetails, 100000);
			String base = Constant.IMAGE_BASE_PATH;
			chSftp.cd(base);
			String[] dirs = path.split("/");
			String p = "";
			for (int i = 0; i < dirs.length; i++) {
				p += "/" + dirs[i];
				try {
					Vector content = chSftp.ls(dirs[i]);
					chSftp.cd(base + p);
					if (content == null) {
						chSftp.mkdir(dirs[i]);
						chSftp.cd(base + p);
					}
				} catch (SftpException e) {
					chSftp.mkdir(dirs[i]);
					chSftp.cd(base + p);
				}
			}
			String dst = base + "/" + path + "/" + fname; // 目标文件名
			chSftp.put(bais, dst, ChannelSftp.OVERWRITE);
			j.setSuccess(true);
			chSftp.quit();
			channel.closeChannel();
		} catch (Exception e) {
			j.setSuccess(false);
			e.printStackTrace();
		}
		return j;
	}

	public static Json UpLoadFile(MultipartFile multipartFile, String path,
			String fname) {
		Json j = new Json();
		String base = Constant.IMAGE_BASE_PATH;
		String filepath = base + "/" + path;
		File file = new File(filepath);
		if (!file.exists()) {
			file.mkdirs();
		}
		File dest = new File(filepath, fname);
		try {
			multipartFile.transferTo(dest);
			j.setSuccess(true);
		} catch (IllegalStateException e) {
			j.setSuccess(false);
			e.printStackTrace();
		} catch (IOException e) {
			j.setSuccess(false);
			e.printStackTrace();
		}
		return j;
	}

	public static Json LocalUpLoadFileWeb(MultipartFile file, String path,
			String fname) {
		// 设置主机ip，端口，用户名，密码
		Map<String, String> sftpDetails = setSftpDetail();
		Json j = new Json();
		SFTPChannel channel = new SFTPChannel();
		try {
			ChannelSftp chSftp = channel.getChannel(sftpDetails, 100000);
			String base = Constant.IMAGE_BASE_PATH;
			chSftp.cd(base);
			String[] dirs = path.split("/");
			String p = "";
			for (int i = 0; i < dirs.length; i++) {
				p += "/" + dirs[i];
				try {
					Vector content = chSftp.ls(dirs[i]);
					chSftp.cd(base + p);
					if (content == null) {
						chSftp.mkdir(dirs[i]);
						chSftp.cd(base + p);
					}
				} catch (SftpException e) {
					chSftp.mkdir(dirs[i]);
					chSftp.cd(base + p);
				}
			}
			String dst = base + "/" + path + "/" + fname; // 目标文件名
			FileProgressMonitor fileProgressMonitor = new FileProgressMonitor(
					file.getSize());
			chSftp.put(file.getInputStream(), dst, fileProgressMonitor,
					ChannelSftp.OVERWRITE);
			j.setSuccess(true);
			chSftp.quit();
			channel.closeChannel();
		} catch (Exception e) {
			j.setSuccess(false);
			e.printStackTrace();
		}
		return j;
	}

	private static Map<String, String> setSftpDetail() {
		Map<String, String> sftpDetails = new HashMap<String, String>();
		sftpDetails.put(SFTPConstants.SFTP_REQ_HOST, Constant.SFTP_REQ_HOST);
		sftpDetails.put(SFTPConstants.SFTP_REQ_USERNAME,
				Constant.SFTP_REQ_USERNAME);
		sftpDetails.put(SFTPConstants.SFTP_REQ_PASSWORD,
				Constant.SFTP_REQ_PASSWORD);
		sftpDetails.put(SFTPConstants.SFTP_REQ_PORT, Constant.SFTP_REQ_PORT);
		return sftpDetails;
	}

//获取9位随机数
	public static String rand_num_code(int num) {
		int[] array = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		Random rand = new Random();
		for (int i = 10; i > 1; i--) {
			int index = rand.nextInt(i);
			int tmp = array[index];
			array[index] = array[i - 1];
			array[i - 1] = tmp;
		}
		int result = 0;
		for (int i = 0; i < num; i++)
			result = result * 10 + array[i];
		
		String code = result+"";
		if (code.length()<num) {
			code="0"+code;
		}
		return code;
	}
	public static boolean validate_phone(String phone) {
		Pattern p = Pattern
				.compile("^1[3|4|5|7|8][0-9]\\d{4,8}$");
		Matcher m = p.matcher(phone);
		return m.matches();
	}
	public static boolean validate_email(String email) {
		Pattern p = Pattern
				.compile("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w{2,3}){1,3})$");
		Matcher m = p.matcher(email);
		return m.matches();
	}
	public static boolean validate_nickname(String nickname) {
		Pattern p = Pattern.compile("^[0-9a-zA-Z\u4e00-\u9fa5]{1,20}$");
		Matcher m = p.matcher(nickname);
		return m.matches();
	}

	public static String createTaken() {
		UUID uuid = UUID.randomUUID();
		String auth_token=uuid.toString().replace("-", "")+rand_num_code(4)+new Date().getTime();
		return auth_token;
	}

	public static boolean validate_password(String loginpassword,
			String password_digest) {
		String password_d = BCrypt.hashpw(loginpassword, password_digest);
		if (password_d.equals(password_digest)) {
			return true;
		}
		return false;
	}

	public static String create_encrypted_password(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

	//leancloud 推送
		public static boolean push_remind(String device_id, String device_type,
				String action, JSONObject name) {
			HttpResponse<JsonNode> jsonResponse = null;
			if (StrUtils.isNotBlank(device_id)&& StrUtils.isNotBlank(device_type)) {
				JSONObject jobj = new JSONObject();
				if ("ios".equals(device_type)) {
					JSONObject j = new JSONObject();
					j.put("deviceToken", device_id);
					jobj.put("where", j);

					name.put("type", action);
					name.put("badge", "Increment");
					name.put("sound", "default");
					jobj.put("data", name);
				} else if ("android".equals(device_type)) {
					JSONObject j = new JSONObject();
					j.put("installationId", device_id);
					jobj.put("where", j);
					j = new JSONObject();
					j.put("action", "com.candzen." + action);
					j.put("name", name);
					jobj.put("data", j);
				}
				String url = Constant.PUSH_REMIND_URL;
				try {
					jsonResponse = Unirest
							.post(url)
							.header("Content-Type", "application/json")
							.header("X-AVOSCloud-Application-Id",
									"hr480h8h46x0cs97j00vghsflgp9tzsgf3vw46l0na7chu7e")
							.header("X-AVOSCloud-Application-Key",
									"dl1ozt85dammxpmut0i1m52mezb0u0vneqqdxqwz3h13ablx")
							.body(jobj.toString()).asJson();
				} catch (UnirestException e) {
					e.printStackTrace();
				}
			}
			if (jsonResponse != null) {
				LOG.info("===============================================push_remind " + jsonResponse.getBody());
				return true;
			} else
				LOG.info("===============================================push_remind null");
			return false;
		}

	// JPush 推送
	public static void jpush_remind(String user_id,String device_type,
			String registration_id, JSONObject data,JSONObject extras) {
		if (StrUtils.isNotBlank(device_type)&& StrUtils.isNotBlank(registration_id)) {
			extras.put("user_id", user_id);
			try {
				String appkey = Constant.JPAPPKEY;
				String mastersecret = Constant.JPMASTERSECRET;
				String token = new BASE64Encoder().encode((appkey + ":" + mastersecret).getBytes());
				// 指定特定推送平台
				JSONArray platform = new JSONArray();
				// 推送给指定的Registration ID（注册ID）
				JSONObject audience = new JSONObject();
				/*JSONArray reg_id = new JSONArray();
				reg_id.add(registration_id);
				audience.put("registration_id", reg_id);*/
				JSONArray tag_and = new JSONArray();
				tag_and.add(user_id);
				tag_and.add(registration_id);
				audience.put("tag_and", tag_and);
				// “通知”对象
				JSONObject notification = new JSONObject();
				JSONObject json = new JSONObject();
				if ("ios".equals(device_type)) {// ios端推送信息
					platform.add("ios");
					JSONObject ios = new JSONObject();
					ios.put("alert", data.getString("alert"));
					ios.put("sound", "default");
					ios.put("badge", "+1");
					/*// 扩展信息（业务使用）
					JSONObject extras = new JSONObject();
					extras.put("flag", data.getInt("flag"));*/
					ios.put("extras", extras);
					notification.put("ios", ios);

					json.put("platform", platform);
					json.put("audience", audience);
					json.put("notification", notification);
					// 推送可选项(ios用)
					JSONObject options = new JSONObject();
					options.put("apns_production", true);// IOS是否为开发环境，False表示要推送开发环境
					json.put("options", options);
				} else if ("android".equals(device_type)) {// android端推送信息
					platform.add("android");
					JSONObject android = new JSONObject();
					android.put("alert", data.getString("content"));
					android.put("title", data.getString("title"));
					/*// 扩展信息（业务使用）
					JSONObject extras = new JSONObject();
					extras.put("type", data.getInt("type"));*/
					android.put("extras", extras);
					notification.put("android", android);

					json.put("platform", platform);
					json.put("audience", audience);
					json.put("notification", notification);
				}
				String type = null;
				if (extras.containsKey("type")) {
					type = extras.getString("type");
				}
				asyncSendPush(user_id, token,type, json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void asyncSendPush(final String u_id, final String token,final String type,
			final JSONObject json) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					HttpResponse<String> jsonResponse = Unirest.post(Constant.JPUSH_REMIND_URL)
							.header("Authorization", "Basic " + token)
							.body(json.toString()).asString();
					if (jsonResponse != null) {
						LOG.info("===============================================push_remind "+ jsonResponse.getBody());
					} else{
						if (type!=null&&"3".equals(type)) {
							set_redis_value("add_user_remind:"+u_id, "true");
						}
						LOG.info("===============================================push_remind null");
					}
				} catch (UnirestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	// JPush 推送消息extras.put("type", 7);//消息推送类别（1交易比赛推送,2模拟交易开始提醒,3添加好友提醒,4阅后即焚消息读取状态,5投资动态评论/点赞推送,6删除评论/点赞推送,
	//7同意添加你为好友,8通讯录好友注册众米推送,9同意添加你为好友,10解除了好友关系）
	public static void jpush_msg(String user_id,String device_type,
			String registration_id, JSONObject data, JSONObject extras) {
		if (StrUtils.isNotBlank(user_id) &&StrUtils.isNotBlank(device_type) && StrUtils.isNotBlank(registration_id)) {
			extras.put("user_id", user_id);
			try {
				String appkey = Constant.JPAPPKEY;
				String mastersecret = Constant.JPMASTERSECRET;
				String token = new BASE64Encoder()
						.encode((appkey + ":" + mastersecret).getBytes());
				// 指定特定推送平台
				JSONArray platform = new JSONArray();
				// 推送给指定的Registration ID（注册ID）
				JSONObject audience = new JSONObject();
				/*JSONArray reg_id = new JSONArray();
				reg_id.add(registration_id);
				audience.put("registration_id", reg_id);*/
				JSONArray tag_and = new JSONArray();
				tag_and.add(user_id);
				tag_and.add(registration_id);
				audience.put("tag_and", tag_and);
				// “通知”对象
				JSONObject message = new JSONObject();
				message.put("msg_content", data.getString("content"));
				message.put("content_type", "text");
				message.put("title", data.getString("title"));
				message.put("extras", extras);
				JSONObject json = new JSONObject();
				if ("ios".equals(device_type)) {// ios端推送信息
					platform.add("ios");
				} else if ("android".equals(device_type)) {// android端推送信息
					platform.add("android");
				}
				json.put("platform", platform);
				json.put("audience", audience);
				json.put("message", message);
				String type = null;
				if (extras.containsKey("type")) {
					type = extras.getString("type");
				}
				asyncSendPush(user_id, token,type, json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static JSONArray get_suspend_stocks_data (String date){
		JSONArray jarr = null;
		String url="http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=FD&sty=SRB&ps=5000&fd="+date;
		try {
			String resultstr = HttpRequestUtil.request(url, null, HttpRequestUtil.REQUEST_TYPE_GET, "utf-8");
			resultstr = resultstr.substring(1,resultstr.length()-1);
			jarr=JSONArray.fromObject(resultstr);
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return jarr;
	}
	public static int byte2ToUnsignedShort(byte[] b, int offset) {
		  int i = (b[3] << 24) & 0xFF000000;  
	        i |= (b[2] << 16) & 0xFF0000;  
	        i |= (b[1] << 8) & 0xFF00;  
	        i |= b[0] & 0xFF;  
	        return i;  
	}
	public static JSONArray grab_news(String symbol,Date d) {
		JSONArray jarr = new JSONArray();
		try {
			String uri = "http://m.guba.eastmoney.com/getdata/articlelist?code="+symbol+"&count=20&type=1&thispage=1&id=&sort=0";
			String result = HttpRequestUtil.request(uri, null, HttpRequestUtil.REQUEST_TYPE_GET, "utf-8");
			Date now_date = new Date();
			String str_nowdate = DateUtils.DateToStr(now_date, "yyyy-MM-dd");
			JSONObject json = JSONObject.fromObject(result);
			JSONArray ret_jarr = json.getJSONArray("re");
			for (int i = 0; i < ret_jarr.size(); i++) {
				JSONObject j = ret_jarr.getJSONObject(i);
				String publish_time = j.getString("post_publish_time");
				Date date = DateUtils.StrToDate(publish_time, "yyyy-MM-dd HH:mm:ss");
				if (d!=null &&(date.getTime()<=d.getTime())) {
					break;
				}
				String p_time = publish_time.substring(0,10);
				if (str_nowdate.equals(p_time)) {
					JSONObject jobj = new JSONObject();
					String post_id =  j.getString("post_id");
					jobj.put("news_url","http://m.guba.eastmoney.com/article/"+post_id);
					if (symbol.startsWith("6")) {
						jobj.put("pic_url", "http://hqzwpic.eastmoney.com/K/"+symbol+"1KD.png");
					}else {
						jobj.put("pic_url", "http://hqzwpic.eastmoney.com/K/"+symbol+"2KD.png");
					}
					jobj.put("new_title", j.getString("post_title"));
					jobj.put("type", "stock_news");
					jobj.put("release_time", publish_time);
					jarr.add(jobj);
				}else {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jarr;
	}

	public static JSONObject get_client_token(String user_name,String password){
		String url = Constant.JCD_TOKEN_DOMAIN+"/oauth/access-token";
		HashMap<String, String> heard = new HashMap<String, String>();
		heard.put("Origin", Constant.JCD_TOKEN_DOMAIN);
		heard.put("Content-type", "application/json");
		JSONObject body = new JSONObject();
		body.put("client_id", Constant.CLIENT_ID);
		body.put("client_secret", Constant.SECRET);
		body.put("grant_type", "client_credentials");
		body.put("scope", Constant.SCOPES);
		body.put("username", user_name);
		body.put("password", password);
		String result = HttpUtils.request(url, heard,body.toString(), "post");
		JSONObject jobj = JSONObject.fromObject(result);
		return jobj;
	}
	public static JSONObject get_account_map(String user_name,String token){
		String url = Constant.JCD_ORDER_DOMAIN+"/api/ib/account-map?associatedID="+user_name;
		HashMap<String, String> heard = new HashMap<String, String>();
		heard.put("Authorization", "Bearer "+token);
		heard.put("TOPApiVersion", Constant.TOPAPIVERSION);
		String result = HttpUtils.request(url, heard,null, "get");
		JSONObject jobj = JSONObject.fromObject(result);
		return jobj;
	}
	public static JSONObject get_profile(String token){
		String url = Constant.JCD_REG_DOMAIN+"/web/user/profile";
		HashMap<String, String> heard = new HashMap<String, String>();
		heard.put("Authorization", "Bearer "+token);
		String result = HttpUtils.request(url, heard,null, "get");
		JSONObject jobj = JSONObject.fromObject(result);
		return jobj;
	}
	public static JSONObject get_account_summary(String account,String token){
		String url = Constant.JCD_ORDER_DOMAIN+"/api/ib/account/"+account+"/summary";
		HashMap<String, String> heard = new HashMap<String, String>();
		heard.put("Authorization", "Bearer "+token);
		heard.put("TOPApiVersion", Constant.TOPAPIVERSION);
		String result = HttpUtils.request(url, heard,null, "get");
		JSONObject jobj = JSONObject.fromObject(result);
		return jobj;
	}
	public static JSONObject get_us_stock_deal_record(String token,String subAccount,String page){
		if ("".equals(page)) {
			page = "0";
		}
		String url = Constant.JCD_BANK_DOMAIN+"/api/bank/record/subAccount/"+subAccount+"/"+page+"/100";
		HashMap<String, String> heard = new HashMap<String, String>();
		heard.put("Authorization", "Bearer "+token);
		String result = HttpUtils.request(url, heard,null, "get");
		JSONObject jobj = JSONObject.fromObject(result);
		return jobj;
	}
	
	public static void setHeaders(HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin","*");
		response.addHeader("Access-Control-Allow-Methods","GET,POST,OPTIONS"); 
		response.addHeader("Access-Control-Allow-Headers", "Content-type,hello");
		response.addHeader("Access-Control-Max-Age", "50");
	}
	public static String get_hk_stock_name(String stocks){
//		stocks = "hk00001,hk00002,hk00003,hk00004,hk00005,hk00006,hk00007,hk00008,hk00009,hk00010,hk00011,hk00012,hk00014,hk00015,hk00016,hk00017,hk00018,hk00019,hk00020,hk00021,hk00022,hk00023,hk00024,hk00025,hk00026,hk00027,hk00028,hk00029,hk00030,hk00031,hk00032,hk00033,hk00034,hk00035,hk00036,hk00037,hk00038,hk00039,hk00040,hk00041";
		String result = null;
		String url = "http://hq.sinajs.cn/?func=getData._hq_cron();&list="+stocks;
		/*try {
			HttpResponse<String> jsonResponse = Unirest.get(url).header("Content-Type", "charset=GBK").asString();
			result = jsonResponse.getBody();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		try {
			result = HttpRequestUtil.request(url, null,HttpRequestUtil.REQUEST_TYPE_GET, "GBK");
		} catch (IOException e) {
			e.printStackTrace();
			LOG.info("=======================================获取数据失败");
		}
		return result;
	}
	public static void main(String[] args) throws Exception {
	        System.out.println(create_encrypted_password("admin"));
	}


}

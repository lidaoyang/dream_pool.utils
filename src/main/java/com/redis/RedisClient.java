package com.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.util.common.Constant;

public class RedisClient {
	
	/**
	 * 读写连接池
	 */
	private static JedisPool RWPool;
	/**
	 * 只读连接池
	 */
	private static JedisPool RPool;

	/**
	 * 建立连接池 真实环境，一般把配置参数缺抽取出来。
	 * 
	 */
	private static JedisPool createJedisPool(String port,String password) {

		// 建立连接池配置参数
		JedisPoolConfig config = new JedisPoolConfig();

		// 最大空闲连接数, 默认8个 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(10);
        
        // 最大连接数, 默认8个
        config.setMaxTotal(500);

		// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(1000 * 100);
        JedisPool pool = null;
		if (password==null) {
			pool = new JedisPool(config, port, 6379,10000);
		}else {
			// 创建连接池
			pool = new JedisPool(config, port, 6379,10000,password);
		}
		return pool;
	}

	/**
	 * 在多线程环境同步初始化
	 */
	private static synchronized void poolInit() {
		if (RWPool == null)
			RWPool = createJedisPool(Constant.SFTP_REQ_HOST, Constant.REDIS_PWD112);
//			RWPool = createJedisPool("127.0.0.1", null);
	}

	/**
	 * 获取一个可读写jedis 对象
	 * 
	 * @return
	 */
	public static Jedis getJedis() {
		if (RWPool == null)
			poolInit();
		Jedis jedis =null;
		try {
			jedis = RWPool.getResource();
		} catch (JedisConnectionException e) {
			e.printStackTrace();
		}
		return jedis;
	}
	/**
	 * 获取一个只读jedis 对象
	 * @return
	 */
	public static Jedis getReadyOnlyJedis() {
		Jedis jedis =null;
		if (RPool==null) {
			RPool = createJedisPool(Constant.SFTP_REQ_HOST216, Constant.REDIS_PWD216);
//			RPool = createJedisPool("127.0.0.1", null);
		}
		try {
			jedis = RPool.getResource();
		} catch (JedisConnectionException e1) {
			e1.printStackTrace();
			createJedisPool(Constant.SFTP_REQ_HOST169, Constant.REDIS_PWD169);
			try {
				jedis = RPool.getResource();
			} catch (JedisConnectionException e2) {
				e2.printStackTrace();
			}
		}
		return jedis;
	}
	/**
	 * 归还一个连接
	 * 
	 * @param jedis
	 */
	public static void returnRes(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}
	
	/**
	 * @param pattern 模糊搜索的Key,然后批量删除
	 * @return 删除个数
	 */
	public static long del_keys(String pattern) {
		Jedis jedis = getJedis();
		long ret=0;
		if (jedis != null) {
			try {
//				Set<String> set_keys = jedis.keys(pattern);
				List<String> setkeys = scan(jedis, pattern);
				if (!setkeys.isEmpty()) {
					String[] keys = setkeys.toArray(new String[setkeys.size()]);
					jedis.del(keys);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return ret;
	}
	
	public static int set_value(String key,String json) {
		Jedis jedis = getJedis();
		int ret =0;
		if (jedis != null) {
			try {
				jedis.set(key, json);
				ret = 1;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return ret;
	}
	public static String get_value(String key) {
		Jedis jedis = getReadyOnlyJedis();
		String verify_code="";
		if (jedis!=null) {
			try {
				verify_code = jedis.get(key);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return verify_code;
	}
	public static List<String> get_value(String[] keys) {
		Jedis jedis = getReadyOnlyJedis();
		List<String> values=null;
		if (jedis!=null) {
			try {
				values = jedis.mget(keys);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return values;
	}
	public static long del_value(String key) {
		Jedis jedis = getJedis();
		long ret=0;
		if (jedis != null) {
			try {
				ret = jedis.del(key);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return ret;
	}
	/**删除多个key
	 * @param keys
	 * @return 删除数量
	 */
	public static long del_value(String[] keys) {
		Jedis jedis = getJedis();
		long ret=0;
		if (jedis != null) {
			try {
				ret = jedis.del(keys);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return ret;
	}
	public static int set_value(byte[] key,Object obj) {
		Jedis jedis = getJedis();
		int ret =0;
		if (jedis != null) {
			try {
				jedis.set(key, serialize(obj));
				ret = 1;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return ret;
	}
	public static Object get_value(byte[] key) {
		Jedis jedis = getReadyOnlyJedis();
		Object result = null;
		if (jedis!=null) {
			try {
				byte[] in = jedis.get(key);
				result = deserialize(in);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return result;
	}
	public static long del_value(byte[] key) {
		Jedis jedis = getJedis();
		long ret=0;
		if (jedis != null) {
			try {
				ret = jedis.del(key);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
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

	/**
	 * 模糊查询Key
	 * @param pattern
	 * @return key 的集合
	 * •h?llo will match hello hallo hhllo 
	 * •h*llo will match hllo heeeello
	 * •h[ae]llo will match hello and hallo, but not hillo
	 * 已过时,可以改用scan方法
	 */
	public static Set<String> get_keys(String pattern){
		Jedis jedis = getReadyOnlyJedis();
		if (jedis != null) {
			try {
				Set<String> keys = jedis.keys(pattern);
				return keys;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return null;
	}
	/**
	 * 向list集合里添加元素,在key 对应 list的头部添加字符串元素
	 * @param key  list集合的key
	 * @param json  list集合的json字符串
	 * @return
	 */
	public static long lpush_list(String key,String json) {
		Jedis jedis = getJedis();
		long ret =0;
		if (jedis != null) {
			try {
				ret = jedis.lpush(key, json);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return ret;
	}
	/**
	 * 向list集合里添加元素,在key 对应 list 的尾部添加字符串元素
	 * @param key  list集合的key
	 * @param json  list集合的json字符串
	 * @return
	 */
	public static long rpush_list(String key,String json) {
		Jedis jedis = getJedis();
		long ret =0;
		if (jedis != null) {
			try {
				ret = jedis.rpush(key, json);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return ret;
	}
	/**
	 * 向list集合里批量添加元素,在key 对应 list的头部添加字符串元素
	 * @param key  list集合的key
	 * @param values  list集合的values元素集合
	 * @return
	 */
	public static long lpush_list(String key,String[] values) {
		Jedis jedis = getJedis();
		long ret =0;
		if (jedis != null) {
			try {
				ret = jedis.lpush(key, values);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return ret;
	}
	/**
	 * 向list集合里批量添加元素,在key 对应 list 的尾部添加字符串元素
	 * @param key  list集合的key
	 * @param json  list集合的values 元素集合
	 * @return
	 */
	public static long rpush_list(String key,String[] values) {
		Jedis jedis = getJedis();
		long ret =0;
		if (jedis != null) {
			try {
				ret = jedis.rpush(key, values);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return ret;
	}
	public static List<String> get_list(String key,long start,long end) {
		Jedis jedis = getReadyOnlyJedis();
		List<String> list = null;
		if (jedis!=null) {
			try {
				list = jedis.lrange(key, start, end);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return list;
	}

	/**增量式迭代当前数据库中的数据库键
	 * @param pattern 模糊匹配的字符串
	 * @return key 的集合
	 */
	public static List<String> scan(String pattern) {
		Jedis jedis = getReadyOnlyJedis();
		List<String> list = new ArrayList<String>();
		if (jedis != null) {
			try {
				ScanParams params = new ScanParams();
				params.count(1000);
				params.match(pattern);
				String cursor = "0";
				do {
					ScanResult<String> result = jedis.scan(cursor, params);
					cursor = result.getStringCursor();
					list.addAll(result.getResult());
				} while (!"0".equals(cursor));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return list;
	}
	
	/**增量式迭代当前数据库中的数据库键
	 * @param pattern 模糊匹配的字符串
	 * @param count	  选项的作用就是让用户告知迭代命令, 在每次迭代中应该从数据集里返回多少元素,只是对增量式迭代命令的一种提示
	 * @return key 的集合
	 */
	public static List<String> scan(String pattern,int count) {
		Jedis jedis = getReadyOnlyJedis();
		List<String> list = new ArrayList<String>();
		if (jedis != null) {
			try {
				ScanParams params = new ScanParams();
				params.count(count);
				params.match(pattern);
				String cursor = "0";
				do {
					ScanResult<String> result = jedis.scan(cursor, params);
					cursor = result.getStringCursor();
					list.addAll(result.getResult());
				} while (!"0".equals(cursor));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				returnRes(jedis);
			}
		}
		return list;
	}
	
	/**
	 * @param jedis Redis实例,当前方法中没有归还链接,用完后需要归还次jedis
	 * @param pattern 模糊匹配的字符串
	 * @return key的集合
	 */
	public static List<String> scan(Jedis jedis,String pattern) {
		List<String> keys = new ArrayList<String>();
		ScanParams params = new ScanParams();
		params.count(1000);
		params.match(pattern);
		String cursor = "0";
		do {
			ScanResult<String> result = jedis.scan(cursor, params);
			cursor = result.getStringCursor();
			keys.addAll(result.getResult());
		} while (!"0".equals(cursor));
		return keys;
	}
	/** 
	 * jedis操作Set 
	 */  
	private static void testSet(Jedis jedis){  
	    //添加  
	    jedis.sadd("user","liuling");  
	    jedis.sadd("user","xinxin");  
	    jedis.sadd("user","ling");  
	    jedis.sadd("user","zhangxinxin");
	    jedis.sadd("user","who");  
	    //移除noname  
	    jedis.srem("user","who");  
	    System.out.println(jedis.smembers("user"));//获取所有加入的value  
	    System.out.println(jedis.sismember("user", "who"));//判断 who 是否是user集合的元素  
	    System.out.println(jedis.srandmember("user"));  
	    System.out.println(jedis.scard("user"));//返回集合的元素个数  
	}  
	/** 
	 * jedis操作List 
	 */  
	private static void testList(Jedis jedis){  
	    //开始前，先移除所有的内容  
	    jedis.del("java framework");  
	    //先向key java framework中存放三条数据 在key 对应 list的头部添加字符串元素
	    jedis.lpush("java framework","spring");  
	    jedis.lpush("java framework","struts");  
	    jedis.lpush("java framework","hibernate");  
	    //再取出所有数据jedis.lrange是按范围取出，  
	    // 第一个是key，第二个是起始位置，第三个是结束位置，jedis.llen获取长度 -1表示取得所有  
	    System.out.println(jedis.lrange("java framework",0,-1));  

	    jedis.del("java framework");
	    //在key 对应 list 的尾部添加字符串元素
	    jedis.rpush("java framework","spring");  
	    jedis.rpush("java framework","struts");  
	    jedis.rpush("java framework","hibernate"); 
	    System.out.println(jedis.lrange("java framework",0,-1));
	}  
	public static void main(String[] args) {
		Jedis jedis = getJedis();
		try {
			System.out.println(scan(jedis,"ZCSR21*"));
//			long d = new Date().getTime()/1000;
//			System.out.println("SH600604:volumes=="+jedis.get("SH600604:volumes"));
//			System.out.println("SH600604:latest=="+jedis.get("SH600604:latest"));
//			System.out.println("SH600604:details=="+jedis.lrange("SH600604:details", 0, -1));
//			System.out.println("SH000001:points==SH000001:points:"+d+"==="+jedis.get("SH000001:points:1447133100"));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			returnRes(jedis);
		}
	}
}
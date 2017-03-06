package com.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.util.common.Constant;

public class RedisClient {

	private static JedisPool pool;

	/**
	 * 建立连接池 真实环境，一般把配置参数缺抽取出来。
	 * 
	 */
	private static void createJedisPool(String port,String password) {

		// 建立连接池配置参数
		JedisPoolConfig config = new JedisPoolConfig();

		// 设置最大连接数
		config.setMaxActive(100);

		// 设置最大阻塞时间，记住是毫秒数milliseconds
		config.setMaxWait(20000l);

		// 设置空间连接
		config.setMaxIdle(5);
		if (password==null) {
			pool = new JedisPool(config, port, 6379,10000);
		}else {
			// 创建连接池
			pool = new JedisPool(config, port, 6379,10000,password);
		}

	}

	/**
	 * 在多线程环境同步初始化
	 */
	private static synchronized void poolInit() {
		if (pool == null)
			createJedisPool(Constant.SFTP_REQ_HOST, Constant.REDIS_PWD112);
//			createJedisPool("127.0.0.1", null);
	}

	/**
	 * 获取一个可读写jedis 对象
	 * 
	 * @return
	 */
	public static Jedis getJedis() {
		if (pool == null)
			poolInit();
		Jedis jedis =null;
		try {
			jedis = pool.getResource();
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				jedis = pool.getResource();
			} catch (JedisConnectionException e2) {
				e2.printStackTrace();
				createJedisPool(Constant.SFTP_REQ_HOST169, Constant.REDIS_PWD169);
//				createJedisPool("192.168.31.188", "e260e7bf");
				try {
					jedis = pool.getResource();
//					Common.send_code_channel("验证码:error,redis数据库获取连接出错[mimikj:"+DateUtils.DateToStr(new Date(), "yyyy-MM-dd HH:mm:ss")+"]", "15158152796");
//					jedis.slaveofNoOne();
				} catch (JedisConnectionException e3) {
					e2.printStackTrace();
				}
			}
		}
		return jedis;
	}
	/**
	 * 获取一个只读jedis 对象
	 * 
	 * @return
	 */
	public static Jedis getReadonlyJedis() {
		Jedis jedis =null;
		createJedisPool(Constant.SFTP_REQ_HOST216, Constant.REDIS_PWD216);
		try {
			jedis = pool.getResource();
		} catch (JedisConnectionException e1) {
			e1.printStackTrace();
			createJedisPool(Constant.SFTP_REQ_HOST169, Constant.REDIS_PWD169);
			try {
				jedis = pool.getResource();
				jedis.slaveofNoOne();
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
		pool.returnResource(jedis);
	}
	
	public static void main(String[] args) {
		Jedis jedis = getJedis();
		try {
//			System.out.println("sz002798:volumes=="+jedis.get("SZ002798:volumes"));
//			System.out.println("sz002798:latest=="+jedis.get("SZ002798:latest"));
//			System.out.println("sz002798:details=="+jedis.lrange("SZ002798:details", 0, -1));
//			System.out.println("SH600069:points==SH600069:points:1469071800"+jedis.get("SH600069:points:1469071800"));
			
			// 保存对象到redis
		/*	String key = "test";
			ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("aaa", "ddasfdsa");
			map.put("bbb", "ddsafd");
			HashMap<String, Object> map2 = new HashMap<String, Object>();
			map2.put("eweee", "deeeee");
			map2.put("dsafdddd", "dddddd");
			HashMap<String, Object> map3 = new HashMap<String, Object>();
			map3.put("dddddddd", "dsafdsafdsa");
			map3.put("ddddsafds", "dsafdsaf");
			list.add(map);
			list.add(map2);
			list.add(map3);
			jedis.set(key.getBytes(), Common.serialize(list));
			
			byte[] in = jedis.get(key.getBytes());
			Object o = Common.deserialize(in);
			ArrayList<HashMap<String, Object>> li = (ArrayList<HashMap<String, Object>>) o;
			for (HashMap<String, Object> hashMap : li) {
				System.out.println(hashMap.keySet()+"====="+hashMap.values());
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
//			returnRes(jedis);
		}
	}
}
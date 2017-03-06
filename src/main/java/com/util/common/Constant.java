package com.util.common;

public class Constant {
	/**
	 * 上午开始时间
	 */
	public final static String MORNING_START_TIME = "09:30";

	/**
	 * 上午结束时间
	 */
	public final static String MORNING_END_TIME = "11:30";

	/**
	 * 下午开始时间
	 */
	public final static String AFTERNOON_START_TIME = "13:00";

	/**
	 * 下结束时间
	 */
	public final static String AFTERNOON_END_TIME = "15:00";
	/**
	 * 阿里云服务器地址
	 */
	public final static String SFTP_REQ_HOST = "10.252.114.119"; //115.29.13.27 正式库;121.41.22.169(公) 10.168.165.127(内)测试库 120.26.57.112(公) 新公网 10.252.114.119(内)新服务器内网地址
	public final static String REDIS_PWD112 = "cd1ec2bd"; //redis 密码
	public final static String SFTP_REQ_HOST216 = "10.160.86.235"; 
	public final static String REDIS_PWD216 = "e260e7bf";
	public final static String SFTP_REQ_HOST169 = "10.168.165.127"; 
	public final static String REDIS_PWD169 = "5fa38c15";
	
	
	/**
	 * 阿里云图片上传服务器用户名
	 */
	public final static String SFTP_REQ_USERNAME = "robot";
	/**
	 * 阿里云图片上传服务器密码
	 */
	public final static String SFTP_REQ_PASSWORD = "candzen";
	/**
	 * 阿里云图片上传服务器端口号
	 */
	public final static String SFTP_REQ_PORT = "22";
	/**
	 * 阿里云图片上传根目录
	 */
	public final static String IMAGE_BASE_PATH = "/usr/local/tomcat/apache-tomcat-7.0.63_8081/webapps/mimikj/images";
//	public final static String IMAGE_BASE_PATH = "F:/workspace3/images";
	/**
	 *权息数据路径
	 */
	public final static String STOCK_RIGHT_DATA_PATH = "/usr/local/tomcat/temp/wsSHSZ_SPLITs.txt";
//	public final static String STOCK_RIGHT_DATA_PATH = "C:/wsWDZ/wsSHSZ_SPLITs.txt";
	/**
	 *K线wdz数据路径
	 */
	public final static String KLINE_DATA_PATH = "/usr/local/tomcat/temp/klines_data/wstock_SHSZ_Day.zip";
//	public final static String KLINE_DATA_PATH = "e:/workspace3/dbback/wstock_SHSZ_Day.zip";
	/**
	 *用户默认头像
	 */
	public final static String USER_DEFALUT_PIC = "user/default/default.png";
	/**
	 * 发送短信接口的host(志晴短信机，停用)
	 */
	public final static String SEND_MSG_HOST = "http://sms.4006555441.com";
	/**
	 * 发送短信接口的地址(示远短信机，使用中)
	 */
	public final static String SEND_MSG_PATH = "http://120.26.69.248/msg/HttpSendSM";
	/**
	 * 发送短信的账号
	 */
	public final static String MSG_ACCOUNT = "004256"; //004256 正式 002002 测试
	/**
	 * 发送短信的账号
	 */
	public final static String MSG_PSWD = "Zhongmiapp889";  //Zhongmiapp889 正式 Sy123456 测试
	/**
	 * 短信验证码过期分钟
	 */
	public final static int EXPIRES_IN = 15;
	
	/**
	 * leancloud 推送URL
	 */
	public final static String PUSH_REMIND_URL = "https://cn.avoscloud.com/1.1/push";
	
	/**
	 *极光推送URL
	 */
	public final static String JPUSH_REMIND_URL = "https://api.jpush.cn/v3/push";
	/**
	 *极光推送APPKEY
	 */
	public final static String JPAPPKEY ="86d269801e79955f8664a430";
	/**
	 *极光推送MASTERSECRET
	 */
	public final static String JPMASTERSECRET = "5075e42463be47bce6842392";
	
//==============================================下面 聚财道相关常量===============================================
	/**
	 * 聚财道域名 Token Endpoints
	 */
//	public final static String JCD_TOKEN_DOMAIN = "https://oauth.jucaidao.com,https://proxy.trubuzz.cn/oauth"; //旧版本
	public final static String JCD_TOKEN_DOMAIN = "https://proxy.jucaidao.com/oauth";
	/**
	 * 聚财道域名 GURU API （行情）
	 */
//	public final static String JCD_GURU_DOMAIN = "https://guruapi.trubuzz.com,https://proxy.trubuzz.com/guru,https://proxy.jucaidao.com/guru";
	public final static String JCD_GURU_DOMAIN = "https://proxy.trubuzz.com/guru";
	/**
	 * 聚财道域名 出入金
	 */
//	public final static String JCD_BANK_DOMAIN = "https://api.followpro.net,https://proxy.trubuzz.cn/omin";
	public final static String JCD_BANK_DOMAIN = "https://proxy.jucaidao.com/omin";
	/**
	 * 聚财道域名 注册
	 */
//	public final static String JCD_REG_DOMAIN = "https://api.jucaidao.com,https://proxy.trubuzz.cn/jucaidao";
	public final static String JCD_REG_DOMAIN = "https://proxy.jucaidao.com/jucaidao";
	/**
	 * 聚财道域名 Order API (交易相关)
	 */
//	public final static String JCD_ORDER_DOMAIN = "https://topapi.jucaidao.com,https://proxy.trubuzz.cn/top";
	public final static String JCD_ORDER_DOMAIN = "https://proxy.jucaidao.com/top";
	/**
	 * 聚财道CLIENT_ID
	 */
	public final static String CLIENT_ID = "YyxOKQNKXWXiX4JUKhjs7aM0RDdbM5KTktqffUcB";
	
	/**
	 *聚财道SECRET
	 */
	public final static String SECRET = "zzDGI0rMnmjKzAd5X0XvfzxfjXsndgFT2i4TIwcq";
	
	/**
	 * 聚财道SCOPES
	 */
//	public final static String SCOPES = "tbCAccountBind,ominBasic";
	public final static String SCOPES = "ominBasic,ominThird,tbAgency";
	
	/**
	 * 聚财道SCOPES
	 */
	public final static String AGENCY = "MI-US-STK";
	/**
	 * 聚财道TOPApiVersion
	 */
	public final static String TOPAPIVERSION = "1.2";
	
	//-----------------------------------------------------------交易比赛排名奖励-----------------------------------------------------
	/**
	 * 周排名，第1名300
	 */
	public final static String W_TOP1 = "300";
	/**
	 * 周排名，第2名150
	 */
	public final static String W_TOP2 = "150";
	/**
	 * 周排名，第3名100
	 */
	public final static String W_TOP3 = "100";
	/**
	 * 周排名，第4-10名50
	 */
	public final static String W_TOP4_10 = "50";
	/**
	 * 周排名，第11-50名10
	 */
	public final static String W_TOP11_50 = "10";
	
	/**
	 * 月排名，第1名2000
	 */
	public final static String M_TOP1 = "2000";
	/**
	 * 月排名，第2名1000
	 */
	public final static String M_TOP2 = "1000";
	/**
	 * 月排名，第3名500
	 */
	public final static String M_TOP3 = "500";
}

package com.hnrd.wxPay.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hnrd.wxPay.model.PayInfo;
import com.thoughtworks.xstream.XStream;

/**
 * 发送给微信服务器的数据需要特定格式和要求，此工具类是调用过程需要用到的方法
 * @author licangyue
 */
public class CommonUtils {
	
	/**
	 * 1.微信支付调起的接口都是需要向微信支付服务器发送xml格式的数据作为通知
	 * 2.服务器验证信息无误时根据业务情况返回数据。因此需要一个将信息转换成xml格式的方法
	 * 3.PayInfo对象封装了要发送到微信服务器上的订单数据信息(具体哪些信息看微信小程序支付开发文档)
	 * 4.xstream提供了将对象转化为xml格式的数据，使用之前需要实例化xstream，这里是对PayInfo对象转xml数据的方法
	 * 5.如果遇到xstream实例化失败的问题，注意xstream的jar包版本与dom4j包的版本匹配问题.
	 */
	
	private static XStream xstream = new XStream();
	public static String payInfoToXML(PayInfo payInfo) {
		xstream.alias("xml", payInfo.getClass());
		return xstream.toXML(payInfo);
	}
	
	/**
	 * 将对象转化为xml数据格式
	 * @param t
	 * @return
	 */
	public static <T> String objectToXML(T t) {
			xstream.alias("xml", t.getClass());
			String xml=xstream.toXML(t); 
			return xml.replace("__", "_").replace("<![CDATA[1]]>", "1");
		}

	/**
	 * 对对象数据进行签名
	 *  微信支付過程中有多处地方需要对传送或者返回的数据进行签名，签名规则
	 * https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_3
	 * @param key
	 * @param parameters
	 * @return
	 * @throws Exception 
	 */
	public static String createSign(String key, Map<String, Object> parameters) throws Exception {
		StringBuffer sb = new StringBuffer();
		if (!(parameters instanceof SortedMap<?, ?>)) {
			parameters = new TreeMap<String, Object>(parameters);
		}
		Set<?> es = parameters.entrySet();
		Iterator<?> it = es.iterator();	
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			Object v = entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k)
					&& !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + key);
		String sign = getMD5(sb.toString().trim()).toUpperCase();
		return sign;
	}
	/**
	 * 对字符串进行MD5加密
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String getMD5(String str) throws Exception {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			return new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {
			throw new Exception("MD5加密出现错误");
		}
	}
	/**
	 * 將對象轉化為 map
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> object2Map(Object obj) {
		Map<String, Object> map = new HashMap<>();
		if (obj==null) {
			return map;
		}
		Class<?> clazz=obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		try {
		for (Field field : fields) {
			field.setAccessible(true);
			map.put(field.getName(), field.get(obj));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 將map转化为实体对象
	 * @param args
	 */
	public static Object map2Object(Map<String, Object> map, Class<?> clazz) {

		if (map == null) {
			return null;
		}
		Object obj = null;
		try {
			obj = clazz.newInstance();
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field field : fields) {
				int mod = field.getModifiers();
				if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
					continue;
				}
				field.setAccessible(true);
				field.set(obj, map.get(field.getName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	/**
	 * 將xml格式的数据解析为map,用于将微信服务器返回的数据进行解析
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> parseXml(String xml) throws Exception {

		Map<String, Object> map = new HashMap<String,Object>();
		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> elementList = root.elements();
		for (Element e : elementList)
			map.put(e.getName(), e.getText());
		return map;
	}
	/**
	 * 获取客户端的ip，订单字段中有个数据需要获取客户端ip
	 * @param request
	 * @return
	 */
	public static String getClientIp(HttpServletRequest request) {

		String ip = request.getHeader("X-Forwarded-For");
		if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			int index = ip.indexOf(",");
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			return ip;
		}
		return request.getRemoteAddr();
	}

	/**
	 * 以post请求的方式向指定url发送xml格式的消息,如要发送get请求将代码中HttpPost对象改为HttpGet
	 * @param url
	 * @param xml
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String sentHttpRequest(String url,String xml) throws ClientProtocolException, IOException {
		
		CloseableHttpClient httpClient=HttpClients.createDefault();
		HttpPost httpPost=new HttpPost(url);
		HttpEntity httpEntity = new StringEntity(xml);
		httpPost.setEntity(httpEntity);;
		try (CloseableHttpResponse httpResponse=httpClient.execute(httpPost)){
			if (httpResponse.getStatusLine().getStatusCode()==200) {
				System.out.println("访问成功！");
			}
			httpPost.setHeader("Content-Type","text/xml");
			httpPost.setHeader("charset","utf-8");
			httpResponse.setHeader("Content-Type","text/html");
			httpResponse.setHeader("charset","utf-8");
			HttpEntity entity=httpResponse.getEntity();
			InputStream iStream=entity.getContent();
			String result = new BufferedReader(new InputStreamReader(iStream))
					  .lines().parallel().collect(Collectors.joining("\n"));
			
			return result;
		
		} catch (Exception e) {
			return "连接异常!";
		}	
	}
	public static void main(String[] args) {
		Object str=122;
		int b =122;
		Integer a=122;
		System.out.println(b==(int)str);
		System.out.println(a.toString().equals("122"));
	}

}

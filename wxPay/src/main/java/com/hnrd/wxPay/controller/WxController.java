package com.hnrd.wxPay.controller;

import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.hnrd.wxPay.constans.Constant;
import com.hnrd.wxPay.model.PayInfo;
import com.hnrd.wxPay.model.WxPayResponesModel;
import com.hnrd.wxPay.pojo.MerchantOrder;
import com.hnrd.wxPay.utils.CommonUtils;
import com.hnrd.wxPay.utils.HttpRequest;
import com.hnrd.wxPay.utils.TimeUtils;

@RestController
@RequestMapping("pay")
public class WxController {
	
	private static final Logger log = LoggerFactory.getLogger(WxController.class);
	/**
	 * 登陆小程序接口：微信用户登陆小程序，先自动生成code发送至用户系统服务器，查看返回openid是否正确用以验证该系统是否为小程序对应的用户系统，
	 * @param code :code由小程序登陆时自动生成，传入后台服务器，发送至微信服务器获取openid以及session，将openid与session返回给前端
	 * openid生成正确时，登陆小程序成功，session用于保存用户会话信息，可以自己定义其他用途
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("getOpenid")
	public ResponseEntity<Map<String, String>> getOpenidAndSession(String code){
		
		StringBuffer sb = new StringBuffer();
		sb.append("appid=").append(Constant.APP_ID);
		sb.append("&secret=").append(Constant.APP_SECRET);
		sb.append("&js_code=").append(code);
		sb.append("&grant_type=").append(Constant.GRANT_TYPE);
		String res = HttpRequest.sendGet(Constant.URL_GET_OPENID, sb.toString());//将拼接好的消息发送至微信服务器获取返回值
		if (res == null || res.equals("")) {
			return null;
		}
		log.info(res);
		Map<String, String> map=JSON.parseObject(res, Map.class);
		return new ResponseEntity<>(map,HttpStatus.OK);
	}
	
	
	
	
	/**
	 * 统一下单接口步骤：
	 * 1.根据订单内容，构建微信订单信息
	 * 2.对订单信息进行签名
	 * 3.将签名加入微信订单信息，将订单对象内的信息转化为xml格式数据，发送至微信服务器
	 * 4.解析微信服务器返回的数据(微信服务器返回xml数据)，主要获取其中的prepay_id，nonceStr
	 * 5.对五个字段进行签名后，连同参与签名的字段和签名返回给调用此接口的客户端（五个字段：appId,timeStamp,nonceStr,prepay_id,signType区分大小写，不能写错）
	 * 6.微信服务器进行签名验证后，允许小程序拉起支付，用户输入密码后，支付成功。
	 * @param outTradeNo 商户订单号：此订单号目的是获取商户系统的订单数据，并根据微信订单字段要求，构建微信订单信息(实际上只要成功构建正确的微信订单信息（payInfo），不管你怎么获取信息都行)
	 * 注：商户订单由后台人员自己构建，这里
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("unifiedorder")
	public ResponseEntity<Map<String,Object>> unifiedorder(String outTradeNo,HttpServletRequest request) throws Exception{
		/**
		 *  1 .根据商户订单信息构建微信订单，商户订单由自己设计，但一定要包含MerchantOrder里的信息
		 *  2.MerchantOrder我给了必须的几个字段，如果需要其他字段可根据需求增加
		 *  3.Merchantorder应根据订单号（outTradeNo）从数据库（最好时redis，mangoDB等缓存数据库）中查询获取，这里为了方便，直接构建一个
		 */
		MerchantOrder merchantOrder=new MerchantOrder();
		merchantOrder.setAttach("备注：随便写");
		merchantOrder.setBody("商品描述：任意字符串，比如：腾讯服务器实例 x 2");
		merchantOrder.setDetail("商品详情：任意字符串");
		merchantOrder.setTotal_fee(1);
		merchantOrder.setOut_trade_no(outTradeNo);
		
		/**
		 * 获取商户订单后，构建微信订单
		 */
		PayInfo payInfo=new PayInfo(merchantOrder, request);
		
		/**
		 * 构建完订单对象，对订单信息进行签名
		 */
		Map<String, Object> map =CommonUtils.object2Map(payInfo);
		String sign =CommonUtils.createSign(Constant.APP_KEY,map);
		
		/**
		 * 将签名sign写入微信订单信息
		 */
		payInfo.setSign(sign);
		
		/**
		 * 将订单信息转化为xml格式
		 */
		String xml = CommonUtils.objectToXML(payInfo);
		
		/**
		 * 发送至微信支付服务器
		 */
		String result = CommonUtils.sentHttpRequest(Constant.URL_UNIFIED_ORDER, xml);
		
		/**
		 * 将返回结果解析成map
		 */
		Map<String,Object> resultMap = CommonUtils.parseXml(result);
		resultMap.forEach((k,v)->System.out.println("key:"+k+"\tvalue"+v));
		/**
		 * 获取map中的数据，并根据返回结果进行处理
		 */
		if (resultMap.get("return_code").equals("SUCCESS")) { //return_codef返回SUCCESS为通信成功
			if (resultMap.get("result_code").equals("SUCCESS")) { //result_code返回SUCCESS为获取prepay_id成功
				
				/**
				 * 将获取的数据封装到对象中，签名后连同签名返回给接口调用的终端
				 */
				String prepay_id=(String)resultMap.get("prepay_id");
				log.info("prepay_id="+prepay_id);
				
				/**
				 * 获取其他参数， appId,nonceStr,package,signType,timeStamp 获取值并进行再次签名
				 */
				String nonceStr=(String) resultMap.get("nonce_str");
				log.info("微信返回的随机字符串："+nonceStr);
				String timeStamp=TimeUtils.localdatetimeTimestamp();	
				
				/**
				 * 对准备返回的参数进行封装，方便调用签名算法签名，WxPayResponesModel是用来封装参数的对象
				 */
				WxPayResponesModel WxPayResponesModel = new WxPayResponesModel();
				WxPayResponesModel.setAppId(Constant.APP_ID);
				WxPayResponesModel.setNonceStr(nonceStr);
				WxPayResponesModel.setPrepay_id(prepay_id);
				WxPayResponesModel.setSignType("MD5");
				WxPayResponesModel.setTimeStamp(timeStamp);
				
				/**
				 * 对参数再次签名
				 */
				Map<String,Object> SignMap = CommonUtils.object2Map(WxPayResponesModel);
				String signAgain=CommonUtils.createSign(Constant.APP_KEY,SignMap);
				log.info("微信返回的签名:"+resultMap.get("sign"));
				log.info("再次签名："+signAgain);
				SignMap.put("appId", Constant.APP_ID);
				SignMap.put("timeStamp",timeStamp);
				SignMap.put("nonceStr",nonceStr);
				SignMap.put("package", "prepay_id="+prepay_id);
				SignMap.put("signType","MD5");
				SignMap.put("sign",signAgain);
				
				/**
				 * 万事具备了，返回最终结果给前端
				 */
				return new ResponseEntity<>(SignMap,HttpStatus.OK); 
			}
			return new ResponseEntity<>(resultMap,HttpStatus.OK); 
		}
		return new ResponseEntity<>(resultMap,HttpStatus.OK); 

	}

}

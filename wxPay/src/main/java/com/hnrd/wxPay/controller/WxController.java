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
import com.hnrd.wxPay.utils.CommonUtils;
import com.hnrd.wxPay.utils.HttpRequest;
import com.hnrd.wxPay.utils.StringToLocalDateTimeUtils;
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
		
		PayInfo payInfo = new PayInfo();//创建微信订单信息对象，应该根据商户订单号查询商户订单后把信息转到微信订单上，这里我直接构建
		payInfo.setAppid(Constant.APP_ID);
		payInfo.setAttach("备注：随便写");
		payInfo.setBody("商品描述：任意字符串，比如：腾讯服务器实例 x 2");
		payInfo.setDetail("商品详情：任意字符串");
		payInfo.setDevice_info("WEB"); // 自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
		payInfo.setFee_type("CNY"); // 币种 默认CNY(人民币) 可不填
		payInfo.setLimit_pay("no_credit"); // 上传此参数no_credit--可限制用户不能使用信用卡支付
		payInfo.setMch_id(Constant.MCH_ID); // 微信支付分配的商户号
		payInfo.setNonce_str("nonce_str"); // 随机字符串，长度要求在32位以内。推荐随机数生成算法
		payInfo.setNotify_url(Constant.URL_NOTIFY); // 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
		payInfo.setOpenid("openid"); // 用户在商户appid下的唯一标识。openid如何获取，可参考 https://developers.weixin.qq.com/miniprogram/dev/api/api-login.html
		payInfo.setOut_trade_no(outTradeNo);// 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*且在同一个商户号下唯一
		payInfo.setSpbill_create_ip(CommonUtils.getClientIp(request)); // APP和H5支付提交用户端ip，Native支付填调用微信支付API的机器IP。
		payInfo.setTime_start(TimeUtils.getFormatTime(StringToLocalDateTimeUtils.getCTTDateTime())); // 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
		payInfo.setTime_expire(TimeUtils.getFormatTime(StringToLocalDateTimeUtils.getCTTDateTime().plusMinutes(60))); // 订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010
		payInfo.setTotal_fee(1); // 总金额
		payInfo.setTrade_type("JSAPI"); // 小程序直接写 JSAPI
		
		// 构建完订单对象，对订单信息进行签名
		Map<String, Object> map =CommonUtils.object2Map(payInfo);
		String sign =CommonUtils.createSign(Constant.APP_KEY,map);
		
		// 将签名sign写入微信订单信息
		payInfo.setSign(sign);
		
		//将订单信息转化为xml格式
		String xml = CommonUtils.objectToXML(payInfo);
		
		//发送至微信支付服务器
		String result = CommonUtils.sentHttpRequest(Constant.URL_UNIFIED_ORDER, xml);
		
		//将返回结果解析成map
		Map<String,Object> resultMap = CommonUtils.parseXml(result);
		resultMap.forEach((k,v)->System.out.println("key:"+k+"\tvalue"+v));
		//判断返回信息
		if (resultMap.get("return_code").equals("SUCCESS")) { //return_codef返回SUCCESS为通信成功
			if (resultMap.get("result_code").equals("SUCCESS")) { //result_code返回SUCCESS为获取prepay_id成功
				//  将获取的数据封装到对象中，签名后连同签名返回给接口调用终端
				String prepay_id=(String)resultMap.get("prepay_id");
				log.info("prepay_id="+prepay_id);
				//获取其他参数， appId,nonceStr,package,signType,timeStamp 获取值并进行再次签名
				String nonceStr=(String) resultMap.get("nonce_str");
				log.info("微信返回的随机字符串："+nonceStr);
				String timeStamp=TimeUtils.localdatetimeTimestamp();	
				//将要返回的参数封装
				WxPayResponesModel WxPayResponesModel = new WxPayResponesModel();
				WxPayResponesModel.setAppId(Constant.APP_ID);
				WxPayResponesModel.setNonceStr(nonceStr);
				WxPayResponesModel.setPrepay_id(prepay_id);
				WxPayResponesModel.setSignType("MD5");
				WxPayResponesModel.setTimeStamp(timeStamp);
				//对参数再次签名
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
				
				//返回数据
				return new ResponseEntity<>(SignMap,HttpStatus.OK); 
			}
			return new ResponseEntity<>(resultMap,HttpStatus.OK); 
		}
		return new ResponseEntity<>(resultMap,HttpStatus.OK); 

	}

}

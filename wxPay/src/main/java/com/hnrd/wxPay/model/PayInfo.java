package com.hnrd.wxPay.model;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import com.hnrd.wxPay.constans.Constant;
import com.hnrd.wxPay.pojo.MerchantOrder;
import com.hnrd.wxPay.utils.CommonUtils;
import com.hnrd.wxPay.utils.RandomUtils;
import com.hnrd.wxPay.utils.TimeUtils;

/**
 * 此对像封装了微信小程序支付时发送给微信服务器的订单数据
 * 详情参考微信支付开发者文档(小程序) https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=9_1
 */
public class PayInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String appid;       //小程序appid

	private String mch_id;      //开通微信支付服务会得到一个商户号，需要到微信商户平台上查看

	private String device_info; //设备号，小程序传"WEB"

	private String nonce_str;   //随机生成32位以内的字符串 

	private String sign;        //签名,将数据发送至微信服务器时需要对订单内容进行签名，详情参考微信文档

	private String sign_type;   //签名类型，默认为"MD5"

	private String body;        //商品描述

	private String detail;      //商品详情 

	private String attach;      //附加数据，可作为自定义参数使用

	private String out_trade_no;//商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*且在同一个商户号下唯一

	private String fee_type;    //符合ISO 4217标准的三位字母代码，默认人民币：CNY，详细列表请参见货币类型
	
	private int total_fee;      //订单总金额，单位为分

	private String spbill_create_ip;//APP和H5支付提交用户端ip，Native支付填调用微信支付API的机器IP

	private String time_start;  //订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则

	private String time_expire; //订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。订单失效时间是针对订单号而言的，由于在请求支付的时候有一个必传参数prepay_id只有两小时的有效期，所以在重入时间超过2小时的时候需要重新请求下单接口获取新的prepay_id。其他详见时间规则

	private String notify_url;  //通知地址,异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。

	private String trade_type;  //交易类型,小程序填 JSAPI 如果类型为Native时，需要一个额外的字段product_id(商户自定义的商品id),

	private String limit_pay;   //指定支付方式，no_credit

	private String openid;      //trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识。
	
	public String getFee_type() {
		return fee_type;
	}

	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}
	
	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getDevice_info() {
		return device_info;
	}

	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public int getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(int total_fee) {
		this.total_fee = total_fee;
	}
	
	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}

	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}

	public String getTime_start() {
		return time_start;
	}

	public void setTime_start(String time_start) {
		this.time_start = time_start;
	}

	public String getTime_expire() {
		return time_expire;
	}

	public void setTime_expire(String time_expire) {
		this.time_expire = time_expire;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

	public String getLimit_pay() {
		return limit_pay;
	}

	public void setLimit_pay(String limit_pay) {
		this.limit_pay = limit_pay;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	public PayInfo() {
		super();
	}

	public PayInfo(MerchantOrder merchantOrder,HttpServletRequest request) {
		this.setBody(merchantOrder.getBody()); 
		this.setDetail(merchantOrder.getDetail());
		this.setAttach(merchantOrder.getAttach());
		this.setTotal_fee(merchantOrder.getTotal_fee()); // 总金额
		this.setOut_trade_no(merchantOrder.getOut_trade_no());// 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*且在同一个商户号下唯一
		this.setAppid(Constant.APP_ID);
		this.setDevice_info("WEB"); // 自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
		this.setFee_type("CNY"); // 币种 默认CNY(人民币) 可不填
		this.setLimit_pay("no_credit"); // 上传此参数no_credit--可限制用户不能使用信用卡支付
		this.setMch_id(Constant.MCH_ID); // 微信支付分配的商户号
		this.setNonce_str(RandomUtils.generateUpperString(32)); // 随机字符串，长度要求在32位以内。推荐随机数生成算法
		this.setNotify_url(Constant.URL_NOTIFY); // 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
		this.setOpenid("openid"); // 用户在商户appid下的唯一标识。openid如何获取，可参考 https://developers.weixin.qq.com/miniprogram/dev/api/api-login.html
		this.setTrade_type("JSAPI"); // 小程序直接写 JSAPI
		this.setSpbill_create_ip(CommonUtils.getClientIp(request)); // APP和H5支付提交用户端ip，Native支付填调用微信支付API的机器IP。
		this.setTime_start(TimeUtils.getFormatTime(TimeUtils.getCTTDateTime(),10)); // 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
		this.setTime_expire(TimeUtils.getFormatTime(TimeUtils.getCTTDateTime().plusMinutes(60),10)); // 订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010
	}
	
}

package com.hnrd.wxPay.model;
/**
 * 调用微信統一下單接口获取数据封装后返回给小程序
 * @author lee
 */
public class WxPayResponesModel {
	//发起再次签名的五个字段 appId,nonceStr,package,signType,timeStamp 与签名
	
	private String appId;
	
	private String timeStamp;
	
	private String nonceStr;
	 
	private String prepay_id;
	
	private String signType="MD5";
	
	private String paySign;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getPrepay_id() {
		return prepay_id;
	}

	public void setPrepay_id(String prepay_id) {
		this.prepay_id = prepay_id;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getPaySign() {
		return paySign;
	}

	public void setPaySign(String paySign) {
		this.paySign = paySign;
	}

	@Override
	public String toString() {
		return "WxPayResponesVo [appId=" + appId + ", timeStamp=" + timeStamp + ", nonceStr=" + nonceStr
				+ ", prepay_id=" + prepay_id + ", signType=" + signType + ", paySign=" + paySign + "]";
	}
	
}

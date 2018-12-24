package com.hnrd.wxPay.constans;
/**
 * 存放微信支付中服务器的接口地址，以及支付过程中需要用到的常量
 * @author lee
 *
 */
public class Constant {

    public static final String DOMAIN = "https://recycle.scublockchain.com";//可访问服务器地址
    //小程序appid
    public static final String APP_ID = "wxfdd49c2d980831a5";//申请小程序开发获取，这里随便写的
    
    public static final String GRANT_TYPE ="authorization_code";//直接写authorization_code
	//密钥
    public static final String APP_SECRET = "9211ea8eb9e45236cbadcbed6830b565";//申请完小程序后，小程序后台查看
    
    //商户号密钥 操作密码：xuyy0819.
    public static final String APP_KEY = "qwertyuiopasdfghjklzxcvbnm123456";//填写自己的，这里随便写的，注意需要32位
    
    //商户号,需要申请成为商户才能获取
    public static final String MCH_ID = "18584811692";//一串数字，自己去查看自己的商户号
    
    //获取 openid url
    public static final String URL_GET_OPENID="https://api.weixin.qq.com/sns/jscode2session";
    
    //统一下单url
    public static final String URL_UNIFIED_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    
    //查询订单url
    public static final String URL_QUERY_ORDER= "https://api.mch.weixin.qq.com/pay/orderquery";
    
    //关闭订单url
    public static final String URL_CLOSE_ORDER= "https://api.mch.weixin.qq.com/pay/closeorder";
    
    //申请退款url
    public static final String URL_REFUND= "https://api.mch.weixin.qq.com/secapi/pay/refund";
    
    //查询退款url
    public static final String URL_REFUND_QUERY= "https://api.mch.weixin.qq.com/pay/refundquery";
    
    //下载对账单url
    public static final String URL_DOWNLOAD_BILL= "https://api.mch.weixin.qq.com/pay/downloadbill";
    
    //下载资金对账单url
    public static final String URL_DOWNLOAD_FUNDFLOW= "https://api.mch.weixin.qq.com/pay/downloadfundflow";
    
    //微信支付结果通知页面地址
    public static final String URL_NOTIFY = Constant.DOMAIN + "/natify/info";
    
    //交易保障url
    public static final String URL_REPORT= "https://api.mch.weixin.qq.com/payitil/report";
    
    //拉取订单评价数据
    public static final String URL_COMMENT="https://api.mch.weixin.qq.com/billcommentsp/batchquerycomment";
    
   //十位数时间格式字符串
    public static final String TIME_FORMAT = "yyyyMMddHHmmss";
    
    //过期时间，单位是day
    public static final int TIME_EXPIRE = 2;  

}

package com.weaver.util.dev.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weaver.util.dev.log.DevLog;
import com.weaver.util.dev.log.LogFactory;
import weaver.rsa.security.RSA;

import java.util.HashMap;
import java.util.Map;

/**
 * description :
 * author ：JHY
 * date : 2020/6/3
 * version : 1.0
 */
public class IdentityVerifyUtil {

    LogFactory log = new DevLog();

    private String appid;
    private String host;
    //系统公钥信息
    private String SPK = null;
    //秘钥信息
    private String SECRET = null;


    public IdentityVerifyUtil(String appid, String host) {
        this.appid = appid;
        this.host = host;
    }


    public void regist() {
        log.info("regist==>开始执行");
        //httpclient的使用请自己封装，可参考ECOLOGY中HttpManager类
        HttpManager http = new HttpManager();
        //请求头信息封装集合
        Map<String, String> heads = new HashMap<String, String>();
        //获取当前异构系统RSA加密的公钥
        String cpk = new RSA().getRSA_PUB();
        log.info("regist==>cpk::"+cpk);
        //当前异构系统用于向ECOLOGY注册时使用的账号密码通过DES加密后密文进行传输
        //kb1906及以上版本 已废弃账号密码校验
        //封装参数到请求头
        heads.put("appid", this.appid);
        heads.put("cpk", cpk);
        //调用ECOLOGY系统接口进行注册
        try {
            String data = http.postDataSSL(this.host + "/api/ec/dev/auth/regist", new HashMap<>(), heads);
            log.info("regist==>data::"+data);
            //返回的数据格式为json，具体格式参数格式请参考文末API介绍。
            //注意此时如果注册成功会返回秘钥信息,请根据业务需要进行保存。
            if (data != null) {
                JSONObject result = JSON.parseObject(data);
                if ("true".equals(result.getString("status"))) {
                    this.SPK = result.getString("spk");
                    this.SECRET = result.getString("secrit");
                    System.out.println( result.getString("secrit"));
                }
            }
        } catch (Exception e) {
            log.error("regist::"+e.getMessage());
            e.printStackTrace();
        }
    }

    public String getToken() {
        //httpclient的使用请自己封装，可参考ECOLOGY中HttpManager类
        HttpManager http = new HttpManager();
        //请求头信息封装集合
        Map<String, String> heads = new HashMap<String, String>();
        RSA rsa = new RSA();
        //对秘钥进行加密传输，防止篡改数据
        String secret = rsa.encrypt(null, SECRET, null, "utf-8", SPK, false);
        //封装参数到请求头
        heads.put("appid", this.appid);
        heads.put("secret", secret);
        //调用ECOLOGY系统接口进行申请
        try {
            String data = http.postDataSSL(this.host + "/api/ec/dev/auth/applytoken", new HashMap<>(), heads);
            if (data != null) {
                JSONObject res = JSON.parseObject(data);
                log.info("token==>data::"+data);
                if ("true".equals(res.getString("status"))) {
                    return res.getString("token");
                }
            }
        } catch (Exception e) {
            log.error("token::"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取请求头信息
     * @param token
     * @param userid
     * @param spk
     * @return
     */
    public  Map<String, String> getHttpHeads(String token,String userid,String spk){
        Map<String, String> heads = new HashMap<>();
        heads.put("token", token);
        heads.put("appid", this.appid);
        RSA rsa = new RSA();
        String secretUserid = rsa.encrypt(null, userid, null, "utf-8", spk, false);
        heads.put("userid", secretUserid);
        return heads;
    }

    public String getSPK() {
        return SPK;
    }

    public String getSECRET() {
        return SECRET;
    }
}

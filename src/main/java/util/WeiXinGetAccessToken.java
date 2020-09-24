package util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WeiXinGetAccessToken {
    //从微信后台拿到APPID和APPSECRET 并封装为常量
    private static final String APPID = "wx8b7c57389365788a";
    private static final String APPSECRET = "76a0851aba46a8272b976add6fffc195";
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    public static  String  accessToken = "37_LLVZWT1kEKpTMFLBOinOroOfjYNkHJQLyDqD9U6AFJZo7B-NRMwrXYLRR5G2zAJO81TXCpA3N9VfuBwFGGBlYXnktpv6dDRk1eYlbuxxNuDhn_m-WvaCG0aaN4sgzhK4aoZFVvM7r_eD6RTIJBSjAGAVNE";
    /**
     * 获取AccessToken
     * @return 返回拿到的access_token及有效期
     */
    public static String getAccessToken() {
        Map<String, String> params = new HashMap<>();
        params.put("appid", APPID);
        params.put("secret", APPSECRET);
        params.put("grant_type", "client_credential");
        String response = HttpClientUtil.doGet("https://api.weixin.qq.com/cgi-bin/token", params);
        System.out.println(response);
        JSONObject accessTokenObject = JSONObject.parseObject(response);
        accessToken = accessTokenObject.getString("access_token");
        Long expire = accessTokenObject.getLong("expires_in");
        System.out.println(accessToken);
        return  accessToken;
    }

    public static void main(String[] args) {
        getAccessToken();
    }
}

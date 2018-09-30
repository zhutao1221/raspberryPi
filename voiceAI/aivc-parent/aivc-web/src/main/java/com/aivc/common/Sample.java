package com.aivc.common;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.aip.speech.AipSpeech;

public class Sample {
	//设置APPID/AK/SK
    public static final String APP_ID = "11395091";
    public static final String API_KEY = "cQ3FQ7iMch4dEyxxYbbKxMx3";
    public static final String SECRET_KEY = "RD6NjGKGK1fZ9kG2GI1CDNP3GCCnhTAC";

    public static void main(String[] args) throws JSONException {
        // 初始化一个AipSpeech
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        
        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
        //client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
        //client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        JSONObject res = client.asr("D:/2.wav", "wav", 8000, null);
        System.out.println(res.toString(2));

    }
}
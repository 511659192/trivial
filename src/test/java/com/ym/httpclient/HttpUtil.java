package com.ym.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author yangmeng44
 * @Date 2017/3/10
 */
public class HttpUtil {



    protected CloseableHttpResponse postBody(String url, String body, String charset) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

//        httpPost.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//        httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
//        httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
//        httpPost.setHeader("Accept-Encoding","gzip, deflate");
        httpPost.setEntity(new StringEntity(body,charset));

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000).setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();
        httpPost.setConfig(requestConfig);


        CloseableHttpResponse closeableHttpResponse = httpclient.execute(httpPost);


        return  closeableHttpResponse;
    }

    @Test
    public void test() throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("http://bizapi.valid.jd.local/api/giftCard/buy");

        httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
        httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
        httpPost.setHeader("Accept-Encoding","gzip, deflate");

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000).setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();
        httpPost.setConfig(requestConfig);


        CloseableHttpResponse closeableHttpResponse = httpclient.execute(httpPost);

        int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            HttpEntity resEntity = closeableHttpResponse.getEntity();
            String result = EntityUtils.toString(resEntity);
            System.out.println(result);
        } else {
            System.out.println("faefafafe");
        }

    }


    protected CloseableHttpResponse postBody(String url,String body,String charset,Map<String,String> headerMap) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
        httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
        httpPost.setHeader("Accept-Encoding","gzip, deflate");
//        for (Map.Entry<String, String> key:headerMap.entrySet()){
//            httpPost.setHeader(key.getKey(),key.getValue());
//        }

        httpPost.setEntity(new StringEntity(body,charset));

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000).setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();
        httpPost.setConfig(requestConfig);


        CloseableHttpResponse closeableHttpResponse = httpclient.execute(httpPost);


        return  closeableHttpResponse;
    }


    public  String getQueryData(String url,String param,Map<String,String> headerMap) throws IOException {
        CloseableHttpResponse closeableHttpResponse= postBody(url,param,"UTF-8",headerMap);
        try {
            if (closeableHttpResponse==null){
                return null;
            }
            return EntityUtils.toString(closeableHttpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("发送请求失败");
            return null;
        }
    }


    public  String getQueryData(String url,String param) throws IOException {
        CloseableHttpResponse closeableHttpResponse= postBody(url,param,"UTF-8");
        try {
            if (closeableHttpResponse==null){
                return null;
            }

            String str = EntityUtils.toString(closeableHttpResponse.getEntity());

            return str;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("发送请求失败");
            return null;
        }
    }


    @Test
    public void  test22() throws Exception {
        HttpUtil httpUtil = new HttpUtil();
        String string=     httpUtil.getQueryData("http://bizapi.vir.jd.local/api/order/getOrderByThirdId","token=GTceu5U0f6Sybkvfp5GtrnAng&thirdOrder=e3c1998f-d8d7-4035-a373-431f2669baf2","UTF-8");

        System.out.println(string);
    }


    public  String getQueryData(String url,String param,String token) throws IOException {
        Map<String,String>  headerMap = new HashMap<String,String>();
        CloseableHttpResponse closeableHttpResponse= postBody(url,param,"UTF-8");
        try {
            if (closeableHttpResponse==null){
                return null;
            }
            return EntityUtils.toString(closeableHttpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("发送请求失败");
            return null;
        }
    }

    public static void main(String[] args) {
        HttpUtil httpUtil = new HttpUtil();
        Map<String,String> headerMap = new HashMap<String,String>();
        headerMap.put("host","192.168.148.104 bizopenv.api.jd.com");

        try {
            String string=     httpUtil.getQueryData("http://bizapi.valid.jd.local/api/giftCard/buy", "mobile=15060778649&paymentType=4&sku=[{\"price\":1,\"num\":1}]&thirdOrder=ym-test-xirtual-20170220_2207&token=RtLbZk60DlnMV7gbY1P2eOL05", headerMap);
            System.out.println(string);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

package com.janady.utils;

import android.util.Log;

import com.janady.NameValuePair;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * @author  编码小王子
 * @date    2018年5月25日 下午6:17:31 
 * @version 1.0
 */
public class HttpUtils {
    // 发送GET请求
    public static String sendGet(String url, Map<String, String> parameters) {
        String result = "";
        BufferedReader in = null;// 读取响应输入流
        StringBuffer sb = new StringBuffer();// 存储参数
        String params = "";// 编码之后的参数
        try {
            // 编码请求参数
            if (parameters.size() == 1) {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8"));
                }
                params = sb.toString();
            } else {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8")).append("&");
                }
                String temp_params = sb.toString();
                params = temp_params.substring(0, temp_params.length() - 1);
            }
            String full_url = url + "?" + params;
            System.out.println(full_url);
            // 创建URL对象
            java.net.URL connURL = new java.net.URL(full_url);
            // 打开URL连接
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL.openConnection();
            // 设置通用属性
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
            // 建立实际的连接
            httpConn.connect();
            // 响应头部获取
            Map<String, List<String>> headers = httpConn.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : headers.keySet()) {
                System.out.println(key + "\t：\t" + headers.get(key));
            }
            // 定义BufferedReader输入流来读取URL的响应,并设置编码方式
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            Log.e("HttpUtil",e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
               // Log.e("HttpUtil",ex.getMessage());
            }
        }
        return result;
    }

    //发送POST请求
    public static String sendPost(String url, List<NameValuePair>  parameters) {
        String result = "";// 返回的结果
        BufferedReader in = null;// 读取响应输入流
        PrintWriter out = null;
        StringBuffer sb = new StringBuffer();// 处理请求参数
        String params = "";// 编码之后的参数
        try {
            // 编码请求参数
            if (parameters.size() == 1) {
                for (NameValuePair temp : parameters) {
                    sb.append(temp.name).append("=").append(java.net.URLEncoder.encode(temp.value.toString(), "UTF-8"));
                }
                params = sb.toString();
            } else {
                Log.v("HttpUtil","HttpUtils Params:\n");
                for (NameValuePair temp : parameters) {
                    if(temp.value.toString().length()>1024){
                        Log.v("HttpUtil",temp.name+" = "+temp.value.toString().substring(0,1024));
                    } else {
                        Log.v("HttpUtil",temp.name + " = " + temp.value);
                    }
                    sb.append(temp.name).append("=").append(java.net.URLEncoder.encode(temp.value.toString(), "UTF-8"))
                            .append("&");
                }
                String temp_params = sb.toString();
                params = temp_params.substring(0, temp_params.length() - 1);
            }
            // 创建URL对象
            java.net.URL connURL = new java.net.URL(url);
            // 打开URL连接
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL.openConnection();
            // 设置通用属性
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
            // 设置POST方式
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            // 获取HttpURLConnection对象对应的输出流
            out = new PrintWriter(httpConn.getOutputStream());
            // 发送请求参数
            out.write(params);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应，设置编码方式
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            //Log.e("HttpUtil",e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


    @SuppressWarnings("deprecation")
    public static JSONObject postData(String jsoncontent, String urlstr) {
        JSONObject jsonobj = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlstr);
            //添加http头信息  , 主要是application/json声明
            //httppost.addHeader("Content-Type", "application/json");
            //StringEntity就是以字符串输出到流
            //jsonobj = new JSONObject(jsoncontent);
            HttpEntity he = new StringEntity(jsoncontent, HTTP.UTF_8);
            ((StringEntity) he).setContentEncoding(HTTP.UTF_8);
            ((StringEntity) he).setContentType("application/json");
            httppost.setEntity(he);

            //如果是以参数传, 就是这个
//		    List<NameValuePair> params = new ArrayList<NameValuePair>();
//	        params.add(new BasicNameValuePair("paramname", "post data"));
//		    httppost.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));

            HttpResponse response = httpclient.execute(httppost);
            //检验状态码200表示成功
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                String returnjson = EntityUtils.toString(response.getEntity());//返回json格式
                jsonobj = new JSONObject(returnjson);
            }
        } catch (ClientProtocolException e) {
            Log.e("HttpUtil",e.getMessage());
        } catch (IOException e) {
            Log.e("HttpUtil",e.getMessage());
        } catch (Exception e) {
            Log.e("HttpUtil",e.getMessage());
        }
        return jsonobj;
    }

    /**
     *EMQ API请求，需要进行HTTP BASIC验证
     */
    public static String EMQ_Get(String urlStr){
        try{
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            String authString = "admin:public";
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            conn.setRequestProperty("Authorization", "Basic " + authStringEnc);//设置Authoriization字段

            conn.connect();

            int status = conn.getResponseCode();

            System.out.println(status);

            if(status == 200){
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str = "";
                StringBuffer sb = new StringBuffer();
                while((str=reader.readLine()) != null){
                    sb.append(str);
                }
                return sb.toString();
            }
            System.out.println("请求emq服务失败");
        }catch(Exception e){
            e.printStackTrace();
        }
        return "error";
    }

    /**
     47
     * 同步GET请求 带Authorization认证
     48
     */

    public static String okhttp_get(String get_url, HashMap<String, Object> get_data, String[] auth_base){

        OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                    .readTimeout(10000L, TimeUnit.MILLISECONDS)
                    .build();


        final String credential = Credentials.basic(auth_base[0], auth_base[1]);

        final String[] result = {""};

        String data_params = generateParameters(get_data);

        String data_url = get_url + data_params;

        final Request request = new Request.Builder()

                .url(data_url)

                .header("Authorization", credential)

                .get()

                .build();

        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            //判断是否成功
            if (response.isSuccessful()){
                result[0] = response.body().string();
            }else {
                return "请求失败";
            }

            /*call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String res = response.body().string();
                        Log.v("Response=", res);
                        if (!res.equals(null)) {
                            result[0] = response.body().string();
                        } else {
                            result[0]="请求失败";
                        }
                    }
                }
            });

            Log.d("HttpUtil", result[0]);*/



        } catch (Exception e) {

            Log.e("HttpUtil","网络GET请求失败！提示信息："+e.getMessage());

        }

        return result[0];

    }
    /**
     * Post请求 带Authorization认证
     * */
    public static String okhttp_post(String get_url, HashMap<String, Object> get_data, String[] auth_base){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();


        final String credential = Credentials.basic(auth_base[0], auth_base[1]);

        String result = "";

        RequestBody fromBody = generateParametersForPost(get_data).build();


        Request request = new Request.Builder()

                .url(get_url)

                .header("Authorization", credential)

                .post(fromBody)

                .build();

        Call call = client.newCall(request);

        try {

            Response response = call.execute();

            //判断是否成功
            if (response.isSuccessful()){
                result = response.body().string();
            }else {
                return "请求失败";
            }
            Log.d("HttpUtil",result);


        } catch (Exception e) {

            Log.e("HttpUtil","网络GET请求失败！提示信息："+e.getMessage());

        }

        return result;

    }

    //拼接参数

    private static String generateParameters(HashMap<String, Object> parameters) {

        String urlAttachment = "";

        if(parameters.size()>0){

            urlAttachment = "?";

            Object[] keys = parameters.keySet().toArray();

            for(Object key : keys)

                urlAttachment += key.toString() + "=" + parameters.get(key).toString() + "&";

            urlAttachment = urlAttachment.substring(0,urlAttachment.length()-1);
        }

        return urlAttachment;
    }

    //拼接参数用于POST请求

    private static FormBody.Builder generateParametersForPost(HashMap<String, Object> parameters) {

        FormBody.Builder builder = new FormBody.Builder();

        if(parameters.size()>0){
            Object[] keys = parameters.keySet().toArray();

            for(Object key : keys){
                Object ff = parameters.get(key);
                String aa = parameters.get(key).toString();

                builder.add(key.toString(),parameters.get(key).toString());
            }
        }

        return builder;
    }

}
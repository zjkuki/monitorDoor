package com.janady;

import android.util.Log;

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
import java.util.List;
import java.util.Map;


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

}
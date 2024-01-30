package com.centit.locode.platform.test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

public class TestPost {

    public static String doFilePost(String url, String filePath) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        // 发送POST请求必须设置如下两行
        // 设置请求头参数
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            File file=new File(filePath);
            FileBody fileBody=new FileBody(file);
            StringBody fileName = new StringBody(fileBody.getFilename());
            StringBody applyNo = new StringBody("fb481acaa95242edbed3bd01100d648d");
            StringBody businessType = new StringBody("businessType-2");
            StringBody docTypeId = new StringBody("edbed3bd01100d648d");
            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("applyNo", applyNo);
            reqEntity.addPart("docTypeId", docTypeId);
            reqEntity.addPart("fileName",fileName);
            reqEntity.addPart("files", fileBody);
            reqEntity.addPart("businessType", businessType);
            httpPost.setEntity(reqEntity);
            httpPost.setHeader("x-auth-token","72e2f701-dc0d-4da9-b52b-ea6f1150a3c4");
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }
    public static void main(String[] args) {
        System.out.println(
            doFilePost("http://localhost:8081/allInOne/dde/run/d785a0fe93d04e9a878108d399ef6ae5?ajax=true",
                "/Users/codefan/Documents/temp/ceshi.xlsx")
        );
    }
}

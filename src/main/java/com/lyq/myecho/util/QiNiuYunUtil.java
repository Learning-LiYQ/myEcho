package com.lyq.myecho.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;

@Component
public class QiNiuYunUtil {
    /**
     * AccessKey
     */
    @Value("${qiniu.key.access}")
    private String accessKey;
    /**
     * SecretKey
     */
    @Value("${qiniu.key.secret}")
    private String secretKey;
    /**
     * 存储空间名
     */
    @Value("${qiniu.bucket.header.name}")
    private String bucket;
    /**
     * 外链
     */
    @Value("${qiniu.bucket.header.url}")
    private String path;

    public String uploadQiniuYun(FileInputStream file) {
        // zone0华东区域,zone1是华北区域,zone2是华南区域
        Configuration cfg = new Configuration(Zone.zone1());
        UploadManager uploadManager = new UploadManager(cfg);
        // 生成上传凭证，然后准备上传
        try {
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            try {
                Response response = uploadManager.put(file, null, upToken, null, null);
                // 解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                return path + "/" + putRet.key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

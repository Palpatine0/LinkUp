package com.enchanted.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.enchanted.constant.AliyunOSSConstant;
import com.enchanted.service.IFileService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class FileServiceImpl implements IFileService {

    @Override
    public Map<String, String> signature(String dir) {
        String endpoint = AliyunOSSConstant.ALIYUN_OSS_ENDPOINT;
        String accessId = AliyunOSSConstant.ALIYUN_OSS_ACCESS_KEY_ID;
        String accessKey = AliyunOSSConstant.ALIYUN_OSS_ACCESS_KEY_SECRET;
        String bucket = AliyunOSSConstant.ALIYUN_OSS_BUCKET_NAME;
        String host = "https://" + bucket + "." + endpoint;

        // Validate 'dir' parameter
        if (!isValidDir(dir)) {
            throw new IllegalArgumentException("Invalid directory");
        }

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);

        try {
            long expireTime = 30; // Policy expiration time in seconds
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);

            // Post policy
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000); // File size limit
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = Base64.encodeBase64String(binaryData);
            String signature = ossClient.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", signature);
            respMap.put("dir", dir); // The directory to upload to
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));

            return respMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle the exception as needed
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public String upload(byte[] fileBytes, String filePath, String fileName) {
        String endpoint = AliyunOSSConstant.ALIYUN_OSS_ENDPOINT;
        String accessKeyId = AliyunOSSConstant.ALIYUN_OSS_ACCESS_KEY_ID;
        String accessKeySecret = AliyunOSSConstant.ALIYUN_OSS_ACCESS_KEY_SECRET;
        String bucketName = AliyunOSSConstant.ALIYUN_OSS_BUCKET_NAME;

        // Initialize the OSS client
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // Upload the file to OSS
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
            ossClient.putObject(bucketName, filePath + fileName, inputStream);

            // Generate the URL to access the file
            String fileUrl = "https://" + bucketName + "." + endpoint + "/" + filePath + fileName;
            return fileUrl;
        } finally {
            // Shut down the OSS client
            ossClient.shutdown();
        }
    }

    // Utility method to validate the 'dir' parameter
    private boolean isValidDir(String dir) {
        // Simple validation: dir should not contain any "../" or start with "/"
        if (dir == null || dir.contains("..") || dir.startsWith("/")) {
            return false;
        }
        // Ensure dir ends with '/'
        if (!dir.endsWith("/")) {
            dir += "/";
        }
        return true;
    }
}

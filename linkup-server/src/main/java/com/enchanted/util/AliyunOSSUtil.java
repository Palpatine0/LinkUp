package com.enchanted.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.enchanted.constant.AliyunOSSConstant;

import java.io.ByteArrayInputStream;

public class AliyunOSSUtil {

    public static String uploadToOSS(String fileName, byte[] qrCodeBytes) {
        // Load Aliyun OSS credentials from environment variables or constants
        String endpoint = AliyunOSSConstant.ALIYUN_OSS_ENDPOINT;
        String accessKeyId = AliyunOSSConstant.ALIYUN_OSS_ACCESS_KEY_ID;
        String accessKeySecret = AliyunOSSConstant.ALIYUN_OSS_ACCESS_KEY_SECRET;
        String bucketName = AliyunOSSConstant.ALIYUN_OSS_BUCKET_NAME;

        // Initialize the OSS client
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // Upload the QR code as an image to OSS
            ByteArrayInputStream inputStream = new ByteArrayInputStream(qrCodeBytes);
            ossClient.putObject(bucketName, fileName, inputStream);

            // Generate the URL to access the file
            String fileUrl = "https://" + bucketName + "." + endpoint + "/" + fileName;
            return fileUrl;
        } finally {
            // Shut down the OSS client
            ossClient.shutdown();
        }
    }
}

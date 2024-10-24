package com.enchanted.service;

import java.util.Map;

public interface IFileService {
    Map<String, String> signature(String dir);

    String upload(byte[] fileBytes, String filePath, String fileName);
}

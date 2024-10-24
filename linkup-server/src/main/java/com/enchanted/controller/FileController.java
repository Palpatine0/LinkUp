package com.enchanted.controller;

import com.enchanted.service.IFileService;
import com.enchanted.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private IFileService fileService;

    @GetMapping("/signature")
    public R signature(String dir) {
        try {
            Map<String, String> policy = fileService.signature(dir);
            if (policy != null) {
                return R.ok().put("data", policy);
            } else {
                return R.error("Failed to generate policy");
            }
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            return R.error("An error occurred");
        }
    }

    @PostMapping("/upload")
    public R upload(@RequestBody byte[] fileBytes, @RequestParam("filePath") String filePath, @RequestParam("fileName") String fileName) {
        String fileUrl = fileService.upload(fileBytes, filePath, fileName);
        if (fileUrl != null) {
            return R.ok().put("data", fileUrl);
        } else {
            return R.error("File upload failed");
        }
    }
}

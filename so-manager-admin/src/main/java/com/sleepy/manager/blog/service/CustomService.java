package com.sleepy.manager.blog.service;

import com.sleepy.manager.blog.common.AssembledData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author gehoubao
 * @create 2021-10-20 15:56
 **/

public interface CustomService {
    AssembledData getAboutUs();

    String getArticleBanner();

    String uploadFile(MultipartFile file, String dirPath) throws IOException;

    String editArticleBanner(String homeBanner);

    String editAboutUs(String appInfo);

    AssembledData getPrivatePolicy();

    String editPrivatePolicy(String privatePolicy);

    AssembledData getServicePolicy();

    String editServicePolicy(String servicePolicy);
}

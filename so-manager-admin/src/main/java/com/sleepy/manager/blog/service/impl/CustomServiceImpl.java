package com.sleepy.manager.blog.service.impl;

import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.blog.service.CustomService;
import com.sleepy.manager.common.config.SoServerConfig;
import com.sleepy.manager.common.utils.DateUtils;
import com.sleepy.manager.system.domain.SysConfig;
import com.sleepy.manager.system.service.ISysConfigService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author gehoubao
 * @create 2021-10-20 15:56
 **/
@Service
public class CustomServiceImpl implements CustomService {

    private static final String ABOUT_APP_CONFIG_KEY = "app.about.content";
    private static final String ARTICLE_BANNER_CONFIG_KEY = "app.home.banner";
    private static final String PRIVATE_POLICY_CONFIG_KEY = "app.policy.private";
    private static final String SERVICE_POLICY_CONFIG_KEY = "app.policy.service";

    private static final long ABOUT_APP_CONFIG_ID = 100L;
    private static final long ARTICLE_BANNER_CONFIG_ID = 101L;
    private static final long PRIVATE_POLICY_CONFIG_ID = 102L;
    private static final long SERVICE_POLICY_CONFIG_ID = 103L;

    public static final String POINT = ".";
    public static final String COMMA = ",";

    @Autowired
    ISysConfigService configService;

    @Override
    public AssembledData getAboutUs() {
        return getConfigData(ABOUT_APP_CONFIG_ID);
    }

    @Override
    public String getArticleBanner() {
        return configService.selectConfigByKey(ARTICLE_BANNER_CONFIG_KEY);
    }

    @Override
    public String uploadFile(MultipartFile file, String dirPath) throws IOException {
        // 获取图片的md5值作为image name
        String imageName = DigestUtils.md5Hex(file.getInputStream());
        String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String filePath = SoServerConfig.getUploadPath() + File.separator + dirPath + File.separator + imageName + fileSuffix;
        File dest = new File(new File(filePath).getAbsolutePath());
        if (!dest.exists()) {
            dest.mkdirs();
        }
        file.transferTo(dest);
        String relUrlPath = (dirPath + File.separator + dest.getName()).replace(File.separator, "/");
        return relUrlPath;
    }

    @Override
    public String editArticleBanner(String homeBanner) {
        return updateConfig(ARTICLE_BANNER_CONFIG_ID, homeBanner);
    }

    @Override
    public String editAboutUs(String appInfo) {
        return updateConfig(ABOUT_APP_CONFIG_ID, appInfo);
    }

    @Override
    public AssembledData getPrivatePolicy() {
        return getConfigData(PRIVATE_POLICY_CONFIG_ID);
    }

    @Override
    public String editPrivatePolicy(String privatePolicy) {
        return updateConfig(PRIVATE_POLICY_CONFIG_ID, privatePolicy);
    }

    @Override
    public AssembledData getServicePolicy() {
        return getConfigData(SERVICE_POLICY_CONFIG_ID);
    }

    @Override
    public String editServicePolicy(String servicePolicy) {
        return updateConfig(SERVICE_POLICY_CONFIG_ID, servicePolicy);
    }

    private String updateConfig(Long configId, String configValue) {
        SysConfig config = new SysConfig();
        config.setConfigId(configId);
        config.setConfigValue(configValue);
        String result = configService.updateConfig(config) == 1 ? "success" : "failed";
        configService.resetConfigCache();
        return result;
    }

    private AssembledData getConfigData(long configId) {
        SysConfig config = configService.selectConfigById(configId);
        AssembledData.Builder dataBuilder = new AssembledData.Builder();
        if (!Objects.isNull(config)) {
            dataBuilder.put("configValue", config.getConfigValue());
            dataBuilder.put("updateTime", DateUtils.dateTime(config.getUpdateTime()));
        }
        return dataBuilder.build();
    }

}
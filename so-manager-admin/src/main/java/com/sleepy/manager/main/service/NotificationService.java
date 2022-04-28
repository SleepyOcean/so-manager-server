package com.sleepy.manager.main.service;

/**
 * @author captain1920
 * @classname NotificationService
 * @description TODO
 * @date 2022/4/26 10:33
 */
public interface NotificationService {

    void send(String mailTo, String title, String content);
}

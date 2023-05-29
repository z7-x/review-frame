package com.z7.bespoke.frame.sftp.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 项目名称：review-frame
 * 类 名 称：SFTPProperties
 * 类 描 述：TODO sftp配置
 * 创建时间：2023/5/29 11:53 上午
 * 创 建 人：z7
 */
@Component
@ConfigurationProperties(prefix = "sftp-oss")
public class SFTPProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    /**
     * 上传文件：本地流写入路径
     */
    private String localDirectory;
    /**
     * 上传文件：远程服务器路径
     */
    private String remoteDirectory;
    /**
     * 访问路径：代理映射的路径
     */
    private String visitRoute;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocalDirectory() {
        return localDirectory;
    }

    public void setLocalDirectory(String localDirectory) {
        this.localDirectory = localDirectory;
    }

    public String getRemoteDirectory() {
        return remoteDirectory;
    }

    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }

    public String getVisitRoute() {
        return visitRoute;
    }

    public void setVisitRoute(String visitRoute) {
        this.visitRoute = visitRoute;
    }
}

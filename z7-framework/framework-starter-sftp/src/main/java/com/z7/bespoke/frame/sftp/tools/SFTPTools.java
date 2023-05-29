package com.z7.bespoke.frame.sftp.tools;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 项目名称：review-frame
 * 类 名 称：SFTPTools
 * 类 描 述：TODO sftp配置
 * 创建时间：2023/5/29 12:12 上午
 * 创 建 人：z7
 */
@Slf4j
public class SFTPTools {

    private String host;
    private int port;
    private String username;
    private String password;

    public SFTPTools(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * @param host     主机
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     * @description: 登陆SFTP
     */
    public Map<String, Object> loginIn(String host, int port, String username, String password) {
        HashMap<String, Object> loginInfo = new HashMap<>();
        ChannelSftp sftp = null;
        JSch jsch = new JSch();
        Session sshSession = null;
        try {
            log.info("SFTP服务器连接username:{},host:{},port:{}", username, host, port);
            sshSession = jsch.getSession(username, host, port);
        } catch (JSchException e) {
            log.error("SFTP创建Session异常：", e);
        }
        log.info("SFTP连接成功建立");
        sshSession.setPassword(password);
        log.info("密码输入成功");
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        sshSession.setConfig(sshConfig);
        try {
            sshSession.connect();
        } catch (JSchException e) {
            log.error("SFTP的Session连接异常：", e);
        }
        log.info("用户" + username + "成功登陆");
        Channel channel = null;
        try {
            channel = sshSession.openChannel("sftp");
        } catch (JSchException e) {
            log.error("SFTP的通道打开异常：", e);
        }
        try {
            channel.connect();
        } catch (JSchException e) {
            log.error("SFTP的通道连接异常：", e);
        }
        sftp = (ChannelSftp) channel;
        log.info("连接到主机IP:{}", host);
        loginInfo.put("sftp", sftp);
        loginInfo.put("session", sshSession);
        return loginInfo;
    }

    /**
     * @param sftp
     * @param session
     * @description: 退出登陆
     */
    public void loginOut(ChannelSftp sftp, Session session) {
        if (sftp != null && sftp.isConnected()) {
            try {
                // 要想关闭sftp的连接,一定要关闭ChannelSftp对象中的session
                if (sftp.getSession() != null) {
                    sftp.getSession().disconnect();
                }
            } catch (JSchException e) {
                log.error("用户退出SFTP服务器出现异常：", e);
            }
            sftp.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    /**
     * @param remoteFilePath
     * @param localFilePath
     * @description: 文件下载
     */
    public void download(String remoteFilePath, String localFilePath) {
        ChannelSftp sftp = null;
        Session session = null;
        try {
            Map<String, Object> loginInfo = loginIn(host, port, username, password);
            sftp = (ChannelSftp) loginInfo.get("sftp");
            session = (Session) loginInfo.get("session");

            sftp.get(remoteFilePath, localFilePath);
            log.info("SFTP文件已下载成功");
        } catch (SftpException e) {
            log.info("SFTP文件下载失败");
            e.printStackTrace();
        } finally {
            loginOut(sftp, session);
        }
    }

    /**
     * @param localFilePath   本地服务器路径
     * @param remoteDirectory 远程服务器路径
     * @description: 上传文件
     */
    public void uploadFile(String localFilePath, String remoteDirectory) {
        JSch jSch = new JSch();
        Session session = null;
        ChannelSftp sftp = null;
        FileInputStream inputStream = null;

        try {
            Map<String, Object> loginInfo = loginIn(host, port, username, password);
            sftp = (ChannelSftp) loginInfo.get("sftp");
            session = (Session) loginInfo.get("session");

            sftp.cd(remoteDirectory);

            File localFile = new File(localFilePath);
            //此处将路径替换成输入流
            inputStream = new FileInputStream(localFile);
            sftp.put(inputStream, localFile.getName());
            log.info("SFTP文件已上传成功");
        } catch (SftpException | FileNotFoundException e) {
            log.info("SFTP文件上传失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            loginOut(sftp, session);
        }
    }

    /**
     * @param createDirectory 创建的目录路径
     * @description: 创建目录
     */
    public boolean createDirectory(String createDirectory, ChannelSftp sftp) {
        try {
            if (isDirExist(createDirectory, sftp)) {
                sftp.cd(createDirectory);
                return true;
            }
            String pathArray[] = createDirectory.split("/");
            StringBuffer filePath = new StringBuffer("/");
            for (String path : pathArray) {
                if (path.equals("")) {
                    continue;
                }
                filePath.append(path + "/");
                if (isDirExist(filePath.toString(), sftp)) {
                    sftp.cd(filePath.toString());
                } else {
                    // 建立目录
                    sftp.mkdir(filePath.toString());
                    // 进入并设置为当前目录
                    sftp.cd(filePath.toString());
                }
            }
            sftp.cd(createDirectory);
            return true;
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param directory
     * @description: 判断目录是否存在
     */
    public boolean isDirExist(String directory, ChannelSftp sftp) {
        boolean isDirExistFlag = false;
        try {
            SftpATTRS sftpATTRS = sftp.lstat(directory);
            isDirExistFlag = true;
            return sftpATTRS.isDir();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isDirExistFlag = false;
            }
        }
        return isDirExistFlag;
    }

    /**
     * @param directory 要创建文件夹的位置路径
     * @param fileName  要创建文件夹的名称
     * @param sftp      sftp连接
     * @description: 创建目录文件夹
     */
    public void mkdir(String directory, String fileName, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            sftp.mkdir(fileName);
            log.info("SFTP文件夹创建成功");
        } catch (Exception e) {
            log.info("SFTP文件夹创建失败");
            e.printStackTrace();
        }
    }

   /*public static void main(String[] args) {
        String host = "47.102.98.16";
        int port = 22;
        String username = "root";
        String password = "Ilove#nb2022";

        //上传
        String localFilePath1 = "/Users/z7/work_directory/file/seven/1685091916132.jpg";
        String remoteDirectory1 = "/mnt/inkScreenResource/binFolder/";
        SFTPTools sftpTools1 = new SFTPTools(host, port, username, password);
        sftpTools1.uploadFile(localFilePath1, remoteDirectory1);

        //下载
        String localFilePath = "/Users/z7/Downloads/";
        String remoteDirectory = "/mnt/inkScreenResource/binFolder/1685091916132.jpg";
        SFTPTools sftpTools = new SFTPTools(host, port, username, password);
        sftpTools.download(remoteDirectory, localFilePath);
    }*/

}

package com.z7.bespoke.frame.sftp.tools;

import lombok.extern.slf4j.Slf4j;

/**
 * 项目名称：review-frame
 * 类 名 称：FileTools
 * 类 描 述：TODO InputStream使用输入流读取内容、OutputStream使用输出流写入内容
 * 创建时间：2023/5/29 1:08 下午
 * 创 建 人：z7
 */
@Slf4j
public class FileTools {

    /**
     * 按照流的流向来分:
     * 输入流：只能从中读取数据，而不能向其写入数据，由InputStream和Reader作为基类。
     * 输出流：只能向其写入数据，而不能从中读取数据。由OutputStream和Writer作为基类。
     *
     * 按数据传输单位划分:
     * 字节流：InputStream：继承自InputStream的流都是用于向程序中输入数据的，且数据单位都是字节（8位）。
     *       OutputStream：继承自OutputStream的流都是程序用于向外输出数据的，且数据单位都是字节（8位）。
     *
     * 字符流：Reader：继承自Reader的流都是用于向程序中输入数据的，且数据单位都是字符（16位）。
     *        Writer：继承自Writer的流都是程序用于向外输出数据的，且数据单位都是字符（16位）。
     */




}

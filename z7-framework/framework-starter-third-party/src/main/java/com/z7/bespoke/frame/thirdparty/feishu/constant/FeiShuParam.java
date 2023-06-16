package com.z7.bespoke.frame.thirdparty.feishu.constant;

/**
 * 项目名称：nb-inext
 * 类 名 称：FeiShuParam
 * 类 描 述：TODO
 * 创建时间：2023/6/13 10:21 上午
 * 创 建 人：z7
 */
public class FeiShuParam {

    public static String VERIFY_URL_CHALLENGE = "challenge";
    public static String VERIFY_URL_ENCRYPT = "encrypt";

    public static String EVENT_SCHEMA = "schema";
    public static String EVENT_SCHEMA_V2 = "2.0";
    public static String EVENT_HEADER = "header";

    public static class Header {
        public static String EVENT_ID = "event_id";
        public static String TOKEN = "token";
        public static String CREATE_TIME = "create_time";
        public static String EVENT_TYPE = "event_type";
        public static String TENANT_KEY = "tenant_key";
        public static String APP_ID = "app_id";
    }

    public static String EVENT_EVENT = "event";
}

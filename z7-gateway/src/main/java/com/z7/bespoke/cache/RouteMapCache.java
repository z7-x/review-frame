package com.z7.bespoke.cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 项目名称：review-frame
 * 类 名 称：RouteMapCache
 * 类 描 述：TODO 路由缓存:集群情况下需要替换成Redis进行存储
 * 创建时间：2023/4/25 17:55 下午
 * 创 建 人：z7
 */
public class RouteMapCache {
    private static ConcurrentHashMap<String, String> cacheMap = new ConcurrentHashMap<>();

    /**
     * 获取缓存的对象
     *
     * @param account
     * @return
     */
    public static String getCache(String account) {
        // 如果缓冲中有该账号，则返回value
        if (cacheMap.containsKey(account)) {
            return cacheMap.get(account);
        }

        return cacheMap.get(account);
    }

    public static List<String> getValues() {
        return cacheMap.values().stream().collect(Collectors.toList());
    }

    /**
     * 移除缓存信息
     *
     * @param account
     */
    public static void removeCache(String account) {
        cacheMap.remove(account);
    }

    public static void put(String key, String value) {
        cacheMap.put(key, value);
    }

    public static boolean hasKey(String key) {
        return cacheMap.containsKey(key);
    }
}

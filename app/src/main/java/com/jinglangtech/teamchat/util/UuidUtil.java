package com.jinglangtech.teamchat.util;

import java.util.UUID;

/**
 * Created by think on 2018/2/8.
 */

public class UuidUtil {
    public static String get24UUID(){
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        // 去掉"-"符号: 4cdbc040-657a-4847-b266-7e31d9e2c3d9
        String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24,28);
        return temp;
    }
}

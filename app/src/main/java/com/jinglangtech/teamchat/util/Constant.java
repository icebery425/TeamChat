package com.jinglangtech.teamchat.util;

import android.os.Environment;

/**
 * 全局常量
 */

public class Constant {


    public final static String ALPHABETS = "abcdefghijklmnopqrstuvwxyz";

    public static final int    IMAGE_MAX_NUMBER   = 9;
    public static final int    IMAGE_MAX_NUMBER_6 = 6;
    public static final String IMAGE_ADD_FLAG     = "000000";
    public final static String REQUEST_FAILED_STR = "系统异常";

    public static final boolean JPUSH_ENABLE = false;

    public static String EVENT_OTHER_LEADER_RESULT_AFTER_HANDLE = "推送";


    public static final int CHOOSE_PEOPLE_MAX = 50;   //涉事人最大数量


    //记录数
    public static String COUNT = "count";
    public static String UID   = "uid";
    public static String BASE_IP = "http://54.223.26.249:8090";
    //public static final String BLUETOOTH_SHORT_CLICK_CMD = "6C6F6E67"; //旧的蓝牙设备指令
    public static final String BLUETOOTH_SHORT_CLICK_CMD = "01";         //新的蓝牙设备指令
    public static final String BLUETOOTH_LONG_CLICK_CMD  = "73686F7274";

    public static final int UPLOAD_FILE_SIZE = 4*1024*1024;

    public static final String BROADCAST_UPDATE_GROUP_INFO   = "broadcst_update_group_info";

    public static final String STORAGE_DIR = "/ms_record";
    public static final String STORAGE_ROOT = Environment.getExternalStorageDirectory() + STORAGE_DIR;

    public static final String AUDIO_DIR = STORAGE_ROOT + "/";

}

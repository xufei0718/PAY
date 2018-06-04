package com.mybank.pc.kits;

import com.jfinal.kit.PropKit;

/**
 * Created by yuhaihui8913 on 2017/11/15.
 */
public class ResKit {

    public static  String getMsg(String key){
        return PropKit.use("msg.properties").get(key);
    }
    public static  String getMsg(String key,String def){
        return PropKit.use("msg.properties").get(key,def);
    }
    public static int getMsgInt(String key){
        return PropKit.use("msg.properties").getInt(key);
    }
    public static int getMsgInt(String key,int def){
        return PropKit.use("msg.properties").getInt(key,def);
    }

    public static long getMsgLong(String key){
        return PropKit.use("msg.properties").getLong(key);
    }
    public static long getMsgLong(String key,long def){
        return PropKit.use("msg.properties").getLong(key,def);
    }

    public static boolean getMsgBoolean(String key){
        return PropKit.use("msg.properties").getBoolean(key);
    }
    public static boolean getMsgBoolean(String key,boolean def){
        return PropKit.use("msg.properties").getBoolean(key,def);
    }

    public static String getConfig(String key){
        return PropKit.use("config.properties").get(key);
    }
    public static String getConfig(String key,String def){
        return PropKit.use("config.properties").get(key,def);
    }

    public static int getConfigInt(String key){
        return PropKit.use("config.properties").getInt(key);
    }
    public static int getConfigInt(String key,int def){
        return PropKit.use("config.properties").getInt(key,def);
    }

    public static long getConfigLong(String key){
        return PropKit.use("config.properties").getLong(key);
    }
    public static long getConfigLong(String key,long def){
        return PropKit.use("config.properties").getLong(key,def);
    }

    public static boolean getConfigBoolean(String key){
        return PropKit.use("config.properties").getBoolean(key);
    }
    public static boolean getConfigBoolean(String key,boolean def){
        return PropKit.use("config.properties").getBoolean(key,def);
    }

}

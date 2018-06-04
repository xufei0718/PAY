package com.mybank.pc.kits;




import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.mail.Mail;
import com.jfplugin.mail.MailKit;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuhaihui8913 on 2017/5/27.
 */
public class _StrKit {
    public static final String CHINESE_REGEX = "[\\u4e00-\\u9fa5]";


    public static String matchResult(Pattern p, String str)
    {
        StringBuilder sb = new StringBuilder();
        Matcher m = p.matcher(str);
        while (m.find())
            for (int i = 0; i <= m.groupCount(); i++)
            {
                sb.append(m.group());
            }
        return sb.toString();
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }



    public static void main(String[] args) throws IOException {
//        byte[] key_array= Base64.decode("+yttlyEjxH44zn/ACScxLg==", Charset.forName("UTF-8"));
//        System.out.println(SecureUtil.aes(key_array).decryptStr("f5ddc6d07606017c336df70c02a47172"));
//        System.out.println(System.getProperty("java.io.tmpdir"));
//        Date lastWeek=DateUtil.lastWeek();
//        Date beginWeek=DateUtil.beginOfWeek(lastWeek);
//        Date endWeek=DateUtil.endOfDay(DateUtil.offsetDay(lastWeek,6));
//
//
//        System.out.println(beginWeek);
//        System.out.println(endWeek);



    }


}

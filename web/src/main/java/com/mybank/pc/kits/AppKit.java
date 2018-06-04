package com.mybank.pc.kits;


import cn.hutool.core.util.StrUtil;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.CardBin;

public class AppKit {

    /**
     * 通用的Excel保存的根位置，使用的时候还需要根据自己的业务定义之下的具体的文件夹例如:
     *
     * getExcelPath()+'/aa/20160203/'
     * @return
     */
    public static String getExcelPath(){
        String ret= (String)CacheKit.get(Consts.CACHE_NAMES.paramCache.name(),"ePath");
        if(StrUtil.isBlank(ret))
            ret="/WEB-INF/excelLocation/";

        return ret;
    }


    public static CardBin checkJJCardBin(String code){
        CardBin cardBin=CacheKit.get(Consts.CACHE_NAMES.cardBin.name(),code);
        if(cardBin==null) {
            LogKit.info("cardBin数据为找到");
            return null;
        }
        if(cardBin.getCardType().equals(Consts.CardBinType.jj.getVal())){
            return cardBin;
        }else{
            LogKit.info("cardBin不是借记卡");
            return null;
        }
    }

    public static CardBin checkDJCardBin(String code){
        CardBin cardBin=CacheKit.get(Consts.CACHE_NAMES.cardBin.name(),code);
        if(cardBin==null) {
            LogKit.info("cardBin数据为找到");
            return null;
        }
        if(cardBin.getCardType().equals(Consts.CardBinType.dj.getVal())){
            return cardBin;
        }else{
            LogKit.info("cardBin不是贷记卡");
            return null;
        }
    }

}

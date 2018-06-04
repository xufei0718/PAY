package com.mybank.pc.merchant.feeamount;

import cn.hutool.core.util.StrUtil;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.mybank.pc.Consts;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.kits.DateKit;
import com.mybank.pc.merchant.model.MerchantFeeAmountRecord;
import com.mybank.pc.merchant.model.MerchantInfo;

public class MerchantFeeAmountRecordCtr extends CoreController {


    /**
     *
     * 商户操作员只能查询本商户的
     * bDate eDate 查询时间区间
     * operName 操作员名字
     * merNo 商户编号
     * type 流水类型
     *
     *
     */
    public void page(){
        Integer merId=null;
        Kv kv= Kv.create();
        MerchantInfo merchantInfo=(MerchantInfo)getAttr(Consts.CURR_USER_MER);
        if(merchantInfo!=null){
            merId=merchantInfo.getId();
            kv.put("merId=",merId);
        }
        String bDate=getPara("bDate");
        String eDate=getPara("eDate");

        if(StrUtil.isNotBlank(bDate)){
            bDate= DateKit.getTimeStampBegin(DateKit.strToDate(bDate,DateKit.yyyy_MM_dd));
            kv.put("cAt>=",bDate);
        }
        if(StrUtil.isNotBlank(eDate)){
            bDate= DateKit.getTimeStampEnd(DateKit.strToDate(eDate,DateKit.yyyy_MM_dd));
            kv.put("cAt<=",eDate);
        }
        String operName=getPara("operName");
        if(StrUtil.isNotBlank(operName))kv.put("operName=",operName);
        String merNo=getPara("merNo");
        if(StrUtil.isNotBlank(merNo))kv.put("merNo=",merNo);
        String type=getPara("type");
        if(StrUtil.isNotBlank(type))kv.put("type=",type);
        Page<MerchantFeeAmountRecord> page=MerchantFeeAmountRecord.dao.paginate(getPN(),getPS(), Db.getSqlPara("merchantFeeAmountRecord.page",Kv.by("cond",kv)));
        renderJson(page);
    }


}

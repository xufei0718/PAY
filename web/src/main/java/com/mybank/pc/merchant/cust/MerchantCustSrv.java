package com.mybank.pc.merchant.cust;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.mybank.pc.merchant.model.MerchantCust;
import com.mybank.pc.merchant.model.MerchantInfo;

import java.util.Date;
import java.util.List;

public class MerchantCustSrv {
    public int totalLastCust(Integer merID){

        Kv kv = Kv.create();

        kv.set("merID", merID);
        kv.set("dayDate", new DateTime().toString("yyyyMMdd"));
        SqlPara sqlPara = Db.getSqlPara("merchant.totalDayCust", kv);

        List<MerchantCust> list= MerchantCust.dao.find(sqlPara);

        if(CollectionUtil.isNotEmpty(list)){
            return list.size();
        }else{
            return 0;
        }
    }

    /**
     * 增加客户服务，接口方式新增客户使用
     * @param merchantCust（必输字段，商户ID，商户编号、姓名、身份证号、手机号、卡号）
     * @return 0：成功 1：商户不存在 2：银行卡已经绑定
     */
    public int addCust(MerchantCust merchantCust){

        MerchantInfo mf = MerchantInfo.dao.findFirst("select * from merchant_info mi where mi.merchantNo=? and mi.dat is null ", merchantCust.getMerNo());

        if (ObjectUtil.isNull(mf)) {
            LogKit.info("服务新增客户：商户不存在");
            return 1;
        }


        List<MerchantCust> mcList = MerchantCust.dao.find("select * from merchant_cust mc where mc.dat is null and  mc.merNo=?  and mc.cardID=? and mc.mobileBank=? and mc.bankcardNo=?", merchantCust.getMerNo(), merchantCust.getCardID(), merchantCust.getMobileBank(), merchantCust.getBankcardNo());
        if (!CollectionUtil.isEmpty(mcList)) {
            //同一商户下已经绑定过银行卡
            LogKit.info("服务新增客户：该客户银行卡已经绑定");
            return 2;
        }

        merchantCust.setCat(new Date());
        merchantCust.save();
        return 0;
    }
}

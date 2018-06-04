package com.mybank.pc.merchant.info;

import cn.hutool.core.util.StrUtil;
import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.Role;
import com.mybank.pc.admin.model.Taxonomy;
import com.mybank.pc.admin.model.User;
import com.mybank.pc.admin.model.UserRole;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.kits.DateKit;
import com.mybank.pc.kits.ext.BCrypt;
import com.mybank.pc.merchant.model.MerchantFee;
import com.mybank.pc.merchant.model.MerchantFeeAmountRecord;
import com.mybank.pc.merchant.model.MerchantInfo;
import com.mybank.pc.merchant.model.MerchantUser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商户信息管理
 */

public class MerchantInfoCtr extends CoreController {
    private MerchantInfoSrv merchantInfoSrv =enhance(MerchantInfoSrv.class);

    public void list() {
        Page<MerchantInfo> page;
        String serach = getPara("search");
        StringBuffer where = new StringBuffer("from merchant_info mi  where 1=1 and mi.dat is null  ");
        if (!isParaBlank("search")) {
            where.append(" and (instr(mi.merchantNo,?)>0 or instr(mi.merchantName,?)>0 or instr(mi.perName,?)>0) ");
            where.append(" ORDER BY mi.cat desc");
            page = MerchantInfo.dao.paginate(getPN(), getPS(), "select * ", where.toString(), serach, serach, serach);
        } else {
            where.append(" ORDER BY mi.cat desc");
            page = MerchantInfo.dao.paginate(getPN(), getPS(), "select * ", where.toString());
        }
        List<Taxonomy> tlist =CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),"merTypeList");
        Map map = new HashMap();
        map.put("tList" ,tlist);
        map.put("page",page);
        renderJson(map);

    }

    @Before({MerchantInfoValidator.class, Tx.class})
    public void save() {

        MerchantInfo merInfo = getModel(MerchantInfo.class,"",true);

        String merNo = merchantInfoSrv.getMerchantNo(merInfo.getMerchantType());
        merInfo.setMerchantNo(merNo);
        merInfo.setCat(new Date());

        merInfo.setMat(new Date());
        merInfo.setStatus(Consts.STATUS.enable.getVal());
        merInfo.setOperID(String.valueOf(currUser().getId()));
        merInfo.save();

        //增加用户信息
        User user = new User();
        user.setLoginname("oper@"+merInfo.getMerchantNo());
        user.setIdcard(merInfo.getCardID());
        user.setNickname("商户"+merInfo.getMerchantNo());
        user.setPhone(merInfo.getMobile());
        user.setCAt(new Date());
        user.setEmailStatus(Consts.YORN.no.isVal());
        user.setPhoneStatus(Consts.YORN.no.isVal());
        user.setMAt(new Date());
        user.setStatus(Consts.STATUS.enable.getVal());
        user.setSignature(merInfo.getMerchantName());
        user.setSalt(BCrypt.gensalt());
        user.setPassword(BCrypt.hashpw(merInfo.getMobile(), user.getSalt()));
        String default_avart = CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "qn_url") + "image/avatar/dft-avatar.jpg";
        user.setAvatar(default_avart);
        user.save();

        //建立商户和操作员关系
        MerchantUser merchantUser = new MerchantUser();
        merchantUser.setMerchantID(merInfo.getId());
        merchantUser.setUserID(user.getId().intValue());
        merchantUser.save();

        //增加默认角色，5为商户操作员
        List <Role> list = Role.dao.findByPropEQ("name","user");
        if(list !=null){
            UserRole ur = new UserRole();
            ur.setRId(list.get(0).getId().intValue());
            ur.setUId(user.getLong("id"));
            ur.save();
        }



        renderSuccessJSON("新增商户信息成功。", "");
    }

    @Before({MerchantInfoValidator.class, Tx.class})
    public void update() {
        MerchantInfo merInfo = getModel(MerchantInfo.class,"",true);

        merInfo.setMat(new Date());
        merInfo.setOperID(String.valueOf(currUser().getId()));
        merInfo.update();
        merchantInfoSrv.removeCacheMerchantInfo(merInfo.getId());
        renderSuccessJSON("更新商户信息成功。", "");
    }

    @Before({Tx.class})
    public void del(){
        int id=getParaToInt("id");
        MerchantInfo merInfo=MerchantInfo.dao.findById(BigInteger.valueOf(id));
        merInfo.setDat(new Date());
        merInfo.update();
        merchantInfoSrv.removeCacheMerchantInfo(merInfo.getId());
        //删除关联操作员用户
        List<MerchantUser> list = MerchantUser.dao.find("select * from merchant_user mu where mu.merchantID=? ",merInfo.getId());
        if(list !=null && list.size()>0){
            Integer userID = list.get(0).getUserID();
            User user =new User();
            user.setId(BigInteger.valueOf(userID));
            user.setDAt(new Date());
            user.update();
            CacheKit.remove(Consts.CACHE_NAMES.user.name(),user.getId());
            CacheKit.remove(Consts.CACHE_NAMES.userRoles.name(),user.getId());
            CacheKit.remove(Consts.CACHE_NAMES.userReses.name(),user.getId());
        }

        renderSuccessJSON("删除商户信息成功。");

    }


    /**
     * 用户禁用操作处理
     **/
    @Before(Tx.class)
    public void forbidden() {
        String merInfoId = getPara("id");
        int id = Integer.parseInt(merInfoId);
        MerchantInfo merInfo = new MerchantInfo();
        merInfo.setId(id);
        merInfo.setStatus(Consts.STATUS.forbidden.getVal());
        merInfo.update();
        //禁用关联操作员用户
        List<MerchantUser> list = MerchantUser.dao.find("select * from merchant_user mu where mu.merchantID=? ",merInfo.getId());
        if(list !=null && list.size()>0){
            Integer userID = list.get(0).getUserID();
            User user =new User();
            user.setId(BigInteger.valueOf(userID));
            user.setStatus(Consts.STATUS.forbidden.getVal());
            user.update();
            }

        renderSuccessJSON("禁用操作执行成功。", "");
    }

    /**
     * 恢复操作处理
     **/
    @Before(Tx.class)
    public void enable() {
        String merInfoId = getPara("id");
        int id = Integer.parseInt(merInfoId);
        MerchantInfo merInfo = new MerchantInfo();
        merInfo.setId(id);
        merInfo.setStatus(Consts.STATUS.enable.getVal());
        merInfo.update();
        //激活关联操作员用户
        List<MerchantUser> list = MerchantUser.dao.find("select * from merchant_user mu where mu.merchantID=? ",merInfo.getId());
        if(list !=null && list.size()>0){
            Integer userID = list.get(0).getUserID();
            User user =new User();
            user.setId(BigInteger.valueOf(userID));
            user.setStatus(Consts.STATUS.enable.getVal());
            user.update();
        }
        renderSuccessJSON("恢复操作执行成功。", "");
    }
    public void loginMerInfo(){
        //获取当前登录用户信息
        MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
        renderJson(merInfo);
    }
    public void listFee() {

        String merID = getPara("id");
        StringBuffer tradeType1 = new StringBuffer("select * from merchant_fee mf  where 1=1 and mf.dat is null ");
        tradeType1.append(" and mf.merID=? and mf.tradeType='1'  order by mf.amountLower desc ");


        StringBuffer tradeType2 = new StringBuffer("select * from merchant_fee mf  where 1=1 and mf.dat is null  ");
        tradeType2.append(" and mf.merID=? and mf.tradeType='2'  order by mf.amountLower desc");
        List<MerchantFee> feeListJ = MerchantFee.dao.find(tradeType1.toString(),merID);
        List<MerchantFee> feeListB = MerchantFee.dao.find(tradeType2.toString(),merID);

        Map map = new HashMap();
        map.put("feeListJ",feeListJ);
        map.put("feeListB",feeListB);
        map.put("merID",merID);
        renderJson(map);

    }
    @Before({ Tx.class})
    public void addFee(){
        MerchantFee merFee   =  getModel(MerchantFee.class,"",true);
        if(merFee.getFeeType().equals("2")){
            merFee.setRatio(merFee.getAmount());
            merFee.setAmount(null);
        }

        List<MerchantFee> feeList;
        StringBuffer str = new StringBuffer("select * from merchant_fee mf  where 1=1 and mf.dat is null   and mf.merID=? and mf.tradeType=? order by mf.amountLower desc");
        //检查最大值
        if(merFee.getTradeType().equals("1")){
            //加急
             feeList = MerchantFee.dao.find(str.toString(),merFee.getMerID(),merFee.getTradeType());
        }else{
            //标准
            feeList = MerchantFee.dao.find(str.toString(),merFee.getMerID(),merFee.getTradeType());
        }

        if(feeList!=null && feeList.size()>0) {
            for (int i = 0; i < feeList.size(); i++) {
                int a = merFee.getAmountUpper().compareTo(feeList.get(i).getAmountLower());
                if (a > 0) {
                    BigDecimal am =  feeList.get(i).getAmountLower();
                    //修改本记录（金额下限），增加新记录
                    MerchantFee mft = feeList.get(i);
                    mft.setAmountLower(merFee.getAmountUpper());
                    mft.update();

                    merFee.setAmountLower(am);
                    merFee.setCat(new Date());
                    merFee.save();
                    break;
                }
                if (merFee.getAmountUpper().compareTo(feeList.get(i).getAmountUpper()) == 0) {
                    //更新记录
                    MerchantFee mft = feeList.get(i);
                    mft.setAmount(merFee.getAmount());
                    mft.setRatio(merFee.getRatio());
                    mft.setFeeType(merFee.getFeeType());
                    mft.setMat(new Date());
                    mft.update();
                    break;
                }
            }
        }else{
            merFee.setAmountLower(new BigDecimal(0));
            merFee.setAmountUpper(new BigDecimal(0));
            merFee.setCat(new Date());
            merFee.save();
        }


        StringBuffer tradeType1 = new StringBuffer("select * from merchant_fee mf  where 1=1 and mf.dat is null   and mf.merID=? and mf.tradeType='1' order by mf.amountLower desc");
        StringBuffer tradeType2 = new StringBuffer("select * from merchant_fee mf  where 1=1 and mf.dat is null  and mf.merID=? and mf.tradeType='2'  order by mf.amountLower desc");
        List<MerchantFee> feeListJ = MerchantFee.dao.find(tradeType1.toString(),merFee.getMerID());
        List<MerchantFee> feeListB = MerchantFee.dao.find(tradeType2.toString(),merFee.getMerID());

        Map map = new HashMap();
        map.put("feeListJ",feeListJ);
        map.put("feeListB",feeListB);
        map.put("resCode",Consts.REQ_JSON_CODE.success.name());
        map.put("resMsg","手续费增加成功");

        renderJson(map);
    }

    @Before({ Tx.class})
    public void delFee(){
        String feeID = getPara("id");
        MerchantFee merFee   =  MerchantFee.dao.findById(feeID);
        List<MerchantFee> feeList;
        StringBuffer str = new StringBuffer("select * from merchant_fee mf  where 1=1 and mf.dat is null   and mf.merID=? and mf.tradeType=? order by mf.amountLower desc");
        //检查最大值
        if(merFee.getTradeType().equals("1")){
            //加急
            feeList = MerchantFee.dao.find(str.toString(),merFee.getMerID(),merFee.getTradeType());
        }else{
            //标准
            feeList = MerchantFee.dao.find(str.toString(),merFee.getMerID(),merFee.getTradeType());
        }

        if(feeList!=null && feeList.size()>1) {
            for (int i = 0; i < feeList.size(); i++) {
                if (merFee.getAmountUpper().compareTo(new BigDecimal(0)) == 0) {
                    //更新记录
                    MerchantFee mft = feeList.get(i+1);
                    mft.setAmountUpper(new BigDecimal(0));
                    mft.setMat(new Date());
                    mft.update();
                    merFee.delete();
                    break;
                }
                if (merFee.getAmountUpper().compareTo(feeList.get(i).getAmountUpper()) == 0) {
                    //更新记录
                    MerchantFee mft = feeList.get(i-1);
                    mft.setAmountLower(merFee.getAmountLower());
                    mft.setMat(new Date());
                    mft.update();
                    merFee.delete();
                    break;
                }
            }
        }else {
            merFee.delete();
        }


        StringBuffer tradeType1 = new StringBuffer("select * from merchant_fee mf  where 1=1 and mf.dat is null   and mf.merID=? and mf.tradeType='1' order by mf.amountLower desc");
        StringBuffer tradeType2 = new StringBuffer("select * from merchant_fee mf  where 1=1 and mf.dat is null  and mf.merID=? and mf.tradeType='2'  order by mf.amountLower desc");
        List<MerchantFee> feeListJ = MerchantFee.dao.find(tradeType1.toString(),merFee.getMerID());
        List<MerchantFee> feeListB = MerchantFee.dao.find(tradeType2.toString(),merFee.getMerID());

        Map map = new HashMap();
        map.put("feeListJ",feeListJ);
        map.put("feeListB",feeListB);
        map.put("resCode",Consts.REQ_JSON_CODE.success.name());
        map.put("resMsg","手续费删除成功");

        renderJson(map);
    }



    @Before({ Tx.class})
    public void addFeeAmount(){
        BigDecimal merFeeAmount   = new BigDecimal(getPara("merFeeAmount"));
        String merID = getPara("id");

        MerchantInfo merchantInfo =MerchantInfo.dao.findById(new Integer(merID));
        BigDecimal merFeeBAmount = merchantInfo.getFeeAmount();
        BigDecimal merFeeAAmount =merchantInfo.getFeeAmount().add(merFeeAmount);

        MerchantFeeAmountRecord mfr = new MerchantFeeAmountRecord();
        mfr.setMerId(merchantInfo.getId());
        mfr.setBAmount(merFeeBAmount);
        mfr.setAmount(merFeeAmount);
        mfr.setAAmount(merFeeAAmount);
        mfr.setType("1");
        mfr.setCAt(new Date());
        mfr.setMerName(merchantInfo.getMerchantName());
        mfr.setMerNo(merchantInfo.getMerchantNo());
        mfr.setOperId(currUser().getId().intValue());
        mfr.setOperName(currUser().getNickname());

        merchantInfo.setFeeAmount(merFeeAAmount);
        merchantInfo.update();
        mfr.save();
        renderSuccessJSON("手续费续存成功。", "");
    }

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
    public void listFeeAmount(){
        String search = getPara("search");

        Integer merId=null;
        Kv kv= Kv.create();
        Boolean isOper = true;

        //获取当前登录用户信息
        MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
        if (merInfo != null) {
            search = merInfo.getMerchantNo();
            merId=merInfo.getId();
            kv.put("merId=",merId);
            isOper = false;
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

        Kv kv1 = Kv.by("cond",kv);
        if(StrUtil.isNotBlank(search)){
            kv1.put("search","%"+search+"%");
        }
        Page<MerchantFeeAmountRecord> page=MerchantFeeAmountRecord.dao.paginate(getPN(),getPS(), Db.getSqlPara("merchantFeeAmountRecord.page",kv1));
        Map map = new HashMap();
        map.put("page", page);
        map.put("isOper", isOper);
        renderJson(map);
    }
}

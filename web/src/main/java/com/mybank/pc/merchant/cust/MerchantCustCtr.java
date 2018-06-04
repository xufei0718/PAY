package com.mybank.pc.merchant.cust;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.jfinal.aop.Before;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;
import com.mybank.pc.CMNSrv;
import com.mybank.pc.Consts;
import com.mybank.pc.collection.entrust.CEntrustSrv;
import com.mybank.pc.collection.model.UnionpayEntrust;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.interceptors.AdminIAuthInterceptor;
import com.mybank.pc.kits.ResKit;
import com.mybank.pc.merchant.info.MerchantInfoSrv;
import com.mybank.pc.merchant.model.MerchantCust;
import com.mybank.pc.merchant.model.MerchantInfo;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathConstants;
import java.io.File;
import java.math.BigInteger;
import java.util.*;


/**
 * 商户信息管理
 */

public class MerchantCustCtr extends CoreController {
    private MerchantInfoSrv merchantInfoSrv = enhance(MerchantInfoSrv.class);
    private CEntrustSrv cEntrustSrv = enhance(CEntrustSrv.class);

    public void list() {
        Page<MerchantCust> page;
        String search = getPara("search");
        String search1 = getPara("search1");

        Kv kv = Kv.create();
        BigInteger i = currUser().getId();
        Boolean isOper = true;
        List<MerchantInfo> merInfoList =new ArrayList<>();

        //获取当前登录用户信息
        MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
        if (merInfo != null) {
            search1 = merInfo.getMerchantNo();
            isOper = false;
        }else{
            merInfoList  = MerchantInfo.dao.find("select *  from merchant_info mi  where 1=1 and mi.dat is null  ");
        }

        kv.set("search", search);
        kv.set("search1", search1);
        SqlPara sqlPara = Db.getSqlPara("merchant.custList", kv);

        page = MerchantCust.dao.paginate(getPN(), getPS(), sqlPara);

        Map map = new HashMap();
        map.put("page", page);
        map.put("isOper", isOper);
        map.put("merInfoList",merInfoList);
        renderJson(map);

    }

    @Before({MerchantCustValidator.class, Tx.class})
    public void save() {

        MerchantCust merCust = getModel(MerchantCust.class,"",true);
        //判断是否是操作员操作
        if (StrUtil.isBlankIfStr(merCust.getMerID())) {
            MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
            merCust.setMerID(merInfo.getId());
            merCust.setMerNo(merInfo.getMerchantNo());
        }else{
            MerchantInfo merchantInfo =  MerchantInfo.dao.findById(merCust.getMerID());
            merCust.setMerNo(merchantInfo.getMerchantNo());
        }


        merCust.setCat(new Date());
        merCust.setMat(new Date());
        merCust.setOperID(String.valueOf(currUser().getId()));



        Kv kv = Kv.create();
        kv.set("customerNm", merCust.getCustName());
        kv.set("certifTp", "01");
        kv.set("certifId", merCust.getCardID());
        kv.set("phoneNo", merCust.getMobileBank());
        kv.set("accNo", merCust.getBankcardNo());
        kv.set("merchantID", merCust.getMerID());
        kv.set("cvn2", "");
        kv.set("expired", "");
        Kv[] resKv = cEntrustSrv.establishAll(kv);

        for (int i = 0; i < resKv.length; i++) {
            if (!resKv[i].getBoolean("isSuccess")) {
                String errMsg;
                if (ObjectUtil.isNotNull(resKv[i].get("unionpayEntrust"))) {
                    errMsg = ((UnionpayEntrust) resKv[i].get("unionpayEntrust")).getRespMsg();
                    if (ObjectUtil.isNull(errMsg)) {
                        errMsg = "远程调用失败（系统内部异常）！";
                    }
                } else {
                    errMsg = "远程调用异常！";
                }

                renderFailJSON(errMsg);
                return;
            }
        }

        merCust.save();
        renderSuccessJSON("新增客户信息成功。", "");
    }

    @Before({MerchantCustValidator.class, Tx.class})
    public void update() {
        MerchantCust merCust = getModel(MerchantCust.class,"",true);

        merCust.setMat(new Date());
        merCust.setOperID(String.valueOf(currUser().getId()));
        merCust.update();
        renderSuccessJSON("更新客户信息成功。", "");
    }
    @Before({Tx.class})
    public void del() {
        int id = getParaToInt("id");
        MerchantCust merchantCust = MerchantCust.dao.findById(BigInteger.valueOf(id));
        merchantCust.setDat(new Date());
        merchantCust.update();
        renderSuccessJSON("删除商户信息成功。");
    }

    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void cust() {
        String merNo = getPara("merNo");
        MerchantInfo merchantInfo = MerchantInfo.dao.findFirst("select * from merchant_info mi where merchantNo=?", merNo);
        String url = "/WEB-INF/template/www/cust.html";
        if (ObjectUtil.isNull(merchantInfo)) {
            String resCode = "error";
            String resMsg = "商户不存在,请与商户客户核对！";
            setAttr("resCode", resCode);
            setAttr("resMsg", resMsg);
            url = "/WEB-INF/template/www/cust-res.html";
        } else {
            setAttr("merchantInfo", merchantInfo);
        }

        render(url);

    }

    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void custAgree() {
        String merNo = getPara("merNo");
        MerchantInfo merchantInfo = MerchantInfo.dao.findFirst("select * from merchant_info mi where merchantNo=?", merNo);
        String url = "/WEB-INF/template/www/cust-agree.html";
        if (ObjectUtil.isNull(merchantInfo)) {
            String resCode = "error";
            String resMsg = "商户不存在,请与商户客户核对！";
            setAttr("resCode", resCode);
            setAttr("resMsg", resMsg);
            url = "/WEB-INF/template/www/cust-res.html";
        } else {
            setAttr("merchantInfo", merchantInfo);
        }

        render(url);

    }

    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void addCust() {

        try {
            //此处一定优先处理上传文件，后处理其它参数
            UploadFile cardImgZ = getFile("cardImgZ", "", 50 * 1024 * 1000);
            UploadFile selfImg = getFile("selfImg", "", 50 * 1024 * 1000);
            UploadFile authImg = getFile("authImg", "", 50 * 1024 * 1000);
            File cardImgZFile = cardImgZ.getFile();
            File selfImgFile = selfImg.getFile();
            File authImgFile = authImg.getFile();
            LogKit.info("上传文件1大小：" + cardImgZFile.length());
            LogKit.info("上传文件2大小：" + selfImgFile.length());
            LogKit.info("上传文件3大小：" + authImgFile.length());

            String merNo = getPara("merNo");
            String custName = getPara("custName");
            String cardID = getPara("cardID");
            String mobileBank = getPara("mobileBank");
            String bankcardNo = getPara("bankcardNo");

            /*String code = getPara("code");
            String smsCode = CacheKit.get(Consts.CACHE_NAMES.smsCode.name(), mobileBank);
            if (ObjectUtil.isNotNull(code)) {
                if (!code.equals(smsCode)) {
                    setAttr("resCode", "error");
                    setAttr("resMsg", "验证码错误！");
                    render("/WEB-INF/template/www/cust-res.html");
                    return;
                }
            }*/
            MerchantInfo mf = MerchantInfo.dao.findFirst("select * from merchant_info mi where mi.merchantNo=? and mi.dat is null ", merNo);

            if (ObjectUtil.isNull(mf)) {
                //商户编号输入错误，商户不存在
                setAttr("resCode", "error");
                setAttr("resMsg", "商户不存在！");
                render("/WEB-INF/template/www/cust-res.html");
                return;
            }


            List<MerchantCust> mcList = MerchantCust.dao.find("select * from merchant_cust mc where mc.dat is null and  mc.merNo=?  and mc.cardID=? and mc.mobileBank=? and mc.bankcardNo=?", merNo, cardID, mobileBank, bankcardNo);
            if (!CollectionUtil.isEmpty(mcList)) {
                //同一商户下已经绑定过银行卡
                setAttr("resCode", "error");
                setAttr("resMsg", "该客户银行卡已经绑定！");
                render("/WEB-INF/template/www/cust-res.html");
                return;
            }


            Kv kv = Kv.create();
            kv.set("customerNm", custName);
            kv.set("certifTp", "01");
            kv.set("certifId", cardID);
            kv.set("phoneNo", mobileBank);
            kv.set("accNo", bankcardNo);
            kv.set("merchantID", mf.getId());
            kv.set("cvn2", "");
            kv.set("expired", "");
            Kv[] resKv = cEntrustSrv.establishAll(kv);

            for (int i = 0; i < resKv.length; i++) {
                if (!resKv[i].getBoolean("isSuccess")) {
                    String errMsg;
                    if (ObjectUtil.isNotNull(resKv[i].get("unionpayEntrust"))) {
                        errMsg = ((UnionpayEntrust) resKv[i].get("unionpayEntrust")).getRespMsg();
                        if (ObjectUtil.isNull(errMsg)) {
                            errMsg = "远程调用失败（系统内部异常）！";
                        }
                    } else {
                        errMsg = "远程调用异常！";
                    }
                    setAttr("resCode", "error");
                    setAttr("resMsg", errMsg);
                    render("/WEB-INF/template/www/cust-res.html");
                    return;
                }
            }


            //保存图片
            String cardImgZID = CMNSrv.saveFile(cardImgZFile, FileUtil.getType(cardImgZFile));
            String selfImgID = CMNSrv.saveFile(selfImgFile, FileUtil.getType(selfImgFile));
            String authImgID = CMNSrv.saveFile(authImgFile, FileUtil.getType(authImgFile));
            //调用四要素验证接口进行绑卡，如果成功则记录
            MerchantCust mc = new MerchantCust();
            mc.setMerID(mf.getId());
            mc.setMerNo(merNo);
            mc.setCustName(custName);
            mc.setCardID(cardID);
            mc.setMobileBank(mobileBank);
            mc.setBankcardNo(bankcardNo);
            mc.setCardImgZ(cardImgZID);
            mc.setSelfImg(selfImgID);
            mc.setAuthImg(authImgID);
            mc.setCat(new Date());
            mc.save();


        } catch (Exception e) {
            e.printStackTrace();
            setAttr("resCode", "error");
            setAttr("resMsg", "系统异常，绑定失败");
            render("/WEB-INF/template/www/cust-res.html");
            return;
        }
        setAttr("resCode", "success");
        setAttr("resMsg", "绑定成功");
        render("/WEB-INF/template/www/cust-res.html");

    }

    //客户绑定银行卡发送短信验证码
    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void sendCode() {
        String flag = "0";
        String url = ResKit.getMsg("url");

        String account = ResKit.getMsg("account");
        String password = ResKit.getMsg("password");
        String id = ResKit.getMsg("id");
        String content = ResKit.getMsg("content");
        String rand = RandomUtil.randomNumbers(6);
        content = String.format(content, rand);
        String mobile = getPara("mobile");


        Map<String, String> map = new HashMap<>();
        map.put("action", "send");
        map.put("userid", id);
        map.put("account", account);
        map.put("password", password);
        map.put("mobile", mobile);
        map.put("content", content);
        String res;
        String message;
        try {
            LogKit.info("发送短信：" + mobile + " 内容：" + content);
            res = HttpKit.post(url, map, "");
            LogKit.info("返回信息：" + res);
            Document doc = XmlUtil.readXML(res);
            message = XmlUtil.getByXPath("//returnsms/message", doc, XPathConstants.STRING).toString();
            if ("ok".equals(message)) {
                CacheKit.remove("smsCode", mobile);
                CacheKit.put("smsCode", mobile, rand);
            } else {
                flag = "1";
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = "1";
        }

        renderJson(flag);
    }
}

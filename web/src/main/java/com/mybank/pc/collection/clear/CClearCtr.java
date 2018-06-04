package com.mybank.pc.collection.clear;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Duang;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.upload.UploadFile;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.User;
import com.mybank.pc.collection.model.CollectionClear;
import com.mybank.pc.collection.model.CollectionCleartotle;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.core.CoreException;
import com.mybank.pc.kits.AppKit;
import com.mybank.pc.kits.DateKit;
import com.mybank.pc.kits.json.filter.BigDecimalValueFilter;
import com.mybank.pc.merchant.model.MerchantInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 *
 *
 *
 * 清分请求处理
 *
 *
 *
 */
public class CClearCtr extends CoreController {
    private CClearSrv cClearSrv= Duang.duang("cClearSrv",CClearSrv.class);
    /**
     *
     * 清分详细分页查询
     *
     */
    public void list(){
        String merNo=getPara("merNo");
        String bTime=getPara("bTime");
        String eTime=getPara("eTime");
        String chargeOff=getPara("chargeOff");


        bTime=DateKit.getTimeStampBegin(DateKit.strToDate(bTime,DateKit.yyyy_MM_dd));
        eTime=DateKit.getTimeStampEnd(DateKit.strToDate(eTime,DateKit.yyyy_MM_dd));

        User user=currUser();
        MerchantInfo merchantInfo=getAttr(Consts.CURR_USER_MER);
        if(merchantInfo!=null){
            merNo=merchantInfo.getMerchantNo();
        }

        Page<CollectionClear> page;
        Kv kv = Kv.create();

        if(StrUtil.isNotBlank(merNo)){
            kv.put("cc.merNO=",merNo);
        }
        if(StrUtil.isNotBlank(chargeOff)){
            kv.put("cc.chargeOff=",chargeOff);
        }
        kv.put("clearTime>=",bTime);
        kv.put("clearTime<=",eTime);
        String sqlKey=merchantInfo==null?"collection_clear.findPage":"collection_clear.findPage4Mer";
        SqlPara sqlPara = Db.getSqlPara(sqlKey, Kv.by("cond", kv));
        page =CollectionClear.dao.paginate(getPN(), getPS(), sqlPara);
        renderJson(JSON.toJSONString(page,new BigDecimalValueFilter()));
    }

    /**
     *
     * 清分汇总统计查询
     *
     *
     */
    public void sum(){
        String merNo=getPara("merNo");
        String bTime=getPara("bTime");
        String eTime=getPara("eTime");
        String changeOff=getPara("changeOff");
        bTime=DateKit.getTimeStampBegin(DateKit.strToDate(bTime,DateKit.yyyy_MM_dd));
        eTime=DateKit.getTimeStampEnd(DateKit.strToDate(eTime,DateKit.yyyy_MM_dd));
        User user=currUser();
        MerchantInfo merchantInfo=getAttr(Consts.CURR_USER_MER);
        if(merchantInfo!=null){
            merNo=merchantInfo.getMerchantNo();
        }
        Kv kv = Kv.create();

        if(StrUtil.isNotBlank(merNo)){
            kv.put("cc.merNO=",merNo);
        }
        if(StrUtil.isNotBlank(changeOff)){
            kv.put("cc.chargeOff=",changeOff);
        }
        kv.put("clearTime>=",bTime);
        kv.put("clearTime<=",eTime);
        String sqlKey=merchantInfo==null?"collection_clear.sum":"collection_clear.sum4Mer";
        SqlPara sqlPara = Db.getSqlPara(sqlKey, Kv.by("cond", kv));
        CollectionClear collectionClear =CollectionClear.dao.findFirst( sqlPara);
        renderJson(JSON.toJSONString(collectionClear,new BigDecimalValueFilter()));
    }

    /**
     *
     * 清分汇总查询
     *
     *
     */
    public void totalList(){
        String bTime=getPara("bTime");
        String eTime=getPara("eTime");
        bTime=DateKit.getTimeStampBegin(DateKit.strToDate(bTime,DateKit.yyyy_MM_dd));
        eTime=DateKit.getTimeStampEnd(DateKit.strToDate(eTime,DateKit.yyyy_MM_dd));
        Page<CollectionCleartotle> page;
        Kv kv = Kv.create();
        kv.put("cleartotleTime>=",bTime);
        kv.put("cleartotleTime<=",eTime);
        SqlPara sqlPara = Db.getSqlPara("collection_clear.findTotalPage", Kv.by("cond", kv));
        page =CollectionCleartotle.dao.paginate(getPN(), getPS(), sqlPara);

        sqlPara = Db.getSqlPara("collection_clear.sumTotal", Kv.by("cond", kv));
//        CollectionCleartotle collectionCleartotle=CollectionCleartotle.dao.findFirst(sqlPara);
//        page.getList().add(collectionCleartotle);

        renderJson(JSON.toJSONString(page,new BigDecimalValueFilter()));
    }

    /**
     *
     * 清分汇总统计查询
     *
     *
     */
    public void sumTotal(){
        String bTime=getPara("bTime");
        String eTime=getPara("eTime");
        bTime=DateKit.getTimeStampBegin(DateKit.strToDate(bTime,DateKit.yyyy_MM_dd));
        eTime=DateKit.getTimeStampEnd(DateKit.strToDate(eTime,DateKit.yyyy_MM_dd));
        Kv kv = Kv.create();
        kv.put("cleartotleTime>=",bTime);
        kv.put("cleartotleTime<=",eTime);
        SqlPara sqlPara = Db.getSqlPara("collection_clear.sumTotal", Kv.by("cond", kv));
        CollectionCleartotle collectionCleartotle=CollectionCleartotle.dao.findFirst(sqlPara);
        renderJson(JSON.toJSONString(collectionCleartotle,new BigDecimalValueFilter()));
    }


    /**
     *
     * 手动清分处理
     *
     */
    public void hmClear(){

        Date date=getParaToDate("clearDate");
        try {
            cClearSrv.doClear(date);

        }catch (CoreException ce){
            LogKit.error(ce.getMsg());
            renderFailJSON("手动清分处理失败,"+ce.getMsg());
            return;
        }
        catch (ActiveRecordException are){
            LogKit.error("手动清分处理失败===>>>"+are.getMessage());
            renderFailJSON("手动清分处理失败，请重试");
            return;
        }catch (RuntimeException re){
            LogKit.error("手动清分处理失败===>>>"+re.getMessage());
            renderFailJSON("手动清分处理失败，请重试");
            return;
        }
        renderSuccessJSON("手动清分处理完成");
    }

    /**
     *
     * 出账处理
     * clearId 清分id
     * chargeOffTradeNo汇款凭证单据号
     * forceUpdate是否强制更新数据
     */
    public void  debit(){
//        Integer clearId=getParaToInt("id");
//        String  chargeOffTradeNo=getPara("chargeOffTradeNo");
//        String amoutOff=getPara("amountOff");
//        String chargeAt=getPara("chargeAt");

//        String forceUpdate=getPara("forceUpdate");//是否强制更新
//        forceUpdate=StrUtil.isBlank(forceUpdate)?Consts.YORN_STR.no.getVal():Consts.YORN_STR.yes.getVal();
//        if(StrUtil.isBlank(chargeOffTradeNo)){
//            renderFailJSON("银行转账凭据号不能为空");
//            return;
//        }
        CollectionClear collectionClear=getModel(CollectionClear.class,"",true);

//        if ((collectionClear.getChargeOff().equals(Consts.YORN_STR.yes.getVal())|| StrUtil.isNotBlank(collectionClear.getChargeOffTradeNo()))){
//                &&forceUpdate.equals(Consts.YORN_STR.no.getVal())){
//            renderFailJSON("该清分数据已经出账");
//            return;
//        }
//        collectionClear.setChargeOffTradeNo(chargeOffTradeNo);
        collectionClear.setChargeOff(Consts.YORN_STR.yes.getVal());
        collectionClear.setMat(new Date());
//        collectionClear.setAmountOff(new BigDecimal(amoutOff));
//        collectionClear.setClearTime(new Date());
//        collectionClear.setChargeAt();
        collectionClear.setOperID(currUser()!=null?currUser().getId().toString():null);
        collectionClear.update();
        renderSuccessJSON("出账处理成功");
    }


    /**
     *
     * 批量出账处理
     * 从清分报表中导入，银行转账凭据号
     *
     *
     */
    public void  batchDebit(){
        UploadFile uf=getFile("file");
        File file=uf.getFile();
        ExcelReader excelReader=ExcelUtil.getReader(file);
        List<List<Object>> readData=excelReader.read();
        int rowCount=readData.size();
        readData = excelReader.read(2,rowCount-2);
        List list=null;
        Object obj=null;
        String chargeOffTradeNo=null;
        String clearNo=null;
        Date chargetAt=null;
        int sCount=0,fCount=0,eCount=0;
        CollectionClear collectionClear=null;

        LogKit.info("批量出账处理开始");
        for (int i=0;i<readData.size();i++){
            list=readData.get(i);
            LogKit.info("批量导入的出账数据==>>"+ list.toString());

            chargeOffTradeNo = (String)list.get(13) ;
            clearNo=(String)list.get(0);
            chargetAt=DateKit.strToDate((String) list.get(12),DateKit.STR_DATEFORMATE);
            collectionClear=CollectionClear.dao.findFristByPropEQ("clearNo",clearNo);
            if(collectionClear!=null){
                if(collectionClear.getChargeOff().equals(Consts.YORN_STR.no.getVal())) {
                    sCount++;
                    collectionClear.setChargeOffTradeNo(chargeOffTradeNo);
                    collectionClear.setChargeOff(Consts.YORN_STR.yes.getVal());
                    collectionClear.setChargeAt(chargetAt);
                    collectionClear.setMat(new Date());
                    collectionClear.setOperID(currUser()!=null?currUser().getId().toString():"");
                    collectionClear.update();
                }else{
                    eCount++;
                }
            }else{
                fCount++;
            }
        }
        String msg="成功处理了{}条数据，有{}条数据已经出账，有{}条数据未找到清分记录";
        msg=StrUtil.format(msg,sCount,eCount,fCount);
        LogKit.info("批量出账处理结束，处理结果===>>>"+msg);
        renderSuccessJSON(msg);
    }

    /**
     *
     * 查询导出全部，主要给公司内部系统操作者使用
     *
     *
     */
    public void exportTotalExcel() {
        String bTime=getPara("bTime");
        String eTime=getPara("eTime");
        String excelTitle=bTime+"至"+eTime+"清分数据汇总";
        bTime=DateKit.getTimeStampBegin(DateKit.strToDate(bTime,DateKit.yyyy_MM_dd));
        eTime=DateKit.getTimeStampEnd(DateKit.strToDate(eTime,DateKit.yyyy_MM_dd));
//        String excelTitle=bTime+"-"+eTime+"清分数据汇总";
        User user=currUser();
        Kv kv = Kv.create();
        kv.put("cleartotleTime>=",bTime);
        kv.put("cleartotleTime<=",eTime);

        SqlPara sqlPara = Db.getSqlPara("collection_clear.findTotalPage", Kv.by("cond", kv));
        List<CollectionCleartotle> list =CollectionCleartotle.dao.find(sqlPara);
        if(list.size()==0){
            renderFailJSON("没有查询到要导出的数据");
            return;
        }
        ExcelWriter writer = ExcelUtil.getWriter();
        List<Map> excelList=new ArrayList<>();
        collectionClearTotleToExcelData(list,excelList);
        writer.merge(7,excelTitle);
        Map<String, String> alias = new HashMap<>();
        alias.put("tradeCount", "交易笔数");
        alias.put("amountSum", "交易金额");
        alias.put("amountFeeSum", "交易手续费金额");
        alias.put("accountFee", "手续费抵扣金额（预存账户）");
        alias.put("tradeFee", "手续费抵扣金额（交易金额）");
        alias.put("bankFee", "银行代收手续费");
        alias.put("amountOff","实际出账金额");
        alias.put("profit","利润");
        writer.setHeaderAlias(alias);
        writer.write(excelList);
        sqlPara = Db.getSqlPara("collection_clear.sumTotal", Kv.by("cond", kv));
        CollectionCleartotle collectionCleartotle=CollectionCleartotle.dao.findFirst(sqlPara);
        int r=list.size()+3;
        writer.writeCellValue(0,r-1,"合计:");
        writer.writeCellValue(0,r,collectionCleartotle.getTradeCount().toString());
        writer.writeCellValue(1,r,collectionCleartotle.getAmountSum().toString());
        writer.writeCellValue(2,r,collectionCleartotle.getAmountFeeSum().toString());
        writer.writeCellValue(3,r,collectionCleartotle.getAccountFee().toString());
        writer.writeCellValue(4,r,collectionCleartotle.getTradeFee().toString());
        writer.writeCellValue(5,r,collectionCleartotle.getBankFee().toString());
        writer.writeCellValue(6,r,collectionCleartotle.getAmountOff().toString());
        writer.writeCellValue(7,r,collectionCleartotle.getProfit().toString());



        String fileFolder="qfhz/"+DateKit.dateToStr(new Date(),DateKit.yyyy_MM_dd)+"/";
        File file=FileUtil.file(PathKit.getWebRootPath() + AppKit.getExcelPath() +fileFolder);
        if(!file.exists())file.mkdirs();
        String fileName = fileFolder+(currUser()!=null?currUser().getLoginname():"")+DateUtil.current(true) + ".xls";
         file = FileUtil.file(PathKit.getWebRootPath() + AppKit.getExcelPath() + fileName);
        try {
            OutputStream out = new FileOutputStream(file);
            writer.flush(out);
            writer.close();
            out.flush();
            out.close();
        }catch (IOException e){
            LogKit.error("文件导出失败=>"+e.getMessage());
            renderFailJSON("文件导出失败");
            return;
        }
        renderSuccessJSON("文件导出成功",fileName);
    }
    
    
    /**
     *
     * 查询导出全部，主要给公司内部系统操作者使用
     *
     *
     */
    public void exportExcel() {
        String merNo=getPara("merNo");
        String bTime=getPara("bTime");
        String eTime=getPara("eTime");
        String changeOff=getPara("changeOff");
        String excelTitle=bTime+"-"+eTime+"清分数据";
        bTime=DateKit.getTimeStampBegin(DateKit.strToDate(bTime,DateKit.yyyy_MM_dd));
        eTime=DateKit.getTimeStampEnd(DateKit.strToDate(eTime,DateKit.yyyy_MM_dd));
        User user=currUser();
        Kv kv = Kv.create();
        if(StrUtil.isNotBlank(merNo)){
            kv.put("cc.merNO=",merNo);
        }
        if(StrUtil.isNotBlank(changeOff)){
            kv.put("cc.chargeOff=",changeOff);
        }
        kv.put("clearTime>=",bTime);
        kv.put("clearTime<=",eTime);

        SqlPara sqlPara = Db.getSqlPara("collection_clear.findPage", Kv.by("cond", kv));
        List<CollectionClear> list =CollectionClear.dao.find(sqlPara);
        if(list.size()==0){
            renderFailJSON("没有查询到要导出的数据");
            return;
        }
        ExcelWriter writer = ExcelUtil.getWriter();
        List<Map> excelList=new ArrayList<>();
        collectionClearToExcelData(list,excelList);
        writer.merge(18,excelTitle);
        Map<String, String> alias = new HashMap<>();
        alias.put("clearNo", "清分流水号");
        alias.put("merNo", "商户编号");
        alias.put("merName", "商户名称");
        alias.put("tradeCount", "交易笔数");
        alias.put("amountSum", "交易金额");
        alias.put("amountFeeSum", "交易手续费金额");
        alias.put("accountFee", "手续费抵扣金额（预存账户）");
        alias.put("tradeFee", "手续费抵扣金额（交易金额）");
        alias.put("bankFee", "银行代收手续费");
        alias.put("amountOff","实际出账金额");
        alias.put("clearTime","清分时间");
        alias.put("profit","利润");
        alias.put("bankNo","收款卡号");
        alias.put("bankAccountName","户名");
        alias.put("bankName","开户行");
        alias.put("bankPhone","预留手机号");
        alias.put("bankCode","行号");
        alias.put("chargeAt","出账汇款时间");
        alias.put("chargeOffTradeNo","汇款凭证单号");
        writer.setHeaderAlias(alias);
        writer.write(excelList);
        sqlPara = Db.getSqlPara("collection_clear.sum", Kv.by("cond", kv));
        CollectionClear collectionClear =CollectionClear.dao.findFirst( sqlPara);
        int r=list.size()+2;
        writer.writeCellValue(0,r,"合计:");
        writer.writeCellValue(1,r,"");
        writer.writeCellValue(2,r,"");
        writer.writeCellValue(3,r,collectionClear.getTradeCount().toString());
        writer.writeCellValue(4,r,collectionClear.getAmountSum().toString());
        writer.writeCellValue(5,r,collectionClear.getAmountFeeSum().toString());
        writer.writeCellValue(6,r,collectionClear.getAccountFee().toString());
        writer.writeCellValue(7,r,collectionClear.getTradeFee().toString());
        writer.writeCellValue(8,r,collectionClear.getBankFee().toString());
        writer.writeCellValue(9,r,collectionClear.getAmountOff().toString());
        writer.writeCellValue(10,r,collectionClear.getProfit().toString());
        writer.writeCellValue(11,r,"");
        writer.writeCellValue(12,r,"");
        writer.writeCellValue(13,r,"");
        writer.writeCellValue(14,r,"");
        writer.writeCellValue(15,r,"");
        writer.writeCellValue(16,r,"");
        writer.writeCellValue(17,r,"");
        writer.writeCellValue(18,r,"");

        String fileFolder="qfmx/"+DateKit.dateToStr(new Date(),DateKit.yyyy_MM_dd)+"/";
        File file=FileUtil.file(PathKit.getWebRootPath() + AppKit.getExcelPath() +fileFolder);
        if(!file.exists())file.mkdirs();
        String fileName = fileFolder+(currUser()!=null?currUser().getLoginname():"")+DateUtil.current(true) + "_"+excelTitle+".xls";
        file = FileUtil.file(PathKit.getWebRootPath() + AppKit.getExcelPath() + fileName);
        try {
            OutputStream out = new FileOutputStream(file);
            writer.flush(out);
            writer.close();
            out.flush();
            out.close();
        }catch (IOException e){
            LogKit.error("文件导出失败=>"+e.getMessage());
            renderFailJSON("文件导出失败");
            return;
        }
        renderSuccessJSON("文件导出成功",fileName);
    }

    /**
     *
     * 查询导出全部，商户操作员使用
     *
     *
     */
    public void exportExcel4Mer() {
        String merNo=getPara("merNo");
        String bTime=getPara("bTime");
        String eTime=getPara("eTime");
        String changeOff=getPara("changeOff");
        String excelTitle=bTime+"-"+eTime+"清分数据";
        bTime=DateKit.getTimeStampBegin(DateKit.strToDate(bTime,DateKit.yyyy_MM_dd));
        eTime=DateKit.getTimeStampEnd(DateKit.strToDate(eTime,DateKit.yyyy_MM_dd));

        User user=currUser();
        MerchantInfo merchantInfo=getAttr(Consts.CURR_USER_MER);
        if(merchantInfo!=null){
            merNo=merchantInfo.getMerchantNo();
        }
        Kv kv = Kv.create();
        if(StrUtil.isNotBlank(merNo)){
            kv.put("cc.merNO=",merNo);
        }
        if(StrUtil.isNotBlank(changeOff)){
            kv.put("cc.chargeOff=",changeOff);
        }
        kv.put("clearTime>=",bTime);
        kv.put("clearTime<=",eTime);

        SqlPara sqlPara = Db.getSqlPara("collection_clear.findPage", Kv.by("cond", kv));
        List<CollectionClear> list =CollectionClear.dao.find(sqlPara);
        if(list.size()==0){
            renderFailJSON("没有查询到要导出的数据");
            return;
        }
        ExcelWriter writer = ExcelUtil.getWriter();
        List<Map> excelList=new ArrayList<>();
        collectionClearToExcelData4Mer(list,excelList);
        writer.merge(11,excelTitle);
        Map<String, String> alias = new HashMap<>();
        alias.put("clearNo", "清分流水号");
        alias.put("merNo", "商户编号");
        alias.put("merName", "商户名称");
        alias.put("tradeCount", "交易笔数");
        alias.put("amountSum", "交易金额");
        alias.put("amountFeeSum", "交易手续费金额");
        alias.put("accountFee", "手续费抵扣金额（预存账户）");
        alias.put("tradeFee", "手续费抵扣金额（交易金额）");
        alias.put("amountOff","实际出账金额");
        alias.put("clearTime","清分时间");
        alias.put("chargeAt","出账汇款时间");
        alias.put("chargeOffTradeNo","汇款凭证单号");
        writer.setHeaderAlias(alias);
        writer.write(excelList);

        sqlPara = Db.getSqlPara("collection_clear.sum", Kv.by("cond", kv));
        CollectionClear collectionClear =CollectionClear.dao.findFirst( sqlPara);
        int r=list.size()+2;
        writer.writeCellValue(0,r,"合计:");
        writer.writeCellValue(1,r,"");
        writer.writeCellValue(2,r,"");
        writer.writeCellValue(3,r,collectionClear.getTradeCount().toString());
        writer.writeCellValue(4,r,collectionClear.getAmountSum().toString());
        writer.writeCellValue(5,r,collectionClear.getAmountFeeSum().toString());
        writer.writeCellValue(6,r,collectionClear.getAccountFee().toString());
        writer.writeCellValue(7,r,collectionClear.getTradeFee().toString());
        writer.writeCellValue(8,r,collectionClear.getAmountOff().toString());
        writer.writeCellValue(9,r,"");
        writer.writeCellValue(10,r,"");
        writer.writeCellValue(11,r,"");



        String fileFolder="qfmx/"+DateKit.dateToStr(new Date(),DateKit.yyyy_MM_dd)+"/";
        File file=FileUtil.file(PathKit.getWebRootPath() + AppKit.getExcelPath() +fileFolder);
        if(!file.exists())file.mkdirs();
        String fileName = fileFolder+(currUser()!=null?currUser().getLoginname():"")+DateUtil.current(true) + ".xls";
        file = FileUtil.file(PathKit.getWebRootPath() + AppKit.getExcelPath() + fileName);
        try {
            OutputStream out = new FileOutputStream(file);
            writer.flush(out);
            writer.close();
            out.flush();
            out.close();
        }catch (IOException e){
            LogKit.error("文件导出失败=>"+e.getMessage());
            renderFailJSON("文件导出失败");
            return;
        }
        renderSuccessJSON("文件导出成功",fileName);
    }




    /**
     *
     * Excel 辅助数据转换方法 collectionClear
     *
     * @param list
     * @param ret
     */
    private void collectionClearToExcelData(List<CollectionClear> list,List<Map> ret){
        Map<String,String> map=null;
        for (CollectionClear collectionClear:list){
            map=new LinkedHashMap<>();
            map.put("clearNo",collectionClear.getClearNo());
            map.put("merNo",collectionClear.getMerNO());
            map.put("merName",collectionClear.getMerName());
            map.put("tradeCount",collectionClear.getTradeCount()!=null?collectionClear.getTradeCount().toString():"0");
            map.put("amountSum",collectionClear.getAmountSum()!=null?collectionClear.getAmountSum().toString():"0");
            map.put("amountFeeSum",collectionClear.getAmountFeeSum()!=null?collectionClear.getAmountFeeSum().toString():"0");
            map.put("accountFee",collectionClear.getAccountFee()!=null?collectionClear.getAccountFee().toString():"0");
            map.put("tradeFee",collectionClear.getTradeFee()!=null?collectionClear.getTradeFee().toString():"0");
            map.put("bankFee",collectionClear.getBankFee()!=null?collectionClear.getBankFee().toString():"0");
            map.put("amountOff",collectionClear.getAmountOff()!=null?collectionClear.getAmountOff().toString():"0");
            map.put("profit",collectionClear.getProfit());//利润
            map.put("clearTime",DateKit.dateToStr(collectionClear.getClearTime(),DateKit.format4Login));
            map.put("bankNo",collectionClear.getBankNo());
            map.put("bankAccountName",collectionClear.getBankAccountName());
            map.put("bankName",collectionClear.getBankName());
            map.put("bankPhone",collectionClear.getBankPhone());
            map.put("bankCode",collectionClear.getBankCode());
            map.put("chargeAt",DateKit.dateToStr(collectionClear.getChargeAt(),DateKit.format4Login));
            map.put("chargeOffTradeNo",collectionClear.getChargeOffTradeNo());

            ret.add(map);
        }


    }

    /**
     *
     * Excel 辅助数据转换方法 collectionClear
     *
     * @param list
     * @param ret
     */
    private void collectionClearToExcelData4Mer(List<CollectionClear> list,List<Map> ret){
        Map<String,String> map=null;
        for (CollectionClear collectionClear:list){
            map=new LinkedHashMap<>();
            map.put("clearNo",collectionClear.getClearNo());
            map.put("merNo",collectionClear.getMerNO());
            map.put("merName",collectionClear.getMerName());
            map.put("tradeCount",collectionClear.getTradeCount()!=null?collectionClear.getTradeCount().toString():"0");
            map.put("amountSum",collectionClear.getAmountSum()!=null?collectionClear.getAmountSum().toString():"0");
            map.put("amountFeeSum",collectionClear.getAmountFeeSum()!=null?collectionClear.getAmountFeeSum().toString():"0");
            map.put("accountFee",collectionClear.getAccountFee()!=null?collectionClear.getAccountFee().toString():"0");
            map.put("tradeFee",collectionClear.getTradeFee()!=null?collectionClear.getTradeFee().toString():"0");
//            map.put("bankFee",collectionClear.getBankFee()!=null?collectionClear.getBankFee().toString():"0");
            map.put("amountOff",collectionClear.getAmountOff()!=null?collectionClear.getAmountOff().toString():"0");
//            map.put("profit",collectionClear.getProfit());//利润
            map.put("clearTime",DateKit.dateToStr(collectionClear.getClearTime(),DateKit.format4Login));
            map.put("chargeAt",DateKit.dateToStr(collectionClear.getChargeAt(),DateKit.format4Login));
            map.put("chargeOffTradeNo",collectionClear.getChargeOffTradeNo());

            ret.add(map);
        }


    }

    /**
     *
     * Excel 辅助数据转换方法 CollectionClearTotle
     *
     * @param list
     * @param ret
     */
    private void collectionClearTotleToExcelData(List<CollectionCleartotle> list,List<Map> ret){
        Map<String,String> map=null;
        for (CollectionCleartotle collectionCleartotle:list){
            map=new LinkedHashMap<>();
            map.put("tradeCount",collectionCleartotle.getTradeCount()!=null?collectionCleartotle.getTradeCount().toString():"0");
            map.put("amountSum",collectionCleartotle.getAmountSum()!=null?collectionCleartotle.getAmountSum().toString():"0");
            map.put("amountFeeSum",collectionCleartotle.getAmountFeeSum()!=null?collectionCleartotle.getAmountFeeSum().toString():"0");
            map.put("accountFee",collectionCleartotle.getAccountFee()!=null?collectionCleartotle.getAccountFee().toString():"0");
            map.put("tradeFee",collectionCleartotle.getTradeFee()!=null?collectionCleartotle.getTradeFee().toString():"0");
            map.put("bankFee",collectionCleartotle.getBankFee()!=null?collectionCleartotle.getBankFee().toString():"0");
            map.put("amountOff",collectionCleartotle.getAmountOff()!=null?collectionCleartotle.getAmountOff().toString():"0");
            map.put("profit",collectionCleartotle.getProfit());
            ret.add(map);
        }
    }

    /**
     * 清分邮件发送处理
     *
     *
     */
    public void sendClearEmail(){
        String date=getPara("date");
        if(StrUtil.isNotBlank(date)){
            cClearSrv.sendClearEmail(DateKit.strToDate(date,DateKit.yyyy_MM_dd));
        }else{
           renderFailJSON("清分日期不能未空");
           return;
        }
        renderSuccessJSON("清分邮件发送成功");
    }


}

package com.mybank.pc;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.core.CoreException;
import com.mybank.pc.interceptors.AdminIAuthInterceptor;
import com.mybank.pc.kits.AppKit;
import com.mybank.pc.kits.DateKit;
import com.mybank.pc.kits.QiNiuKit;
import com.mybank.pc.kits._StrKit;
import com.mybank.pc.merchant.info.MerchantInfoSrv;
import com.qiniu.common.QiniuException;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * 简介       通用的公共的controller
 * <p>
 * 项目名称:   [mb-pc]
 * 包:        [com.mb.pc]
 * 类名称:    [CMNCtr]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2017/12/7]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class CMNCtr extends CoreController {

    private MerchantInfoSrv merchantInfoSrv =enhance(MerchantInfoSrv.class);
    /**
     * 图片的格式为base64Str上传图片到图片服务器默认七牛，返回图片显示的url
     */
    public void act00() {
        String base64Str = getPara("b64s");
        String savePath = getPara("sp");

        if (StrUtil.isBlank(base64Str)) {
            renderFailJSON("图片上传失败，上传的图片数据为空");
            return;
        }

        if (StrUtil.isBlank(savePath))
            savePath = "/cmn/pic/";

        String picServerUrl = CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "qn_url");
        String picName = DateKit.dateToStr(new Date(), DateKit.yyyyMMdd) + "/" + _StrKit.getUUID() + ".jpg";

        String qnRs = null;
        try {
            qnRs = QiNiuKit.put64image(base64Str, savePath + picName);
        } catch (IOException e) {
            LogKit.error("上传base64图片失败:" + e.getMessage());
            throw new CoreException("上传图片到服务器失败>>" + e.getMessage());
        }
        if (qnRs == null) {
            renderFailJSON("图片上传失败");
            return;
        } else {
            if (qnRs.indexOf("200") > -1) {
                renderSuccessJSON("图片上传成功", picServerUrl + savePath + picName);
            } else {
                LogKit.error("base64上传失败:" + qnRs);
                renderFailJSON("图片上传失败", "");
                return;
            }
        }
    }

    /**
     * 七牛图片上传 ，图片以文件形式上传
     */
    public void act01() {
        UploadFile uf = getFile("file");
        File file = uf.getFile();
        String savePath = getPara("sp");
        if (StrUtil.isBlank(savePath))
            savePath = "/cmn/pic/";

        String picServerUrl = CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "qn_url");
        String picName = DateKit.dateToStr(new Date(), DateKit.yyyyMMdd) + "/" + _StrKit.getUUID() + ".jpg";
        try {
            QiNiuKit.upload(file, savePath + picName);
        } catch (QiniuException e) {
            LogKit.error("七牛上传图片失败>>" + e.getMessage());
            renderFailJSON("图片上传失败");
        }
        renderSuccessJSON("图片上传成功", picServerUrl + savePath + picName);

    }


    /**
     * 下载根据excel 路径 下载excel
     */
    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void act02() {
        String ePath = getPara("ePath");
        File file = FileUtil.file(PathKit.getWebRootPath() + AppKit.getExcelPath() + ePath);
        int index = ePath.lastIndexOf("/");
        String str = ePath.substring(index, ePath.length());
        renderFile(file, str);
    }


    /**
     * 图片上传
     */
    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void act03() {
        UploadFile uf = getFile("file");
        File file = uf.getFile();
        String fileID = CMNSrv.saveFile(file, FileUtil.getType(file));
        if (fileID == null) {
            renderFailJSON("图片上传失败");
        } else {
            renderSuccessJSON("图片上传成功", fileID);
        }


    }

    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void act04() {
        String picid = getPara("picid");
        //读取本地图片输入流
        InputStream fis = null;
        OutputStream out = null;

        try {

            CMNSrv.MongoFileVO mvo = CMNSrv.loadFile(picid);


            fis = mvo.getInputStream();
            getResponse().setContentType("image/jpeg");
            out = getResponse().getOutputStream();
            IoUtil.copy(fis, out);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            renderNull();
        }


    }
    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void act05(){
        String merNo = getPara("merNo");
        String path =  CacheKit.get(Consts.CACHE_NAMES.paramCache.name(),"siteDomain")+"/mer01/cust?merNo="+merNo;
//        QrCodeRender qrCodeRender  = new  QrCodeRender(path,300,300);
//        qrCodeRender.setContext(getRequest(),getResponse());
//        qrCodeRender.render();
        renderQrCode(path,300,300);
    }



    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void act06(){
        String path = PathKit.getWebRootPath()+ CacheKit.get(Consts.CACHE_NAMES.paramCache.name(),"ePath");
        String docName ="auth_"+DateKit.getNowUTC()+".doc";
        File docFile = new File(path+docName);
        merchantInfoSrv.createAgreeDoc(null,docFile);
        //读取本地图片输入流
        InputStream fis = null;
        OutputStream out = null;
        try {
            fis = IoUtil.toStream(docFile);
            HttpServletResponse response = getResponse();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename="+ new String("委托代扣授权表.doc".getBytes("UTF-8"),"ISO8859-1"));
            response.setHeader("Content-Length",  String.valueOf(docFile.length()));//设置内容长度
            out = response.getOutputStream();
            IoUtil.copy(fis, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            renderNull();
        }

    }



}

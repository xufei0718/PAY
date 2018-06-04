package com.mybank.pc.admin.art;


import cn.hutool.core.util.StrUtil;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.Content;
import com.mybank.pc.admin.model.Mapping;
import com.mybank.pc.admin.model.Taxonomy;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.interceptors.AdminAAuthInterceptor;
import com.mybank.pc.kits.DateKit;
import com.mybank.pc.kits.QiNiuKit;
import com.mybank.pc.kits._StrKit;

import java.io.IOException;
import java.util.*;

/**
 * 简介
 * <p>
 * 项目名称:   [cms]
 * 包:        [com.dbd.cms.controller.admin.content]
 * 类名称:    [ArtCtr]
 * 类描述:    [内容管理]
 * 创建人:    [于海慧]
 * 创建时间:  [2017/1/3]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class ArtCtr extends CoreController {

    public void list() {
        Page<Content> page;
        String serach = getPara("search");
        StringBuffer where = new StringBuffer("from s_content c left join s_user u on c.userId=u.id where 1=1 and c.dAt is null ");
        if (!isParaBlank("search")) {
            where.append(" and (instr(c.title,?)>0 or instr(c.text,?)>0 or instr(c.summary,?)>0 or instr(c.metaKeywords,?)>0 or instr(u.nickname,?)>0)");
            where.append(" order by c.cAt desc");
            page = Content.dao.paginate(getPN(), getPS(), "select c.* ", where.toString(), serach, serach, serach, serach, serach);
        } else {
            where.append(" order by c.cAt desc");
            page = Content.dao.paginate(getPN(), getPS(), "select c.* ", where.toString());
        }
        renderJson(page);
    }

    @Before({Tx.class})
    public void save() {
        Content content=getModel(Content.class,"",true);
        String base64Str=getPara("thumbnailBase64");
        String tax=getPara("tax");


        if(StrUtil.isNotBlank(base64Str)){
            String imgKey= "/art/thumbnail/"+ DateKit.dateToStr(new Date(),DateKit.yyyyMMdd)+"/"+ _StrKit.getUUID()+".jpg";
            String qnRs=null;
            try {
                qnRs=QiNiuKit.put64image(base64Str,imgKey);
            } catch (IOException e) {
                LogKit.error("七牛上传base64图片失败:"+e.getMessage());
                renderFailJSON("缩略图上传失败", "");
                return ;
            }
            if(qnRs==null) {
                renderFailJSON("图片上传失败", "");
                return ;
            }else{
                if(qnRs.equals(Consts.YORN_STR.yes.name())){
                    content.setThumbnail(imgKey);
                }else{
                    LogKit.error("七牛base64上传失败:"+qnRs);
                    renderFailJSON("缩略图上传失败", "");
                    return ;
                }
            }
        }
        content.setUserid(currUser()!=null?currUser().getId():null);
        content.setCAt(new Date());
        content.setStatus(Consts.CHECK_STATUS.normal.getVal());
        if(content.getId()==null)
            content.saveWithoutClean();
        else {
            content.setMAt(new Date());
            content.setFlag("01");
            Mapping.dao.delByCId(content.getId());
            content.updateWithoutClean();
        }
        if(StrUtil.isNotBlank(tax)){
            String[] tax_array=tax.split(",");
            Mapping mapping=null;
            for(String s:tax_array){
                mapping=new Mapping();
                mapping.setCid(content.getId());
                mapping.setTid(new Long(s));
                mapping.save();
            }
        }

        renderSuccessJSON("保存成功","");
    }

    @Before({Tx.class})
    public void update() {

        renderSuccessJSON("保存成功","");

    }

    @Before({Tx.class})
    public void del(){

        String ids=getPara("ids");
        List<Integer> idsList=new ArrayList<Integer>();
        if(ids==null){
            renderFailJSON("没有找到要删除的文章","");
            return ;
        }else{
            String[] array=ids.split(",");
            for(String s:array){
                idsList.add(Integer.parseInt(s));
            }
        }


        for(Integer i:idsList){
            Content c=Content.dao.findById(i);
            c.setDAt(new Date());
            c.updateWithoutClean();
        }
        renderSuccessJSON("删除成功","");

    }
    @Clear(AdminAAuthInterceptor.class)
    public void get(){
        Long id=getParaToLong("id");
        List<Taxonomy> all=Taxonomy.dao.findAllListByModule("art");
        List<Taxonomy> own=Taxonomy.dao.findTaxsByCId(id);
        if(!all.isEmpty()) {
            all.get(0).getChildren().removeAll(own);
            for(Taxonomy taxonomy:own){
                taxonomy.setChecked(true);
            }
            all.get(0).getChildren().addAll(own);
        }
        Map<String,Object> map=new HashMap<>();
        map.put("art", Content.dao.findById(id));
        map.put("artTaxList",all);
        renderJson(map);
    }

    @Clear(AdminAAuthInterceptor.class)
    public void getTax(){
        Long cId=getParaToLong("cId");
        renderJson(Taxonomy.dao.findTaxsByCId(cId));
    }



    public void publish(){
        Long id=getParaToLong("id");
        Content content=Content.dao.findById(id);
        content.setFlag("00");
        content.setMAt(new Date());
        content.setPAt(new Date());
        content.updateWithoutClean();
        renderSuccessJSON("文章发布成功");
    }


}

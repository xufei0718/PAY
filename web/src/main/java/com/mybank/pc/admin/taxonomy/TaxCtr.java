package com.mybank.pc.admin.taxonomy;

import cn.hutool.core.util.StrUtil;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.Taxonomy;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.core.CoreData;

import java.util.Date;
import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/12/6.
 */
public class TaxCtr extends CoreController {

    public void treeJsonArray(){
        List list=null;
        if(isParaExists("module")){
            String  cacheData=CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),getPara("module"));
            if(StrUtil.isBlank(cacheData))
                list=Taxonomy.dao.findAllListByModule(getPara("module"));
            else{
                renderJson(cacheData);
                return ;
            }
        }else {
            list = Taxonomy.dao.findAllList();
        }
        renderJson(list);
    }


    @Before({TaxValidator.class,Tx.class})
    public void save(){
        Taxonomy taxonomy=getModel(Taxonomy.class,"",true);
        if(taxonomy.getParentId()==null){
            taxonomy.setParentId(new Long("0"));
        }
        taxonomy.save();
        CoreData.loadTax();
        renderSuccessJSON("新增分类成功");
    }
    @Before({TaxValidator.class,Tx.class})
    public void update(){
        Taxonomy taxonomy=getModel(Taxonomy.class,"",true);
        taxonomy.update();
        Taxonomy.dao.updateChildrenModule(taxonomy.getId(),taxonomy.getModule());
        CoreData.loadTax();
        renderSuccessJSON("更新分类成功");
    }

    public void del(){
        Long id=getParaToLong("id");
        Taxonomy taxonomy=Taxonomy.dao.findById(id);
        taxonomy.setDAt(new Date());
        taxonomy.update();
        CoreData.loadTax();
        renderSuccessJSON("删除分类成功");
    }

}

package com.mybank.pc.admin.res;


import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.Res;
import com.mybank.pc.admin.model.RoleRes;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.interceptors.AdminAAuthInterceptor;
import com.mybank.pc.interceptors.AdminIAuthInterceptor;

import java.util.List;

/**
 * 简介
 * <p>
 * 项目名称:  [cms]
 * 包:       [com.dbd.cms.controller.admin]
 * 类名称:    [ResCtr]
 * 类描述:    [菜单管理]
 * 创建人:    [于海慧]
 * 创建时间:  [2016/12/2]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
@Before(AdminIAuthInterceptor.class)
public class ResCtr extends CoreController {



    /**
     * @param
     * @return void
     * @throws
     * @author: 于海慧  2016/12/5
     * @Description: 树形json数据
     **/
    @Clear(AdminAAuthInterceptor.class)
    public void listTree() {
        int id = getParaToInt("id",0);
        renderJson(Res.dao.listTree(null));
    }

    @Before({ResValidator.class,Tx.class})
    public void move(){
        int id=getParaToInt("id");
        int pId=getParaToInt("pId");
        Res res=Res.dao.findById(id);
        res.setPid(pId);
        res.update();
        renderSuccessJSON("操作成功","");
    }
    @Before({ResValidator.class,Tx.class})
    public void save(){
        Res res=getModel(Res.class);
        res.save();
        CacheKit.removeAll(Consts.CACHE_NAMES.userReses.name());
        renderSuccessJSON("新增成功","");
    }
    @Before({ResValidator.class,Tx.class})
    public void update(){
        Res res=getModel(Res.class);
        res.update();
        CacheKit.removeAll(Consts.CACHE_NAMES.userReses.name());
        renderSuccessJSON("更新成功","");
    }
    @Before(Tx.class)
    public void del(){
        int id=getParaToInt("id");
        Res.dao.deleteById(id);
        List<RoleRes> list=RoleRes.dao.find("select * from s_role_res where resId=?",id);
        for(RoleRes rr:list){
            RoleRes.dao.deleteById(rr.getId().longValue());
            CacheKit.removeAll(Consts.CACHE_NAMES.userReses.name());
            CacheKit.removeAll(Consts.CACHE_NAMES.userRoles.name());
        }
        renderSuccessJSON("删除成功","");
    }

}

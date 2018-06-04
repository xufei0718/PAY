package com.mybank.pc.merchant.user;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.Role;
import com.mybank.pc.admin.model.User;
import com.mybank.pc.admin.model.UserRole;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.kits.ext.BCrypt;
import com.mybank.pc.merchant.model.MerchantInfo;
import com.mybank.pc.merchant.model.MerchantUser;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by 于海慧（125227112@qq.com） on 2016/12/2.
 */

public class MerchantUserCtr extends CoreController {
    public void list() {
        MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);

        Page<User> page;
        String serach = getPara("search");
        StringBuffer where = new StringBuffer("from merchant_user mu left JOIN s_user u on mu.userID = u.id where mu.merchantID ="+merInfo.getId()+" and u.loginname <> 'oper@"+merInfo.getMerchantNo()+"' and u.d_at is null");
        if (!isParaBlank("search")) {
            where.append(" and (instr(u.email,?)>0 or instr(u.phone,?)>0 or instr(u.nickname,?)>0 or instr(u.loginname,?)>0)");
            where.append(" ORDER BY u.c_at");
            page = User.dao.paginate(getPN(), getPS(), "select u.* ", where.toString(), serach, serach, serach, serach);
        } else {
            where.append(" ORDER BY u.c_at");
            page = User.dao.paginate(getPN(), getPS(), "select u.* ", where.toString());
        }
        Map<Object,Object> map = new HashMap<>();
        map.put("page",page);
        map.put("merInfo",merInfo);
        renderJson(map);
    }

    @Before({MerchantUserValidator.class, Tx.class})
    public void save() {

        User user = getModel(User.class,"",true);
        MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
        Integer[] roledIds = null;
        if (isParaExists("roleIds")) {
            String roleIds_str=getPara("roleIds");
            String[] roleIds_str_array=roleIds_str.split(",");
            roledIds=new Integer[roleIds_str_array.length];
            for (int i = 0; i < roleIds_str_array.length; i++) {
                roledIds[i]=Integer.parseInt(roleIds_str_array[i]);
            }
        }
        //建立用户信息
        user.setLoginname(user.getLoginname()+"@"+merInfo.getMerchantNo());
        user.setCAt(new Date());
        user.setEmailStatus(Consts.YORN.no.isVal());
        user.setPhoneStatus(Consts.YORN.no.isVal());
        user.setMAt(new Date());
        user.setStatus(Consts.STATUS.enable.getVal());
        user.setSignature("商户自建用户");
        user.setSalt(BCrypt.gensalt());
        user.setPassword(BCrypt.hashpw(user.getPhone(), user.getSalt()));
        String default_avart = CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "qn_url") + "image/avatar/dft-avatar.jpg";
        user.setAvatar(default_avart);
        user.save();

        //建立商户和操作员关系
        MerchantUser merchantUser = new MerchantUser();
        merchantUser.setMerchantID(merInfo.getId());
        merchantUser.setUserID(user.getId().intValue());
        merchantUser.save();

        if (roledIds != null) {
            UserRole ur = null;
            for (Integer i : roledIds) {
                ur = new UserRole();
                ur.setRId(i);
                ur.setUId(user.getLong("id"));
                ur.save();
            }
        }
        renderSuccessJSON("新增用户信息成功。", "");
    }

    @Before({MerchantUserValidator.class, Tx.class})
    public void update() {
        User user = getModel(User.class,"",true);
        user.setMAt(new Date());
        Integer[] roledIds = null;
        if (isParaExists("roleIds")) {
            String roleIds_str=getPara("roleIds");
            String[] roleIds_str_array=roleIds_str.split(",");
            roledIds=new Integer[roleIds_str_array.length];
            for (int i = 0; i < roleIds_str_array.length; i++) {
                roledIds[i]=Integer.parseInt(roleIds_str_array[i]);
            }
        }


        if (roledIds != null) {
            UserRole.dao.delByUserId(user.getId().intValue());
            UserRole ur = null;
            for (Integer i : roledIds) {
                ur = new UserRole();
                ur.setRId(i);
                ur.setUId(user.getId().longValue());
                ur.save();
            }
        }
        CacheKit.remove(Consts.CACHE_NAMES.user.name(),user.getId());
        CacheKit.remove(Consts.CACHE_NAMES.user.name(),"id_"+user.getId());
        CacheKit.remove(Consts.CACHE_NAMES.userRoles.name(),user.getId());
        CacheKit.remove(Consts.CACHE_NAMES.userRoles.name(),"findUserOwnRoles_"+user.getId());
        CacheKit.remove(Consts.CACHE_NAMES.userReses.name(),user.getId());
        CacheKit.remove(Consts.CACHE_NAMES.userReses.name(),"findResesByUserId_"+user.getId());
        user.update();
        renderSuccessJSON("更新用户信息成功。", "");
    }
    @Before({Tx.class})
    public void del(){
        int id=getParaToInt("id");
        User user=User.dao.findById(BigInteger.valueOf(id));
        user.setDAt(new Date());
        user.update();
        CacheKit.remove(Consts.CACHE_NAMES.user.name(),user.getId());
        CacheKit.remove(Consts.CACHE_NAMES.user.name(),"id_"+user.getId());
        CacheKit.remove(Consts.CACHE_NAMES.userRoles.name(),user.getId());
        CacheKit.remove(Consts.CACHE_NAMES.userRoles.name(),"findUserOwnRoles_"+user.getId());
        CacheKit.remove(Consts.CACHE_NAMES.userReses.name(),user.getId());
        CacheKit.remove(Consts.CACHE_NAMES.userReses.name(),"findResesByUserId_"+user.getId());
        renderSuccessJSON("删除用户信息成功。");
    }

    /**
     * @param
     * @return void
     * @throws
     * @author: 于海慧  2016/12/10
     * @Description:用户禁用操作处理
     **/
    @Before(Tx.class)
    public void forbidden() {
        String userIds = getPara("ids");
        String[] ids = StrUtil.split(userIds,",");
        int id;
        User user = null;
        for (String s:ids){
            id = Integer.parseInt(s);
            user = new User();
            user.setId(BigInteger.valueOf(id));
            user.setStatus(Consts.STATUS.forbidden.getVal());
            user.update();
            CacheKit.remove(Consts.CACHE_NAMES.user.name(),user.getId());
            CacheKit.remove(Consts.CACHE_NAMES.user.name(),"id_"+user.getId());
        }

        renderSuccessJSON("禁用操作执行成功。", "");
    }

    /**
     * @param
     * @return void
     * @throws
     * @author: 于海慧  2016/12/10
     * @Description:恢复操作处理
     **/
    @Before(Tx.class)
    public void resumed() {
        String userIds = getPara("ids");
        String[] ids= StrUtil.split(userIds,",");
        int id;
        User user = null;
        for(String s:ids) {
            id = Integer.parseInt(s);
            user = new User();
            user.setId(BigInteger.valueOf(id));
            user.setStatus(Consts.STATUS.enable.getVal());
            user.update();
            CacheKit.remove(Consts.CACHE_NAMES.user.name(),user.getId());
            CacheKit.remove(Consts.CACHE_NAMES.user.name(),"id_"+user.getId());
        }

        renderSuccessJSON("恢复操作执行成功。", "");
    }


    public void resetPwd(){
        /*if(currUser()!=null&&!currUser().getIsAdmin().equals(Consts.YORN_STR.yes.getVal())){
            renderFailJSON("重置密码需要超级管理员权限");
            return;
        }*/
        Integer id=getParaToInt("id");
        User user=User.dao.findById(id);
        String newPwd= RandomUtil.randomString(6);
        user.setSalt(BCrypt.gensalt());
        user.setPassword(BCrypt.hashpw(newPwd,user.getSalt()));
        user.update();
        renderSuccessJSON("新密码为 "+newPwd+" 请尽快登录进行密码修改!");
    }
    public void rolelist() {
        List<Role> roleList =Role.dao.find("select * from s_role r where r.name like 'mer-%'");

        renderJson(roleList);
    }



}

package com.mybank.pc.interceptors;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.Res;
import com.mybank.pc.admin.model.User;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.core.CoreException;
import com.mybank.pc.kits.ReqKit;
import com.mybank.pc.kits.ResKit;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yuhaihui8913 on 2017/11/16.
 * 请求访问控制前置处理
 *
 */
public class AdminAAuthInterceptor implements Interceptor{


    @Override
    public void intercept(Invocation invocation) {
        CoreController controller=(CoreController) invocation.getController();
        String ak=invocation.getActionKey();
        User user=controller.getAttr(Consts.CURR_USER);
        HttpServletRequest request=controller.getRequest();
        boolean flag=false;
        Set<String> resStrs=controller.getAttr(Consts.CURR_USER_RESES);

        if(resStrs.contains(ak)){
            flag=true;
        }

        //是否需要用户身份认证,方便测试
        if(!ResKit.getConfigBoolean("userAuth"))
            flag=true;

        if(flag) {
            invocation.invoke();
        } else {
            if(ReqKit.isAjaxRequest(controller.getRequest())){
                controller.renderUnauthorizationJSON("admin");
            }else {
                throw new CoreException("访问权限认证失败！");
            }
        }


    }

}

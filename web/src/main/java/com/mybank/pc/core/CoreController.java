package com.mybank.pc.core;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.jfinal.core.Controller;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.Role;
import com.mybank.pc.admin.model.User;

import java.util.List;

/**
 * Created by 于海慧（125227112@qq.com） on 2016/12/1.
 */
public class CoreController extends Controller {
    public static final String PAGENUMBER = "pn";
    public static final String PAGESIZE = "ps";
    public static final String ORDER="order";
    public static final String ERROR_MSG="_err_msg";
    public static final String SUCCESS_MSG="_suc_msg";


    /**
     * @param @return 参数说明
     * @return int    返回类型
     * @throws
     * @Title: getPN
     * @Description: 获取当前第几页
     */
    public int getPN() {
        int pagenumber = getParaToInt(PAGENUMBER, 0);

        pagenumber = (pagenumber == 0) ? 1 : pagenumber;
        return pagenumber;
    }

    /**
     * @param @return 参数说明
     * @return int    返回类型
     * @throws
     * @Title: getPS
     * @Description: 每页显示多少条，如果页面上没有设置默认显示15条。
     */
    public int getPS() {
        return getParaToInt(PAGESIZE, 15);
    }

    /**
     * @param
     * @return void
     * @throws
     * @author: 于海慧  2016/12/4
     * @Description:获取表单的排序
     **/
    public String getOrder(){
        String order=getPara("order");
        return (StringUtils.isEmpty(order))?"asc":"desc";
    }

    /**
     *
     * @param data 数据 参数数组 可变参数，第一位 返回消息，第二位返回的数据
     * @return void
     * @throws
     * @author: 于海慧  2016/12/4
     * @Description: 请求成功的JSON返回结果
     **/
    public void renderSuccessJSON(String ...data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resCode", Consts.REQ_JSON_CODE.success.name());
        jsonObject.put("resMsg", data.length==0  ? "操作成功" : data[0]);
        jsonObject.put("resData", data.length>=2?data[1]:"");
        renderJson(jsonObject.toJSONString());
    }

    /**
     * @param data 数据   可变参数，第一位 返回消息，第二位返回的数据
     * @return void
     * @throws
     * @author: 于海慧  2016/12/4
     * @Description: 请求失败的JSON返回结果
     **/
    public void renderFailJSON(String ...data ) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resCode", Consts.REQ_JSON_CODE.fail.name());
        jsonObject.put("resMsg", data.length==0 ? "操作失败" : data[0]);
        jsonObject.put("resData", data.length>=2?data[1]:"");
        renderJson(jsonObject.toJSONString());
    }

    /**
     * @param
     * @return void
     * @throws
     * @author: 于海慧  2017/1/16
     * @Description:用户未通过认证返回结果
     **/
    public void renderUnauthenticationJSON(String str) {
        getResponse().setStatus(401);
        getResponse().setHeader("customData",str);
        renderFailJSON("用户身份无效或被禁用，再进行操作。");
        return;
    }



    public void renderUnauthorizationJSON(String str){
        getResponse().setStatus(403);
        getResponse().setHeader("customData",str);
        renderFailJSON("您没有权限访问该资源。");
        return;
    }


    private static  ValueFilter filter = new ValueFilter() {
        public Object process(Object obj, String s, Object v) {
            if(v==null)
                return "";
            return v;
        }
    };

    protected User currUser(){
        return (User)getAttr(Consts.CURR_USER);
    }

    protected List<Role> currUserRoles(){return getAttr(Consts.CURR_USER_ROLES);}



}

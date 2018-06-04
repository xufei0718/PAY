package com.mybank.pc.admin.taxonomy;

import com.jfinal.core.Controller;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.Taxonomy;
import com.mybank.pc.core.CoreValidator;

import java.util.List;

/**
 * 简介
 * <p>
 * 项目名称:   [whbx]
 * 包:        [com.mb.pc.admin.taxonomy]
 * 类名称:    [TaxValidator]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2017/12/6]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class TaxValidator extends CoreValidator {

    public static final String TAX_TITLE_EXIST="分类名称已经存在";
    @Override
    protected void validate(Controller controller) {
        String ak=getActionKey();
        Taxonomy tax=controller.getModel(Taxonomy.class,"",true);
        if(tax.getParentId()==null)tax.setParentId(new Long("0"));
        List<Taxonomy> taxList;
        if(ak.equals("/ad05/save")){
            taxList=Taxonomy.dao.findByTitleAndPId(tax.getTitle(),tax.getParentId());
            if(!taxList.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),TAX_TITLE_EXIST);
            }

        }else if(ak.equals("/ad05/update")){
            taxList=Taxonomy.dao.findByTitleAndPIdAndNEId(tax.getTitle(),tax.getParentId(),tax.getId());
            if(!taxList.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),TAX_TITLE_EXIST);
            }
        }
    }

    @Override
    protected void handleError(Controller controller) {
        controller.renderJson(getErrorJSON());
    }
}

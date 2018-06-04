package com.mybank.pc.merchant.cust;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.mybank.pc.Consts;
import com.mybank.pc.core.CoreValidator;
import com.mybank.pc.merchant.model.MerchantCust;
import com.mybank.pc.merchant.model.MerchantInfo;

import java.util.List;

/**
 * 表单字段验证
 */
public class MerchantCustValidator extends CoreValidator {
	public static final String MERCHANTCUST_EXIST = "该客户银行卡已经绑定";



	@Override
	protected void validate(Controller controller) {
		MerchantCust merCust = controller.getModel(MerchantCust.class, "", true);

		String ak = getActionKey();
		LogKit.debug("ActionKey =" + ak);
		if (ak.equals("/mer01/save")) {
			//判断是否是操作员操作
			if (StrUtil.isBlankIfStr(merCust.getMerID())) {
				MerchantInfo merInfo = controller.getAttr(Consts.CURR_USER_MER);
				merCust.setMerID(merInfo.getId());
				merCust.setMerNo(merInfo.getMerchantNo());
			}
			List<MerchantCust> mcList = MerchantCust.dao.find("select * from merchant_cust mc where mc.dat is null and  mc.merID=?  and mc.cardID=? and mc.mobileBank=? and mc.bankcardNo=?", merCust.getMerID(), merCust.getCardID(), merCust.getMobileBank(), merCust.getBankcardNo());
			if (!CollectionUtil.isEmpty(mcList)) {
				//同一商户下已经绑定过银行卡
				addError(Consts.REQ_JSON_CODE.fail.name(), MERCHANTCUST_EXIST);
				return;
			}

		} else if (ak.equals("/mer01/update")) {
			List<MerchantCust> mcList = MerchantCust.dao.find("select * from merchant_cust mc where mc.dat is null and  mc.merID=?  and mc.cardID=? and mc.mobileBank=? and mc.bankcardNo=? and mc.id <>?", merCust.getMerID(), merCust.getCardID(), merCust.getMobileBank(), merCust.getBankcardNo() ,merCust.getId());
			if (!CollectionUtil.isEmpty(mcList)) {
				//同一商户下已经绑定过银行卡
				addError(Consts.REQ_JSON_CODE.fail.name(), MERCHANTCUST_EXIST);
				return;
			}
		}
	}

	@Override
	protected void handleError(Controller controller) {
		controller.renderJson(getErrorJSON());
	}
}

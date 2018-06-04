package com.mybank.pc.core;

import com.jfinal.plugin.activerecord.Model;
import com.mybank.pc.Consts;
import org.jsoup.Jsoup;

import java.util.List;
import java.util.Map;

/**
 * Created by 于海慧（125227112@qq.com） on 2016/11/30.
 */
public abstract class CoreModel<M extends CoreModel<M>> extends Model<M> {

	/**
	 * 防止xss攻击处理
	 * 
	 * @return
	 */
	@Override
	public boolean save() {
		for (Map.Entry me : _getAttrs().entrySet()) {
			if (me.getValue() instanceof String) {
				set((String) me.getKey(), Jsoup.clean((String) me.getValue(), Consts.basicWithImages));
			}
		}
		return super.save();
	}

	public boolean saveWithoutClean() {
		return super.save();
	}

	/**
	 * 防止xss攻击处理
	 * 
	 * @return
	 */
	@Override
	public boolean update() {
		for (Map.Entry me : _getAttrs().entrySet()) {
			if (me.getValue() instanceof String)
				set((String) me.getKey(), Jsoup.clean((String) me.getValue(), Consts.basicWithImages));
		}
		return super.update();
	}

	public boolean updateWithoutClean() {
		return super.update();
	}

	public String getYOrNTxt(boolean val) {
		return (val) ? Consts.YORN.yes.getLabel() : Consts.YORN.no.getLabel();
	}

	public String getStatusTxt(String val) {
		if (val == null)
			return "";
		return (val.equals(Consts.STATUS.enable.getVal()) ? Consts.STATUS.enable.getValTxt()
				: Consts.STATUS.forbidden.getValTxt());
	}

	public List<M> findByPropEQ(String name, Object val) {
		return super.find("select * from " + getTableName() + " where " + name + "=?", val);
	}

	public M findFristByPropEQ(String name, Object val) {
		return super.findFirst("select * from " + getTableName() + " where " + name + "=?", val);
	}

	public List<M> findByPropLIKE(String name, String val) {
		return super.find("select * from " + getTableName() + " where " + name + " like ?", "%" + val + "%");
	}

	public List<M> findAll() {
		return super.find("select * from " + getTableName());
	}

	public abstract String getTableName();

}

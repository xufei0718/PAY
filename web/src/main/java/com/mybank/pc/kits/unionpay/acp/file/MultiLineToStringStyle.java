package com.mybank.pc.kits.unionpay.acp.file;

import java.util.Collection;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class MultiLineToStringStyle extends ToStringStyle {

	private static final long serialVersionUID = 1L;

	private String parentPrefix = "";
	private String prefix = "    ";

	public MultiLineToStringStyle() {
		this.setContentStart("[");
		this.setContentEnd(System.lineSeparator() + "]");
		this.setFieldSeparatorAtStart(true);
		this.setFieldSeparator(System.lineSeparator() + prefix);
		this.setArraySeparator(System.lineSeparator());
	}

	private MultiLineToStringStyle(String parentPrefix) {
		this.parentPrefix = parentPrefix;
		this.prefix = this.parentPrefix + "    ";
		this.setContentStart("[");
		this.setContentEnd(System.lineSeparator() + parentPrefix + "]");
		this.setFieldSeparatorAtStart(true);
		this.setFieldSeparator(System.lineSeparator() + prefix);
		this.setArraySeparator(System.lineSeparator());
	}

	@Override
	protected void appendDetail(StringBuffer buffer, String fieldName, @SuppressWarnings("rawtypes") Collection coll) {
		Object[] objs = coll.toArray();
		buffer.append(super.getContentStart());
		if (objs.length > 0) {
			buffer.append(System.lineSeparator());
		}
		for (int i = 0; i < objs.length; i++) {
			if (i > 0) {
				buffer.append(getArraySeparator());
			}
			buffer.append(this.prefix + "    ");
			appendDetail(buffer, fieldName, objs[i], this.prefix + "    ");
		}
		buffer.append((objs.length > 0 ? (System.lineSeparator() + this.prefix) : "") + "]");
	}

	@Override
	protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
		appendDetail(buffer, fieldName, value, this.prefix);
	}

	private void appendDetail(StringBuffer buffer, String fieldName, Object value, String prefix) {
		Class<?> valueClass = value == null ? null : value.getClass();
		if (valueClass != null) {
			if (valueClass.getName().startsWith("com.mybank.pc.kits.unionpay.acp.file.collection.model")) {
				buffer.append(ToStringBuilder.reflectionToString(value, new MultiLineToStringStyle(prefix)));
				return;
			}
		}
		buffer.append(value);
	}

}
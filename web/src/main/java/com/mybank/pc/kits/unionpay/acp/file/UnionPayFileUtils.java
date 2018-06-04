package com.mybank.pc.kits.unionpay.acp.file;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class UnionPayFileUtils {

	public static String parseTxtTitlesLine(List<String> titles) {
		return StringUtils.join(titles, "|");
	}

	public static <T> T parseTxtLine(String line, List<String> titles, Class<T> clazz) {
		T obj = null;
		if (StringUtils.isNotBlank(line)) {
			String[] cvs = line.split("\\|", titles.size());
			if (cvs.length == titles.size()) {

				try {
					obj = clazz.newInstance();
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}

				Field field = null;
				for (int i = 0, size = titles.size(); i < size; ++i) {
					try {
						field = clazz.getDeclaredField(titles.get(i));
						field.setAccessible(true);
						field.set(obj, cvs[i]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}
		return obj;
	}

	public static String toTxtLine(Object obj, List<String> titles) {
		StringBuffer lineBuffer = new StringBuffer(150);
		if (obj != null) {
			Class<?> clazz = obj.getClass();
			Field field = null;
			Object tmp = null;
			String tmpValue = null;

			for (int i = 0, size = titles.size(); i < size; ++i) {
				try {
					field = clazz.getDeclaredField(titles.get(i));
					field.setAccessible(true);
					tmp = field.get(obj);
					if (tmp == null || StringUtils.isBlank(tmpValue = String.valueOf(tmp))) {
						lineBuffer.append("");
					} else {
						lineBuffer.append(tmpValue);
					}
				} catch (Exception e) {
					e.printStackTrace();
					lineBuffer.append("");
				}
				if (i < size - 1) {
					lineBuffer.append("|");
				}
			}

		}

		return lineBuffer.toString();
	}

}

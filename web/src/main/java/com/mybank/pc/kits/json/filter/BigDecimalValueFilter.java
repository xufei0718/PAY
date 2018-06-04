package com.mybank.pc.kits.json.filter;

import com.alibaba.fastjson.serializer.ValueFilter;

import java.math.BigDecimal;

/**
 *
 * fastJson obj 转 json 格式的时候会出现bigDecmail 小数点丢失,此类可以处理这种问题。
 *
 *
 * 需要使用fastJson的 JSON.toJsonString()
 *
 */

public class BigDecimalValueFilter implements ValueFilter {


    @Override
    public Object process(Object o, String s, Object o1) {

        if (null != o1 && o1 instanceof BigDecimal) {
            return ((BigDecimal) o1).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return o1;
    }
}

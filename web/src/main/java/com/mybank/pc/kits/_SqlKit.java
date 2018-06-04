package com.mybank.pc.kits;

import java.util.List;

/**
 * 简介
 * <p>
 * 项目名称:   [csap]
 * 包:        [com.yhh.csap.kits]
 * 类名称:    [_SqlKit]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2018/5/5]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class _SqlKit {
    public static void joinIds(List<Integer> idList, StringBuilder ret) {
        ret.append("(");
        boolean isFirst = true;
        for (Integer id : idList) {
            if (isFirst) {
                isFirst = false;
            } else {
                ret.append(", ");
            }
            ret.append(id.toString());
        }
        ret.append(")");
    }

}

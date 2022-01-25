package net.hlinfo.example.utils;

import java.util.Collection;
import java.util.Map;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.core.misc.PrimitiveArrayUtil;
import org.nutz.lang.Strings;

import net.hlinfo.opt.Func;

public class BeetlBlankFun implements Function {
	public Boolean call(Object[] paras, Context ctx) {
		 System.out.println("paras.length:"+paras.length);
        if (paras.length == 0)
            return true;

        if (paras.length == 1) {
            Object result = paras[0];
            return isEmpty(result);
        }

        for (Object result : paras) {

            boolean isEmpty = isEmpty(result);
            if (!isEmpty) {
                return false;
            }
        }
        return true;

    }

    protected boolean isEmpty(Object result) {
        if (result == null) {
            return true;
        }
        System.out.println(result instanceof String);
        if (result instanceof String) {
        	System.out.println(Func.isBlank(result+""));
            return Func.isBlank(result+"");
        } else if (result instanceof Collection) {
            return ((Collection) result).size() == 0;
        } else if (result instanceof Map) {
            return ((Map) result).size() == 0;
        } else if (result.getClass().isArray()) {
            return result.getClass().getComponentType().isPrimitive()
                    ? PrimitiveArrayUtil.getSize(result) == 0
                    : ((Object[]) result).length == 0;
        } else {
            return false;
        }

    }
}
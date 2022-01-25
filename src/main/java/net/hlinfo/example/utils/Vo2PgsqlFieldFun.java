package net.hlinfo.example.utils;

import org.beetl.core.Context;
import org.beetl.core.Function;

import net.hlinfo.opt.Func;


public class Vo2PgsqlFieldFun  implements Function{

	@Override
	public Object call(Object[] paras, Context ctx) {
		// TODO Auto-generated method stub
		String str = "*";
		try {
			if(paras.length == 3) {
				str = Funs.vo2PgsqlField(Class.forName(paras[0] + "")
						, paras[1] + ""
						, paras[2] + ""
						,true);
			}else if(paras.length == 5) {
				str = Funs.vo2PgsqlField(Class.forName(paras[0] + "")
						, paras[1] + ""
						, paras[2] + ""
						, paras[3] + ""
						, paras[4] + ""
						,true);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return str;
	}

}

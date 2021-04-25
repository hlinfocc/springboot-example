package net.hlinfo.example.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("后端数据返回")
public class Resp<T> {
	@ApiModelProperty("状态码[100, 服务挂掉了，进入融断器，200:成功,500:失败, 401:没有权限访问该接口，402:token过期，重新登陆,403:token无效或为空"
			+ "]")
	private int code;
	
	@ApiModelProperty("是否成功[true:成功，false：失败]")
	private boolean success;
	
	@ApiModelProperty("返回的消息")
	private String msg;
	
	@ApiModelProperty("存储返回的数据")
	private T data;
	
	@ApiModelProperty("数据的总条数，用于layui表格")
	private int count;
	
	public Resp() {}
	
	public Resp(int code) {
		this.code = code;
	}
	public Resp(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	public Resp(int code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
	
	public Resp(int code, boolean success, String msg, T data, int count) {
		super();
		this.code = code;
		this.success = success;
		this.msg = msg;
		this.data = data;
		this.count = count;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Resp ok(int code) {
		this.setCode(code);
		this.setSuccess(true);
		return this;
	}
	public Resp ok(int code, String msg) {
		this.setCode(code);
		this.setMsg(msg);
		this.setSuccess(true);
		return this;
	}
	public Resp ok(int code, String msg, T data) {
		this.setCode(code);
		this.setMsg(msg);
		this.setSuccess(true);
		this.setData(data);
		return this;
	}
	/**
	 * 
	 * @param code
	 * @param count
	 * @param msg
	 * @param data
	 * @return
	 */
	public Resp ok(int code,int count, String msg, T data) {
		this.setCode(code);
		this.setCount(count);
		this.setMsg(msg);
		this.setSuccess(true);
		this.setData(data);
		return this;
	}
	public Resp ok(String msg) {
		this.setCode(200);
		this.setMsg(msg);
		this.setSuccess(true);
		return this;
	}
	
	/**
	 * 进入了融断器
	 * @param msg
	 * @return
	 */
	public Resp fb(String msg) {
		this.setCode(100);
		this.setMsg(msg);
		this.setSuccess(false);
		return this;
	}
	public Resp ok(String msg,boolean success) {
		this.setCode(200);
		this.setMsg(msg);
		this.setSuccess(success);
		return this;
	}
	public Resp ok(String msg, T data) {
		this.setCode(200);
		this.setMsg(msg);
		this.setData(data);
		this.setSuccess(true);
		return this;
	}
	public Resp error(int code) {
		this.setCode(code);
		this.setSuccess(false);
		return this;
	}
	public Resp error(int code, String msg) {
		this.setCode(code);
		this.setMsg(msg);
		this.setSuccess(false);
		return this;
	}
	public Resp error(int code, String msg, T data) {
		this.setCode(code);
		this.setMsg(msg);
		this.setData(data);
		this.setSuccess(false);
		return this;
	}
	public Resp error(String msg) {
		this.setCode(500);
		this.setMsg(msg);
		this.setSuccess(false);
		return this;
	}
	public Resp error(String msg, T data) {
		this.setCode(500);
		this.setMsg(msg);
		this.setData(data);
		this.setSuccess(false);
		return this;
	}
	
	public Resp code(int code) {
		this.setCode(code);
		return this;
	}
	public Resp msg(String msg) {
		this.setMsg(msg);
		return this;
	}
	public Resp count(int count) {
		this.setCount(count);
		return this;
	}
	public Resp success(boolean success) {
		this.setSuccess(success);
		return this;
	}
	public Resp data(T data) {
		this.setData(data);
		return this;
	}
}

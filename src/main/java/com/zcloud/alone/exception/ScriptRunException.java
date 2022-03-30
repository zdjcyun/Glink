package com.zcloud.alone.exception;


/**
 * 捕获脚步运行时出现的异常信息
 */
public class ScriptRunException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String msg;

	public ScriptRunException() {
		super();
	}

	public ScriptRunException(String msg) {
		super(msg);
		this.msg = msg;
	}

	public ScriptRunException(String errorMsg, String msg) {
		super(errorMsg);
		this.msg = msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}

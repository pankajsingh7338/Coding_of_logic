package com.actolap.wse.commons;

import io.swagger.annotations.ApiModelProperty;

import com.actolap.wse.enums.ResponseCode;

public class GenericResponse {

	private boolean s;

	/** The completed in. */
	private long ci;

	/** The error details. */
	private String ed;

	private String msg;

	/** The response code. */

	protected ResponseCode rc;

	private String d;

	
	private String stateName;
	
	@ApiModelProperty(value = "Completed time to respond in milli seconds", required = true)
	public long getCi() {
		return ci;
	}

	public void setCi(long ci) {
		this.ci = ci;
	}

	@ApiModelProperty(value = "Error Message, In case of failed response")
	public String getEd() {
		return ed;
	}

	public void setEd(String ed) {
		this.ed = ed;
	}

	@ApiModelProperty(value = "Message, In case of successful response. This field is optional", required = false)
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@ApiModelProperty(value = "Response code", allowableValues = "SUCCESS(10000), FAILED(99999), UNSECURE(500)", required = true)
	public ResponseCode getRc() {
		return rc;
	}

	public void setRc(ResponseCode rc) {
		this.rc = rc;
	}

	public static GenericResponse buildUnSecure() {
		GenericResponse gn = new GenericResponse();
		gn.setRc(ResponseCode.UNSECURE);
		gn.setEd("You are not authorised to access this feature!!");
		return gn;
	}

	public static GenericResponse loginUnSecure() {
		GenericResponse gn = new GenericResponse();
		gn.setRc(ResponseCode.UNSECURE_LOGGED_IN);
		gn.setEd("User logged in from another browser");
		return gn;
	}

	public static GenericResponse tokenExpired() {
		GenericResponse gn = new GenericResponse();
		gn.setRc(ResponseCode.UNSECURE_LOGGED_IN);
		gn.setEd("Your session has expired. Please log in again");
		return gn;
	}
	public static GenericResponse restrictedIP(String state) {
		GenericResponse gn = new GenericResponse();
		gn.setRc(ResponseCode.RESTRICTED_IP);
		gn.setEd("RESTRICTED_IP");
		gn.setStateName(state);
		return gn;
	}

	@ApiModelProperty(value = "Response failed or passed", required = true)
	public boolean isS() {
		return s;
	}

	public void setS(boolean s) {
		this.s = s;
	}

	@ApiModelProperty(value = "Data, Returns Id in case an object is created", required = true)
	public String getD() {
		return d;
	}

	public void setD(String d) {
		this.d = d;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	
	
	
}


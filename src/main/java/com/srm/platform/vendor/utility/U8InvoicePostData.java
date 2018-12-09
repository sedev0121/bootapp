package com.srm.platform.vendor.utility;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class U8InvoicePostData {

	private String cbustype = "";
	private String cdepcode = "";
	private String cpersoncode = "";
	private String cpbvmemo = "";

	private String cpbvbilltype;
	private String cpbvcode;
	private String dpbvdate = Utils.formatDateTime(new Date());
	private String cvencode;
	private String cunitcode;
	private String cptcode;
	private Float ipbvtaxrate;
	private String cexch_name = "人民币";
	private Float cexchrate = 1.0F;
	private String dvoudate = Utils.formatDateTime(new Date());
	private String cpbvmaker;
	private String idiscountaxtype;
	private String is_blue;

	private List<U8InvoicePostEntry> list;

	public String getIsBlue() {
		return is_blue;
	}
	
	public void setIsBlue(String is_blue) {
		this.is_blue = is_blue;
	}
	
	public String getCpbvbilltype() {
		return cpbvbilltype;
	}

	public void setCpbvbilltype(String cpbvbilltype) {
		this.cpbvbilltype = cpbvbilltype;
	}

	public String getCpbvcode() {
		return cpbvcode;
	}

	public void setCpbvcode(String cpbvcode) {
		this.cpbvcode = cpbvcode;
	}

	public String getDpbvdate() {
		return dpbvdate;
	}

	public void setDpbvdate(String dpbvdate) {
		this.dpbvdate = dpbvdate;
	}

	public String getCvencode() {
		return cvencode;
	}

	public void setCvencode(String cvencode) {
		this.cvencode = cvencode;
	}

	public String getCunitcode() {
		return cunitcode;
	}

	public void setCunitcode(String cunitcode) {
		this.cunitcode = cunitcode;
	}

	public String getCptcode() {
		return cptcode;
	}

	public void setCptcode(String cptcode) {
		this.cptcode = cptcode;
	}

	public Float getIpbvtaxrate() {
		return ipbvtaxrate;
	}

	public void setIpbvtaxrate(Float ipbvtaxrate) {
		this.ipbvtaxrate = ipbvtaxrate;
	}

	public String getCexch_name() {
		return cexch_name;
	}

	public void setCexch_name(String cexch_name) {
		this.cexch_name = cexch_name;
	}

	public Float getCexchrate() {
		return cexchrate;
	}

	public void setCexchrate(Float cexchrate) {
		this.cexchrate = cexchrate;
	}

	public String getDvoudate() {
		return dvoudate;
	}

	public void setDvoudate(String dvoudate) {
		this.dvoudate = dvoudate;
	}

	public String getCpbvmaker() {
		return cpbvmaker;
	}

	public void setCpbvmaker(String cpbvmaker) {
		this.cpbvmaker = cpbvmaker;
	}

	public String getIdiscountaxtype() {
		return idiscountaxtype;
	}

	public void setIdiscountaxtype(String idiscountaxtype) {
		this.idiscountaxtype = idiscountaxtype;
	}

	public String getCbustype() {
		return cbustype;
	}

	public void setCbustype(String cbustype) {
		this.cbustype = cbustype;
	}

	public String getCdepcode() {
		return cdepcode;
	}

	public void setCdepcode(String cdepcode) {
		this.cdepcode = cdepcode;
	}

	public String getCpersoncode() {
		return cpersoncode;
	}

	public void setCpersoncode(String cpersoncode) {
		this.cpersoncode = cpersoncode;
	}

	public String getCpbvmemo() {
		return cpbvmemo;
	}

	public void setCpbvmemo(String cpbvmemo) {
		this.cpbvmemo = cpbvmemo;
	}

	public List<U8InvoicePostEntry> getList() {
		return list;
	}

	public void setList(List<U8InvoicePostEntry> list) {
		this.list = list;
	}

}

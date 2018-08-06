package com.srm.platform.vendor.utility;

public class Constants {

	public final static int STATE_NEW = 1;
	public final static int STATE_SUBMIT = 2;
	public final static int STATE_CONFIRM = 3;
	public final static int STATE_CANCEL = 4;
	public final static int STATE_PASS = 5;
	public final static int STATE_VERIFY = 6;
	public final static int STATE_PUBLISH = 7;

	public final static int CREATE_TYPE_BUYER = 0;
	public final static int CREATE_TYPE_VENDOR = 1;

	public final static int INQUERY_TYPE_NORMAL = 1;
	public final static int INQUERY_TYPE_RANGE = 2;

	public final static int PURCHASE_ORDER_STATE_START = 0;
	public final static int PURCHASE_ORDER_STATE_DEPLOY = 1;
	public final static int PURCHASE_ORDER_STATE_REVIEW = 2;
	public final static int PURCHASE_ORDER_STATE_CANCEL = 3;

	public final static int PURCHASE_IN_STATE_WAIT = 0;
	public final static int PURCHASE_IN_STATE_START = 1;
	public final static int PURCHASE_IN_STATE_FINISH = 2;

	public final static int STATEMENT_STATE_NEW = 1;
	public final static int STATEMENT_STATE_SUBMIT = 2;
	public final static int STATEMENT_STATE_CONFIRM = 3;
	public final static int STATEMENT_STATE_CANCEL = 4;
	public final static int STATEMENT_STATE_VERIFY = 5;
	public final static int STATEMENT_STATE_INVOICE_NUM = 6;
	public final static int STATEMENT_STATE_INVOICE_PUBLISH = 7;

	public final static int NOTICE_TYPE_USER = 1;
	public final static int NOTICE_TYPE_SYSTEM = 2;
	public final static int NOTICE_TYPE_ALERT = 3;

	public final static int NOTICE_STATE_NEW = 1;
	public final static int NOTICE_STATE_SUBMIT = 2;
	public final static int NOTICE_STATE_PUBLISH = 3;
	public final static int NOTICE_STATE_CANCEL = 4;

	public final static int STATEMENT_DETAIL_TYPE_BASIC = 1;
	public final static int STATEMENT_DETAIL_TYPE_WEIWAI = 2;

	public final static String KEY_DEFAULT_UNIT_LIST = "my_unit_list";

	public final static String PATH_UPLOADS_SHIP = "ship";
	public final static String PATH_UPLOADS_NOTICE = "notice";
	public final static String PATH_UPLOADS_STATEMENT = "statement";

}

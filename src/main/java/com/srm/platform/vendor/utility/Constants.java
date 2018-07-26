package com.srm.platform.vendor.utility;

public class Constants {

	public static int STATE_NEW = 1;
	public static int STATE_SUBMIT = 2;
	public static int STATE_CONFIRM = 3;
	public static int STATE_CANCEL = 4;
	public static int STATE_PASS = 5;
	public static int STATE_VERIFY = 6;
	public static int STATE_PUBLISH = 7;

	public static int CREATE_TYPE_BUYER = 0;
	public static int CREATE_TYPE_VENDOR = 1;

	public static int PURCHASE_ORDER_STATE_START = 0;
	public static int PURCHASE_ORDER_STATE_DEPLOY = 1;
	public static int PURCHASE_ORDER_STATE_REVIEW = 2;
	public static int PURCHASE_ORDER_STATE_CANCEL = 3;

	public static int PURCHASE_IN_FINISH_STATE_NO = 0;
	public static int PURCHASE_IN_FINISH_STATE_YES = 1;

	public static int STATEMENT_STATE_NEW = 1;
	public static int STATEMENT_STATE_SUBMIT = 2;
	public static int STATEMENT_STATE_CONFIRM = 3;
	public static int STATEMENT_STATE_CANCEL = 4;

	public static int NOTICE_TYPE_USER = 1;
	public static int NOTICE_TYPE_SYSTEM = 2;
	public static int NOTICE_TYPE_ALERT = 3;

	public static int NOTICE_STATE_NEW = 1;
	public static int NOTICE_STATE_SUBMIT = 2;
	public static int NOTICE_STATE_PUBLISH = 3;
	public static int NOTICE_STATE_CANCEL = 4;

	public static int STATEMENT_DETAIL_TYPE_BASIC = 1;
	public static int STATEMENT_DETAIL_TYPE_WEIWAI = 2;

	public static String KEY_DEFAULT_UNIT_LIST = "my_unit_list";

	public static String PATH_UPLOADS = "uploads";
	public static String PATH_UPLOADS_SHIP = PATH_UPLOADS + "/ship";
	public static String PATH_UPLOADS_NOTICE = PATH_UPLOADS + "/notice";
	public static String PATH_UPLOADS_STATEMENT = PATH_UPLOADS + "/statement";

}

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
	public final static int PURCHASE_ORDER_STATE_CONFIRM = 2;
	public final static int PURCHASE_ORDER_STATE_CLOSE = 3;
	public final static int PURCHASE_ORDER_STATE_CLOSE_ROW = 10;

	public final static int PURCHASE_ORDER_ROW_CLOSE_STATE_NO = 0;
	public final static int PURCHASE_ORDER_ROW_CLOSE_STATE_YES = 1;
	
	public final static int PURCHASE_IN_STATE_WAIT = 0;
	public final static int PURCHASE_IN_STATE_START = 1;
	public final static int PURCHASE_IN_STATE_FINISH = 2;

	public final static int STATEMENT_STATE_NEW = 1; //采购新建
	public final static int STATEMENT_STATE_SUBMIT = 2; //采购提交
	public final static int STATEMENT_STATE_REVIEW = 3; //采购审核
	public final static int STATEMENT_STATE_DEPLOY = 4; //采购发布
	public final static int STATEMENT_STATE_CANCEL = 5; //采购撤回
	public final static int STATEMENT_STATE_CONFIRM = 6; //供应商确认
	public final static int STATEMENT_STATE_DENY = 7; //供应商退回
	
	public final static int INVOICE_STATE_NONE = 0; //未开发票
	public final static int INVOICE_STATE_DONE = 1; //供应商已开发票
	public final static int INVOICE_STATE_CANCELED = 2; //采购发票已退回
	public final static int INVOICE_STATE_CONFIRMED = 3; //采购发票已审核
	public final static int INVOICE_STATE_UPLOAD_ERP = 4; //采购已传递ERP
	
	public final static int NOTICE_TYPE_USER = 1;
	public final static int NOTICE_TYPE_SYSTEM = 2;
	public final static int NOTICE_TYPE_ALERT = 3;

	public final static int NOTICE_STATE_NEW = 1;
	public final static int NOTICE_STATE_SUBMIT = 2;
	public final static int NOTICE_STATE_PUBLISH = 3;
	public final static int NOTICE_STATE_CANCEL = 4;

	public final static int STATEMENT_TYPE_BASIC = 1;
	public final static int STATEMENT_TYPE_WEIWAI = 2;

	public final static String KEY_DEFAULT_UNIT_LIST = "my_unit_list";


	public final static String PATH_UPLOADS_SHIP = "ship";
	public final static String PATH_UPLOADS_NOTICE = "notice";
	public final static String PATH_UPLOADS_STATEMENT = "statement";
	public final static String PATH_UPLOADS_INQUERY = "inquery";
	
	public final static String KEY_AUTO_TASK_STATEMENT_DATE = "auto_task_statement_date";
	public final static String KEY_AUTO_TASK_START_DATE = "auto_task_start_date";
	public final static String KEY_AUTO_TASK_START_TIME = "auto_task_start_time";
	
	public final static int PERMISSION_DIMENSION_COMPANY = 1;
	public final static int PERMISSION_DIMENSION_ACCOUNT = 2;
	public final static int PERMISSION_DIMENSION_STORE = 3;
	public final static int PERMISSION_DIMENSION_VENDOR = 4;
	public final static int PERMISSION_DIMENSION_INVENTORY = 5;
	
	public final static boolean TEST = false;
	
	public final static int DELIVERY_STATE_NEW = 1;
	public final static int DELIVERY_STATE_SUBMIT = 2;
	public final static int DELIVERY_STATE_OK = 3;
	public final static int DELIVERY_STATE_DELIVERED = 4;
	public final static int DELIVERY_STATE_ARRIVED = 5;	
	public final static int DELIVERY_STATE_CANCEL = 6;
	public final static int DELIVERY_STATE_CONFIRM_CANCEL = 7;
	
	public final static int DELIVERY_CANCEL_CONFIRMED = 1;
	
	public final static String DELIVERY_CANCEL_TYPE_YES = "1";
	public final static String DELIVERY_CANCEL_TYPE_NO = "2";
	
	public final static int DELIVERY_ROW_STATE_NEW = 1;
	public final static int DELIVERY_ROW_STATE_SUBMIT = 2;
	public final static int DELIVERY_ROW_STATE_OK = 3;
	public final static int DELIVERY_ROW_STATE_CANCEL = 4;
	
	public final static int VALID_YES = 1;
	public final static int VALID_NO = 0;
	
	public final static double DEFAULT_TAX_RATE = 13;
	
	public final static int BOX_TYPE_DELIVERY = 1;
	public final static int BOX_TYPE_DIAOBO = 2;
	
	public final static int PASSWORD_TYPE_NORMAL = 1;
	public final static int PASSWORD_TYPE_SECOND = 2;
	
	public final static String DEFAULT_SECOND_PASSWORD = "123";
	
	public final static String VERSION = "20190722";
	
}

package com.srm.platform.vendor.u8api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "u8api")
public class AppProperties {
	private String from_account;

	private String app_key;
	private int error_code_success;

	private System system;
	private Vendor vendor;
	private PurchaseOrder purchaseOrder;
	private PurInvoice purInvoice;
	private PurchaseIn purchaseIn;
	private Inventory inventory;
	private VenPriceAdjust venPriceAdjust;

	public System getSystem() {
		return system;
	}

	public void setSystem(System system) {
		this.system = system;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public PurchaseOrder getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

	public PurInvoice getPurInvoice() {
		return purInvoice;
	}

	public void setPurInvoice(PurInvoice purInvoice) {
		this.purInvoice = purInvoice;
	}

	public PurchaseIn getPurchaseIn() {
		return purchaseIn;
	}

	public void setPurchaseIn(PurchaseIn purchaseIn) {
		this.purchaseIn = purchaseIn;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public VenPriceAdjust getVenPriceAdjust() {
		return venPriceAdjust;
	}

	public void setVenPriceAdjust(VenPriceAdjust venPriceAdjust) {
		this.venPriceAdjust = venPriceAdjust;
	}

	public int getError_code_success() {
		return error_code_success;
	}

	public void setError_code_success(int error_code_success) {
		this.error_code_success = error_code_success;
	}

	public String getApp_key() {
		return app_key;
	}

	public void setApp_key(String app_key) {
		this.app_key = app_key;
	}

	public String getFrom_account() {
		return from_account;
	}

	public void setFrom_account(String from_account) {
		this.from_account = from_account;
	}

	public static class Vendor {
		private String batch_get;
		private String get;

		public String getBatch_get() {
			return batch_get;
		}

		public void setBatch_get(String batch_get) {
			this.batch_get = batch_get;
		}

		public String getGet() {
			return get;
		}

		public void setGet(String get) {
			this.get = get;
		}

	}

	public static class PurchaseOrder {
		private String batch_get;
		private String get;

		public String getBatch_get() {
			return batch_get;
		}

		public void setBatch_get(String batch_get) {
			this.batch_get = batch_get;
		}

		public String getGet() {
			return get;
		}

		public void setGet(String get) {
			this.get = get;
		}

	}

	public static class PurInvoice {
		private String batch_get;

		public String getBatch_get() {
			return batch_get;
		}

		public void setBatch_get(String batch_get) {
			this.batch_get = batch_get;
		}

	}

	public static class System {
		private String token;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

	}

	public static class PurchaseIn {
		private String batch_get;

		public String getBatch_get() {
			return batch_get;
		}

		public void setBatch_get(String batch_get) {
			this.batch_get = batch_get;
		}
	}

	public static class Inventory {
		private String batch_get;

		public String getBatch_get() {
			return batch_get;
		}

		public void setBatch_get(String batch_get) {
			this.batch_get = batch_get;
		}
	}

	public static class VenPriceAdjust {
		private String batch_get;
		private String get;

		public String getBatch_get() {
			return batch_get;
		}

		public void setBatch_get(String batch_get) {
			this.batch_get = batch_get;
		}

		public String getGet() {
			return get;
		}

		public void setGet(String get) {
			this.get = get;
		}

	}

}

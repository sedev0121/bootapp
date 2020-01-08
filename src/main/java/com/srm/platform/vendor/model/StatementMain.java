package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.searchitem.StatementSearchResult;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.Utils;

@Entity

@SqlResultSetMapping(name = "StatementSearchResult", classes = {
		@ConstructorResult(targetClass = StatementSearchResult.class, columns = {
				@ColumnResult(name = "code", type = String.class), 
				@ColumnResult(name = "date", type = String.class),
				@ColumnResult(name = "type", type = String.class),
				@ColumnResult(name = "company_name", type = String.class),
				@ColumnResult(name = "vendor_code", type = String.class),
				@ColumnResult(name = "vendor_name", type = String.class),
				@ColumnResult(name = "vendor_address", type = String.class),
				@ColumnResult(name = "state", type = String.class),
				@ColumnResult(name = "make_date", type = String.class),
				@ColumnResult(name = "review_date", type = String.class),
				@ColumnResult(name = "deploy_date", type = String.class),
				@ColumnResult(name = "confirm_date", type = String.class),
				@ColumnResult(name = "invoice_code", type = String.class),
				@ColumnResult(name = "invoice_state", type = String.class),
				@ColumnResult(name = "invoice_type", type = String.class),
				@ColumnResult(name = "erp_invoice_make_name", type = String.class),
				@ColumnResult(name = "erp_invoice_make_date", type = String.class),
				@ColumnResult(name = "task_code", type = String.class), 
				@ColumnResult(name = "cost_sum", type = String.class),
				@ColumnResult(name = "tax_cost_sum", type = String.class),
				@ColumnResult(name = "adjust_cost_sum", type = String.class),
				@ColumnResult(name = "tax_sum", type = String.class),
		}) 
})

@Table(name = "statement_main")
public class StatementMain {
	@Id
	private String code;

	private Date date;
	private Integer type = Constants.STATEMENT_TYPE_BASIC;	
	private Integer state = Constants.STATEMENT_STATE_NEW;
	
	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "statement_company_id", referencedColumnName = "id")
	private StatementCompany statementCompany;
	
	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "vendor_code", referencedColumnName = "code")
	Vendor vendor;
	
	@Column(name = "tax_rate")
	private Integer taxRate = 13;
	
	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "make_id", referencedColumnName = "id")
	Account maker;

	@JsonProperty("make_date")
	private Date makeDate;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "review_id", referencedColumnName = "id")
	Account reviewer;
	
	@JsonProperty("review_date")
	private Date reviewDate;
	
	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "cancel_id", referencedColumnName = "id")
	Account canceler;
	
	@JsonProperty("cancel_date")
	private Date cancelDate;
	
	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "deploy_id", referencedColumnName = "id")
	Account deployer;
	
	@JsonProperty("deploy_date")
	private Date deployDate;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "confirm_id", referencedColumnName = "id")
	Account confirmer;

	@JsonProperty("confirm_date")
	private Date confirmDate;

	@JsonProperty("invoice_make_date")
	private Date invoiceMakeDate;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "invoice_make_id", referencedColumnName = "id")
	Account invoiceMaker;

	@Column(name = "invoice_code")
	private String invoiceCode;
	
	@Column(name = "invoice_state")
	private Integer invoiceState = Constants.INVOICE_STATE_NONE;
	
	@JsonProperty("invoice_type")
	private Integer invoiceType;
	
	@JsonProperty("erp_invoice_make_name")
	private String erpInvoiceMakeName;
	
	@JsonProperty("erp_invoice_make_date")
	private Date erpInvoiceMakeDate;
	
	@JsonProperty("invoice_confirm_date")
	private Date invoiceConfirmDate;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "invoice_confirm_id", referencedColumnName = "id")
	Account invoiceConfirmer;
	
	@JsonProperty("invoice_cancel_date")
	private Date invoiceCancelDate;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "invoice_cancel_id", referencedColumnName = "id")
	Account invoiceCanceler;
	
	@JsonProperty("cost_sum")
	private Double costSum;
	
	@JsonProperty("tax_cost_sum")
	private Double taxCostSum;
	
	@JsonProperty("adjust_cost_sum")
	private Double adjustCostSum;
	
	@JsonProperty("tax_sum")
	private Double taxSum;
	
	@JsonProperty("task_code")
	private String taskCode;

	public StatementMain() {
		setCode(Utils.generateId());
		setDate(new Date());
		setMakeDate(new Date());
		setState(Constants.STATEMENT_STATE_NEW);
		setInvoiceState(Constants.INVOICE_STATE_NONE);
	}

	public Double getCostSum() {
		return costSum;
	}

	public void setCostSum(Double costSum) {
		this.costSum = costSum;
	}

	public Double getTaxCostSum() {
		return taxCostSum;
	}

	public void setTaxCostSum(Double taxCostSum) {
		this.taxCostSum = taxCostSum;
	}

	public Double getAdjustCostSum() {
		return adjustCostSum;
	}

	public void setAdjustCostSum(Double adjustCostSum) {
		this.adjustCostSum = adjustCostSum;
	}

	public Double getTaxSum() {
		return taxSum;
	}

	public void setTaxSum(Double taxSum) {
		this.taxSum = taxSum;
	}	

	public String getTaskCode() {
		return taskCode;
	}

	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}


	public Integer getType() {
		return type;
	}


	public void setType(Integer type) {
		this.type = type;
	}


	public Integer getState() {
		return state;
	}


	public void setState(Integer state) {
		this.state = state;
	}


	public StatementCompany getStatementCompany() {
		return statementCompany;
	}

	public void setStatementCompany(StatementCompany statementCompany) {
		this.statementCompany = statementCompany;
	}

	public Vendor getVendor() {
		return vendor;
	}


	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}


	public Integer getTaxRate() {
		return taxRate;
	}


	public void setTaxRate(Integer taxRate) {
		this.taxRate = taxRate;
	}


	public Date getInvoiceConfirmDate() {
		return invoiceConfirmDate;
	}


	public void setInvoiceConfirmDate(Date invoiceConfirmDate) {
		this.invoiceConfirmDate = invoiceConfirmDate;
	}


	public Account getInvoiceConfirmer() {
		return invoiceConfirmer;
	}


	public void setInvoiceConfirmer(Account invoiceConfirmer) {
		this.invoiceConfirmer = invoiceConfirmer;
	}


	public Date getInvoiceCancelDate() {
		return invoiceCancelDate;
	}


	public void setInvoiceCancelDate(Date invoiceCancelDate) {
		this.invoiceCancelDate = invoiceCancelDate;
	}


	public Account getInvoiceCanceler() {
		return invoiceCanceler;
	}


	public void setInvoiceCanceler(Account invoiceCanceler) {
		this.invoiceCanceler = invoiceCanceler;
	}


	public Account getMaker() {
		return maker;
	}


	public void setMaker(Account maker) {
		this.maker = maker;
	}


	public Date getMakeDate() {
		return makeDate;
	}


	public void setMakeDate(Date makeDate) {
		this.makeDate = makeDate;
	}


	public Account getCanceler() {
		return canceler;
	}


	public void setCanceler(Account canceler) {
		this.canceler = canceler;
	}


	public Date getCancelDate() {
		return cancelDate;
	}


	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}


	public Account getReviewer() {
		return reviewer;
	}


	public void setReviewer(Account reviewer) {
		this.reviewer = reviewer;
	}


	public Date getReviewDate() {
		return reviewDate;
	}


	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}


	public Account getDeployer() {
		return deployer;
	}


	public void setDeployer(Account deployer) {
		this.deployer = deployer;
	}


	public Date getDeployDate() {
		return deployDate;
	}


	public void setDeployDate(Date deployDate) {
		this.deployDate = deployDate;
	}


	public Account getConfirmer() {
		return confirmer;
	}


	public void setConfirmer(Account confirmer) {
		this.confirmer = confirmer;
	}


	public Date getConfirmDate() {
		return confirmDate;
	}


	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}


	public Date getInvoiceMakeDate() {
		return invoiceMakeDate;
	}


	public void setInvoiceMakeDate(Date invoiceMakeDate) {
		this.invoiceMakeDate = invoiceMakeDate;
	}


	public Account getInvoiceMaker() {
		return invoiceMaker;
	}


	public void setInvoiceMaker(Account invoiceMaker) {
		this.invoiceMaker = invoiceMaker;
	}


	public String getInvoiceCode() {
		return invoiceCode;
	}


	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	public Integer getInvoiceState() {
		return invoiceState;
	}


	public void setInvoiceState(Integer invoiceState) {
		this.invoiceState = invoiceState;
	}


	public Integer getInvoiceType() {
		return invoiceType;
	}


	public void setInvoiceType(Integer invoiceType) {
		this.invoiceType = invoiceType;
	}


	public String getErpInvoiceMakeName() {
		return erpInvoiceMakeName;
	}


	public void setErpInvoiceMakeName(String erpInvoiceMakeName) {
		this.erpInvoiceMakeName = erpInvoiceMakeName;
	}


	public Date getErpInvoiceMakeDate() {
		return erpInvoiceMakeDate;
	}


	public void setErpInvoiceMakeDate(Date erpInvoiceMakeDate) {
		this.erpInvoiceMakeDate = erpInvoiceMakeDate;
	}
	
}

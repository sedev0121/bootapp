package com.srm.platform.vendor.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.springframework.security.core.context.SecurityContextHolder;

import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.StatementSearchResult;
import com.srm.platform.vendor.utility.Utils;

@Entity

@SqlResultSetMapping(name = "StatementSearchResult", classes = {
		@ConstructorResult(targetClass = StatementSearchResult.class, columns = {
				@ColumnResult(name = "code", type = String.class), @ColumnResult(name = "state", type = String.class),
				@ColumnResult(name = "maker", type = String.class),
				@ColumnResult(name = "makedate", type = String.class),
				@ColumnResult(name = "verifier", type = String.class),
				@ColumnResult(name = "verifydate", type = String.class),
				@ColumnResult(name = "confirmer", type = String.class),
				@ColumnResult(name = "confirmdate", type = String.class),
				@ColumnResult(name = "invoicenummaker", type = String.class),
				@ColumnResult(name = "invoicenumdate", type = String.class),
				@ColumnResult(name = "u8invoicemaker", type = String.class),
				@ColumnResult(name = "u8invoicedate", type = String.class),
				@ColumnResult(name = "remark", type = String.class),
				@ColumnResult(name = "invoice_code", type = String.class),
				@ColumnResult(name = "vendor_name", type = String.class),
				@ColumnResult(name = "vendor_code", type = String.class),
				@ColumnResult(name = "type", type = String.class),
				@ColumnResult(name = "invoice_type", type = String.class) }) })

@Table(name = "statement_main")
public class StatementMain {
	@Id
	private String code;

	private Date makedate;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "maker_id", referencedColumnName = "id")
	Account maker;

	private Date confirmdate;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "confirmer_id", referencedColumnName = "id")
	Account confirmer;

	private Date verifydate;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "verifier_id", referencedColumnName = "id")
	Account verifier;

	private Date invoicenumdate;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "invoicenummaker_id", referencedColumnName = "id")
	Account invoicenummaker;

	private Date u8invoicedate;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "u8invoicemaker_id", referencedColumnName = "id")
	Account u8invoicemaker;

	@Column(name = "invoice_code")
	private String invoiceCode;

	@Column(name = "tax_rate")
	private Float taxRate = 16F;

	private Integer type = 1;
	private Integer invoiceType = 1;
	private String remark;
	private Integer state;
	private String u8invoiceid;

	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "vendor_code", referencedColumnName = "code")
	Vendor vendor;

	private String attachFileName;
	private String attachOriginalName;

	private String invoiceCancelReason;
	private Date invoiceCancelDate;

	public StatementMain() {

	}

	public String getInvoiceCancelReason() {
		return invoiceCancelReason;
	}

	public void setInvoiceCancelReason(String invoiceCancelReason) {
		this.invoiceCancelReason = invoiceCancelReason;
	}

	public Date getInvoiceCancelDate() {
		return invoiceCancelDate;
	}

	public void setInvoiceCancelDate(Date invoiceCancelDate) {
		this.invoiceCancelDate = invoiceCancelDate;
	}

	public StatementMain(AccountRepository accountRepository) {
		this.code = Utils.generateId();
		this.makedate = new Date();
		this.state = Constants.STATEMENT_STATE_NEW;
		this.maker = accountRepository
				.findOneByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
	}

	public String getU8invoiceid() {
		return u8invoiceid;
	}

	public void setU8invoiceid(String u8invoiceid) {
		this.u8invoiceid = u8invoiceid;
	}

	public Integer getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(Integer invoiceType) {
		this.invoiceType = invoiceType;
	}

	public Date getInvoicenumdate() {
		return invoicenumdate;
	}

	public void setInvoicenumdate(Date invoicenumdate) {
		this.invoicenumdate = invoicenumdate;
	}

	public Account getInvoicenummaker() {
		return invoicenummaker;
	}

	public void setInvoicenummaker(Account invoicenummaker) {
		this.invoicenummaker = invoicenummaker;
	}

	public Date getU8invoicedate() {
		return u8invoicedate;
	}

	public void setU8invoicedate(Date u8invoicedate) {
		this.u8invoicedate = u8invoicedate;
	}

	public Account getU8invoicemaker() {
		return u8invoicemaker;
	}

	public void setU8invoicemaker(Account u8invoicemaker) {
		this.u8invoicemaker = u8invoicemaker;
	}

	public String getAttachFileName() {
		return attachFileName;
	}

	public void setAttachFileName(String attachFileName) {
		this.attachFileName = attachFileName;
	}

	public String getAttachOriginalName() {
		return attachOriginalName;
	}

	public void setAttachOriginalName(String attachOriginalName) {
		this.attachOriginalName = attachOriginalName;
	}

	public Date getConfirmdate() {
		return confirmdate;
	}

	public void setConfirmdate(Date confirmdate) {
		this.confirmdate = confirmdate;
	}

	public Account getConfirmer() {
		return confirmer;
	}

	public void setConfirmer(Account confirmer) {
		this.confirmer = confirmer;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Float getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Float taxRate) {
		this.taxRate = taxRate;
	}

	public Date getMakedate() {
		return makedate;
	}

	public void setMakedate(Date makedate) {
		this.makedate = makedate;
	}

	public Date getVerifydate() {
		return verifydate;
	}

	public void setVerifydate(Date verifydate) {
		this.verifydate = verifydate;
	}

	public Account getMaker() {
		return maker;
	}

	public void setMaker(Account maker) {
		this.maker = maker;
	}

	public Account getVerifier() {
		return verifier;
	}

	public void setVerifier(Account verifier) {
		this.verifier = verifier;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

}

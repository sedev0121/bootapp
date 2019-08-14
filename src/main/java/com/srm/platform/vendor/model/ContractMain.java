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
import com.srm.platform.vendor.searchitem.ContractSearchResult;
import com.srm.platform.vendor.searchitem.StatementSearchResult;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.Utils;

@Entity

@SqlResultSetMapping(name = "ContractSearchResult", classes = {
		@ConstructorResult(targetClass = ContractSearchResult.class, columns = {
				@ColumnResult(name = "code", type = String.class), 
				@ColumnResult(name = "state", type = Integer.class),
				@ColumnResult(name = "name", type = String.class),
				@ColumnResult(name = "type", type = Integer.class),
				@ColumnResult(name = "kind", type = Integer.class),
				@ColumnResult(name = "company_name", type = String.class),
				@ColumnResult(name = "vendor_code", type = String.class),
				@ColumnResult(name = "vendor_name", type = String.class),
				@ColumnResult(name = "project_no", type = String.class),
				@ColumnResult(name = "price_type", type = Integer.class),
				@ColumnResult(name = "date", type = String.class),
				@ColumnResult(name = "start_date", type = String.class),
				@ColumnResult(name = "end_date", type = String.class),
				@ColumnResult(name = "quantity_type", type = Integer.class),
				@ColumnResult(name = "tax_rate", type = String.class),
				@ColumnResult(name = "pay_mode", type = String.class),
				@ColumnResult(name = "make_name", type = String.class),
				@ColumnResult(name = "make_date", type = String.class), 
				@ColumnResult(name = "memo", type = String.class),
		}) 
})

@Table(name = "contract_main")
public class ContractMain {
	@Id
	private String code;

	private Date date = new Date();
	
	private String name;
	
	@JsonProperty("project_no")
	private String projectNo;
	
	@JsonProperty("base_price")
	private Double basePrice;
	
	@JsonProperty("floating_price")
	private Double floatingPrice;
	
	private String memo;
	
	@JsonProperty("pay_mode")
	private String payMode;
	
	private Integer type = Constants.CONTRACT_TYPE_YES;	
	private Integer state = Constants.CONTRACT_STATE_NEW;
	private Integer kind = Constants.CONTRACT_KIND_CAIGOU;
	
	@JsonProperty("price_type")
	private Integer priceType = Constants.CONTRACT_PRICE_TYPE_FLOATING;
	
	@JsonProperty("quantity_type")
	private Integer quantityType = Constants.CONTRACT_QUANTITY_TYPE_NO;
	
	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "company_id", referencedColumnName = "id")
	private Company company;
	
	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "vendor_code", referencedColumnName = "code")
	Vendor vendor;
	
	@Column(name = "tax_rate")
	private Float taxRate = 16F;
	
	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "make_id", referencedColumnName = "id")
	Account maker;

	@JsonProperty("make_date")
	private Date makeDate = new Date();

	@JsonProperty("start_date")
	private Date startDate;
	
	@JsonProperty("end_date")
	private Date endDate;

	public ContractMain() {
		setCode(Utils.generateId());
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProjectNo() {
		return projectNo;
	}

	public void setProjectNo(String projectNo) {
		this.projectNo = projectNo;
	}

	public Double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}

	public Double getFloatingPrice() {
		return floatingPrice;
	}

	public void setFloatingPrice(Double floatingPrice) {
		this.floatingPrice = floatingPrice;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
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

	public Integer getKind() {
		return kind;
	}

	public void setKind(Integer kind) {
		this.kind = kind;
	}

	public Integer getPriceType() {
		return priceType;
	}

	public void setPriceType(Integer priceType) {
		this.priceType = priceType;
	}

	public Integer getQuantityType() {
		return quantityType;
	}

	public void setQuantityType(Integer quantityType) {
		this.quantityType = quantityType;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public Float getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Float taxRate) {
		this.taxRate = taxRate;
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	
}

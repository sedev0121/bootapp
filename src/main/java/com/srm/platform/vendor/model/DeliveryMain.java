package com.srm.platform.vendor.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srm.platform.vendor.searchitem.BoxSearchResult;
import com.srm.platform.vendor.searchitem.DeliverySearchResult;
import com.srm.platform.vendor.searchitem.SellerSearchResult;
import com.srm.platform.vendor.searchitem.StatementSearchResult;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.Utils;

@Entity

@SqlResultSetMapping(name = "DeliverySearchResult", classes = {
	@ConstructorResult(targetClass = DeliverySearchResult.class, columns = {
		@ColumnResult(name = "id", type = Long.class), 
		@ColumnResult(name = "code", type = String.class),
		@ColumnResult(name = "company_name", type = String.class),
		@ColumnResult(name = "vendor_name", type = String.class),
		@ColumnResult(name = "store_name", type = String.class),
		@ColumnResult(name = "store_address", type = String.class),
		@ColumnResult(name = "contact", type = String.class),
		@ColumnResult(name = "estimated_arrival_date", type = Date.class),
		@ColumnResult(name = "create_date", type = Date.class),
		@ColumnResult(name = "state", type = Integer.class) 
	}) 
})

@Table(name = "delivery_main")
public class DeliveryMain implements Serializable {
	private static final long serialVersionUID = 5855332316773551036L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String code;

	private Integer state = Constants.DELIVERY_STATE_NEW;
	

	@JsonProperty("create_date")
	private Date createDate = new Date();

	@JsonProperty("estimated_arrival_date")
	private Date estimatedArrivalDate = new Date();

	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "store_id")
	@ManyToOne()
	private Store store;

	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "company_id")
	@ManyToOne()
	private Company company;

	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "vendor_code")
	@ManyToOne()
	private Vendor vendor;

	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "creater_id")
	@ManyToOne()
	private Account creater;

	@JsonProperty("confirm_date")
	private Date confirmDate;
	
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "confirmer_id")
	@ManyToOne()
	private Account confirmer;
	
	public DeliveryMain() {
		this.code = Utils.generateId();
	}

	public Account getCreater() {
		return creater;
	}

	public void setCreater(Account creater) {
		this.creater = creater;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Date getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}

	public Account getConfirmer() {
		return confirmer;
	}

	public void setConfirmer(Account confirmer) {
		this.confirmer = confirmer;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getEstimatedArrivalDate() {
		return estimatedArrivalDate;
	}

	public void setEstimatedArrivalDate(Date estimatedArrivalDate) {
		this.estimatedArrivalDate = estimatedArrivalDate;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

}

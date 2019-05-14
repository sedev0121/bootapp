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
import com.srm.platform.vendor.searchitem.SellerSearchResult;

@SqlResultSetMapping(name = "BoxSearchResult", classes = {
		@ConstructorResult(targetClass = BoxSearchResult.class, columns = {
				@ColumnResult(name = "id", type = String.class), 
				@ColumnResult(name = "code", type = String.class),
				@ColumnResult(name = "bind_date", type = Date.class), 
				@ColumnResult(name = "box_class_name", type = String.class), 
				@ColumnResult(name = "bind_property", type = String.class), 
				@ColumnResult(name = "vendor_code", type = String.class), 
				@ColumnResult(name = "vendor_name", type = String.class), 
				@ColumnResult(name = "inventory_code", type = String.class), 
				@ColumnResult(name = "inventory_name", type = String.class), 
				@ColumnResult(name = "inventory_spec", type = String.class), 
				@ColumnResult(name = "delivery_code", type = String.class), 
				@ColumnResult(name = "deliver_number", type = String.class), 
				@ColumnResult(name = "quantity", type = Double.class), 
				@ColumnResult(name = "state", type = String.class),
				@ColumnResult(name = "used", type = String.class) 
				}) 
		})

@Entity
@Table(name = "box")
public class Box implements Serializable {
	private static final long serialVersionUID = 5855332316773551036L;
	public static final Integer BOX_IS_USING = 1;
	public static final Integer BOX_IS_EMPTY = 0;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String code;

	private Integer state;
	private Integer used;
	
	@JsonProperty("bind_date")
	private Date bindDate;

	private Double quantity;
	
	@JsonProperty("bind_property")
	private String bindProperty;
	
	private String deliveryCode;
	private String inventoryCode;
	
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "box_class_id")
	@ManyToOne()
	private BoxClass boxClass;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BoxClass getBoxClass() {
		return boxClass;
	}

	public Date getBindDate() {
		return bindDate;
	}

	public void setBindDate(Date bindDate) {
		this.bindDate = bindDate;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getBindProperty() {
		return bindProperty;
	}

	public void setBindProperty(String bindProperty) {
		this.bindProperty = bindProperty;
	}


	public String getDeliveryCode() {
		return deliveryCode;
	}

	public void setDeliveryCode(String deliveryCode) {
		this.deliveryCode = deliveryCode;
	}

	public String getInventoryCode() {
		return inventoryCode;
	}

	public void setInventoryCode(String inventoryCode) {
		this.inventoryCode = inventoryCode;
	}

	public void setBoxClass(BoxClass boxClass) {
		this.boxClass = boxClass;
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

	public Integer getUsed() {
		return used;
	}

	public void setUsed(Integer used) {
		this.used = used;
	}

}

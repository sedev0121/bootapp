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

import com.srm.platform.vendor.searchitem.BoxSearchResult;
import com.srm.platform.vendor.searchitem.SellerSearchResult;

@SqlResultSetMapping(name = "BoxSearchResult", classes = {
		@ConstructorResult(targetClass = BoxSearchResult.class, columns = {
				@ColumnResult(name = "id", type = String.class), @ColumnResult(name = "code", type = String.class),
				@ColumnResult(name = "spec", type = String.class), @ColumnResult(name = "memo", type = String.class),
				@ColumnResult(name = "state", type = String.class),
				@ColumnResult(name = "used", type = String.class) }) })

@Entity
@Table(name = "box")
public class Box implements Serializable {
	private static final long serialVersionUID = 5855332316773551036L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String code;

	private String spec;
	private String memo;
	private Integer state;
	private Integer used;

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

	public void setBoxClass(BoxClass boxClass) {
		this.boxClass = boxClass;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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

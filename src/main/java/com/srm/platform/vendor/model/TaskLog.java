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
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srm.platform.vendor.searchitem.TaskLogSearchResult;

@Entity

@SqlResultSetMapping(name = "TaskLogSearchResult", classes = {
	@ConstructorResult(targetClass = TaskLogSearchResult.class, columns = {
		@ColumnResult(name = "id", type = Long.class), 
		@ColumnResult(name = "vendor_code", type = String.class), 
		@ColumnResult(name = "vendor_name", type = String.class), 
		@ColumnResult(name = "company_name", type = String.class),
		@ColumnResult(name = "type", type = Integer.class),
		@ColumnResult(name = "state", type = Integer.class),
		@ColumnResult(name = "failed_reason", type = String.class),
		@ColumnResult(name = "create_date", type = Date.class),
	}) 
})

@Table(name = "task_log")
public class TaskLog implements Serializable {
	private static final long serialVersionUID = 5855332316773551036L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonProperty("failed_reason")
	private String failedReason;
	
	private Integer state;

	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "task_id")
	@ManyToOne()
	private Task task;
	
	@ManyToOne()
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "vendor_code", referencedColumnName = "code", nullable=true)
	private Vendor vendor;
	
	@OneToOne()
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "statement_code", referencedColumnName = "code", nullable=true)
	private StatementMain statement;
	
	@JsonProperty("create_date")
	private Date createDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StatementMain getStatement() {
		return statement;
	}

	public void setStatement(StatementMain statement) {
		this.statement = statement;
	}

	public String getFailedReason() {
		return failedReason;
	}

	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}	
	
}

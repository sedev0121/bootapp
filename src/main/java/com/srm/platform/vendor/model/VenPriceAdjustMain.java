package com.srm.platform.vendor.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.security.core.context.SecurityContextHolder;

import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.utility.UniqueIdGenerator;

@Entity
@Table(name = "venpriceadjust_main")
public class VenPriceAdjustMain {

	@Id
	String ccode;

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "cvencode", referencedColumnName = "code")
	Vendor vendor;

	Integer isupplytype;

	Integer type;
	Integer createtype;

	Integer itaxrate;

	Integer iverifystate;

	Date dstartdate;
	Date denddate;

	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "maker_id", referencedColumnName = "id")
	Account maker;

	Date dmakedate;

	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "cverifier_id", referencedColumnName = "id")
	Account verifier;

	Date dverifydate;

	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "creviewer_id", referencedColumnName = "id")
	Account reviewer;

	Date dreviewdate;

	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "cpublisher_id", referencedColumnName = "id")
	Account publisher;

	Date dpublishdate;

	public VenPriceAdjustMain(AccountRepository accountRepository) {

		this.ccode = UniqueIdGenerator.generateId();
		this.isupplytype = 1;
		this.type = 1;
		this.itaxrate = 17;
		this.iverifystate = 1;
		this.dmakedate = new Date();
		this.dstartdate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.dstartdate);
		cal.add(Calendar.MONTH, 1);
		this.denddate = cal.getTime();
		this.maker = accountRepository
				.findOneByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		this.reviewer = new Account();
		this.verifier = new Account();
		this.publisher = new Account();
		this.vendor = new Vendor();

	}

	public VenPriceAdjustMain() {

	}

	public String getCcode() {
		return ccode;
	}

	public void setCcode(String ccode) {
		this.ccode = ccode;
	}

	public Integer getCreatetype() {
		return createtype;
	}

	public void setCreatetype(Integer createtype) {
		this.createtype = createtype;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public Integer getIsupplytype() {
		return isupplytype;
	}

	public void setIsupplytype(Integer isupplytype) {
		this.isupplytype = isupplytype;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getItaxrate() {
		return itaxrate;
	}

	public void setItaxrate(Integer itaxrate) {
		this.itaxrate = itaxrate;
	}

	public Integer getIverifystate() {
		return iverifystate;
	}

	public void setIverifystate(Integer iverifystate) {
		this.iverifystate = iverifystate;
	}

	public Date getDstartdate() {
		return dstartdate;
	}

	public void setDstartdate(Date dstartdate) {
		this.dstartdate = dstartdate;
	}

	public Date getDenddate() {
		return denddate;
	}

	public void setDenddate(Date denddate) {
		this.denddate = denddate;
	}

	public Date getDmakedate() {
		return dmakedate;
	}

	public void setDmakedate(Date dmakedate) {
		this.dmakedate = dmakedate;
	}

	public Date getDverifydate() {
		return dverifydate;
	}

	public void setDverifydate(Date dverifydate) {
		this.dverifydate = dverifydate;
	}

	public Date getDreviewdate() {
		return dreviewdate;
	}

	public void setDreviewdate(Date dreviewdate) {
		this.dreviewdate = dreviewdate;
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

	public Account getReviewer() {
		return reviewer;
	}

	public void setReviewer(Account reviewer) {
		this.reviewer = reviewer;
	}

	public Account getPublisher() {
		return publisher;
	}

	public void setPublisher(Account publisher) {
		this.publisher = publisher;
	}

	public Date getDpublishdate() {
		return dpublishdate;
	}

	public void setDpublishdate(Date dpublishdate) {
		this.dpublishdate = dpublishdate;
	}

}

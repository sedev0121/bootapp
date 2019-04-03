package com.srm.platform.vendor.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriUtils;
import org.thymeleaf.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.model.NoticeRead;
import com.srm.platform.vendor.model.OperationHistory;
import com.srm.platform.vendor.model.Price;
import com.srm.platform.vendor.model.VenPriceAdjustDetail;
import com.srm.platform.vendor.model.VenPriceAdjustMain;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.BoxClassRepository;
import com.srm.platform.vendor.repository.BoxRepository;
import com.srm.platform.vendor.repository.CompanyRepository;
import com.srm.platform.vendor.repository.DeliveryDetailRepository;
import com.srm.platform.vendor.repository.DeliveryMainRepository;
import com.srm.platform.vendor.repository.FunctionActionRepository;
import com.srm.platform.vendor.repository.FunctionRepository;
import com.srm.platform.vendor.repository.InventoryClassRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.NoticeReadRepository;
import com.srm.platform.vendor.repository.NoticeRepository;
import com.srm.platform.vendor.repository.OperationHistoryRepository;
import com.srm.platform.vendor.repository.PermissionGroupFunctionActionRepository;
import com.srm.platform.vendor.repository.PermissionGroupRepository;
import com.srm.platform.vendor.repository.PermissionGroupUserRepository;
import com.srm.platform.vendor.repository.PermissionUserScopeRepository;
import com.srm.platform.vendor.repository.PriceRepository;
import com.srm.platform.vendor.repository.ProvideClassRepository;
import com.srm.platform.vendor.repository.PurchaseInDetailRepository;
import com.srm.platform.vendor.repository.PurchaseInMainRepository;
import com.srm.platform.vendor.repository.PurchaseOrderDetailRepository;
import com.srm.platform.vendor.repository.PurchaseOrderMainRepository;
import com.srm.platform.vendor.repository.StatementDetailRepository;
import com.srm.platform.vendor.repository.StatementMainRepository;
import com.srm.platform.vendor.repository.StoreRepository;
import com.srm.platform.vendor.repository.VenPriceAdjustDetailRepository;
import com.srm.platform.vendor.repository.VenPriceAdjustMainRepository;
import com.srm.platform.vendor.repository.VendorClassRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.searchitem.DimensionTargetItem;
import com.srm.platform.vendor.service.SessionCounter;
import com.srm.platform.vendor.u8api.ApiClient;
import com.srm.platform.vendor.u8api.AppProperties;
import com.srm.platform.vendor.utility.AccountPermission;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.GenericJsonResponse;
import com.srm.platform.vendor.utility.U8VenpriceadjustPostData;
import com.srm.platform.vendor.utility.U8VenpriceadjustPostEntry;
import com.srm.platform.vendor.utility.UploadFileHelper;
import com.srm.platform.vendor.utility.Utils;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 7074132560318771710L;

}

@Controller
@PreAuthorize("isAuthenticated()")
public class CommonController {
	public final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public ApiClient apiClient;

	@Autowired
	public AppProperties appProperties;

	@PersistenceContext
	public EntityManager em;

	@Autowired
	public VendorRepository vendorRepository;

	@Autowired
	public CompanyRepository companyRepository;

	@Autowired
	public NoticeRepository noticeRepository;

	@Autowired
	public OperationHistoryRepository operationHistoryRepository;

	@Autowired
	public NoticeReadRepository noticeReadRepository;

	@Autowired
	public AccountRepository accountRepository;

	@Autowired
	public HttpSession httpSession;

	@Autowired
	public SessionCounter sessionCounter;

	@Autowired
	public DeliveryMainRepository deliveryMainRepository;
	
	@Autowired
	public DeliveryDetailRepository deliveryDetailRepository;
	
	@Autowired
	public VenPriceAdjustMainRepository venPriceAdjustMainRepository;

	@Autowired
	public VenPriceAdjustDetailRepository venPriceAdjustDetailRepository;

	@Autowired
	public PriceRepository priceRepository;

	@Autowired
	public ProvideClassRepository provideClassRepository;

	@Autowired
	public PermissionGroupRepository permissionGroupRepository;

	@Autowired
	public PermissionGroupUserRepository permissionGroupUserRepository;

	@Autowired
	public PermissionUserScopeRepository permissionUserScopeRepository;

	@Autowired
	public BoxClassRepository boxClassRepository;

	@Autowired
	public BoxRepository boxRepository;

	@Autowired
	public InventoryClassRepository inventoryClassRepository;

	@Autowired
	public InventoryRepository inventoryRepository;

	@Autowired
	public FunctionRepository functionRepository;

	@Autowired
	public FunctionActionRepository functionActionRepository;

	@Autowired
	public PermissionGroupFunctionActionRepository permissionGroupFunctionActionRepository;

	@Autowired
	public PermissionGroupUserRepository permissionGroupUserReopsitory;

	@Autowired
	public PurchaseInMainRepository purchaseInMainRepository;

	@Autowired
	public PurchaseInDetailRepository purchaseInDetailRepository;

	@Autowired
	public PurchaseOrderMainRepository purchaseOrderMainRepository;

	@Autowired
	public PurchaseOrderDetailRepository purchaseOrderDetailRepository;

	@Autowired
	public StatementMainRepository statementMainRepository;

	@Autowired
	public StatementDetailRepository statementDetailRepository;

	@Autowired
	public StoreRepository storeRepository;

	@Autowired
	public VendorClassRepository vendorClassRepository;

	protected int currentPage;
	protected int maxResults;
	protected int pageSize;

	public boolean isVendor() {

		return hasAuthority("ROLE_VENDOR");
	}

	public boolean isAdmin() {

		return hasAuthority("ROLE_ADMIN");
	}

	public void show404() {
		throw new ResourceNotFoundException();
	}

	public void show403() {
		throw new AccessDeniedException("access denied");
	}

	public AccountPermission getPermissionScopeOfFunction(Long functionActionId) {
		List<DimensionTargetItem> scopeList = permissionGroupRepository
				.findPermissionScopeOf(this.getLoginAccount().getId(), functionActionId);
		AccountPermission accountPermission = new AccountPermission(this.getLoginAccount().getId(), functionActionId,
				scopeList);
		return accountPermission;
	}

	public boolean hasAuthority(String authority) {
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();
		for (GrantedAuthority a : authorities) {
			if (a.getAuthority().equals(authority)) {
				return true;
			}
		}
		return false;
	}

	public List<String> getUnitListFor(String permissionKey) {
		String defaultUnitList = (String) httpSession.getAttribute(Constants.KEY_DEFAULT_UNIT_LIST);
		String value = (String) httpSession.getAttribute(permissionKey);
		if (value == null)
			value = "";

		value = StringUtils.append(value, defaultUnitList);

		List<String> result = Arrays.asList(StringUtils.split(value, ","));
		return result;

	}

	public boolean isVisibleNotice(Long noticeId) {
		boolean isVisible = false;
		List<NoticeRead> readList = noticeReadRepository.findListByNoticeId(noticeId);
		for (NoticeRead read : readList) {
			if (read.getAccount().getId() == this.getLoginAccount().getId()) {
				isVisible = true;
				break;
			}
		}

		return isVisible;
	}

	public void setReadDate(Long noticeId) {
		NoticeRead noticeRead = noticeReadRepository.findOneByNoticeAndAccount(noticeId,
				this.getLoginAccount().getId());
		if (noticeRead == null) {
			noticeRead = new NoticeRead();
			noticeRead.setAccount(this.getLoginAccount());
			noticeRead.setNotice(noticeRepository.findOneById(noticeId));
		}

		if (noticeRead != null) {
			noticeRead.setReadDate(new Date());
			noticeReadRepository.save(noticeRead);
		}
	}

	public void sendmessage(String title, List<Account> toList, String url) {
		Notice notice = new Notice();
		notice.setState(Constants.NOTICE_STATE_PUBLISH);
		notice.setType(Constants.NOTICE_TYPE_SYSTEM);
		notice.setTitle(title);
		notice.setContent(title);
		notice.setCreateDate(new Date());
		notice.setUrl(url);
		notice = noticeRepository.save(notice);

		for (Account account : toList) {
			NoticeRead noticeRead = new NoticeRead();
			noticeRead.setNotice(notice);
			noticeRead.setAccount(account);
			noticeReadRepository.save(noticeRead);
		}
	}

	protected String getOperationHistoryType() {
		return null;
	};
	
	public void addOpertionHistory(String targetId, String content) {
		OperationHistory operationHistory = new OperationHistory();
		operationHistory.setTargetId(targetId);
		operationHistory.setTargetType(this.getOperationHistoryType());
		operationHistory.setContent(content);
		operationHistory.setAccount(this.getLoginAccount());

		operationHistory = operationHistoryRepository.save(operationHistory);
	}

	public Account getLoginAccount() {

		Account account = (Account) httpSession.getAttribute("account");

		if (account == null) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			account = accountRepository.findOneByUsername(authentication.getName());

			if (account != null) {
				httpSession.setAttribute("account", account);
			}
		}

		return account;
	}

	public List<Long> convertListStrToLongList(String listStr) {
		List<Long> idList = new ArrayList<>();

		if (listStr != null) {
			List<String> accountList = Arrays.asList(StringUtils.split(listStr, ","));

			if (accountList != null) {
				for (String temp : accountList) {
					if (temp != null)
						idList.add(Long.valueOf(temp));
				}
			}

		} else {
			return new ArrayList<>();
		}

		return idList;
	}

	protected GenericJsonResponse<VenPriceAdjustMain> u8VenPriceAdjust(VenPriceAdjustMain main) {

		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, Object> map = new HashMap<>();

		GenericJsonResponse<VenPriceAdjustMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS,
				null, main);
		try {

			map = new HashMap<>();

			String postJson = createJsonString(main);
			Map<String, String> getParams = new HashMap<>();

			getParams.put("biz_id", main.getCcode());
			getParams.put("sync", "1");

			String response = apiClient.generateVenpriceadjust(getParams, postJson);

			map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
			});

			int errorCode = Integer.parseInt((String) map.get("errcode"));
			String errmsg = String.valueOf(map.get("errmsg"));

			if (errorCode != appProperties.getError_code_success()) {
				jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, errorCode + ":" + errmsg, main);
			}

		} catch (IOException e) {
			logger.info(e.getMessage());
			jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, "服务器错误！", main);
		}

		return jsonResponse;
	}

	// 更新价格表
	protected void updatePriceTable(VenPriceAdjustMain venPriceAdjustMain) {

		List<VenPriceAdjustDetail> list = venPriceAdjustDetailRepository.findByMainId(venPriceAdjustMain.getCcode());
		for (VenPriceAdjustDetail item : list) {
			if (venPriceAdjustMain.getType() == Constants.INQUERY_TYPE_RANGE && item.getIvalid() == 0)
				continue;

			Price price = new Price();
			price.setVendor(venPriceAdjustMain.getVendor());
			price.setInventory(item.getInventory());
			price.setCreateby(venPriceAdjustMain.getMaker().getId());
			price.setCreatedate(venPriceAdjustMain.getDmakedate());
			price.setFavdate(venPriceAdjustMain.getDstartdate());
			price.setFcanceldate(venPriceAdjustMain.getDenddate());
			price.setFnote(item.getCbodymemo());
			price.setFprice(item.getIunitprice());
			price.setFtax((float) item.getItaxrate());
			price.setFtaxprice(item.getItaxunitprice());
			price.setFisoutside(false);
			price.setFcheckdate(new Date());
			price.setDescription(item.getInventory().getSpecs());
			// price.setFauxunit(item.getInventory().getPuunitName());
			priceRepository.save(price);
		}

	}

	protected String createJsonString(VenPriceAdjustMain main) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";

		U8VenpriceadjustPostData post = new U8VenpriceadjustPostData();
		post.setCcode(main.getCcode());
		post.setIsupplytype(main.getIsupplytype());
		post.setMaker(this.getLoginAccount().getRealname());

		List<U8VenpriceadjustPostEntry> entryList = new ArrayList<>();

		List<VenPriceAdjustDetail> detailList = venPriceAdjustDetailRepository.findByMainId(main.getCcode());
		for (VenPriceAdjustDetail detail : detailList) {
			if (main.getType() == Constants.INQUERY_TYPE_RANGE && detail.getIvalid() == 0)
				continue;

			U8VenpriceadjustPostEntry entry = new U8VenpriceadjustPostEntry();
			entry.setCinvcode(detail.getInventory().getCode());
			entry.setCvencode(main.getVendor().getCode());
			entry.setDstartdate(Utils.formatDate(detail.getDstartdate()));
			entry.setItaxrate(detail.getItaxrate());
			entry.setItaxunitprice(detail.getItaxunitprice());
			entry.setIunitprice(detail.getIunitprice());

			entryList.add(entry);
		}

		post.setEntry(entryList);

		try {
			Map<String, U8VenpriceadjustPostData> map = new HashMap<>();
			map.put("venpriceadjust", post);
			jsonString = mapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonString;
	}

	public ResponseEntity<Resource> download(String filePath, String downloadFileName) {

		Resource file = UploadFileHelper.getResource(filePath);

		if (file == null) {
			show404();
		}

		downloadFileName = UriUtils.encodePath(downloadFileName, Charsets.UTF_8.toString());

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileName + "\"")
				.body(file);
	}

}

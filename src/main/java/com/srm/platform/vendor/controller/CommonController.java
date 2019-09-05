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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.model.NoticeRead;
import com.srm.platform.vendor.model.OperationHistory;
import com.srm.platform.vendor.model.Price;
import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.model.VenPriceAdjustDetail;
import com.srm.platform.vendor.model.VenPriceAdjustMain;
import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.repository.AccountRepository;
import com.srm.platform.vendor.repository.AttachFileRepository;
import com.srm.platform.vendor.repository.BoxClassRepository;
import com.srm.platform.vendor.repository.BoxRepository;
import com.srm.platform.vendor.repository.CompanyRepository;
import com.srm.platform.vendor.repository.ContractDetailRepository;
import com.srm.platform.vendor.repository.ContractMainRepository;
import com.srm.platform.vendor.repository.DeliveryDetailRepository;
import com.srm.platform.vendor.repository.DeliveryMainRepository;
import com.srm.platform.vendor.repository.FunctionActionRepository;
import com.srm.platform.vendor.repository.FunctionRepository;
import com.srm.platform.vendor.repository.InventoryClassRepository;
import com.srm.platform.vendor.repository.InventoryRepository;
import com.srm.platform.vendor.repository.MasterRepository;
import com.srm.platform.vendor.repository.NoticeClassRepository;
import com.srm.platform.vendor.repository.NoticeReadRepository;
import com.srm.platform.vendor.repository.NoticeRepository;
import com.srm.platform.vendor.repository.OperationHistoryRepository;
import com.srm.platform.vendor.repository.PermissionGroupFunctionActionRepository;
import com.srm.platform.vendor.repository.PermissionGroupRepository;
import com.srm.platform.vendor.repository.PermissionGroupUserRepository;
import com.srm.platform.vendor.repository.PermissionUserScopeRepository;
import com.srm.platform.vendor.repository.PriceRepository;
import com.srm.platform.vendor.repository.PurchaseInDetailRepository;
import com.srm.platform.vendor.repository.PurchaseInMainRepository;
import com.srm.platform.vendor.repository.PurchaseOrderDetailRepository;
import com.srm.platform.vendor.repository.PurchaseOrderMainRepository;
import com.srm.platform.vendor.repository.StatementCompanyRepository;
import com.srm.platform.vendor.repository.StatementDetailRepository;
import com.srm.platform.vendor.repository.StatementMainRepository;
import com.srm.platform.vendor.repository.StoreRepository;
import com.srm.platform.vendor.repository.TaskLogRepository;
import com.srm.platform.vendor.repository.TaskRepository;
import com.srm.platform.vendor.repository.VenPriceAdjustDetailRepository;
import com.srm.platform.vendor.repository.VenPriceAdjustMainRepository;
import com.srm.platform.vendor.repository.VendorClassRepository;
import com.srm.platform.vendor.repository.VendorRepository;
import com.srm.platform.vendor.searchitem.DimensionTargetItem;
import com.srm.platform.vendor.searchitem.StatementDetailItem;
import com.srm.platform.vendor.service.SessionCounter;
import com.srm.platform.vendor.u8api.AppProperties;
import com.srm.platform.vendor.u8api.RestApiClient;
import com.srm.platform.vendor.u8api.RestApiResponse;
import com.srm.platform.vendor.utility.AccountPermissionInfo;
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
	public RestApiClient apiClient;
	
	@Autowired
	public RestApiClient restApiClient;
	
	@Autowired
	public AppProperties appProperties;

	@PersistenceContext
	public EntityManager em;

	@Autowired
	public MasterRepository masterRepository;
	
	@Autowired
	public VendorRepository vendorRepository;

	@Autowired
	public NoticeClassRepository noticeClassRepository;
	
	@Autowired
	public CompanyRepository companyRepository;

	@Autowired
	public StatementCompanyRepository statementCompanyRepository;
	
	@Autowired
	public NoticeRepository noticeRepository;
	
	@Autowired
	public TaskRepository taskRepository;
	
	@Autowired
	public TaskLogRepository taskLogRepository;

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
	public ContractMainRepository contractMainRepository;

	@Autowired
	public ContractDetailRepository contractDetailRepository;	
	
	@Autowired
	public StoreRepository storeRepository;

	@Autowired
	public VendorClassRepository vendorClassRepository;
	
	@Autowired
	public AttachFileRepository attachFileRepository;

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

	public AccountPermissionInfo getPermissionScopeOfFunction(Long functionActionId) {
		List<DimensionTargetItem> scopeList = permissionGroupRepository.findPermissionScopeOf(this.getLoginAccount().getId(), functionActionId);
		List<Long> groupList = permissionGroupRepository.findGroupsOfAccountFunction(this.getLoginAccount().getId(), functionActionId);
		AccountPermissionInfo accountPermissionInfo = new AccountPermissionInfo(this.getLoginAccount().getId(), functionActionId, scopeList, groupList);
		logger.info(accountPermissionInfo.toString());
		return accountPermissionInfo;
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
	
	public void addOpertionHistory(String targetId, String action, String content) {
		OperationHistory operationHistory = new OperationHistory();
		operationHistory.setTargetId(targetId);
		operationHistory.setTargetType(this.getOperationHistoryType());
		operationHistory.setAction(action);
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
	
	public void deleteAttach(String filePath) {

		Resource file = UploadFileHelper.getResource(filePath);
		if (file != null) {
			try {
				file.getFile().delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	protected GenericJsonResponse<VenPriceAdjustMain> u8price(VenPriceAdjustMain main) {

		GenericJsonResponse<VenPriceAdjustMain> jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.SUCCESS, null,
				main);

		RestApiResponse response = apiClient.postForU8Price(createU8PricePostData(main));

		if (!response.isSuccess()) {
			response.getErrmsg();
			jsonResponse = new GenericJsonResponse<>(GenericJsonResponse.FAILED, response.getErrmsg(), main);
		}

		return jsonResponse;
	}

	private Map<String, Object> createU8PricePostData(VenPriceAdjustMain main) {
		
		Map<String, Object> postParams = new HashMap<>();
		postParams.put("ccode", main.getCcode()); // 调价单号
		postParams.put("ddate", Utils.formatDateTime(main.getDmakedate())); // 单据日期
		postParams.put("cmainmemo", ""); // 备注
		postParams.put("cpersoncode", this.getLoginAccount().getEmployeeNo()); // 业务员编号
		postParams.put("cmaker", main.getMaker().getRealname()); // 制单人
		postParams.put("isupplytype", main.getIsupplytype()); //1表示采购，2表示委外
		

		List<VenPriceAdjustDetail> list = venPriceAdjustDetailRepository.findByMainId(main.getCcode());
		List<Map<String, String>> listParams = new ArrayList<Map<String, String>>();
		for (VenPriceAdjustDetail detail : list) {
			Map<String, String> row = new HashMap<>();
			row.put("dstartdate", Utils.formatDateTime(detail.getDstartdate())); // 调价开始日期
			row.put("cinvcode", detail.getInventory().getCode()); // 存货编码
			row.put("cvencode", main.getVendor().getCode()); // 供应商编号
			row.put("iunitprice", Utils.priceNumber(detail.getIunitprice())); // 未税单价
			row.put("itaxrate", detail.getItaxrate().toString()); // 税率
			row.put("itaxunitprice", Utils.priceNumber(detail.getItaxunitprice())); // 含税单价
			row.put("ivouchrowno", detail.getRowno().toString()); // 行号
			
			listParams.add(row);
		}

		postParams.put("detail", listParams);

		return postParams;
	}

}

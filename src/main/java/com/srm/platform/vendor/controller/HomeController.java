package com.srm.platform.vendor.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.srm.platform.vendor.model.Notice;
import com.srm.platform.vendor.model.NoticeClass;
import com.srm.platform.vendor.repository.NoticeClassRepository;
import com.srm.platform.vendor.repository.NoticeRepository;
import com.srm.platform.vendor.searchitem.NoticeSearchResult;
import com.srm.platform.vendor.utility.Constants;
import com.srm.platform.vendor.utility.UploadFileHelper;

@Controller
@RequestMapping(path = "/")

public class HomeController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SpringTemplateEngine templateEngine;
	
	@PersistenceContext
	public EntityManager em;
	
	@Autowired
	public NoticeRepository noticeRepository;

	@Autowired
	public NoticeClassRepository noticeClassRepository;
	
	@GetMapping("/forbidden")
	public String forbidden() {
		return "denied";
	}

	@GetMapping(value = "/login")
	public String login(Model model) {
		List<Map<String, Object>> noticeMapList = new ArrayList<Map<String, Object>>();
		List<NoticeClass> noticeClassList = noticeClassRepository.findLast3Class();
		for(NoticeClass noticeClass : noticeClassList) {
			List<NoticeSearchResult> noticeList = getLastNotice(noticeClass);
			Map<String, Object> oneClass = new HashMap<>();
			oneClass.put("title", noticeClass.getName());
			oneClass.put("list", noticeList);
			
			noticeMapList.add(oneClass);
		}
		
		model.addAttribute("noticeList", noticeMapList);	
		model.addAttribute("version", Constants.VERSION);
		return "login";
	}
	
	@ResponseBody
	@RequestMapping(value = "/keepalive")
	public String keepAlive() {
		return "alive";
	}

	@GetMapping(value = "/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}

		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			cookie.setMaxAge(0);
			cookie.setValue(null);
			cookie.setPath("/");
			response.addCookie(cookie);
		}

		return "redirect:/login?logout";
	}

	@RequestMapping(value = "/test")
	public String test(Model model) {
		model.addAttribute("url", "/changepassword?id=1&token=e4706254-415a-4dac-84bf-7a621f2d405d");

		Map<String, Object> test = new HashMap<>();
		test.put("url", "test");

		Context context = new Context();

		context.setVariables(test);
		String html = templateEngine.process("email/resetpassword", context);

		return "email/resetpassword";
	}

	private List<NoticeSearchResult> getLastNotice(NoticeClass noticeClass) {
		String selectQuery = "SELECT distinct a.*, b.realname create_name, d.realname verify_name, e.name class_name, null read_date FROM notice a left join account b on a.create_account=b.id "
				+ "left join account d on d.id=a.verify_account left join notice_class e on a.class_id=e.id where type=1 and a.state=3 and class_id=:class_id order by a.verify_date desc";
		Query q = em.createNativeQuery(selectQuery, "NoticeSearchResult");
		q.setParameter("class_id", noticeClass.getId());
		
		return q.setFirstResult(0).setMaxResults(5).getResultList();	
	}
	
	@GetMapping("/{id}/noticedownload")
	public ResponseEntity<Resource> download(@PathVariable("id") Long id) {
		Notice notice = noticeRepository.findOneById(id);
		String filePath = Constants.PATH_UPLOADS_NOTICE + File.separator + notice.getAttachFileName();
		String downloadFileName = notice.getAttachOriginalName();
		
		Resource file = UploadFileHelper.getResource(filePath);

		if (file == null) {
			throw new ResourceNotFoundException();
		}

		downloadFileName = UriUtils.encodePath(downloadFileName, Charsets.UTF_8.toString());

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileName + "\"")
				.body(file);
	}
	
	@RequestMapping(value = "/about_us")
	public String aboutUs(Model model) {
		return "home/about_us";
	}
	
	@RequestMapping(value = "/link_us")
	public String linkUs(Model model) {
		return "home/link_us";
	}
	
	@RequestMapping(value = "/intro")
	public String manual(Model model) {
		return "home/intro";
	}
	
	@RequestMapping(value = "/others")
	public String others(Model model) {
		return "home/others";
	}
}

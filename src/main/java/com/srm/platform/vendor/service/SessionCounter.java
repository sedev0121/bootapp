package com.srm.platform.vendor.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class SessionCounter implements HttpSessionListener, Serializable {

	private static final long serialVersionUID = 5277719764945733139L;
	private List<HttpSession> sessions = new ArrayList<>();
	public static final String COUNTER = "session-counter";

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		sessions.add(session);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		sessions.remove(session);
	}

	public int getActiveSessionNumber() {
		return sessions.size();
	}

	public List<Long> getActiveAccountList() {
		List<Long> list = new ArrayList<>();
		for (HttpSession session : sessions) {
			if (session.getAttribute("account_id") != null) {
				list.add((Long) session.getAttribute("account_id"));
			}

		}
		return list;
	}
}
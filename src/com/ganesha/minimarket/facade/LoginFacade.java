package com.ganesha.minimarket.facade;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.model.User;

public class LoginFacade {

	private static LoginFacade instance;

	public static LoginFacade getInstance() {
		if (instance == null) {
			instance = new LoginFacade();
		}
		return instance;
	}

	private LoginFacade() {
	}

	public boolean login(String loginId) {
		Session session = HibernateUtils.openSession();
		try {

			Criteria criteria = session.createCriteria(User.class);
			criteria.add(Restrictions.eq("login", loginId).ignoreCase());

			User user = (User) criteria.uniqueResult();

			if (user != null) {
				Main.setUserLogin(user);
				return true;
			} else {
				return false;
			}
		} finally {
			session.close();
		}
	}
}
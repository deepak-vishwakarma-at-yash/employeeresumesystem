package com.yash.EmployeeInformation.service;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import com.yash.EmployeeInformation.dao.UserDao;
import com.yash.EmployeeInformation.util.ConnectionUtil;

/**
 * Session Bean implementation class UserService
 */
@Stateless
@LocalBean
public class UserService implements UserServiceLocal {

    /**
     * Default constructor. 
     */
	
	@Inject
	ConnectionUtil connectionUtil;
	
	@Inject
	UserDao userDao;
	
    public UserService() {
        // TODO Auto-generated constructor stub
    }

}

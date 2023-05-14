package com.example.miplatform.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.miplatform.dao.TestDao;

@Service
public class TestService {

	@Autowired
	TestDao testDao;

	public void searchList(HttpServletRequest request, HttpServletResponse response){
		testDao.searchDao(request, response);
	}

	public void saveList(HttpServletRequest request, HttpServletResponse response){
		testDao.saveDto(request, response);
	}
}

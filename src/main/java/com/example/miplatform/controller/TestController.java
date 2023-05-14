package com.example.miplatform.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.miplatform.service.TestService;

@Controller
public class TestController {

	@Autowired
	TestService testService;

	@PostMapping("/search")
	public void search(HttpServletRequest request, HttpServletResponse response){
		testService.searchList(request, response);
	}	

	@PostMapping("/save")
	public void save(HttpServletRequest request, HttpServletResponse response){
		testService.saveList(request, response);
	}
}

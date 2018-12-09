package com.north.study.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter("/*")
public class AgentFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String agent = req.getHeader("User-Agent");
		System.out.println(agent);
//		StringTokenizer st = new StringTokenizer(Agent,";");
//
//		st.nextToken();
//
//		//得到用户的浏览器名  
//
//		String userbrowser = st.nextToken();  
//
//		//得到用户的操作系统名 
//
//		String useros = st.nextToken();  
//		System.getProperty("os.name");            
//		System.getProperty("os.version");  
//		System.getProperty("os.arch"); 
//		request.getHeader("User-Agent");
		
		chain.doFilter(request, response);
		
	}
	
	

}

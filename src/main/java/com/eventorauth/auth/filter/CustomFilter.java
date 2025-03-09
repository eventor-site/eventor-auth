package com.eventorauth.auth.filter;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

public class CustomFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException, ServletException {

		HttpServletResponse httpResponse = (HttpServletResponse)response;
		if (!httpResponse.isCommitted()) {
			chain.doFilter(request, response);
		} else {
			logger.warn("Response already committed, skipping filter");
		}

	}
}

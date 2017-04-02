/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ea.cs.gui.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionFilter implements Filter {

	private FilterConfig filterConfig = null;

	public SessionFilter() {
	}

	private void doBeforeProcessing(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		((HttpServletRequest) request).getSession(true);
	}

	private void doAfterProcessing(ServletRequest request, ServletResponse response) throws IOException, ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		doBeforeProcessing(request, response);

		Throwable problem = null;
		try {
//			String url = ((HttpServletRequest) request).getRequestURL().toString();
//			if (url.indexOf("/faces/") > 0 && url.indexOf("/private/") > 0) {
//				((HttpServletResponse) response).sendRedirect(((HttpServletRequest) request).getContextPath() + "/faces/login.xhtml");
//			} else {
				chain.doFilter(request, response);
			//}
		} catch (Throwable t) {
			// If an exception is thrown somewhere down the filter chain, we still want to execute our after processing,
			// and then rethrow the problem after that.
			problem = t;
			t.printStackTrace();
		}

		doAfterProcessing(request, response);

		// If there was a problem, we want to rethrow it if it is a known type, otherwise log it.
		if (problem != null) {
			if (problem instanceof ServletException) {
				throw (ServletException) problem;
			}
			if (problem instanceof IOException) {
				throw (IOException) problem;
			}
		}
	}

	public FilterConfig getFilterConfig() {
		return (this.filterConfig);
	}

	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
		if (filterConfig != null) {
		}
	}

	@Override
	public String toString() {
		if (filterConfig == null) {
			return ("AuthenticationFilter() is null");
		}
		StringBuffer sb = new StringBuffer("AuthenticationFilter(");
		sb.append(filterConfig);
		sb.append(")");
		return (sb.toString());
	}
}

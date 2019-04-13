package com.actolap.lyve.fe.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.actolap.lyve.fe.config.Config;



public class CDNInterceptor implements HandlerInterceptor {
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object arg2) throws Exception {
		if (Config.dev) {
			request.getSession().setAttribute("version", Config.cssAndJsVersion);
			request.getSession().setAttribute("cdn", request.getContextPath());
		} else {
			if (!Config.version.equals(request.getSession().getAttribute(
					"version"))) {
				request.getSession().setAttribute("version", Config.cssAndJsVersion);
			}
		}
//		if (request.getContextPath().equals("/")) {
//			request.getSession().setAttribute("ctp", "/#/");
//		} else {
//			request.getSession().setAttribute("ctp",
//					request.getContextPath() + "/#/");
//		}
		return true;
	}

	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}

	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {

	}
}


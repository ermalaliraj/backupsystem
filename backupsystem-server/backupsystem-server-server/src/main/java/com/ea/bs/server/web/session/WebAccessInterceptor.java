package com.ea.bs.server.web.session;

import java.lang.reflect.Method;
import java.security.Principal;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WebAccessInterceptor {

	@Resource
	protected SessionContext ctx;

	// @EJB(name="SessioneTicketPS")
	// SessioneTicketPSLocal sessioneTicketPS;

	private static final Log log = LogFactory.getLog(WebAccessInterceptor.class);

	@AroundInvoke
	public Object verificaSessioneTicket(InvocationContext inv) throws Exception {
		Principal principal = ctx.getCallerPrincipal();
		Method method = inv.getMethod();

		log.debug("[SERVER] Access method: " + method.getName() + " with principal (user)" + principal.getName());

		if (!"getStatus".equals(method.getName())) {
			//sessioneTicketPS.refreshSessione(principal.getName());
		}
		return inv.proceed();
	}
}

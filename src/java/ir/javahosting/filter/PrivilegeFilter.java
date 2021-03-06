package ir.javahosting.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ir.javahosting.domain.Function;
import ir.javahosting.domain.Role;
import ir.javahosting.domain.User;
import ir.javahosting.service.PrivilegeService;
import ir.javahosting.service.impl.PrivilegeServiceImpl;
import ir.javahosting.utils.Constant;


@WebFilter(filterName="PrivilegeFilter" ,urlPatterns="/manage/*")
public class PrivilegeFilter implements Filter {
	private PrivilegeService ps = new PrivilegeServiceImpl();
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request;
		HttpServletResponse response;
		try {
			request = (HttpServletRequest) req;
			response = (HttpServletResponse) res;
		} catch (ClassCastException e) {
			throw new ServletException(e);
		}
		
		User user = (User) request.getSession().getAttribute(Constant.USER_LOGIN_FLAG);
		if(user==null){
			response.sendRedirect(request.getContextPath()+"/op/login.jsp");
			return;
		}
		Set<Function> functions = new HashSet<Function>();
			
		List<Role> roles = ps.findUserRoles(user);
			
		for(Role role:roles){
			List<Function> funs = ps.findRoleFunctions(role);
			functions.addAll(funs);
		}
		
		String uri = request.getRequestURI();
		String queryString = request.getQueryString();
		String fullUri = uri+(queryString==null?"":("?"+queryString));					
			
		boolean hasPermission = false;
		for(Function fun:functions){
			if(fullUri.startsWith(fun.getUri())){
				hasPermission = true;
				break;
			}
		}
			
		if(!hasPermission){
			response.getWriter().write("???????????? ???????? ??????????");
			return;
		}
		chain.doFilter(request, response);
	}

	public void destroy() {

	}

}

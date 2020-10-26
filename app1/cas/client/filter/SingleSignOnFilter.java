package filters;

import java.io.IOException;

import cas.client.Constants;
import database.DB;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import domains.User;

@WebFilter(filterName="F3",urlPatterns="/*")
public class LocalLoginFilter implements Filter {
	public LocalLoginFilter() {
		super();
		// TODO Auto-generated constructor stub
	}
	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	protected void loginFail(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().println("登录失败");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected boolean doLogin(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			HttpSession session = request.getSession();
			String userId = (String) session
					.getAttribute(Constants.LOCAL_USER_ID);
			User user=DB.getUser(userId);
			session.setAttribute("user", user);
			return true;
		} catch (Exception e) {
			return false;
		}

	}
	
	final public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpSession session=((HttpServletRequest)request).getSession();
		Boolean CAS_LOGIN=(Boolean) session.getAttribute(Constants.LOCAL_LOGINED);
		if(CAS_LOGIN!=null&&CAS_LOGIN)
			chain.doFilter(request, response);
		else
		{
			if(doLogin((HttpServletRequest)request,(HttpServletResponse)response))
			{
				session.setAttribute(Constants.LOCAL_LOGINED, true);
				chain.doFilter(request, response);
			}else{
				loginFail((HttpServletRequest)request,(HttpServletResponse)response);
			}
		}
	}



}

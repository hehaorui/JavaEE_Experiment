package cas.client.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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

import cas.client.Constants;

@WebFilter(filterName="F2",urlPatterns="/*")
public class SingleSignOnFilter implements Filter {
	private String CAS_LOGIN_URL="http://localhost:8080/cas/login.do";
	private String CAS_USER_URL="http://localhost:8080/cas/getUser.do";

	public SingleSignOnFilter() {
		// TODO Auto-generated constructor stub
	}
	public void destroy() {
		// TODO Auto-generated method stub
	}
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();
		System.out.println("SingleSignOnFilter sessionID："+session.getId());
		String CAS_ST = httpRequest.getParameter(Constants.CAS_ST);
		if (CAS_ST != null) {		//由controller产生的重定向，可直接转到资源页
			// CAS返回ST
			session.setAttribute(Constants.LOCAL_ST, CAS_ST);
			String LOCAL_SERVICE = httpRequest
					.getParameter(Constants.LOCAL_SERVICE);
			if (LOCAL_SERVICE != null && !LOCAL_SERVICE.equals("")) {
				if(LOCAL_SERVICE.endsWith("view.do")) {
					httpResponse.sendRedirect(LOCAL_SERVICE);
				}
				else {
					httpResponse.sendRedirect(LOCAL_SERVICE+"view.do");
				}
			}
			else
				httpResponse.sendRedirect(httpRequest.getContextPath());
			return;
		} else {
			String LOCAL_ST = (String) session.getAttribute(Constants.LOCAL_ST);
			if (LOCAL_ST == null) {
				// 没有在本应用或其他应用页登陆过，跳转到CAS登录
				httpResponse.sendRedirect(CAS_LOGIN_URL + "?"
						+ Constants.LOCAL_SERVICE + "="
						+ httpRequest.getRequestURL());
			} else {	//在其他应用登陆过
				System.out.println("从session获取值LOCAL_USER_ID");
				String LOCAL_USER_ID = (String) session
						.getAttribute(Constants.LOCAL_USER_ID);
				if (LOCAL_USER_ID == null) {
					// 获取LOCAL_USER_ID
					System.out.println("LOCAL_USER_ID为空");
					try {
						URL url = new URL(CAS_USER_URL + "?" + Constants.CAS_ST
								+ "=" + LOCAL_ST + "&host="
								+ httpRequest.getServerName() + "&app="
								+ httpRequest.getContextPath() + "&"
								+ Constants.LOCAL_SERVICE + "="
								+ httpRequest.getRequestURL() + "&sessionId="
								+ session.getId());
						System.out.println("这是一个url："+url);
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(url.openStream()));
						LOCAL_USER_ID = reader.readLine();
						reader.close();
						session.setAttribute(Constants.LOCAL_USER_ID,
								LOCAL_USER_ID);
					} catch (Exception e) {
						e.printStackTrace();
						// 跳转到CAS登录
						httpResponse.sendRedirect(CAS_LOGIN_URL + "?"
								+ Constants.LOCAL_SERVICE + "="
								+ httpRequest.getRequestURL());
					}

				}
				chain.doFilter(request, response);

			}
		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
//		CAS_LOGIN_URL = fConfig.getInitParameter(Constants.CAS_LOGIN_URL);
//		CAS_USER_URL = fConfig.getInitParameter(Constants.CAS_USER_URL);
	}

}

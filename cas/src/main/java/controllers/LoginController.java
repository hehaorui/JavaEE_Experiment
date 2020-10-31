package controllers;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cas.server.Constants;
import database.DB;
import domains.ServiceTicket;
import domains.TicketGrantingService;
import domains.User;
@WebServlet(value="/login.do")
public class LoginController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * 访问页面
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String LOCAL_SERVICE=request.getParameter("LOCAL_SERVICE");
		// 先检查有没有cookie，有说明在其他系统登陆过，需要重定向到cas-server重新签发一个ST
		Cookie cookies[] = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(Constants.CAS_TGS)) {
					// 从cookie中获取TGS
					String CAS_TGS = cookie.getValue();	
					// 根据TGS在数据库中寻找对应的ST，重新签发ST
					TicketGrantingService TGS = DB.findTicketGrantingServicebyTGS(CAS_TGS);
					List<ServiceTicket> l = DB.findServiceTicketbyTGS(TGS);
					if(l.size()!=0) {
						ServiceTicket ST=l.get(0);
						DB.deleteServiceTicket(ST.getSt()); // 删除使用过一次的ST
						Random random=new Random();
						String CAS_ST=CAS_TGS+System.currentTimeMillis()+String.valueOf(random.nextInt(100)); // 重新生成一个ST
						DB.addServiceTicket(TGS.getUser(),CAS_ST,CAS_TGS); // 生成ST，ST对应TGS
						
						if (TGS != null) {
							response.sendRedirect(LOCAL_SERVICE + "?"
									+ Constants.CAS_ST + "=" + CAS_ST + "&"
									+ Constants.LOCAL_SERVICE + "=" + LOCAL_SERVICE);
							return;
						}
					}
				}
				else {
					break;
				}
			}
		}
		// 如果没有cookie，说明没有登陆过，转发至登陆界面
		request.setAttribute(Constants.LOCAL_SERVICE, LOCAL_SERVICE);
		request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id=request.getParameter("id");
		String pwd=request.getParameter("pwd");
		String LOCAL_SERVICE=request.getParameter("LOCAL_SERVICE");
		User user =DB.findUser(id,pwd); // 在数据库寻找相应的用户信息
		HttpSession session=request.getSession(); // 生成一个session
		// 如果输入的用户名密码正确，则cas-server生成TGS。TGS再生成ST，再重定向传回ST和cookie（TGS）
		if (user != null) {
			session.setAttribute("user", user); // session 里面存储用户对象
			String CAS_TGS = user.getId() + System.nanoTime(); // cas-server生成TGS
			Cookie cookie = new Cookie(Constants.CAS_TGS, CAS_TGS); // cookie
			cookie.setMaxAge(-1); // 浏览器关闭，cookie就会失效
			response.addCookie(cookie); 
			DB.addTicketGrantingService(user,CAS_TGS);
			Random random=new Random();
			String CAS_ST=CAS_TGS+System.currentTimeMillis()+String.valueOf(random.nextInt(100));
			DB.addServiceTicket(user,CAS_ST,CAS_TGS); // 生成ST，ST对应TGS
			
			// 如果从 app1/app2 本地登录
			if (LOCAL_SERVICE != null && !LOCAL_SERVICE.equals("")) {
				response.sendRedirect(LOCAL_SERVICE + "?"
						+ Constants.CAS_ST + "=" + CAS_ST + "&"
						+ Constants.LOCAL_SERVICE + "=" + LOCAL_SERVICE);
			} 
			// 如果没有设定来自域，重定向到/cas/main.do
			else 
				response.sendRedirect(request.getContextPath()+"/main.do");
		} else { 
			// 如果用户名密码输入错误，则重新到登陆界面
			response.sendRedirect(request.getContextPath());
		}
	}

}

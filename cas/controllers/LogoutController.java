package controllers;


import java.io.IOException;
import java.util.List;

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

@WebServlet(value="/logout.do")
public class LogoutController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession();
		session.invalidate(); // session失效
		Cookie cookies[] = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(Constants.CAS_TGS)) {
					String CAS_TGS = cookie.getValue();
					// 通过TGS寻找生成的ST，删除数据库内的存储
					List<ServiceTicket> l = DB.findServiceTicketbyTGS(DB.findTicketGrantingServicebyTGS(CAS_TGS));
					cookie.setMaxAge(0); // 通过修改cookie的生命周期删除cookie
					response.addCookie(cookie);
					for(int i=0;i<l.size();i++) {
						DB.deleteSessionStorage(l.get(i).getSt());
						DB.deleteServiceTicket(l.get(i).getSt());
					}
					DB.deleteTicketGrantingService(CAS_TGS);
				}
			}
		}
		// 注销登录后，重新回到登陆界面
		response.sendRedirect(request.getContextPath()+"/login.do");
	}

}

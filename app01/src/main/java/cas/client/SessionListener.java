package cas.client;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

//监听session的创建，协助完成session的销毁
@WebListener
public class SessionListener implements HttpSessionListener {

    public SessionListener() {
        // TODO Auto-generated constructor stub
    }

    public void sessionCreated(HttpSessionEvent event) {
    	HttpSession session=event.getSession();
    	SessionMap.put(session);
    	session.setMaxInactiveInterval(1);
        System.out.printf("id为%s的会话已经创建\n", session.getId());
    }

    public void sessionDestroyed(HttpSessionEvent event) {
    	 HttpSession session=event.getSession();
    	 System.out.printf("id为%s的会话已经销毁\n", session.getId());
    }
	
}

package cas.client;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Application Lifecycle Listener implementation class SessionListener
 *
 */
@WebListener
public class SessionListener implements HttpSessionListener {//实现对会话的创建与销毁的监听

    /**
     * Default constructor. 
     */
    public SessionListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent event) {
    	HttpSession session=event.getSession();
    	SessionMap.put(session);
    	session.setMaxInactiveInterval(1);
        System.out.printf("id为%s的会话已经创建\n", session.getId());
    }

	/**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent event) {
    	 HttpSession session=event.getSession();
    	 System.out.printf("id为%s的会话已经销毁\n", session.getId());
    }
	
}

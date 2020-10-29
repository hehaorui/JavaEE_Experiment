package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import domains.Mapping;
import domains.ServiceTicket;
import domains.SessionStorage;
import domains.TicketGrantingService;
import domains.User;

public class DB {
	// 存储服务票据ST对象
	private static Set<ServiceTicket> serviceTickets = new HashSet<>();
	// 存储票据授权服务TGS对象
	private static Set<TicketGrantingService> ticketGrantingServices = new HashSet<>();
	// 存储用户对象
	private static Set<User> users = new HashSet<>();
	// 存储session
	private static Set<SessionStorage> sessionStorages = new HashSet<>();
	// 存储用户、作用域相关的映射表
	private static Set<Mapping> mappings = new HashSet<>();
	// 存储TGS和ST的对应关系
	private static Map<TicketGrantingService,ServiceTicket> t_s = new HashMap<>();
	
	static {
		User u1=addUser("01", "0");
		User u2=addUser("02", "0");
		addMapping(1L,u1,"app1u1","localhost","/app1");
		addMapping(2L,u1,"app2u1","localhost","/app2");
		
		addMapping(1L,u2,"app1u2","localhost","/app1");
		addMapping(2L,u2,"app2u2","localhost","/app2");

	}

	// 添加用户
	public static User addUser(String id, String pwd) {
		User u = new User();
		u.setId(id);
		u.setPwd(pwd);
		users.add(u);
		return u;
	}

	
	// 根据服务票据ST内容查询服务票据ST对象
	public static ServiceTicket findServiceTicketbySt(String st) {
		for (ServiceTicket s : serviceTickets) {
			if (s.getSt().equals(st)) {
				return s;
			}
		}
		return null;
	}
	
	// 根据票据授权服务TGS内容查找票据授权服务TGS对象
	public static TicketGrantingService findTicketGrantingServicebyTGS(String TGS) {
		for (TicketGrantingService t : ticketGrantingServices) {
			if (t.getTGS().equals(TGS)) {
				return t;
			}
		}
		return null;
	}
	
	// 根据票据授权服务对象TGS寻找服务票据ST
	public static List<ServiceTicket> findServiceTicketbyTGS(TicketGrantingService TGS) {
		 List<ServiceTicket> l=new ArrayList<ServiceTicket>();
		for (Entry<TicketGrantingService, ServiceTicket> entry : t_s.entrySet()) {
			if (entry.getKey().equals(TGS)) {
				l.add(entry.getValue());
			}
		}
		return l;
	}

	// 根据用户的ID和密码寻找用户
	public static User findUser(String id, String pwd) {
		for (User u : users) {
			if (u.getId().equals(id) && u.getPwd().equals(pwd)) {
				return u;
			}
		}
		return null;
	}

	// 添加ST对象
	public static void addServiceTicket(User user, String st,String TGS) {
		ServiceTicket serviceTicket = new ServiceTicket();
		TicketGrantingService ticketGrantingService = new TicketGrantingService();
		serviceTicket.setUser(user);
		serviceTicket.setSt(st);
		ticketGrantingService.setUser(user);
		ticketGrantingService.setTGS(TGS);
		t_s.put(ticketGrantingService,serviceTicket); // 将TGS和ST相互对应
		serviceTickets.add(serviceTicket);
	}
	
	// 添加票据授权服务TGS对象
	public static void addTicketGrantingService(User user, String TGS) {
		TicketGrantingService ticketGrantingService = new TicketGrantingService();
		ticketGrantingService.setUser(user);
		ticketGrantingService.setTGS(TGS);
		ticketGrantingServices.add(ticketGrantingService);
	}

	// 存储session
	public static void addSessionStorage(String LOCAL_SERVICE, String CAS_ST, String sessionId) {
		SessionStorage sessionStorage = new SessionStorage();
		sessionStorage.setLocalService(LOCAL_SERVICE);
		sessionStorage.setSt(CAS_ST);
		sessionStorage.setSessionId(sessionId);
		sessionStorages.add(sessionStorage);

	}

	// 添加映射表
	public static void addMapping(Long id,User casUser,String localUser, String host,String app)
	{
		Mapping m=new Mapping();
		m.setId(id);
		m.setCasUser(casUser);
		m.setLocalUser(localUser);
		m.setHost(host);
		m.setApp(app);
		mappings.add(m);
	}

	// 根据host,app,user三个属性寻找对应的映射表
	public static Mapping findMappingByHostAndAppAndCasUser(String host, String app, User user) {
		for (Mapping m : mappings) {
			if (m.getHost().equals(host) && m.getApp().equals(app) && m.getCasUser().equals(user)) {
				return m;
			}
		}
		return null;
	}

	// 根据票据服务ST寻找对应的session存储
	public static List<SessionStorage> findSessionStorage(String CAS_ST) {
		List<SessionStorage> list = new ArrayList<>();
		for (SessionStorage s : sessionStorages) {
			if (s.getSt().equals(CAS_ST)) {
				list.add(s);
			}
		}
		return list;
	}

	// 根据内容寻找ST对象
	public static List<ServiceTicket> findServiceTicket(String CAS_ST) {
		List<ServiceTicket> list = new ArrayList<>();
		for (ServiceTicket s : serviceTickets) {
			if (s.getSt().equals(CAS_ST)) {
				list.add(s);
			}
		}
		return list;
	}
	
	// 根据内容寻找TGS对象
	public static List<TicketGrantingService> findTicketGrantingService(String CAS_TGS) {
		List<TicketGrantingService> list = new ArrayList<>();
		for (TicketGrantingService t : ticketGrantingServices) {
			if (t.getTGS().equals(CAS_TGS)) {
				list.add(t);
			}
		}
		return list;
	}
	
	// 删除session存储
	public static void deleteSessionStorage(String CAS_ST) {
		sessionStorages.removeAll(findSessionStorage(CAS_ST));

	}
	

	// 删除票据服务对象ST，并清空TGS与之对应的Map
	public static void deleteServiceTicket(String CAS_ST) {
		serviceTickets.removeAll(findServiceTicket(CAS_ST));
		t_s.clear();
	}
	
	// 删除票据授权服务对象TGS，并清空ST与之对应的Map
	public static void deleteTicketGrantingService(String CAS_TGS) {
		ticketGrantingServices.removeAll(findTicketGrantingService(CAS_TGS));
		t_s.clear();
	}

}

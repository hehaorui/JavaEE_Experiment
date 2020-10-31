package database;

import java.util.HashSet;
import java.util.Set;

import domains.User;

public class DB {//模拟数据库
	private static Set<User> users=new HashSet<User>();
	static {
		addUser("app01u1","001","App01_张三","xxx",10);
		addUser("app01u2","001","App01_李四","xxx",10);
	}
	
	public static void addUser(String id,String pwd,String name,String email,int age)
	{
		User u=new User();
		u.setAge(age);
		u.setEmail(email);
		u.setId(id);
		u.setName(name);
		u.setPwd(pwd);
		users.add(u);
	}

	public static User getUser(String userId) {
		for(User u:users)
		{
			if(u.getId().equals(userId))
				return u;
		}
		return null;
	}
}

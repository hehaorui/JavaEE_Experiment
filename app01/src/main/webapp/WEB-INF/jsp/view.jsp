<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>App01</title>
</head>
<body>
<p>这是App01</p>
<br>id：${user.id }
<br>name：${user.name }
<br>age：${user.age }
<br>email：${user.email }
<p><a href="http://localhost:8080/cas/logout.do">注销</a></p>
</body>
</html>
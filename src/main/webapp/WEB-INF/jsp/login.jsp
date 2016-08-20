<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>P2016 Login</title>
</head>
<body>
    <form action="<%=request.getContextPath()%>/login" method="post" class="form-horizontal">
        <div>
            <label for="email"></label>
            <input type="text" id="email" name="email" placeholder="Enter Email" required>
        </div>
        <div>
            <label for="password"><i class="fa fa-lock"></i></label>
            <input type="password" id="password" name="password" placeholder="Enter Password" required>
        </div>

        <div>
            <input type="submit" value="Log in">
        </div>
    </form>
</body>
</html>

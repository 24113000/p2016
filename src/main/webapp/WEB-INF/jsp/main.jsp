<%--
  Created by IntelliJ IDEA.
  User: Sergii
  Date: 18.08.2016
  Time: 20:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>MAIN PAGE</title>
        <script src="<%=request.getContextPath()%>/javascript/lib/angular.js"></script>

        <script src="<%=request.getContextPath()%>/javascript/scripts/main/mainController.js"></script>
        <script src="<%=request.getContextPath()%>/javascript/scripts/main/mainServices.js"></script>

        <script type="application/javascript">
            angular.module('FileBrowserApp').constant('contextNameConst', '<%=request.getContextPath()%>');
        </script>
    </head>
    <body>
        <h1>MAIN PAGE</h1>
        <div ng-app="FileBrowserApp">
            <div ng-controller="fileBrowserController as fileCtrl">
                <button style="height: 40px; width: 60px" ng-click="fileCtrl.getRootFolderChildren()"></button>
            </div>
        </div>
    </body>
</html>

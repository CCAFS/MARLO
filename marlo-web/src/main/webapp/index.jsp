<html>
<head>
    <title>JSP Redirect</title>
    </head>
    <body>
       <%
          String redirectURL = "http://clarisatest.ciat.cgiar.org/swagger/home.html";
          response.sendRedirect(redirectURL);
        %>
    </body>
</html>
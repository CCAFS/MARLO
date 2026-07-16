<html>
<head>
    <title>MARLO</title>
</head>
<body>
   <%
      // Redirect to MARLO login (not to CLARISA)
      String redirectURL = request.getContextPath() + "/login.do";
      response.sendRedirect(redirectURL);
   %>
</body>
</html>
<html>
<head>
    <title>MARLO</title>
</head>
<body>
   <%
      // Redirigir al login de MARLO (no a CLARISA)
      String redirectURL = request.getContextPath() + "/login.do";
      response.sendRedirect(redirectURL);
   %>
</body>
</html>
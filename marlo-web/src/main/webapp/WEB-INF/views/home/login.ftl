[#ftl]
[#assign globalLibs = ["jquery", "datatables"] /]


[#include "/WEB-INF/global/header.ftl" /]

<form id="login" name="login" method="POST" action="marlo-web/login.do" >

  <h6><label for="user.email" class="editable">Email:<span class="red">*</span></label></h6>
  <input type="text" id="user.email" name="user.email" value="" class=" required">

  <h6><label for="user.password" class="editable">Password:<span class="red">*</span></label></h6>
  <input type="password" id="user.password" name="user.password" value="" class=" required">

  <input type="submit" id="login_login" name="login" value="Login">
</form>

[#include "/WEB-INF/global/footer.ftl" /]
[#ftl]
You are logged in. 
<br>
<p> Users Online : ${online} </p>

<h3> users list </h3>
[#assign users = action.getUsersOnline()]
[#list users as us]
<p>${(us.firstName)!'null'}</p>
[/#list]

<a id="userLogOut" href="[@s.url action="logout" namespace="/" /]">[@s.text name="header.logout" /]</a>
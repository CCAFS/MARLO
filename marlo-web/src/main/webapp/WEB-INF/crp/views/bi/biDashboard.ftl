[#ftl]
[#assign title = "MARLO BI" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs", "malihu-custom-scrollbar-plugin"] /]
[#assign customJS = ["" ] /]
[#assign customCSS = [
  "${baseUrlMedia}/css/bi/biDashboard.css"
  ] 
/]
[#assign currentSection = "bi" /] 


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
<center>
<iframe width="1000" height="600" src="https://app.powerbi.com/view?r=eyJrIjoiZGRjMzhkM2ItZjA4Ny00YjBkLWI0NjYtYjI1YjRiMjBhYzg0IiwidCI6IjZhZmEwZTAwLWZhMTQtNDBiNy04YTJlLTIyYTdmOGMzNTdkNSIsImMiOjh9" frameborder="0" allowFullScreen="true"></iframe>
</center>

[#include "/WEB-INF/global/pages/footer.ftl"]

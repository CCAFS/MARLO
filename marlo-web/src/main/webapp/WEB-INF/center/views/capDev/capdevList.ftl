[#ftl]

[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customCSS = ["${baseUrl}/global/css/customDataTable.css", 
					  "${baseUrlMedia}/css/capDev/capacityDevelopment.css"] /]


[#assign customJS = [
 	"${baseUrlMedia}/js/capDev/capdevList.js"] 
/]

[#assign currentSection = "capdev" /]


[#assign breadCrumb = [
  {"label":"capdevList", "nameSpace":"/capdev", "action":"${(centerSession)!}/capdev"}
]/]


[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]
[#import "/WEB-INF/center/macros/capdevListTemplate.ftl" as capdevList /]






	<div class="container">
		<div class="row titleContainer">
			<div class="col-md-12">
				<p>Capacity Development Tracking Tool</p>
			</div>
			
		
		</div>

		<div class="row">
			<div class="helpMessage infoText col-md-12 capdevinfo">
				<img class="col-md-2" src="${baseUrl}/global/images/icon-help.png" />
   				  [@s.text name="capdev.help.list"][/@s.text] 
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 titleContainer">
				Latest CAPDEV Interventions
			</div>
		</div>
		
		<div>[@capdevList.capdevList capdevs=capDevs canValidate=true canEdit=editable namespace="/capdev" defaultAction="${(centerSession)!}/detailCapdev" /]</div>
			
		
	 </div>



[#include "/WEB-INF/center/pages/footer.ftl"]
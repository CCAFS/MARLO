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
	  <div class="title-container">
	   <h3>Capacity Development Tracking Tool</h3>
	   <div class="download-template-button">
  	   <a href="[@s.url action='${centerSession}/downloadFile' /]" >
          <button type="button" class="capdevButtons downloadButtonHome" title="Download template to upload the list of participants">
            [@s.text name="capdev.downloadTemplate" /]
          </button>
        </a>
      </div>
	  </div>

		<div class="row">
			<div class="helpMessage infoText col-md-12 capdevinfo">
				<img class="col-md-2" src="${baseUrl}/global/images/icon-help.png" />
   				  [@s.text name="capdev.help.list"][/@s.text] 
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 subTitle-container">
				<h4>Latest CAPDEV Interventions</h4>
				<hr>
			</div>
		</div>
		
		<div>[@capdevList.capdevList capdevs=capDevs canValidate=true canEdit=editable namespace="/capdev" defaultAction="${(centerSession)!}/detailCapdev" /]</div>
			
	 </div>

[#include "/WEB-INF/center/pages/footer.ftl"]
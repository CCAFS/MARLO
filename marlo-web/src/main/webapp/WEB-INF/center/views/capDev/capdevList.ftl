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

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/center/macros/capdevListTemplate.ftl" as capdevList /]

<div class="container">
  [#-- Help Text --]
  <div class="helpMessage infoText capdevinfo">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.png" />[@s.text name="capdev.help.list"][/@s.text] 
  </div>
  <br />
  
  <div class="title-container">
   <strong><h3 class="headTitle">Capacity Development Tracking Tool</h3></strong>
   <div class="download-template-button">
     <a href="[@s.url action='${centerSession}/downloadFile' /]" >
        <button type="button" class="capdevButtons downloadButtonHome" title="Download template to upload the list of participants">
          [@s.text name="capdev.downloadTemplate" /]
        </button>
      </a>
    </div>
  </div>

  <h3 class="sectionTitle">Latest CAPDEV Interventions</h3><hr />
  <div>[@capdevList.capdevList capdevs=capDevs canValidate=true canEdit=editable namespace="/capdev" defaultAction="${(centerSession)!}/detailCapdev" /]</div>
  
  <div class="row form-group">
    <div class="addButtons botones">
      <div class="buttons-content" >
        [#-- Group Item --]
        <a class="addButton" href="[@s.url namespace='/capdev' action='${centerSession}/addCapdev'][@s.param name='projectID']${projectID}[/@s.param][@s.param name='capdevID']${capdevID}[/@s.param][@s.param name='capdevCategory']2[/@s.param] [@s.param name='edit' value="true" /][/@s.url]" title="Add a capacity development intervention destinated  at a group of people ">[@s.text name="capdev.addgroupItem" /]</a>
        [#-- Individual Item --]
        <a class="addButton" href="[@s.url namespace='/capdev' action='${centerSession}/addCapdev' ][@s.param name='projectID']${projectID}[/@s.param][@s.param name='capdevID']${capdevID}[/@s.param][@s.param name='capdevCategory']1[/@s.param][@s.param name='edit' value="true" /][/@s.url]" title="Add a capacity development intervention destinated  at one person">[@s.text name="capdev.addindividualItem" /]</a>
      </div>
    </div>
  </div>


  <div class="row form-group conventionContainer">
    <div class="itemName">
      <div class="col-md-1"> Annexes:</div>
      <div class="col-md-2">
        <img src="${baseUrl}/center/images/capdev/participants.png" class="capDevIconConvention" />
        <div>Lista of participants</div>
      </div>
      <div class="col-md-2">
        <img src="${baseUrl}/center/images/capdev/deliverable.png" class="capDevIconConvention" />
        <div>Supporting documents</div>
      </div>
      <div class="col-md-7">
        If an icon is highlighted, it means that the capacity development intervention has any of those: list of participants or supporting documents, or has both.
      </div>
    </div>
  </div>
		
</div>

[#include "/WEB-INF/global/pages/footer.ftl"]
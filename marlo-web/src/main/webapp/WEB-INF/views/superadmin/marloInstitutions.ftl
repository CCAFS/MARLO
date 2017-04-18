[#ftl]
[#assign title = "MARLO Admin" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = [ "${baseUrl}/js/superadmin/marloInstitutions.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/superadmin/superadmin.css","${baseUrl}/css/global/customDataTable.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "institutions" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"institutions", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        
          [#-- Requested Institutions--]
          <h4 class="sectionTitle">[@s.text name="Partner Request" /]</h4>
          [@partnersList partners=partners  canEdit=editable namespace="/marloInstitutions" defaultAction="${(crpSession)!}/marloInstitutions"/]
          
          
         
          
        [/@s.form]
      </div>
    </div>
  </div>
</section>



[#include "/WEB-INF/global/pages/footer.ftl" /]


[#macro partnersList partners={} canEdit=false  namespace="/" defaultAction=""]
  <table class="partnerList" id="partners">
    <thead>
      <tr class="subHeader">
        <th id="partnerName">[@s.text name="Partner Name" /]</th>
        <th id="partnerType" >[@s.text name="Type" /]</th>
        <th id="isHqPartner">[@s.text name="Is HQ?" /]</th>
        <th id="requestedBy" >[@s.text name="Requested By" /]</th>
        <th id="action">[@s.text name="Action" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if partners?has_content]
      [#list partners as partner]
        
        <tr>
        [#-- partner name --]
        <td class="deliverableId">
            ${partner.locElement.name}
        </td>
          [#-- partner type --]
          <td class="left">
            ${partner.institutionType.name}
          </td>
          [#-- is HQ? --]
          <td >
            [#if institution.name?has_content]
              <span class="icon-20 icon-check" title="Complete"></span>
            [#else]
              <span class="icon-20 icon-uncheck" title=""></span> 
            [/#if]
          </td>
          [#-- Requested by --]
          <td class="text-center">
          ${(partner.requestedBy)!'none'}
          </td>
          [#-- Action --]
          <td class="fair"> 
          [#if deliverable.requeriedFair()]
            <span class="[#attempt][#if action.isF(deliverable.id)??][#if action.isF(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">F</span>
            <span class="[#attempt][#if action.isA(deliverable.id)??][#if action.isA(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">A</span>
            <span class="[#attempt][#if action.isI(deliverable.id)??][#if action.isI(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">I</span>
            <span class="[#attempt][#if action.isR(deliverable.id)??][#if action.isR(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">R</span>
          [#else]
            <p class="message">Not applicable</p>
          [/#if]
          </td>
        </tr>  
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]


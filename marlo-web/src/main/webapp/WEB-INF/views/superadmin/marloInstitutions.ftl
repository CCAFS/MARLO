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
          [#assign partners = [
            {"id":1,"partnerName":"test1", "institutionType":{"name":"CGIAR center"}, "institution":{"name":"yes"},"locElement":{"name":"colombia"},"createdBy":{"composedName":"Carlos Rios"}},
            {"id":2,"partnerName":"test2", "institutionType":{"name":"CGIAR center"}, "institution":{"name":""},"locElement":{"name":"colombia"},"createdBy":{"composedName":"Carlos Rios"}}
          ]/]
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
        <th id="partnerCountry">[@s.text name="Country" /]</th>
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
              ${partner.partnerName}
          </td>
          [#-- partner type --]
          <td class="left">
            ${partner.institutionType.name}
          </td>
          [#-- is HQ? --]
          <td class="text-center">
            [#if partner.institution.name?has_content]
              <span class="icon-20 icon-check" title="Complete"></span>
            [#else]
              <span class="icon-20 icon-uncheck" title=""></span> 
            [/#if]
          </td>
          [#-- Country --]
          <td class="text-center">
            ${partner.locElement.name}
          </td>
          [#-- Requested by --]
          <td class="text-center">
          ${(partner.createdBy.composedName)!'none'}
          </td>
          [#-- Action --]
          <td class="">
            <a href="[@s.url namespace="" action="${(crpSession)!}/addPartner"][@s.param name='requestID']${partner.id?c}[/@s.param][/@s.url]">
              <span class="text-center col-md-6 glyphicon glyphicon-ok" style="cursor:pointer;"> Accept</span>
            </a>
            <a href="[@s.url namespace="" action="${(crpSession)!}/removePartner"][@s.param name='requestID']${partner.id?c}[/@s.param][/@s.url]">
              <span class="text-center col-md-6 glyphicon glyphicon-remove" style="cursor:pointer;"> Reject</span>
            </a>
          </td>
        </tr>  
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]


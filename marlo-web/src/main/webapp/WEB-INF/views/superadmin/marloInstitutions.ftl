[#ftl]
[#assign title = "MARLO Admin" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs", "flat-flags"] /]
[#assign customJS = [ "${baseUrlMedia}/js/superadmin/marloInstitutions.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/superadmin/superadmin.css","${baseUrlMedia}/css/global/customDataTable.css" ] /]
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
          <h4 class="sectionTitle">[@s.text name="marloRequestInstitution.title" /]</h4>  
          [@partnersList partners=partners  canEdit=editable namespace="/marloInstitutions" defaultAction="${(crpSession)!}/marloInstitutions"/]
          
        [/@s.form]
      </div>
    </div>
  </div>
</section>

<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="loading" style="display:none"></div>
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Reject the request</h4>
      </div>
      <div class="modal-body">
        <div class="requestInfo">
        
        </div>
        
        <div class="form-group">
          [@customForm.textArea name="marloRequestInstitution.justification" required=true className="limitWords-30" /]
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-danger rejectButton"> <span class="glyphicon glyphicon-remove"></span> Reject</button>
      </div>
    </div>
  </div>
</div>


[#include "/WEB-INF/global/pages/footer.ftl" /]


[#macro partnersList partners={} canEdit=false  namespace="/" defaultAction=""]
  <ul class="list-group">
    [#if partners?has_content]
      [#list partners as partner]
        <li class="list-group-item partnerRequestItem">
          <div class="loading" style="display:none"></div>
          [#-- Partner name --]
          <div class="requestInfo">
            <div class="form-group">
               <h4 style="font-family: 'Open Sans';">${partner.partnerInfo}</h4>
               <span class="hiddenTitle" style="display:none">${partner.partnerName}</span>
               [#if partner.webPage??]
                <i>(<a href="${partner.webPage}">${partner.webPage}</a>)</i>
               [/#if]
               <hr />
            </div>
            
            <div class="form-group">
              [#-- Partner type --]
              <p><strong>[@s.text name="Type" /]:</strong> <i>${partner.institutionType.name}</i></p>
              [#-- Country --]
              <p><strong>[@s.text name="Country" /]:</strong> <i class="flag-sm flag-sm-${(partner.countryISO?upper_case)!}"></i> <i>${partner.countryInfo}</i></p>
              [#-- Requested by --]
              <p><strong>[@s.text name="Requested By" /]:</strong> <i>${(partner.createdBy.composedName?html)!'none'}</i></p>
            </div>
            
            <div class="form-group sameness" style="display:none">
              <strong>Similar institutions in MARLO:</strong>
              <ul>
              </ul>
            </div>
          </div>
          
          [#-- Action --]
          <div class="btn-group pull-right" role="group" aria-label="..."">
            [#-- Accept --]
            <a class="btn btn-success btn-sm" href="[@s.url namespace="" action="superadmin/addPartner"][@s.param name='requestID']${partner.id?c}[/@s.param][/@s.url]">
              <span class="glyphicon glyphicon-ok"></span> Accept
            </a>
            [#-- Reject --]
            <a class="btn btn-danger btn-sm rejectRequest partnerRequestId-${partner.id}" href="[@s.url namespace="" action="superadmin/removePartner"][@s.param name='requestID']${partner.id?c}[/@s.param][/@s.url]">
               <span class="glyphicon glyphicon-remove"></span> Reject
            </a>
          </div>
          <div class="clearfix"></div>
        </li>
      [/#list]
    [#else]
      <div class="text-center">
        No partner requested yet.
      </div>
    [/#if]
  </ul>
  
[/#macro]



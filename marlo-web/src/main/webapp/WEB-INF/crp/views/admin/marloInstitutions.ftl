[#ftl]
[#assign title = "MARLO Institutions" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "flat-flags", "google-diff-match-patch", "jquery-pretty-text-diff", "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [ 
  "${baseUrl}/global/js/superadmin/marloInstitutions.js"
  ] 
/]
[#assign customCSS = [ 
  "${baseUrl}/global/css/superadmin/superadmin.css",
  "${baseUrl}/global/css/superadmin/marloInstitutions.css"
  ]
 /]
[#assign currentSection = "admin" /]
[#assign currentStage = "institutions" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"institutions", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/institutionRequestMacro.ftl" as institutionRequest /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        <br />
        <div class="text-right">
          <a class="btn btn btn-default" data-toggle="modal" data-target="#allInstitutions">
            <span class="glyphicon glyphicon-list-alt"></span>  All Institutions & location offices
          </a>
        </div>
      
        [#-- Requested Institutions--]
        <h4 class="sectionTitle">[@s.text name="marloRequestInstitution.title" /]</h4>  
        [@institutionRequest.partnersList partners=partners  canEdit=editable namespace="/marloInstitutions" defaultAction="${(crpSession)!}/marloInstitutions"/]
        
        [#-- Requested Office Locations--]
        <h4 class="sectionTitle">Request Country office(s):</h4>
        [@institutionRequest.officesRequest partners=countryOfficesList  canEdit=editable namespace="/marloInstitutions" defaultAction="${(crpSession)!}/marloInstitutions"/]
        
      </div>
    </div>
  </div>
</section>

[#-- Reject the request MODAL --]
[#include "/WEB-INF/global/macros/rejectInstitutionPopup.ftl" /]

[#-- All institutions MODAL --]
[#include "/WEB-INF/global/macros/allInstitutionsPopup.ftl" /]


[#include "/WEB-INF/crp/pages/footer.ftl" /]
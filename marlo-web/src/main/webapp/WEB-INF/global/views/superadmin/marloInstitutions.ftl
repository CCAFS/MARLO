[#ftl]
[#assign title = "MARLO Admin" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = [ "flat-flags", "google-diff-match-patch", "jquery-pretty-text-diff", "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [ "${baseUrl}/global/js/superadmin/marloInstitutions.js" ] /]
[#assign customCSS = [ 
  "${baseUrl}/global/css/superadmin/superadmin.css",
  "${baseUrl}/global/css/superadmin/marloInstitutions.css"
  ] 
/]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "institutions" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"institutions", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/crp/pages/header.ftl" /]
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
        [#include "/WEB-INF/global/views/superadmin/menu-superadmin.ftl" /]
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
        [@partnersList partners=partners  canEdit=editable namespace="/marloInstitutions" defaultAction="${(crpSession)!}/marloInstitutions"/]
        
        [#-- Requested Office Locations--]
        <h4 class="sectionTitle">Request Country office(s):</h4>
        [@officesRequest partners=countryOffices  canEdit=editable namespace="/marloInstitutions" defaultAction="${(crpSession)!}/marloInstitutions"/]
      </div>
    </div>
  </div>
</section>

[#-- Reject the request MODAL --]
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


[#-- All institutions MODAL --]
<div class="modal fade" id="allInstitutions" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">All Institutions & location offices</h4>
      </div>
      <div class="modal-body">
        [#-- All institutions table --]
        <table id="allInstitutionsTable" class="display table table-striped table-hover" width="100%">
          <thead>
            <tr>
                <th>ID</th>
                <th>Acronym</th>
                <th>Name</th>
                <th>Type</th> 
            </tr>
          </thead> 
        </table>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>


[#include "/WEB-INF/crp/pages/footer.ftl" /]


[#macro partnersList partners={} canEdit=false  namespace="/" defaultAction=""]
  <ul class="list-group">
    [#if partners?has_content]
      [#list partners as partner]
        <li id="partnerRequestItem-${(partner.id)!}" class="list-group-item partnerRequestItem">
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
              [#-- Requested Source --]
              <p><strong>[@s.text name="Requested Source" /]:</strong> <i>${(partner.requestSource)}</i></p>
              [#-- Requested by --]
              <p><strong>[@s.text name="Requested By" /]:</strong> <i>${(partner.createdBy.composedName?html)!'none'}</i></p>
            </div>
            
          </div>
          
          <div class="form-group col-md-8 sameness" style="display:none">
            <br />
            <div class="grayBox">
              <strong>Similar institutions in MARLO:</strong>
              <ul></ul>
            </div>
          </div>
          
          [#-- Action --]
          <div class="btn-group pull-right" role="group" aria-label="..."">
            [#-- Edit --]
            <a class="btn btn-default btn-sm editRequest" href="#">
              <span class="glyphicon glyphicon-pencil"></span> Edit Request
            </a>
            [#-- Accept --]
            <a class="btn btn-success btn-sm" href="[@s.url namespace="" action="superadmin/addPartner"][@s.param name='requestID']${partner.id?c}[/@s.param][/@s.url]">
              <span class="glyphicon glyphicon-ok"></span> Accept
            </a>
            [#-- Reject --]
            <a class="btn btn-danger btn-sm rejectRequest partnerRequestId-${partner.id}" href="#">
               <span class="glyphicon glyphicon-remove"></span> Reject
            </a>
          </div>
          
          <div class="clearfix"></div>
          
          [#-- Edit Form --]
          <form class="editForm editForm-${(partner.id)!} simpleBox" style="display:none">
            <input type="hidden" name="requestID"  value="${(partner.id)!}"/>
            <div class="form-group row">
              <div class="col-md-3">
                <label for="">Acronym:</label>
                <input type="text" class="form-control input-sm" name="acronym" value="${(partner.acronym)!}" />
              </div>
              <div class="col-md-9">
                <label for="">Name: [@customForm.req required=true /]</label>
                <input type="text" class="form-control input-sm" name="name" value="${(partner.partnerName)!}" />
              </div>
            </div>
            <div class="form-group row">
              <div class="col-md-6">
                [@customForm.select name="type" value=partner.institutionType required=true label="" i18nkey="Institution Type" listName="institutionTypesList" keyFieldName="id"  displayFieldName="name" /]
              </div>
              <div class="col-md-6">
                [@customForm.select name="country" value="'${partner.locElement.isoAlpha2}'" required=true label="" i18nkey="Country ISO Code" listName="countriesList" keyFieldName="isoAlpha2"  displayFieldName="name" /]
              </div>
            </div>
            <div class="form-group">
              <label for="">Web Page</label>
              <input type="text" class="form-control input-sm" name="webPage" value="${(partner.webPage)!}" />
            </div>
            <hr />
            <div class="form-group">
              <label for="">Justification</label>
              <textarea class="form-control input-sm" name="modificationJustification" id="" cols="30" rows="10">${(partner.modificationJustification)!}</textarea>
            </div>
            <button class="saveButton">Save</button>
            <button class="cancelButton">Cancel</button>
          </form>
          
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

[#macro officesRequest partners={} canEdit=false  namespace="/" defaultAction=""]
  <ul class="list-group">
    [#if partners?has_content]
      [#list partners as partner]
        <li id="partnerRequestItem-${(partner.id)!}" class="list-group-item partnerRequestItem">
          <div class="loading" style="display:none"></div>
          
          [#-- Partner name --]
          <div class="requestInfo">
            <div class="form-group">
               <h4 style="font-family: 'Open Sans';">${partner.institution.composedName}</h4>
               <hr />
            </div>
            
            <div class="form-group">
              [#-- Country --]
              <p><strong>[@s.text name="Country" /]:</strong> <i class="flag-sm flag-sm-${(partner.countryISO?upper_case)!}"></i> <i>${partner.countryInfo}</i></p>
              [#-- Requested Source --]
              <p><strong>[@s.text name="Requested Source" /]:</strong> <i>${(partner.requestSource)}</i></p>
              [#-- Requested by --]
              <p><strong>[@s.text name="Requested By" /]:</strong> <i>${(partner.createdBy.composedName?html)!'none'}</i></p>
            </div>
            
          </div>
          
          [#-- Action --]
          <div class="btn-group pull-right" role="group" aria-label="..."">
            [#-- Accept --]
            <a class="btn btn-success btn-sm" href="[@s.url namespace="" action="superadmin/addPartner"][@s.param name='requestID']${partner.id?c}[/@s.param][/@s.url]">
              <span class="glyphicon glyphicon-ok"></span> Accept
            </a>
            [#-- Reject --]
            <a class="btn btn-danger btn-sm rejectRequest partnerRequestId-${partner.id}" href="#">
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



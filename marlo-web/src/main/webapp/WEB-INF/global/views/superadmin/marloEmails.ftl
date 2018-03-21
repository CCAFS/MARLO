[#ftl]
[#assign title = "MARLO Admin - Emails" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [ "${baseUrl}/global/js/superadmin/emails.js" ] /]
[#assign customCSS = [ 
  "${baseUrl}/global/css/superadmin/superadmin.css"
  ] 
/]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "emails" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"emails", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/crp/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]


<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/global/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
      
        <h4 class="sectionTitle">Emails on track</h4>
        <div id="" class="borderBox ">
         <div class="loading" style="display:none"></div>
         <table id="marloEmailsTable" class="display table table-striped table-hover" width="100%">
            <thead>
              <tr>
              [#--  
                <th class="id"></th>
                <th class="to"></th>
                <th class="cc"></th>
                <th class="bbc"></th>
                <th class="subject"></th>
                <th class="message"></th>
                <th class="tried"></th>
                <th class="date"></th>
                <th class="error"></th>
                <th class="succes"></th>
                <th class="fileName"></th>
              --]
                <th>ID</th>
                <th>Subject</th>
                <th>Error</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              [#if emails??]
              [#list emails as email]
                <tr id="emailrow-${email.id}">
                  <td>${email.id}</td>
                  <td>
                    <a href="#" class="" data-toggle="modal" data-target="#emailPopup-${email.id}">${email.subject}</a>
                  </td>
                  <td>
                    <a href="#" class="" data-toggle="modal" data-target="#emailPopup-${email.id}">${email.error}</a>
                  </td>
                  <td>${email.date}</td>
                </tr>
              [/#list]
              [/#if]
            </tbody>
          </table>
         <br />
         <div class="form-group">
            <button type="button" class="sendEmails btn btn-primary">Re-[@s.text name="form.buttons.sendEmails" /]</button>
         </div>
        </div>

      </div>
    </div>
  </div>
</section>

[#if emails??]
  [#list emails as email]
    [@emailPopupMacro email /]
  [/#list]
[/#if]

[#include "/WEB-INF/crp/pages/footer.ftl" /]


[#macro emailPopupMacro email ]
  <!-- Modal -->
  <div class="modal fade" id="emailPopup-${email.id}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog modal-lg" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title" id="myModalLabel">${email.subject}</h4>
        </div>
        <div class="modal-body">
          <div class="form-group row">
            <div class="col-md-6">
              <p><strong>TO: </strong>${(email.to)!}</p>
              <p><strong>CC: </strong>${(email.cc)!}</p>
              <p><strong>BBC: </strong>${(email.bbc)!}</p>
              <p><strong>Date: </strong>${(email.date)!}</p>
            </div>
            <div class="col-md-6">
              <p><strong>Error: </strong>${(email.error)!}</p>
              <p><strong>Times tried: </strong>${(email.tried)!}</p>
              <p><strong>File: </strong>${(email.fileName)!}</p>
            </div>
          </div>
          <hr />
          <div class="form-group" style="max-height: 500px;overflow-x: auto;">
            ${email.message}
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>
[/#macro]
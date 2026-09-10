[#ftl]
[#assign title = "MARLO Admin - Emails" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [ "${baseUrlCdn}/global/js/superadmin/emails.js?20260910" ] /]
[#assign customCSS = [ 
  "${baseUrlCdn}/global/css/superadmin/superadmin.css",
  "${baseUrlCdn}/global/css/superadmin/marloEmails.css?20260910"
  ] 
/]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "emails" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"emails", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
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
        <div class="emailsOnTrack borderBox">
          <div class="loading" style="display:none"></div>
          <div class="emailsTableHeader">
            <h5 class="emailsSubtitle">Emails not sent</h5>
            <div class="emails-search-wrap">
              <input type="text" id="marloEmailsSearch" class="form-control emails-search" placeholder="Search email..." />
              <div class="iconSearch emails-search-icon">
                <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
              </div>
            </div>
          </div>
          <table id="marloEmailsTable" class="table table-striped table-hover" width="100%">
            <thead>
              <tr>
                <th>ID</th>
                <th>Subject</th>
                <th>Error</th>
                <th>Date</th>
                <th>Message ID</th>
              </tr>
            </thead>
            <tbody>
              [#if emails??]
              [#list emails as email]
                [#-- data-search and data-order hold the plain text: DataTables reads the cell markup as
                     it is. Auto-escaping is on with an HTML output format, so no ?html here --]
                <tr id="emailrow-${email.id}">
                  <td>${email.id}</td>
                  <td data-search="${(email.subject)!""}" data-order="${(email.subject)!""}">
                    <a href="#" class="" data-toggle="modal" data-target="#emailPopup-${email.id}">${(email.subject)!""}</a>
                  </td>
                  <td data-search="${(email.error)!""}" data-order="${(email.error)!""}">
                    <a href="#" class="" data-toggle="modal" data-target="#emailPopup-${email.id}">${(email.error)!""}</a>
                  </td>
                  [#-- Rendered as yyyy-MM-dd HH:mm so that ordering the column by text orders it by date --]
                  <td class="emailDate">[#if email.date??]${email.date?string('yyyy-MM-dd HH:mm')}[/#if]</td>
                  <td class="emailMessageId">${(email.messageID)!}</td>
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

[#include "/WEB-INF/global/pages/footer.ftl" /]


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
              <p><strong>message ID: </strong>${(email.messageID)!}</p>
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
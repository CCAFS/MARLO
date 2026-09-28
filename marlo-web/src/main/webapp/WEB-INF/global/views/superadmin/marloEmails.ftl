[#ftl]
[#assign title = "MARLO Admin - Emails" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [ "${baseUrlCdn}/global/js/superadmin/emails.js?20260928-1" ] /]
[#assign customCSS = [
  "${baseUrlCdn}/global/css/superadmin/superadmin.css?20260914",
  "${baseUrlCdn}/global/css/superadmin/marloEmails.css?20260928a"
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

        <h4 class="sectionTitle">[@s.text name="marloEmails.title" /]</h4>
        [#-- The rows come a page at a time from emailLogs.do; the page script reads its texts from these attributes --]
        <div class="emailsOnTrack borderBox" id="emailsOnTrack"
          data-text-empty="[@s.text name="marloEmails.table.empty" /]"
          data-text-info="[@s.text name="marloEmails.table.info" /]"
          data-text-info-empty="[@s.text name="marloEmails.table.infoEmpty" /]"
          data-text-info-filtered="[@s.text name="marloEmails.table.infoFiltered" /]"
          data-text-loading="[@s.text name="marloEmails.table.loading" /]"
          data-text-load-error="[@s.text name="marloEmails.table.loadError" /]"
          data-text-not-recorded="[@s.text name="marloEmails.filter.notRecorded" /]"
          data-text-background="[@s.text name="marloEmails.source.background" /]"
          data-text-sent="[@s.text name="marloEmails.detail.sent" /]"
          data-text-not-sent="[@s.text name="marloEmails.detail.notSent" /]"
          data-text-detail-loading="[@s.text name="marloEmails.detail.loading" /]"
          data-text-detail-error="[@s.text name="marloEmails.detail.loadError" /]"
          data-text-resend-filtered="[@s.text name="marloEmails.resend.filtered" /]"
          data-text-resend-confirm="[@s.text name="marloEmails.resend.confirm" /]"
          data-text-resend-running="[@s.text name="marloEmails.resend.running" /]"
          data-text-resend-result="[@s.text name="marloEmails.resend.result" /]"
          data-text-resend-error="[@s.text name="marloEmails.resend.error" /]">

          <ul class="nav nav-tabs emailsTabs" role="tablist">
            <li class="active">
              <a href="#" role="tab" data-status="notSent">[@s.text name="marloEmails.tab.notSent" /] <span class="badge emailsCount-notSent"></span></a>
            </li>
            <li>
              <a href="#" role="tab" data-status="sent">[@s.text name="marloEmails.tab.sent" /] <span class="badge emailsCount-sent"></span></a>
            </li>
          </ul>

          <div class="emailsFilters">
            <div class="emailsFilter">
              <label for="emailsFilterGlobalUnit">[@s.text name="marloEmails.filter.globalUnit" /]</label>
              <select id="emailsFilterGlobalUnit" class="form-control input-sm" name="globalUnit">
                <option value="">[@s.text name="marloEmails.filter.all" /]</option>
                [#list (globalUnits)![] as globalUnit]
                  <option value="${globalUnit.id}">${(globalUnit.acronym)!globalUnit.id}</option>
                [/#list]
                <option value="none">[@s.text name="marloEmails.filter.notRecorded" /]</option>
              </select>
            </div>
            <div class="emailsFilter">
              <label for="emailsFilterSource">[@s.text name="marloEmails.filter.source" /]</label>
              <select id="emailsFilterSource" class="form-control input-sm" name="source">
                <option value="">[@s.text name="marloEmails.filter.all" /]</option>
                [#list (sourceActions)![] as sourceAction]
                  <option value="${sourceAction}" class="emailsSourceOption">${sourceAction}</option>
                [/#list]
                <option value="none">[@s.text name="marloEmails.filter.notRecorded" /]</option>
              </select>
            </div>
            <div class="emailsFilter">
              <label for="emailsFilterFrom">[@s.text name="marloEmails.filter.from" /]</label>
              <input type="date" id="emailsFilterFrom" class="form-control input-sm" name="from" />
            </div>
            <div class="emailsFilter">
              <label for="emailsFilterTo">[@s.text name="marloEmails.filter.to" /]</label>
              <input type="date" id="emailsFilterTo" class="form-control input-sm" name="to" />
            </div>
            <div class="emailsFilter emailsFilter-search">
              <div class="emails-search-wrap">
                <input type="text" id="marloEmailsSearch" class="form-control emails-search" name="search"
                  placeholder="[@s.text name="marloEmails.filter.search" /]" />
                <div class="iconSearch emails-search-icon">
                  <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
                </div>
              </div>
            </div>
            <div class="emailsFilter">
              <button type="button" class="btn btn-link btn-sm emailsClearFilters">[@s.text name="marloEmails.filter.clear" /]</button>
            </div>
          </div>

          <table id="marloEmailsTable" class="table table-striped table-hover" width="100%">
            <thead>
              <tr>
                <th>[@s.text name="marloEmails.table.date" /]</th>
                <th>[@s.text name="marloEmails.table.subject" /]</th>
                <th>[@s.text name="marloEmails.table.to" /]</th>
                <th>[@s.text name="marloEmails.table.globalUnit" /]</th>
                <th>[@s.text name="marloEmails.table.source" /]</th>
                <th>[@s.text name="marloEmails.table.tried" /]</th>
                <th>[@s.text name="marloEmails.table.error" /]</th>
              </tr>
            </thead>
            <tbody></tbody>
          </table>

          <div class="emailsResend">
            <button type="button" class="sendEmails btn btn-primary" disabled></button>
            <span class="emailsResendStatus" role="status" aria-live="polite"></span>
          </div>
        </div>
      </div>
    </div>
  </div>
</section>

[#-- One popup for every email: its content is loaded from emailLogDetail.do when a row is opened --]
<div class="modal fade" id="emailDetailPopup" tabindex="-1" role="dialog" aria-labelledby="emailDetailTitle">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="emailDetailTitle"></h4>
      </div>
      <div class="modal-body">
        <p class="emailDetailLoading"></p>
        <div class="emailDetailBody" style="display:none">
          <div class="row">
            <div class="col-md-6">
              <p><strong>[@s.text name="marloEmails.detail.to" /]: </strong><span data-field="to"></span></p>
              <p><strong>[@s.text name="marloEmails.detail.cc" /]: </strong><span data-field="cc"></span></p>
              <p><strong>[@s.text name="marloEmails.detail.bcc" /]: </strong><span data-field="bcc"></span></p>
              <p><strong>[@s.text name="marloEmails.detail.date" /]: </strong><span data-field="date"></span></p>
              <p><strong>[@s.text name="marloEmails.table.globalUnit" /]: </strong><span data-field="globalUnit"></span></p>
              <p><strong>[@s.text name="marloEmails.table.source" /]: </strong><span data-field="source"></span></p>
            </div>
            <div class="col-md-6">
              <p><strong>[@s.text name="marloEmails.detail.status" /]: </strong><span data-field="status"></span></p>
              <p><strong>[@s.text name="marloEmails.detail.tried" /]: </strong><span data-field="tried"></span></p>
              <p><strong>[@s.text name="marloEmails.detail.error" /]: </strong><span data-field="error"></span></p>
              <p><strong>[@s.text name="marloEmails.detail.file" /]: </strong><span data-field="fileName"></span></p>
              <p><strong>[@s.text name="marloEmails.detail.messageId" /]: </strong><span data-field="messageId"></span></p>
            </div>
          </div>
          <hr />
          [#-- The message is stored HTML: a sandboxed frame shows it without running any script it carries --]
          <iframe class="emailDetailMessage" sandbox="" title="[@s.text name="marloEmails.table.subject" /]"></iframe>
        </div>
      </div>
      <div class="modal-footer">
        <span class="emailsResendStatus emailDetailResendStatus" role="status" aria-live="polite"></span>
        <button type="button" class="btn btn-primary emailDetailResend" style="display:none">[@s.text name="marloEmails.resend.one" /]</button>
        <button type="button" class="btn btn-default" data-dismiss="modal">[@s.text name="form.buttons.close" /]</button>
      </div>
    </div>
  </div>
</div>

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#ftl]
[#assign title = "MARLO Admin - Emails" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [ "${baseUrl}/global/js/superadmin/emails.js" ] /]
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
                  <th>ID</th>
                  <th>Subject</th>
                  <th>Error</th>
                  
                  <th>Tried</th>
              </tr>
            </thead> 
          </table>
         <br />
         <div class="form-group">
            <button type="button" class="sendEmails btn btn-primary">[@s.text name="form.buttons.sendEmails" /]</button>
         </div>
        </div>

      </div>
    </div>
  </div>
</section>


[#include "/WEB-INF/crp/pages/footer.ftl" /]
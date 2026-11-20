[#ftl]
[#-- All institutions dialog (CHG-PARTREQ-001): the table is filled by the all-institutions popup script (DataTables); --]
[#-- marloInstitutions.js (prqDialog) opens it from the page header button. --]
<div class="prq-dialog" id="allInstitutions" data-prq-dialog="all" hidden>
  <div class="prq-dialog__panel prq-dialog__panel--wide" tabindex="-1">
    <div class="prq-dialog__head">
      <h2 class="prq-dialog__title" id="allInstitutionsTitle">[@s.text name="marloRequestInstitution.ui.dialog.all.title" /]</h2>
      <button type="button" class="prq-dialog__x" data-prq-close aria-label="[@s.text name="marloRequestInstitution.ui.dialog.close" /]"></button>
    </div>
    <div class="prq-dialog__body">
      [#-- All institutions table --]
      <table id="allInstitutionsTable" class="display prq-table" width="100%">
        <thead>
          <tr>
            <th>[@s.text name="marloRequestInstitution.ui.table.id" /]</th>
            <th>[@s.text name="marloRequestInstitution.ui.field.acronym" /]</th>
            <th>[@s.text name="marloRequestInstitution.ui.field.name" /]</th>
            <th>[@s.text name="marloRequestInstitution.ui.field.type" /]</th>
          </tr>
        </thead>
      </table>
    </div>
    <div class="prq-dialog__foot">
      <button type="button" class="prq-btn prq-btn--ghost" data-prq-close>[@s.text name="marloRequestInstitution.ui.dialog.close" /]</button>
    </div>
  </div>
</div>
[#-- All institutions table script --]
[#assign customJS =  [ "${baseUrlCdn}/global/js/allInstitutionsPopup.js?20261120" ]  + customJS/]

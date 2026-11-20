[#ftl]
[#-- Decision dialogs of the superadmin Institutions screen (CHG-PARTREQ-001). --]
[#-- Markup only: marloInstitutions.js (prqDialog) opens them, sets role / aria-modal / aria-labelledby, traps focus and --]
[#-- returns it to the opener. Every dialog keeps the same shell: head with the title id, body, foot. --]

[#-- The reasons a reviewer can quote; Reject partner and Reject offices pick the ones that apply --]
[#macro reasonsList keys]
  <details class="prq-reasons">
    <summary class="prq-reasons__summary">[@s.text name="marloRequestInstitution.ui.dialog.reasons" /]</summary>
    <ul class="prq-reasons__list">
      [#list keys as key]
        <li>[@s.text name="marloRequestInstitution.ui.dialog.reasons.${key}" /]</li>
      [/#list]
    </ul>
  </details>
[/#macro]

[#-- Reject a partner request --]
<div class="prq-dialog" id="prqRejectDialog" data-prq-dialog="reject" hidden>
  <div class="prq-dialog__panel" tabindex="-1">
    <div class="loading" style="display:none"></div>
    <div class="prq-dialog__head">
      <h2 class="prq-dialog__title" id="prqRejectDialogTitle">[@s.text name="marloRequestInstitution.ui.dialog.reject.title" /]</h2>
      <button type="button" class="prq-dialog__x" data-prq-close aria-label="[@s.text name="marloRequestInstitution.ui.dialog.close" /]"></button>
    </div>
    <div class="prq-dialog__body">
      [#-- Name of the request being rejected: filled by marloInstitutions.js --]
      <p class="prq-dialog__target" data-prq-target></p>
      <div class="prq-field">
        <label class="prq-label" for="prqRejectJustification">[@s.text name="marloRequestInstitution.ui.dialog.reject.justification" /] <span class="prq-req" aria-hidden="true">*</span></label>
        <textarea class="prq-textarea" id="prqRejectJustification" name="justification" rows="4" aria-required="true" aria-describedby="prqRejectJustificationHelp prqRejectJustificationError"></textarea>
        <p class="prq-help" id="prqRejectJustificationHelp">[@s.text name="marloRequestInstitution.ui.dialog.reject.help" /]</p>
        <p class="prq-error" id="prqRejectJustificationError" hidden>[@s.text name="marloRequestInstitution.ui.dialog.reject.required" /]</p>
      </div>
      <label class="prq-check" for="prqRejectNotify">
        <input type="checkbox" class="prq-checkbox" id="prqRejectNotify" name="sendNotification" value="true" checked />
        <span>[@s.text name="marloRequestInstitution.ui.dialog.reject.notify" /]</span>
      </label>
      [@reasonsList keys=["exists", "managing", "notLegal", "subDepartment", "person", "office"] /]
    </div>
    <div class="prq-dialog__foot">
      <button type="button" class="prq-btn prq-btn--ghost" data-prq-close>[@s.text name="marloRequestInstitution.ui.dialog.cancel" /]</button>
      <button type="button" class="prq-btn prq-btn--danger" data-prq-confirm>[@s.text name="marloRequestInstitution.ui.dialog.reject.confirm" /]</button>
    </div>
  </div>
</div>

[#-- Reject the selected country offices --]
<div class="prq-dialog" id="prqRejectOfficesDialog" data-prq-dialog="rejectOffices" hidden>
  <div class="prq-dialog__panel" tabindex="-1">
    <div class="loading" style="display:none"></div>
    <div class="prq-dialog__head">
      <h2 class="prq-dialog__title" id="prqRejectOfficesDialogTitle">[@s.text name="marloRequestInstitution.ui.dialog.rejectOffices.title" /]</h2>
      <button type="button" class="prq-dialog__x" data-prq-close aria-label="[@s.text name="marloRequestInstitution.ui.dialog.close" /]"></button>
    </div>
    <div class="prq-dialog__body">
      [#-- Countries being rejected: filled by marloInstitutions.js --]
      <p class="prq-dialog__target" data-prq-target></p>
      <div class="prq-field">
        <label class="prq-label" for="prqRejectOfficesJustification">[@s.text name="marloRequestInstitution.ui.dialog.reject.justification" /] <span class="prq-req" aria-hidden="true">*</span></label>
        <textarea class="prq-textarea" id="prqRejectOfficesJustification" name="justification" rows="4" aria-required="true" aria-describedby="prqRejectOfficesJustificationHelp prqRejectOfficesJustificationError"></textarea>
        <p class="prq-help" id="prqRejectOfficesJustificationHelp">[@s.text name="marloRequestInstitution.ui.dialog.reject.help" /]</p>
        <p class="prq-error" id="prqRejectOfficesJustificationError" hidden>[@s.text name="marloRequestInstitution.ui.dialog.reject.required" /]</p>
      </div>
      <label class="prq-check" for="prqRejectOfficesNotify">
        <input type="checkbox" class="prq-checkbox" id="prqRejectOfficesNotify" name="sendNotification" value="true" checked />
        <span>[@s.text name="marloRequestInstitution.ui.dialog.reject.notify" /]</span>
      </label>
      [@reasonsList keys=["officeExists"] /]
    </div>
    <div class="prq-dialog__foot">
      <button type="button" class="prq-btn prq-btn--ghost" data-prq-close>[@s.text name="marloRequestInstitution.ui.dialog.cancel" /]</button>
      <button type="button" class="prq-btn prq-btn--danger" data-prq-confirm>[@s.text name="marloRequestInstitution.ui.dialog.rejectOffices.confirm" /]</button>
    </div>
  </div>
</div>

[#-- Confirm accepting a partner request (the card's accept form submits when this is confirmed) --]
<div class="prq-dialog" id="prqAcceptDialog" data-prq-dialog="accept" hidden>
  <div class="prq-dialog__panel" tabindex="-1">
    <div class="prq-dialog__head">
      <h2 class="prq-dialog__title" id="prqAcceptDialogTitle">[@s.text name="marloRequestInstitution.ui.dialog.accept.title" /]</h2>
      <button type="button" class="prq-dialog__x" data-prq-close aria-label="[@s.text name="marloRequestInstitution.ui.dialog.close" /]"></button>
    </div>
    <div class="prq-dialog__body">
      <p class="prq-dialog__target" data-prq-target></p>
      <p class="prq-dialog__text">[@s.text name="marloRequestInstitution.ui.dialog.accept.text" /]</p>
    </div>
    <div class="prq-dialog__foot">
      <button type="button" class="prq-btn prq-btn--ghost" data-prq-close>[@s.text name="marloRequestInstitution.ui.dialog.cancel" /]</button>
      <button type="button" class="prq-btn prq-btn--primary" data-prq-confirm>[@s.text name="marloRequestInstitution.ui.dialog.accept.confirm" /]</button>
    </div>
  </div>
</div>

[#-- Confirm accepting the selected country offices --]
<div class="prq-dialog" id="prqAcceptOfficesDialog" data-prq-dialog="acceptOffices" hidden>
  <div class="prq-dialog__panel" tabindex="-1">
    <div class="loading" style="display:none"></div>
    <div class="prq-dialog__head">
      <h2 class="prq-dialog__title" id="prqAcceptOfficesDialogTitle">[@s.text name="marloRequestInstitution.ui.dialog.acceptOffices.title" /]</h2>
      <button type="button" class="prq-dialog__x" data-prq-close aria-label="[@s.text name="marloRequestInstitution.ui.dialog.close" /]"></button>
    </div>
    <div class="prq-dialog__body">
      <p class="prq-dialog__target" data-prq-target></p>
      <p class="prq-dialog__text">[@s.text name="marloRequestInstitution.ui.dialog.acceptOffices.text" /]</p>
    </div>
    <div class="prq-dialog__foot">
      <button type="button" class="prq-btn prq-btn--ghost" data-prq-close>[@s.text name="marloRequestInstitution.ui.dialog.cancel" /]</button>
      <button type="button" class="prq-btn prq-btn--primary" data-prq-confirm>[@s.text name="marloRequestInstitution.ui.dialog.acceptOffices.confirm" /]</button>
    </div>
  </div>
</div>

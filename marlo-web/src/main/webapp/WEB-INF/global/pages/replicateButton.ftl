[#ftl]
[#-- TODO: Activate when checkbox is ready --]
[#if reportingActive && false]
  <div class="upkeepCheckbox">
    [#assign replicateNextPhaseText = "Replicate this information to ${(actualPhase.next.next.composedName)!} after save"/]
    [@customForm.checkmark id="replicateNextPhase" name="replicateNextPhase" label="${replicateNextPhaseText}" value="true" checked=false cssClass="" cssClassLabel="font-normal" /]
  </div>
[/#if]
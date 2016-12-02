[#ftl]
[#-- Ranking --]
<div class="fullBlock">
<h4 class="headTitle">Ranking</h4>
  <table id="rankingTable" class="default">
    <tbody>
      <tr>
        <td class="key">[@s.text name="to be defined" /]</td> 
        <td class="value">[@customForm.rank name="1" editable=editable/]</td>
      </tr>
    </tbody>
  </table>
</div>
[#-- Compliance check (Data products only) --]
<h4 class="headTitle">Compliance check (Data products only)</h4>
<br />
<div class="fullBlock simpleBox" > 
  <div class="quetion borderBox">
    <h5>Have you had a process of data quality assurance in place?</h5>
    <div class="col-md-4">
    <div class="radio">
      <label><input type="radio" name="optradio">Yes, but not documented</label>
    </div>
    <div class="radio">
      <label><input type="radio" name="optradio">Yes, and documented</label>
    </div>
    <div class="radio">
      <label><input type="radio" name="optradio">No</label>
    </div>
    </div>
    
    [#-- FILE --]
    <div class="col-md-8">
      <div class="col-md-6 form-group fileUploadContainer">
        <label>[@customForm.text name="Proof of submission" readText=!editable /]:</label>
        [#-- Input File --]
        [#if editable]
        <div class="fileUpload" style="display:block"> <input class="upload" type="file" name="file" data-url=""></div>
        [/#if]
      </div>
      <span class="col-md-1"> <br /> or</span>
      <div class="col-md-5">
      <label>[@customForm.text name="Please give us the link" readText=!editable /]:</label>
      <input type="text" />
      </div>
    </div>
  </div>
</div>
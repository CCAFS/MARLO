[#ftl]
<script type="text/javascript">
  var currentPlanningYear, currentReportingYear;
  var formBefore;
  var justificationLimitWords = 100;
  var errorMessages = [];
  var hashScroll = true;
  var forceChange = false;
  
  var RECAPTCHAT_SITE_KEY="${config.recaptchatSiteKey}"
  var GOOGLE_API_KEY="${config.googleApiKey}";
  var PUSHER_KEY = "${config.pushApiKey}";
  
  var MIN_DATE = '2000-01-01';
  var MAX_DATE = '2030-12-31';
  
  var actionName = "${actionName}";
  var production = ${config.production?string};
  var baseURL = "${baseUrl}";
  var baseUrl = "${baseUrl}";
  var baseUrlMedia = "${baseUrlMedia}";
  var baseCdnUrl = "${baseUrlCdn}";
  var debugMode = ${config.debug?string};
  var editable = ${editable?string};
  var canEdit = ${canEdit?string};
  var draft = ${draft?string};
  var autoSaveActive =  ${(((autoSaveActive)!true) && ((currentUser.autoSave)!false) && editable)?string};
  var currentCrpID=${(crpID)!'-1'};
  var currentCrpSession='${(crpSession)!'-1'}';
  var currentCycleYear = ${(currentCycleYear)!1999};
  var reportingActive= ${((reportingActive)!false)?string};
  var upKeepActive= ${((upKeepActive)!false)?string};
  var projectPreSetting= ${((project.projectInfo.projectEditLeader)!false)?string('0','1')};
  var currentUserName = "${(currentUser.composedCompleteName)!}";
  var phaseID = ${(actualPhase.id)!'""'};

  
  var currentCenterID=${(centerID)!'-1'};
  var centerSession="${(centerSession)!}";
  
  var centerGlobalUnit = ${((centerGlobalUnit)!false)?string};
  
</script>

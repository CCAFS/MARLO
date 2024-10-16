[#ftl]
[#-------------------------------------------------------------------------------- 
    Please don't modify this file, if you want add/remove an extra dependency 
    refer to bower.js and execute (bower install/uninstall --save) then (grunt) 
    commands in marlo-web directory.
----------------------------------------------------------------------------------]

[#macro css_imports libraryName]
  [#-- bower:css --]
  [#if libraryName="jquery-ui" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/jquery-ui/themes/base/jquery-ui.min.css" />[/#if]
  [#if libraryName="jReject" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/jReject/css/jquery.reject.css" />[/#if]
  [#if libraryName="select2" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/select2/dist/css/select2.min.css" />[/#if]
  [#if libraryName="datatables" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/datatables/media/css/jquery.dataTables.css" />[/#if]
  [#if libraryName="dropzone" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/dropzone/dist/min/dropzone.min.css" />[/#if]
  [#if libraryName="animate.css" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/animate.css/animate.css" />[/#if]
  [#if libraryName="bootstrap" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/bootstrap/dist/css/bootstrap.min.css?20220316a" />[/#if]
  [#if libraryName="flat-flags" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/flat-flags/css/main.css" />[/#if]
  [#if libraryName="flag-icon-css" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/flag-icon-css/css/flag-icon.min.css" />[/#if]
  [#if libraryName="bootstrap-select" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/bootstrap-select/dist/css/bootstrap-select.css" />[/#if]
  [#if libraryName="cytoscape-panzoom" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/cytoscape-panzoom/cytoscape.js-panzoom.css" />[/#if]
  [#if libraryName="cytoscape-panzoom" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/cytoscape-panzoom/font-awesome-4.0.3/css/font-awesome.css" />[/#if]
  [#if libraryName="datatables.net-bs" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/datatables.net-bs/css/dataTables.bootstrap.css" />[/#if]
  [#if libraryName="intro.js" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/intro.js/introjs.css" />[/#if]
  [#if libraryName="font-awesome" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/font-awesome/css/font-awesome.css" />[/#if]
  [#if libraryName="jquery-tag-editor" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/jquery-tag-editor/jquery.tag-editor.css" />[/#if]
  [#if libraryName="pickadate" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/pickadate/lib/themes/classic.css" />[/#if]
  [#if libraryName="pickadate" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/pickadate/lib/themes/classic.date.css" />[/#if]
  [#if libraryName="pickadate" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/pickadate/lib/themes/classic.time.css" />[/#if]
  [#if libraryName="components-font-awesome" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/components-font-awesome/css/fontawesome-all.css" />[/#if]
  [#if libraryName="trumbowyg" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/trumbowyg/dist/ui/trumbowyg.min.css" />[/#if]
  [#if libraryName="malihu-custom-scrollbar-plugin" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/malihu-custom-scrollbar-plugin/jquery.mCustomScrollbar.css" />[/#if]
  [#if libraryName="cookieconsent" ]<link rel="stylesheet" href="${baseUrlCdn}/global/bower_components/cookieconsent/build/cookieconsent.min.css" />[/#if]
  [#-- endbower --]
[/#macro]

[#macro js_imports libraryName]
  [#-- bower:js --]
  [#if libraryName="jquery"]<script src="${baseUrlCdn}/global/bower_components/jquery/dist/jquery.min.js"></script>[/#if]
  [#if libraryName="jquery-ui"]<script src="${baseUrlCdn}/global/bower_components/jquery-ui/jquery-ui.min.js"></script>[/#if]
  [#if libraryName="jquery-mask-plugin"]<script src="${baseUrlCdn}/global/bower_components/jquery-mask-plugin/dist/jquery.mask.min.js"></script>[/#if]
  [#if libraryName="html5shiv"]<script src="${baseUrlCdn}/global/bower_components/html5shiv/dist/html5shiv.js"></script>[/#if]
  [#if libraryName="jReject"]<script src="${baseUrlCdn}/global/bower_components/jReject/js/jquery.reject.js"></script>[/#if]
  [#if libraryName="noty"]<script src="${baseUrlCdn}/global/bower_components/noty/js/noty/packaged/jquery.noty.packaged.js"></script>[/#if]
  [#if libraryName="select2"]<script src="${baseUrlCdn}/global/bower_components/select2/dist/js/select2.min.js"></script>[/#if]
  [#if libraryName="autogrow-textarea"]<script src="${baseUrlCdn}/global/bower_components/autogrow-textarea/jquery.autogrowtextarea.js"></script>[/#if]
  [#if libraryName="datatables"]<script src="${baseUrlCdn}/global/bower_components/datatables/media/js/jquery.dataTables.js"></script>[/#if]
  [#if libraryName="dropzone"]<script src="${baseUrlCdn}/global/bower_components/dropzone/dist/min/dropzone.min.js"></script>[/#if]
  [#if libraryName="jsUri"]<script src="${baseUrlCdn}/global/bower_components/jsUri/Uri.js"></script>[/#if]
  [#if libraryName="bootstrap"]<script src="${baseUrlCdn}/global/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>[/#if]
  [#if libraryName="bootstrap-select"]<script src="${baseUrlCdn}/global/bower_components/bootstrap-select/dist/js/bootstrap-select.js"></script>[/#if]
  [#if libraryName="vanilla-color-picker"]<script src="${baseUrlCdn}/global/bower_components/vanilla-color-picker/dist/vanilla-color-picker.min.js"></script>[/#if]
  [#if libraryName="countdown"]<script src="${baseUrlCdn}/global/bower_components/countdown/dest/jquery.countdown.js"></script>[/#if]
  [#if libraryName="cytoscape"]<script src="${baseUrlCdn}/global/bower_components/cytoscape/dist/cytoscape.js"></script>[/#if]
  [#if libraryName="cytoscape-panzoom"]<script src="${baseUrlCdn}/global/bower_components/cytoscape-panzoom/cytoscape-panzoom.js"></script>[/#if]
  [#if libraryName="datatables.net"]<script src="${baseUrlCdn}/global/bower_components/datatables.net/js/jquery.dataTables.js"></script>[/#if]
  [#if libraryName="datatables.net-bs"]<script src="${baseUrlCdn}/global/bower_components/datatables.net-bs/js/dataTables.bootstrap.js"></script>[/#if]
  [#if libraryName="ev-emitter"]<script src="${baseUrlCdn}/global/bower_components/ev-emitter/ev-emitter.js"></script>[/#if]
  [#if libraryName="imagesloaded"]<script src="${baseUrlCdn}/global/bower_components/imagesloaded/imagesloaded.js"></script>[/#if]
  [#if libraryName="qtip2"]<script src="${baseUrlCdn}/global/bower_components/qtip2/jquery.qtip.js"></script>[/#if]
  [#if libraryName="qtip2"]<script src="${baseUrlCdn}/global/bower_components/qtip2/basic/jquery.qtip.js"></script>[/#if]
  [#if libraryName="cytoscape-qtip"]<script src="${baseUrlCdn}/global/bower_components/cytoscape-qtip/cytoscape-qtip.js"></script>[/#if]
  [#if libraryName="intro.js"]<script src="${baseUrlCdn}/global/bower_components/intro.js/intro.js"></script>[/#if]
  [#if libraryName="blueimp-file-upload"]<script src="${baseUrlCdn}/global/bower_components/blueimp-file-upload/js/jquery.fileupload.js"></script>[/#if]
  [#if libraryName="google-diff-match-patch"]<script src="${baseUrlCdn}/global/bower_components/google-diff-match-patch/diff_match_patch.js"></script>[/#if]
  [#if libraryName="jquery-pretty-text-diff"]<script src="${baseUrlCdn}/global/bower_components/jquery-pretty-text-diff/jquery.pretty-text-diff.min.js"></script>[/#if]
  [#if libraryName="caret"]<script src="${baseUrlCdn}/global/bower_components/caret/jquery.caret.js"></script>[/#if]
  [#if libraryName="jquery-tag-editor"]<script src="${baseUrlCdn}/global/bower_components/jquery-tag-editor/jquery.tag-editor.min.js"></script>[/#if]
  [#if libraryName="sly"]<script src="${baseUrlCdn}/global/bower_components/sly/dist/sly.min.js"></script>[/#if]
  [#if libraryName="pickadate"]<script src="${baseUrlCdn}/global/bower_components/pickadate/lib/picker.js"></script>[/#if]
  [#if libraryName="pickadate"]<script src="${baseUrlCdn}/global/bower_components/pickadate/lib/picker.date.js"></script>[/#if]
  [#if libraryName="pickadate"]<script src="${baseUrlCdn}/global/bower_components/pickadate/lib/picker.time.js"></script>[/#if]
  [#if libraryName="pusher-js"]<script src="${baseUrlCdn}/global/bower_components/pusher-js/dist/web/pusher.js"></script>[/#if]
  [#if libraryName="trumbowyg"]<script src="${baseUrlCdn}/global/bower_components/trumbowyg/dist/trumbowyg.min.js"></script>[/#if]
  [#if libraryName="trumbowyg"]<script src="${baseUrlCdn}/global/bower_components/trumbowyg/dist/plugins/cleanpaste/trumbowyg.cleanpaste.min.js"></script>[/#if]
  [#if libraryName="trumbowyg"]<script src="${baseUrlCdn}/global/bower_components/trumbowyg/dist/plugins/allowtagsfrompaste/trumbowyg.allowtagsfrompaste.js"></script>[/#if]
  [#if libraryName="jquery-mousewheel"]<script src="${baseUrlCdn}/global/bower_components/jquery-mousewheel/jquery.mousewheel.js"></script>[/#if]
  [#if libraryName="malihu-custom-scrollbar-plugin"]<script src="${baseUrlCdn}/global/bower_components/malihu-custom-scrollbar-plugin/jquery.mCustomScrollbar.js"></script>[/#if]
  [#if libraryName="cookieconsent"]<script src="${baseUrlCdn}/global/bower_components/cookieconsent/build/cookieconsent.min.js"></script>[/#if]
  [#if libraryName="vue"]<script src="${baseUrlCdn}/global/bower_components/vue/dist/vue.min.js"></script>[/#if]
  [#-- endbower --]
[/#macro]

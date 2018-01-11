[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

[#macro outputsList outputs={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction=""]
  <table class="outputList" id="outputs">
    <thead>
      <tr class="subHeader">
        <th id="ids">[@s.text name="programImpact.outputList.idTable" /]</th>
        <th id="outputTitles" >[@s.text name="programImpact.outputList.title" /]</th>
        <th id="outputDateAdded">[@s.text name="programImpact.outputList.date" /]</th>
        <th id="outputRF">[@s.text name="programImpact.outputList.requiredFields" /]</th>
        <th id="outputDelete">[@s.text name="programImpact.outputList.delete" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if outputs?has_content]
      [#list outputs as output]
        <tr>
        [#-- ID --]
        <td class="outputId">
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='outputID']${output.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /]">O${output.id}</a>
        </td>
          [#-- output statement --]
          <td class="left"> 
            [#if output.title?has_content]
                <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='outputID']${output.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" title="${output.title}">
                [#if output.title?length < 120] ${output.title}</a> [#else] [@utilities.wordCutter string=output.title maxPos=120 /]...</a> [/#if]
            [#else]
                <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='outputID']${output.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url] ">
                  [@s.text name="programImpact.outputList.title.none" /]
                </a>
            [/#if]
          </td>
          [#-- output Year --]
          <td class="text-center">
          ${(output.dateAdded)!'none'}
          </td>
          
          [#-- Output required fields --]
          <td class="text-center">
            [#if action.getCenterOutputStatus(output.id)??]
              [#if !((action.getCenterOutputStatus(output.id)).missingFields)?has_content]
                <span class="icon-20 icon-check" title="Complete"></span>
              [#else]
                <span class="icon-20 icon-uncheck" title=""></span> 
              [/#if]
            [#else]
                <span class="icon-20 icon-uncheck" title=""></span>
            [/#if]
          </td>
          
          [#-- Delete output--]
          <td class="text-center">
            [#if canEdit && action.centerCanBeDeleted(output.id, output.class.name)!false]
              <a id="removeOutput-${output.id}" class="removeOutput" href="#" title="">
                <img src="${baseUrl}/global/images/trash.png" title="[@s.text name="programImpact.outputList.removeOutcome" /]" /> 
              </a>
            [#else]
              <img src="${baseUrl}/global/images/trash_disable.png" title="[@s.text name="programImpact.outputList.cannotDelete" /]" />
            [/#if]
          </td>          
        </tr>  
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]
/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.SrfCrossCuttingIssueManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorTargetManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloManager;
import org.cgiar.ccafs.marlo.data.model.SrfCrossCuttingIssue;
import org.cgiar.ccafs.marlo.data.model.SrfSlo;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.struts2.ServletActionContext;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 */
public class SLOsAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  // String literal constants for request parameter binding
  private static final String SLOS_LIST_PREFIX = "slosList[";
  private static final String CROSS_CUTTING_ISSUES_PREFIX = "srfCrossCuttingIssues[";
  private static final String SRF_SLO_INDICATORS_PREFIX = "].srfSloIndicators[";
  private static final String SRF_SLO_INDICATOR_TARGETS_PREFIX = "].srfSloIndicatorTargets[";
  private static final String ID_SUFFIX = "].id";
  private static final String TITLE_SUFFIX = "].title";
  private static final String DESCRIPTION_SUFFIX = "].description";
  private static final String NAME_SUFFIX = "].name";
  private static final String YEAR_SUFFIX = "].year";
  private static final String VALUE_SUFFIX = "].value";
  private static final String TARGETS_INDICATOR_SUFFIX = "].targetsIndicator";


  private Map<Long, String> idoList;


  private final SrfSloManager srfSloManager;

  private final SrfSloIndicatorManager srfSloIndicatorManager;

  private final SrfSloIndicatorTargetManager srfSloIndicatorTargetManager;

  private final SrfCrossCuttingIssueManager srfCrossCuttingIssueManager;

  private List<SrfSlo> slosList;

  private List<SrfCrossCuttingIssue> srfCrossCuttingIssues;


  @Inject
  public SLOsAction(APConfig config, SrfSloManager srfSloManager, SrfSloIndicatorManager srfSloIndicatorManager,
    SrfSloIndicatorTargetManager srfSloIndicatorTargetManager, SrfCrossCuttingIssueManager srfCrossCuttingIssueManager) {
    super(config);
    this.srfSloManager = srfSloManager;
    this.srfSloIndicatorManager = srfSloIndicatorManager;
    this.srfSloIndicatorTargetManager = srfSloIndicatorTargetManager;
    this.srfCrossCuttingIssueManager = srfCrossCuttingIssueManager;
  }


  public Map<Long, String> getIdoList() {
    return idoList;
  }


  public List<SrfSlo> getSlosList() {
    return slosList;
  }


  public List<SrfCrossCuttingIssue> getSrfCrossCuttingIssues() {
    return srfCrossCuttingIssues;
  }


  private int findMaxIndex(Map<String, String[]> parameterMap, String keyPattern) {
    Pattern pattern = Pattern.compile(keyPattern);
    int maxIndex = -1;

    for (String key : parameterMap.keySet()) {
      Matcher matcher = pattern.matcher(key);
      if (matcher.matches()) {
        int currentIndex = Integer.parseInt(matcher.group(1));
        if (currentIndex > maxIndex) {
          maxIndex = currentIndex;
        }
      }
    }

    return maxIndex;
  }


  /**
   * Manual binding for SLOs from request parameters.
   * Accepts elements with any meaningful content (not just ID).
   */
  private void bindSlosFromRequest() {
    slosList = new ArrayList<>();
    Map<String, String[]> parameterMap = ServletActionContext.getRequest().getParameterMap();
    int maxSloIndex = findMaxIndex(parameterMap, "^slosList\\[(\\d+)\\]\\..*$");

    for (int index = 0; index <= maxSloIndex; index++) {
      String idParam = parameterMap.containsKey(SLOS_LIST_PREFIX + index + ID_SUFFIX)
        ? parameterMap.get(SLOS_LIST_PREFIX + index + ID_SUFFIX)[0] : null;
      String titleParam = parameterMap.containsKey(SLOS_LIST_PREFIX + index + TITLE_SUFFIX)
        ? parameterMap.get(SLOS_LIST_PREFIX + index + TITLE_SUFFIX)[0] : null;
      String descriptionParam = parameterMap.containsKey(SLOS_LIST_PREFIX + index + DESCRIPTION_SUFFIX)
        ? parameterMap.get(SLOS_LIST_PREFIX + index + DESCRIPTION_SUFFIX)[0] : null;

      // Accept row if ANY field has meaningful content
      boolean hasAnyContent = (titleParam != null && !titleParam.trim().isEmpty())
        || (descriptionParam != null && !descriptionParam.trim().isEmpty());

      if (!hasAnyContent && idParam == null) {
        continue;
      }

      if (hasAnyContent || (idParam != null && !idParam.isEmpty())) {
        SrfSlo slo = new SrfSlo();

        // Set ID if present
        if (idParam != null && !idParam.isEmpty()) {
          try {
            slo.setId(Long.parseLong(idParam));
          } catch (NumberFormatException e) {
            // Invalid ID, skip
          }
        }

        // Set fields
        if (titleParam != null) {
          slo.setTitle(titleParam.trim());
        }
        if (descriptionParam != null) {
          slo.setDescription(descriptionParam.trim());
        }

        // Bind nested indicators
        List<SrfSloIndicator> indicators = bindSloIndicatorsFromRequest(index, parameterMap);
        if (indicators != null && !indicators.isEmpty()) {
          slo.setSrfSloIndicators(new java.util.HashSet<>(indicators));
        }

        slosList.add(slo);
      }
    }
  }


  /**
   * Manual binding for SLO indicators (nested) from request parameters.
   */
  private List<SrfSloIndicator> bindSloIndicatorsFromRequest(int sloIndex, Map<String, String[]> parameterMap) {
    List<SrfSloIndicator> indicators = new ArrayList<>();
    int maxIndicatorIndex =
      findMaxIndex(parameterMap, "^slosList\\[" + sloIndex + "\\]\\.srfSloIndicators\\[(\\d+)\\]\\..*$");

    for (int indicatorIndex = 0; indicatorIndex <= maxIndicatorIndex; indicatorIndex++) {
      String prefix = SLOS_LIST_PREFIX + sloIndex + SRF_SLO_INDICATORS_PREFIX + indicatorIndex;
      String idSuffix = ID_SUFFIX;
      String idParam =
        parameterMap.containsKey(prefix + idSuffix) ? parameterMap.get(prefix + idSuffix)[0] : null;
      String titleParam =
        parameterMap.containsKey(prefix + TITLE_SUFFIX) ? parameterMap.get(prefix + TITLE_SUFFIX)[0] : null;
      String descriptionParam =
        parameterMap.containsKey(prefix + DESCRIPTION_SUFFIX) ? parameterMap.get(prefix + DESCRIPTION_SUFFIX)[0] : null;

      // Accept indicator if ANY field has content
      boolean hasAnyContent = (titleParam != null && !titleParam.trim().isEmpty())
        || (descriptionParam != null && !descriptionParam.trim().isEmpty());

      if (!hasAnyContent && idParam == null) {
        continue;
      }

      if (hasAnyContent || (idParam != null && !idParam.isEmpty())) {
        SrfSloIndicator indicator = new SrfSloIndicator();

        // Set ID if present
        if (idParam != null && !idParam.isEmpty()) {
          try {
            indicator.setId(Long.parseLong(idParam));
          } catch (NumberFormatException e) {
            // Invalid ID, skip
          }
        }

        // Set title
        if (titleParam != null) {
          indicator.setTitle(titleParam.trim());
        }

        // Set description (use empty string if not provided to avoid NULL constraint violation)
        if (descriptionParam != null) {
          indicator.setDescription(descriptionParam.trim());
        } else {
          indicator.setDescription(""); // Default to empty string instead of null
        }

        // Bind nested targets
        java.util.Set<SrfSloIndicatorTarget> targets = bindSloIndicatorTargetsFromRequest(sloIndex, indicatorIndex, parameterMap);
        if (targets != null && !targets.isEmpty()) {
          indicator.setSrfSloIndicatorTargets(targets);
        }

        indicators.add(indicator);
      }
    }

    return indicators;
  }


  /**
   * Manual binding for SLO Indicator Targets (nested) from request parameters.
   */
  private java.util.Set<SrfSloIndicatorTarget> bindSloIndicatorTargetsFromRequest(int sloIndex, int indicatorIndex,
    Map<String, String[]> parameterMap) {
    java.util.Set<SrfSloIndicatorTarget> targets = new java.util.HashSet<>();
    int maxTargetIndex = findMaxIndex(parameterMap,
      "^slosList\\[" + sloIndex + "\\]\\.srfSloIndicators\\[" + indicatorIndex
        + "\\]\\.srfSloIndicatorTargets\\[(\\d+)\\]\\..*$");

    for (int targetIndex = 0; targetIndex <= maxTargetIndex; targetIndex++) {
      String prefix = SLOS_LIST_PREFIX + sloIndex + SRF_SLO_INDICATORS_PREFIX + indicatorIndex + SRF_SLO_INDICATOR_TARGETS_PREFIX + targetIndex;
      String idParam =
        parameterMap.containsKey(prefix + ID_SUFFIX) ? parameterMap.get(prefix + ID_SUFFIX)[0] : null;
      String yearParam =
        parameterMap.containsKey(prefix + YEAR_SUFFIX) ? parameterMap.get(prefix + YEAR_SUFFIX)[0] : null;
      String valueParam =
        parameterMap.containsKey(prefix + VALUE_SUFFIX) ? parameterMap.get(prefix + VALUE_SUFFIX)[0] : null;
      String unitParam =
        parameterMap.containsKey(prefix + TARGETS_INDICATOR_SUFFIX) ? parameterMap.get(prefix + TARGETS_INDICATOR_SUFFIX)[0] : null;

      // Accept target if ANY field has content
      boolean hasAnyContent = (yearParam != null && !yearParam.trim().isEmpty())
        || (valueParam != null && !valueParam.trim().isEmpty())
        || (unitParam != null && !unitParam.trim().isEmpty());

      if (!hasAnyContent && idParam == null) {
        continue;
      }

      if (hasAnyContent || (idParam != null && !idParam.isEmpty())) {
        SrfSloIndicatorTarget target = new SrfSloIndicatorTarget();

        // Set ID if present
        if (idParam != null && !idParam.isEmpty()) {
          try {
            target.setId(Long.parseLong(idParam));
          } catch (NumberFormatException e) {
            // Invalid ID, skip
          }
        }

        // Set year
        if (yearParam != null && !yearParam.trim().isEmpty()) {
          try {
            target.setYear(Integer.parseInt(yearParam.trim()));
          } catch (NumberFormatException e) {
            // Invalid year, set to 0
            target.setYear(0);
          }
        }

        // Set value
        if (valueParam != null && !valueParam.trim().isEmpty()) {
          try {
            target.setValue(new java.math.BigDecimal(valueParam.trim()));
          } catch (NumberFormatException e) {
            // Invalid value, skip
          }
        }

        // Set unit (targetsIndicator)
        if (unitParam != null) {
          target.setTargetsIndicator(unitParam.trim());
        }

        targets.add(target);
      }
    }

    return targets;
  }


  /**
   * Manual binding for Cross-Cutting Issues from request parameters.
   * Accepts elements with any meaningful content (not just ID).
   */
  private void bindCrossCuttingIssuesFromRequest() {
    srfCrossCuttingIssues = new ArrayList<>();
    Map<String, String[]> parameterMap = ServletActionContext.getRequest().getParameterMap();
    int maxIssueIndex = findMaxIndex(parameterMap, "^srfCrossCuttingIssues\\[(\\d+)\\]\\..*$");

    for (int index = 0; index <= maxIssueIndex; index++) {
      String idParam = parameterMap.containsKey(CROSS_CUTTING_ISSUES_PREFIX + index + ID_SUFFIX)
        ? parameterMap.get(CROSS_CUTTING_ISSUES_PREFIX + index + ID_SUFFIX)[0] : null;
      String nameParam = parameterMap.containsKey(CROSS_CUTTING_ISSUES_PREFIX + index + NAME_SUFFIX)
        ? parameterMap.get(CROSS_CUTTING_ISSUES_PREFIX + index + NAME_SUFFIX)[0] : null;

      // Accept row if ANY field has meaningful content
      boolean hasAnyContent = (nameParam != null && !nameParam.trim().isEmpty());

      if (!hasAnyContent && idParam == null) {
        continue;
      }

      if (hasAnyContent || (idParam != null && !idParam.isEmpty())) {
        SrfCrossCuttingIssue issue = new SrfCrossCuttingIssue();

        // Set ID if present
        if (idParam != null && !idParam.isEmpty()) {
          try {
            issue.setId(Long.parseLong(idParam));
          } catch (NumberFormatException e) {
            // Invalid ID, skip
          }
        }

        // Set name
        if (nameParam != null) {
          issue.setName(nameParam.trim());
        }

        srfCrossCuttingIssues.add(issue);
      }
    }
  }


  @Override
  public void prepare() throws Exception {

    if (this.isHttpPost()) {
      // POST: Clear lists and bind manually from request
      slosList = new ArrayList<>();
      srfCrossCuttingIssues = new ArrayList<>();
      bindSlosFromRequest();
      bindCrossCuttingIssuesFromRequest();
    } else {
      // GET: Load from database
      slosList = srfSloManager.findAll();
      if (slosList == null) {
        slosList = new ArrayList<>();
      }
      srfCrossCuttingIssues = srfCrossCuttingIssueManager.findAll();
      if (srfCrossCuttingIssues == null) {
        srfCrossCuttingIssues = new ArrayList<>();
      }
    }

  }


  @Override
  public String save() {
    if (!this.canAccessSuperAdmin()) {
      return NOT_AUTHORIZED;
    }

    // ================== Save SLOs Pattern ==================
    // Step 1: Collect IDs submitted in form
    List<Long> inputSloIds = (slosList != null)
      ? slosList.stream().filter(slo -> slo.getId() != null).map(SrfSlo::getId).toList()
      : new ArrayList<>();

    // Step 2: Take snapshot of existing records BEFORE save loop
    List<SrfSlo> existingBeforeSave = srfSloManager.findAll();
    if (existingBeforeSave == null) {
      existingBeforeSave = new ArrayList<>();
    }

    // Step 3 & 4: Save all SLOs (new and updates) and their nested indicators
    if (slosList != null) {
      for (SrfSlo slo : slosList) {
        // Save the SLO first
        SrfSlo savedSlo = srfSloManager.saveSrfSlo(slo);

        // Take snapshot of existing indicators for this SLO BEFORE save loop
        List<SrfSloIndicator> existingIndicatorsBeforeSave = new ArrayList<>();
        if (savedSlo.getId() != null) {
          SrfSlo existingSlo = srfSloManager.getSrfSloById(savedSlo.getId());
          if (existingSlo != null && existingSlo.getSrfSloIndicators() != null) {
            existingIndicatorsBeforeSave.addAll(existingSlo.getSrfSloIndicators());
          }
        }

      // Save all indicators for this SLO, then collect their IDs AFTER save
        List<Long> inputIndicatorIds = new ArrayList<>();
        if (slo.getSrfSloIndicators() != null) {
          for (SrfSloIndicator indicator : slo.getSrfSloIndicators()) {
            indicator.setSrfSlo(savedSlo);
            SrfSloIndicator savedIndicator = srfSloIndicatorManager.saveSrfSloIndicator(indicator);
            // Add ID AFTER save (new indicators now have ID)
            if (savedIndicator.getId() != null) {
              inputIndicatorIds.add(savedIndicator.getId());
            }

            // Take snapshot of existing targets for this indicator BEFORE save
            List<SrfSloIndicatorTarget> existingTargetsBeforeSave = new ArrayList<>();
            if (savedIndicator.getId() != null) {
              SrfSloIndicator existingIndicator = srfSloIndicatorManager.getSrfSloIndicatorById(savedIndicator.getId());
              if (existingIndicator != null && existingIndicator.getSrfSloIndicatorTargets() != null) {
                existingTargetsBeforeSave.addAll(existingIndicator.getSrfSloIndicatorTargets());
              }
            }

            // Save all targets for this indicator, then collect their IDs AFTER save
            List<Long> inputTargetIds = new ArrayList<>();
            if (indicator.getSrfSloIndicatorTargets() != null) {
              for (SrfSloIndicatorTarget target : indicator.getSrfSloIndicatorTargets()) {
                target.setSrfSloIndicator(savedIndicator);
                SrfSloIndicatorTarget savedTarget = srfSloIndicatorTargetManager.saveSrfSloIndicatorTarget(target);
                // Add ID AFTER save (new targets now have ID)
                if (savedTarget.getId() != null) {
                  inputTargetIds.add(savedTarget.getId());
                }
              }
            }

            // Delete targets not in input (from snapshot, not fresh query)
            for (SrfSloIndicatorTarget existingTarget : existingTargetsBeforeSave) {
              if (existingTarget.getId() != null && !inputTargetIds.contains(existingTarget.getId())) {
                srfSloIndicatorTargetManager.deleteSrfSloIndicatorTarget(existingTarget.getId());
              }
            }
          }
        }

        // Delete indicators not in input (from snapshot, not fresh query)
        for (SrfSloIndicator existingIndicator : existingIndicatorsBeforeSave) {
          if (existingIndicator.getId() != null && !inputIndicatorIds.contains(existingIndicator.getId())) {
            srfSloIndicatorManager.deleteSrfSloIndicator(existingIndicator.getId());
          }
        }
      }
    }

    // Step 5: Delete SLOs not in input (from snapshot, not fresh query)
    for (SrfSlo existingSlo : existingBeforeSave) {
      if (existingSlo.getId() != null && !inputSloIds.contains(existingSlo.getId())) {
        srfSloManager.deleteSrfSlo(existingSlo.getId());
      }
    }


    // ================== Save Cross-Cutting Issues Pattern ==================
    // Step 1: Collect IDs submitted in form
    List<Long> inputIssueIds = (srfCrossCuttingIssues != null)
      ? srfCrossCuttingIssues.stream().filter(issue -> issue.getId() != null)
        .map(SrfCrossCuttingIssue::getId).toList()
      : new ArrayList<>();

    // Step 2: Take snapshot of existing records BEFORE save loop
    List<SrfCrossCuttingIssue> existingIssuesBeforeSave = srfCrossCuttingIssueManager.findAll();
    if (existingIssuesBeforeSave == null) {
      existingIssuesBeforeSave = new ArrayList<>();
    }

    // Step 3 & 4: Save all issues (new and updates)
    if (srfCrossCuttingIssues != null) {
      for (SrfCrossCuttingIssue issue : srfCrossCuttingIssues) {
        srfCrossCuttingIssueManager.saveSrfCrossCuttingIssue(issue);
      }
    }

    // Step 5: Delete issues not in input (from snapshot, not fresh query)
    for (SrfCrossCuttingIssue existingIssue : existingIssuesBeforeSave) {
      if (existingIssue.getId() != null && !inputIssueIds.contains(existingIssue.getId())) {
        srfCrossCuttingIssueManager.deleteSrfCrossCuttingIssue(existingIssue.getId());
      }
    }

    return SUCCESS;
  }


  public void setIdoList(Map<Long, String> idoList) {
    this.idoList = idoList;
  }


  public void setSlosList(List<SrfSlo> slosList) {
    this.slosList = slosList;
  }


  public void setSrfCrossCuttingIssues(List<SrfCrossCuttingIssue> srfCrossCuttingIssues) {
    this.srfCrossCuttingIssues = srfCrossCuttingIssues;
  }


  @Override
  public void validate() {
    // Validation logic can be added here if needed
  }

}
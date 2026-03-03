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
import org.cgiar.ccafs.marlo.data.manager.SrfIdoManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIdoManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.SrfCrossCuttingIssue;
import org.cgiar.ccafs.marlo.data.model.SrfIdo;
import org.cgiar.ccafs.marlo.data.model.SrfSlo;
import org.cgiar.ccafs.marlo.data.model.SrfSloIdo;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.struts2.ServletActionContext;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 */
public class IDOsAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;


  private static final String IDOS_LIST_PREFIX = "idosList[";
  private static final String SRF_SUB_IDOS_PREFIX = "].srfSubIdos[";
  private static final String ID_SUFFIX = "].id";
  private static final String DESCRIPTION_SUFFIX = "].description";
  private static final String IS_CROSS_CUTTING_SUFFIX = "].isCrossCutting";
  private static final String SRF_CROSS_CUTTING_ISSUE_ID_SUFFIX = "].srfCrossCuttingIssue.id";
  private static final String SRF_SLO_IDOS_SUFFIX = "].srfSloIdos";

  private HashMap<Long, String> idoList;

  private final SrfSloManager srfSloManager;
  private final SrfIdoManager srfIdoManager;
  private final SrfCrossCuttingIssueManager srfCrossCuttingIssueManager;
  private final SrfSubIdoManager srfSubIdoManager;
  private final SrfSloIdoManager srfSloIdoManager;

  private List<SrfSlo> slosList;

  private List<SrfIdo> idosList;

  private List<SrfCrossCuttingIssue> srfCrossCuttingIssues;

  // Temporary storage for sub-idos before save to avoid HashSet duplicate issue with null IDs
  // Key format: "idoIndex"
  private Map<String, List<SrfSubIdo>> pendingSubIdos = new HashMap<>();
  
  // Temporary storage for SLO-IDO relationships before save (to avoid null ID issue)
  // Key format: "idoIndex"
  private Map<String, List<SrfSloIdo>> pendingSloIdos = new HashMap<>();


  @Inject
  public IDOsAction(APConfig config, SrfSloManager srfSloManager, SrfIdoManager srfIdoManager,
    SrfCrossCuttingIssueManager srfCrossCuttingIssueManager, SrfSubIdoManager srfSubIdoManager,
    SrfSloIdoManager srfSloIdoManager) {
    super(config);
    this.srfSloManager = srfSloManager;
    this.srfIdoManager = srfIdoManager;
    this.srfCrossCuttingIssueManager = srfCrossCuttingIssueManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.srfSloIdoManager = srfSloIdoManager;
  }

  public HashMap<Long, String> getIdoList() {
    return idoList;
  }


  public List<SrfIdo> getIdosList() {
    return idosList;
  }


  public List<SrfSlo> getSlosList() {
    return slosList;
  }


  public List<SrfCrossCuttingIssue> getSrfCrossCuttingIssues() {
    return srfCrossCuttingIssues;
  }


  @Override
  public void prepare() throws Exception {

    slosList = srfSloManager.findAll();

    idosList = srfIdoManager.findAll();

    srfCrossCuttingIssues = srfCrossCuttingIssueManager.findAll();

    if (this.isHttpPost()) {
      idosList.clear();
      pendingSubIdos.clear();
      // Manual binding from request
      bindIdosFromRequest();
    }

  }

  /**
   * Manual binding for IDOs from request parameters.
   */
  private void bindIdosFromRequest() {
    Map<String, String[]> parameterMap = ServletActionContext.getRequest().getParameterMap();
    idosList = new ArrayList<>();
    int maxIndex = 100;

    for (int index = 0; index < maxIndex; index++) {
      String prefix = IDOS_LIST_PREFIX + index;
      String idParam =
        parameterMap.containsKey(prefix + ID_SUFFIX) ? parameterMap.get(prefix + ID_SUFFIX)[0] : null;
      String descriptionParam = parameterMap.containsKey(prefix + DESCRIPTION_SUFFIX)
        ? parameterMap.get(prefix + DESCRIPTION_SUFFIX)[0] : null;
      String isCrossCuttingParam = parameterMap.containsKey(prefix + IS_CROSS_CUTTING_SUFFIX)
        ? parameterMap.get(prefix + IS_CROSS_CUTTING_SUFFIX)[0] : null;
      String crossCuttingIssueIdParam = parameterMap.containsKey(prefix + SRF_CROSS_CUTTING_ISSUE_ID_SUFFIX)
        ? parameterMap.get(prefix + SRF_CROSS_CUTTING_ISSUE_ID_SUFFIX)[0] : null;

      // If none of the expected parameters exist, stop processing
      if (idParam == null && descriptionParam == null && isCrossCuttingParam == null
        && crossCuttingIssueIdParam == null) {
        continue;
      }

      SrfIdo ido = new SrfIdo();

      // Set ID if present
      if (idParam != null && !idParam.isEmpty()) {
        try {
          ido.setId(Long.parseLong(idParam));
        } catch (NumberFormatException e) {
          // Invalid ID, skip
        }
      }

      // Set fields
      if (descriptionParam != null) {
        ido.setDescription(descriptionParam.trim());
      }

      // Set isCrossCutting
      if (isCrossCuttingParam != null) {
        ido.setIsCrossCutting("true".equalsIgnoreCase(isCrossCuttingParam));
      }

      // Set Cross-Cutting Issue
      if (crossCuttingIssueIdParam != null && !crossCuttingIssueIdParam.isEmpty()) {
        try {
          Long crossCuttingIssueId = Long.parseLong(crossCuttingIssueIdParam);
          SrfCrossCuttingIssue crossCuttingIssue =
            srfCrossCuttingIssueManager.getSrfCrossCuttingIssueById(crossCuttingIssueId);
          ido.setSrfCrossCuttingIssue(crossCuttingIssue);
        } catch (NumberFormatException e) {
          // Invalid ID
        }
      }

      // Bind SLO-IDO relationships - store temporarily to avoid null ID issue
      String[] sloIdosParams = parameterMap.get(prefix + SRF_SLO_IDOS_SUFFIX);
      if (sloIdosParams != null && sloIdosParams.length > 0) {
        List<SrfSloIdo> sloIdosList = new ArrayList<>();
        for (String sloIdStr : sloIdosParams) {
          try {
            Long sloId = Long.parseLong(sloIdStr);
            SrfSlo slo = srfSloManager.getSrfSloById(sloId);
            if (slo != null) {
              SrfSloIdo sloIdo = new SrfSloIdo();
              sloIdo.setSrfSlo(slo);
              // Don't set SrfIdo yet - it will be set after save when it has an ID
              sloIdosList.add(sloIdo);
            }
          } catch (NumberFormatException e) {
            // Invalid SLO ID
          }
        }
        if (!sloIdosList.isEmpty()) {
          String sloIdosKey = String.valueOf(index);
          pendingSloIdos.put(sloIdosKey, sloIdosList);
        }
      }

      // Bind nested sub-idos
      List<SrfSubIdo> subIdos = bindSubIdosFromRequest(index, parameterMap);
      if (subIdos != null && !subIdos.isEmpty()) {
        // Store sub-idos in temporary map to avoid HashSet issue with null IDs
        String key = String.valueOf(index);
        pendingSubIdos.put(key, subIdos);
      }

      idosList.add(ido);
    }
  }

  /**
   * Manual binding for Sub-IDOs (nested) from request parameters.
   */
  private List<SrfSubIdo> bindSubIdosFromRequest(int idoIndex, Map<String, String[]> parameterMap) {
    List<SrfSubIdo> subIdos = new ArrayList<>();
    int maxIndex = 100;

    for (int subIdoIndex = 0; subIdoIndex < maxIndex; subIdoIndex++) {
      String prefix = IDOS_LIST_PREFIX + idoIndex + SRF_SUB_IDOS_PREFIX + subIdoIndex;
      String idParam =
        parameterMap.containsKey(prefix + ID_SUFFIX) ? parameterMap.get(prefix + ID_SUFFIX)[0] : null;
      String descriptionParam = parameterMap.containsKey(prefix + DESCRIPTION_SUFFIX)
        ? parameterMap.get(prefix + DESCRIPTION_SUFFIX)[0] : null;

      // If none of the expected parameters exist, stop processing
      if (idParam == null && descriptionParam == null) {
        continue;
      }

      SrfSubIdo subIdo = new SrfSubIdo();

      // Set ID if present
      if (idParam != null && !idParam.isEmpty()) {
        try {
          subIdo.setId(Long.parseLong(idParam));
        } catch (NumberFormatException e) {
          // Invalid ID, skip
        }
      }

      // Set description
      if (descriptionParam != null) {
        subIdo.setDescription(descriptionParam.trim());
      }

      subIdos.add(subIdo);
    }

    return subIdos;
  }


  @Override
  public String save() {
    if (!this.canAccessSuperAdmin()) {
      return NOT_AUTHORIZED;
    }

    // ================== Save IDOs Pattern ==================
    // Step 1: Collect IDs submitted in form
    List<Long> inputIdoIds =
      (idosList != null) ? idosList.stream().filter(ido -> ido.getId() != null).map(SrfIdo::getId).toList()
        : new ArrayList<>();

    // Step 2: Take snapshot of existing records BEFORE save loop
    List<SrfIdo> existingBeforeSave = srfIdoManager.findAll();
    if (existingBeforeSave == null) {
      existingBeforeSave = new ArrayList<>();
    }

    // Step 3 & 4: Save all IDOs (new and updates) and their nested sub-idos
    if (idosList != null) {
      int idoIndex = 0;
      for (SrfIdo ido : idosList) {
        // Save the IDO first
        SrfIdo savedIdo = srfIdoManager.saveSrfIdo(ido);

        // Save SLO-IDO relationships (now that IDO has an ID)
        String sloIdosKey = String.valueOf(idoIndex);
        List<SrfSloIdo> sloIdosToSave = pendingSloIdos.get(sloIdosKey);
        if (sloIdosToSave != null && !sloIdosToSave.isEmpty()) {
          for (SrfSloIdo sloIdo : sloIdosToSave) {
            sloIdo.setSrfIdo(savedIdo); // Now safe - IDO has an ID
            srfSloIdoManager.saveSrfSloIdo(sloIdo);
          }
          // Add all saved SLO-IDOs to the Set for the model
          savedIdo.setSrfSloIdos(new HashSet<>(sloIdosToSave));
        } else if (ido.getSrfSloIdos() != null && !ido.getSrfSloIdos().isEmpty()) {
          // Handle existing SLO-IDO relationships if any
          for (SrfSloIdo sloIdo : ido.getSrfSloIdos()) {
            sloIdo.setSrfIdo(savedIdo);
            srfSloIdoManager.saveSrfSloIdo(sloIdo);
          }
        }

        // Take snapshot of existing sub-idos for this IDO BEFORE save loop
        List<SrfSubIdo> existingSubIdosBeforeSave = new ArrayList<>();
        if (savedIdo.getId() != null) {
          SrfIdo existingIdo = srfIdoManager.getSrfIdoById(savedIdo.getId());
          if (existingIdo != null && existingIdo.getSrfSubIdos() != null) {
            existingSubIdosBeforeSave.addAll(existingIdo.getSrfSubIdos());
          }
        }

        // Get sub-idos from pending map (stored during binding to avoid HashSet duplicate issue)
        String key = String.valueOf(idoIndex);
        List<SrfSubIdo> subIdosFromRequest = pendingSubIdos.get(key);

        // Save all sub-idos for this IDO, then collect their IDs AFTER save
        List<Long> inputSubIdoIds = new ArrayList<>();
        Set<SrfSubIdo> savedSubIdosSet = new HashSet<>();
        if (subIdosFromRequest != null) {
          for (SrfSubIdo subIdo : subIdosFromRequest) {
            subIdo.setSrfIdo(savedIdo);
            SrfSubIdo savedSubIdo = srfSubIdoManager.saveSrfSubIdo(subIdo);
            // Add ID AFTER save (new sub-idos now have ID)
            if (savedSubIdo.getId() != null) {
              inputSubIdoIds.add(savedSubIdo.getId());
              savedSubIdosSet.add(savedSubIdo); // Add to set now that it has an ID
            }
          }
        }

        // Assign saved sub-idos to the IDO (now they have IDs, Set won't drop duplicates)
        savedIdo.setSrfSubIdos(savedSubIdosSet);

        // Delete sub-idos not in input (from snapshot, not fresh query)
        for (SrfSubIdo existingSubIdo : existingSubIdosBeforeSave) {
          if (existingSubIdo.getId() != null && !inputSubIdoIds.contains(existingSubIdo.getId())) {
            srfSubIdoManager.deleteSrfSubIdo(existingSubIdo.getId());
          }
        }

        idoIndex++; // Increment for next IDO
      }
    }

    // Step 5: Delete IDOs not in input (from snapshot, not fresh query)
    for (SrfIdo existingIdo : existingBeforeSave) {
      if (existingIdo.getId() != null && !inputIdoIds.contains(existingIdo.getId())) {
        srfIdoManager.deleteSrfIdo(existingIdo.getId());
      }
    }

    // Clear pending maps after processing
    pendingSubIdos.clear();
    pendingSloIdos.clear();

    return SUCCESS;
  }


  public void setIdoList(HashMap<Long, String> idoList) {
    this.idoList = idoList;
  }


  public void setIdosList(List<SrfIdo> idosList) {
    this.idosList = idosList;
  }


  public void setSlosList(List<SrfSlo> slosList) {
    this.slosList = slosList;
  }


  public void setSrfCrossCuttingIssues(List<SrfCrossCuttingIssue> srfCrossCuttingIssues) {
    this.srfCrossCuttingIssues = srfCrossCuttingIssues;
  }


  @Override
  public void validate() {
    if (save) {

    }
  }

}
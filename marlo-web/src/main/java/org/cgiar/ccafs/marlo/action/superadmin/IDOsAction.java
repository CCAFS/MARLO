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
    // On POST, avoid exposing the internal list to Struts automatic nested binding.
    // The real POST binding is done manually in prepare().
    if (this.isHttpPost()) {
      return new ArrayList<>();
    }
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
      pendingSloIdos.clear();
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

    System.out.println("=== BINDING DEBUG ===");
    System.out.println("Total parameters received: " + parameterMap.size());
    parameterMap.keySet().stream().filter(k -> k.startsWith("idosList")).forEach(k -> {
      System.out.println("  " + k + " = " + java.util.Arrays.toString(parameterMap.get(k)));
    });
    System.out.println("=== END BINDING DEBUG ===");

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

      System.out.println("Processing IDO index " + index + ": idParam=" + idParam + ", description=" + descriptionParam);

      // Skip if this is a new empty IDO (no ID, empty description)
      if ((idParam == null || idParam.isEmpty()) && (descriptionParam == null || descriptionParam.trim().isEmpty())) {
        System.out.println("  Skipping empty new IDO");
        continue;
      }

      SrfIdo ido = null;

      // If ID exists, load the existing IDO from database (not create a new one)
      if (idParam != null && !idParam.isEmpty()) {
        try {
          Long idoId = Long.parseLong(idParam);
          ido = srfIdoManager.getSrfIdoById(idoId);
          System.out.println("  Loaded existing IDO ID: " + idoId + ", found: " + (ido != null));
        } catch (NumberFormatException e) {
          // Invalid ID, create new
          ido = null;
          System.out.println("  Invalid ID format: " + idParam);
        }
      }

      // If no existing IDO found, create new one
      if (ido == null) {
        ido = new SrfIdo();
        System.out.println("  Created new IDO");
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
          System.out.println("  Cross-cutting issue ID from form: " + crossCuttingIssueId);
          
          // Only update if a valid positive ID is provided
          if (crossCuttingIssueId > 0) {
            SrfCrossCuttingIssue crossCuttingIssue =
              srfCrossCuttingIssueManager.getSrfCrossCuttingIssueById(crossCuttingIssueId);
            
            if (crossCuttingIssue != null) {
              ido.setSrfCrossCuttingIssue(crossCuttingIssue);
              System.out.println("  Set cross-cutting issue: " + crossCuttingIssueId);
            } else {
              // ID doesn't exist in database
              System.out.println("  Cross-cutting issue ID not found in database: " + crossCuttingIssueId);
              // For existing IDOs, don't modify; for new ones, leave as null
              if (ido.getId() == null) {
                ido.setSrfCrossCuttingIssue(null);
              }
            }
          } else {
            // -1 or 0 means "no selection" - only clear for NEW IDOs, preserve for existing
            System.out.println("  No cross-cutting issue selected (ID: " + crossCuttingIssueId + ")");
            if (ido.getId() == null) {
              // New IDO - ensure it's null
              ido.setSrfCrossCuttingIssue(null);
              System.out.println("    New IDO, cleared cross-cutting issue");
            } else {
              // Existing IDO - don't touch it, preserve current value from database
              System.out.println("    Existing IDO, preserving current cross-cutting issue from database");
            }
          }
        } catch (NumberFormatException e) {
          System.out.println("  Invalid cross-cutting issue ID format: " + crossCuttingIssueIdParam);
          // For new IDOs, ensure it's null
          if (ido.getId() == null) {
            ido.setSrfCrossCuttingIssue(null);
          }
        }
      } else {
        // No cross-cutting issue parameter provided
        if (ido.getId() == null) {
          // New IDO - ensure it's null
          ido.setSrfCrossCuttingIssue(null);
        }
        // For existing IDOs, don't touch it (parameter not in form)
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

    System.out.println("=== BINDING SUMMARY ===");
    System.out.println("Total IDOs bound: " + idosList.size());
    idosList.forEach(ido -> System.out.println("  IDO: id=" + ido.getId() + ", description=" + ido.getDescription()));
    System.out.println("pendingSloIdos entries: " + pendingSloIdos.size());
    pendingSloIdos.forEach((k, v) -> System.out.println("  Key " + k + ": " + v.size() + " SLO-IDOs"));
    System.out.println("pendingSubIdos entries: " + pendingSubIdos.size());
    pendingSubIdos.forEach((k, v) -> System.out.println("  Key " + k + ": " + v.size() + " Sub-IDOs"));
    System.out.println("=== END BINDING SUMMARY ===");
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

      SrfSubIdo subIdo = null;

      // If ID exists, load the existing SubIdo from database
      if (idParam != null && !idParam.isEmpty()) {
        try {
          Long subIdoId = Long.parseLong(idParam);
          subIdo = srfSubIdoManager.getSrfSubIdoById(subIdoId);
        } catch (NumberFormatException e) {
          // Invalid ID, create new
          subIdo = null;
        }
      }

      // If no existing SubIdo found, create new one
      if (subIdo == null) {
        subIdo = new SrfSubIdo();
      }

      // Set description if present
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

    System.out.println("=== SAVE START ===");
    System.out.println("idosList size: " + (idosList != null ? idosList.size() : "null"));
    if (idosList != null) {
      idosList.forEach(ido -> System.out.println("  Save IDO: id=" + ido.getId() + ", description=" + ido.getDescription()));
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
        // Validation: Ensure cross-cutting issue is valid before save
        // Note: For existing IDOs, we only update if explicitly provided valid value
        // For new IDOs, it's okay to have null
        if (ido.getSrfCrossCuttingIssue() != null) {
          Long crossCuttingIssueId = ido.getSrfCrossCuttingIssue().getId();
          if (crossCuttingIssueId == null || crossCuttingIssueId <= 0) {
            // Invalid ID - for new IDOs, clear to null; for existing, this shouldn't happen
            if (ido.getId() == null) {
              System.out.println("  New IDO: Clearing invalid cross-cutting issue ID: " + crossCuttingIssueId);
              ido.setSrfCrossCuttingIssue(null);
            } else {
              // Existing IDO with invalid incoming value: restore persisted relation to avoid identifier mutation errors
              SrfIdo persistedIdo = srfIdoManager.getSrfIdoById(ido.getId());
              if (persistedIdo != null) {
                ido.setSrfCrossCuttingIssue(persistedIdo.getSrfCrossCuttingIssue());
                System.out.println("  WARNING: Restored existing cross-cutting issue from database for IDO " + ido.getId());
              }
            }
          } else {
            // Verify it exists in database
            SrfCrossCuttingIssue validated = srfCrossCuttingIssueManager.getSrfCrossCuttingIssueById(crossCuttingIssueId);
            if (validated == null) {
              System.out.println("  WARNING: Cross-cutting issue ID doesn't exist in database: " + crossCuttingIssueId);
              if (ido.getId() == null) {
                // New IDO - clear it
                ido.setSrfCrossCuttingIssue(null);
              } else {
                // Existing IDO with invalid incoming value: restore persisted relation
                SrfIdo persistedIdo = srfIdoManager.getSrfIdoById(ido.getId());
                if (persistedIdo != null) {
                  ido.setSrfCrossCuttingIssue(persistedIdo.getSrfCrossCuttingIssue());
                  System.out.println("  WARNING: Restored existing cross-cutting issue from database for IDO " + ido.getId());
                }
              }
            }
          }
        }
        
        // Log before save to ensure ID is present
        System.out.println("  Before save - IDO: id=" + ido.getId() + ", description=" + ido.getDescription() + 
          ", cross_cutting_issue=" + (ido.getSrfCrossCuttingIssue() != null ? ido.getSrfCrossCuttingIssue().getId() : "null"));
        
        // Save the IDO first (saveSrfIdo handles merge for existing entities via update())
        SrfIdo savedIdo = srfIdoManager.saveSrfIdo(ido);
        
        // Log after save
        System.out.println("  After save - IDO: id=" + savedIdo.getId() + ", description=" + savedIdo.getDescription());

        // ========== SLO-IDOs: Take snapshot BEFORE any changes ==========
        List<SrfSloIdo> existingSloIdosBeforeSave = new ArrayList<>();
        if (savedIdo.getId() != null) {
          SrfIdo existingIdo = srfIdoManager.getSrfIdoById(savedIdo.getId());
          if (existingIdo != null && existingIdo.getSrfSloIdos() != null) {
            existingSloIdosBeforeSave.addAll(existingIdo.getSrfSloIdos());
          }
        }

        // Save SLO-IDO relationships (now that IDO has an ID)
        String sloIdosKey = String.valueOf(idoIndex);
        List<SrfSloIdo> sloIdosToSave = pendingSloIdos.get(sloIdosKey);

        // Save new SLO-IDO relationships and collect IDs
        List<Long> inputSloIdoIds = new ArrayList<>();
        Set<SrfSloIdo> savedSloIdosSet = new HashSet<>();
        if (sloIdosToSave != null && !sloIdosToSave.isEmpty()) {
          for (SrfSloIdo sloIdo : sloIdosToSave) {
            sloIdo.setSrfIdo(savedIdo); // Now safe - IDO has an ID
            SrfSloIdo savedSloIdo = srfSloIdoManager.saveSrfSloIdo(sloIdo);
            if (savedSloIdo.getId() != null) {
              inputSloIdoIds.add(savedSloIdo.getId());
              savedSloIdosSet.add(savedSloIdo);
            }
          }
        }

        // Assign saved SLO-IDOs to the IDO
        savedIdo.setSrfSloIdos(savedSloIdosSet);

        // Delete SLO-IDOs not in input
        System.out.println("=== SLO-IDO Debug for IDO " + idoIndex + " ===");
        System.out.println("Existing SLO-IDO IDs before save: " + existingSloIdosBeforeSave.stream().map(SrfSloIdo::getId).toList());
        System.out.println("Input SLO-IDO IDs after save: " + inputSloIdoIds);
        for (SrfSloIdo existingSloIdo : existingSloIdosBeforeSave) {
          if (existingSloIdo.getId() != null && !inputSloIdoIds.contains(existingSloIdo.getId())) {
            System.out.println("Deleting SLO-IDO with ID: " + existingSloIdo.getId());
            srfSloIdoManager.deleteSrfSloIdo(existingSloIdo.getId());
          }
        }
        System.out.println("=== End SLO-IDO Debug ===");

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

    System.out.println("=== SAVE END (SUCCESS) ===");

    return SUCCESS;
  }

  public void setIdoList(HashMap<Long, String> idoList) {
    this.idoList = idoList;
  }


  public void setIdosList(List<SrfIdo> idosList) {
    // On POST we use manual binding from request parameters in prepare().
    // Ignore automatic setter-based replacement to avoid inconsistent state.
    if (!this.isHttpPost()) {
      this.idosList = idosList;
    }
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
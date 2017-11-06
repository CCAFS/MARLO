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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Christian Garcia - CIAT/CCAFS
 */
public class InstitutionsByBudgetTypeAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = -8182788196525839215L;

  private List<Map<String, Object>> institutions;
  private CrpPpaPartnerManager crpPpaPartnerManager;

  private long budgetTypeID;


  private InstitutionManager institutionManager;


  @Inject
  public InstitutionsByBudgetTypeAction(APConfig config, InstitutionManager institutionManager,
    CrpPpaPartnerManager crpPpaPartnerManager) {
    super(config);

    this.institutionManager = institutionManager;
    this.crpPpaPartnerManager = crpPpaPartnerManager;

  }

  @Override
  public String execute() throws Exception {

    List<Institution> institutionsPpa = new ArrayList<>();

    List<CrpPpaPartner> ppaPartners = crpPpaPartnerManager.findAll().stream()
      .filter(c -> c.getCrp().getId().longValue() == this.getCrpID() && c.isActive()).collect(Collectors.toList());

    for (CrpPpaPartner crpPpaPartner : ppaPartners) {
      institutionsPpa.add(crpPpaPartner.getInstitution());
    }

    institutions = new ArrayList<>();
    Map<String, Object> institution;
    List<Institution> institutionsType = new ArrayList<>();


    if (budgetTypeID == 4) {
      institutionsType = institutionManager.findPPAInstitutions(this.getCrpID());
    } else {

      if (budgetTypeID == 1) {
        institutionsType = institutionManager.findAll().stream()
          .filter(i -> i.isActive() && i.getInstitutionType().getId().intValue() == 3).collect(Collectors.toList());
      } else {
        institutionsType = institutionManager.findAll().stream().filter(i -> i.isActive()).collect(Collectors.toList());
        institutionsType.removeAll(institutionsPpa);
      }


    }


    for (Institution i : institutionsType) {
      institution = new HashMap<>();
      institution.put("id", i.getId());
      institution.put("name", i.getComposedName());
      institutions.add(institution);
    }

    return SUCCESS;

  }

  public List<Map<String, Object>> getInstitutions() {
    return institutions;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    // budgetTypeID = Long.parseLong(StringUtils.trim(((String[])
    // parameters.get(APConstants.BUDGET_TYPE_REQUEST_ID))[0]));

    Map<String, Parameter> parameters = this.getParameters();
    budgetTypeID =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.BUDGET_TYPE_REQUEST_ID).getMultipleValues()[0]));

  }

  public void setInstitutions(List<Map<String, Object>> institutions) {
    this.institutions = institutions;
  }


}

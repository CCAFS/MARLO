/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpPpaPartnersAction extends BaseAction {


  private static final long serialVersionUID = -8561096521514225205L;


  private InstitutionManager institutionManager;

  private CrpManager crpManager;


  private List<Institution> institutions;

  private List<Institution> crpInstitutions;


  private Crp loggedCrp;

  @Inject
  public CrpPpaPartnersAction(APConfig config, InstitutionManager institutionManager, CrpManager crpManager) {
    super(config);
    this.institutionManager = institutionManager;
    this.crpManager = crpManager;
  }

  public List<Institution> getCrpInstitutions() {
    return crpInstitutions;
  }

  public List<Institution> getInstitutions() {
    return institutions;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  @Override
  public void prepare() throws Exception {
    super.prepare();
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    crpInstitutions = new ArrayList<>();
    for (CrpPpaPartner partner : loggedCrp.getCrpPpaPartners()) {
      crpInstitutions.add(partner.getInstitution());
    }


    institutions = institutionManager.findAll();

    System.out.println();

  }

  public void setCrpInstitutions(List<Institution> crpInstitutions) {
    this.crpInstitutions = crpInstitutions;
  }

  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

}

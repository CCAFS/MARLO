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
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class PartnerRequestAction extends BaseAction {


  private static final long serialVersionUID = -4592281983603538935L;


  private PartnerRequestManager partnerRequestManager;

  private InstitutionManager institutionManager;

  private InstitutionTypeManager institutionTypeManager;
  private List<PartnerRequest> partners;

  public PartnerRequestAction(APConfig config, PartnerRequestManager partnerRequestManager,
    InstitutionManager institutionManager, InstitutionTypeManager institutionTypeManager) {
    super(config);
    this.partnerRequestManager = partnerRequestManager;
    this.institutionManager = institutionManager;
    this.institutionTypeManager = institutionTypeManager;

  }

  public List<PartnerRequest> getPartners() {
    return partners;
  }

  @Override
  public void prepare() throws Exception {
    partners = new ArrayList<>(
      partnerRequestManager.findAll().stream().filter(pr -> pr.isActive()).collect(Collectors.toList()));
  }

  public void setPartners(List<PartnerRequest> partners) {
    this.partners = partners;
  }

}

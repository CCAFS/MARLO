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
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class PartnerRequestAction extends BaseAction {


  private static final long serialVersionUID = -4592281983603538935L;

  private PartnerRequestManager partnerRequestManager;
  private InstitutionManager institutionManager;
  private InstitutionTypeManager institutionTypeManager;
  private LocElementManager locElementManager;


  private List<PartnerRequest> partners;
  private long requestID;

  @Inject
  public PartnerRequestAction(APConfig config, PartnerRequestManager partnerRequestManager,
    InstitutionManager institutionManager, InstitutionTypeManager institutionTypeManager,
    LocElementManager locElementManager) {
    super(config);
    this.partnerRequestManager = partnerRequestManager;
    this.institutionManager = institutionManager;
    this.institutionTypeManager = institutionTypeManager;
    this.locElementManager = locElementManager;

  }

  public String addPartner() {

    PartnerRequest partnerRequest = partnerRequestManager.getPartnerRequestById(requestID);

    Institution institution = new Institution();

    institution.setName(partnerRequest.getPartnerName());
    institution.setWebsiteLink(partnerRequest.getWebPage());

    InstitutionType institutionType =
      institutionTypeManager.getInstitutionTypeById(partnerRequest.getInstitutionType().getId());
    institution.setInstitutionType(institutionType);

    institution.setAcronym(partnerRequest.getAcronym());
    institution.setCity(partnerRequest.getCity());

    LocElement locElement = locElementManager.getLocElementById(partnerRequest.getLocElement().getId());
    institution.setLocElement(locElement);

    institution.setAdded(new Date());

    if (partnerRequest.getInstitution() != null) {
      Institution institutionHq = institutionManager.getInstitutionById(partnerRequest.getInstitution().getId());
      institution.setHeadquarter(institutionHq);
    }

    institutionManager.saveInstitution(institution);

    partnerRequest.setAcepted(new Boolean(true));
    partnerRequest.setActive(false);
    partnerRequestManager.savePartnerRequest(partnerRequest);


    return SUCCESS;

  }

  public List<PartnerRequest> getPartners() {
    return partners;
  }

  public long getRequestID() {
    return requestID;
  }

  @Override
  public void prepare() throws Exception {

    if (partnerRequestManager.findAll() != null) {
      partners = new ArrayList<>(
        partnerRequestManager.findAll().stream().filter(pr -> pr.isActive()).collect(Collectors.toList()));
    } else {
      partners = new ArrayList<>();
    }
  }

  public String removePartner() {

    PartnerRequest partnerRequest = partnerRequestManager.getPartnerRequestById(requestID);
    partnerRequest.setAcepted(new Boolean(false));
    partnerRequestManager.savePartnerRequest(partnerRequest);
    partnerRequestManager.deletePartnerRequest(partnerRequest.getId());

    return SUCCESS;

  }

  public void setPartners(List<PartnerRequest> partners) {
    this.partners = partners;
  }

  public void setRequestID(long requestID) {
    this.requestID = requestID;
  }

}

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

package org.cgiar.ccafs.marlo.action.json.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class EditPartnerRequestAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 821788435993637711L;
  // Managers
  private PartnerRequestManager partnerRequestManager;
  private InstitutionTypeManager institutionTypeManager;
  private LocElementManager locElementManager;
  // Variables
  private String requestID;
  private String name;
  private String acronym;
  private String webPage;
  private String type;
  private String country;
  private boolean success;


  @Inject
  public EditPartnerRequestAction(APConfig config, PartnerRequestManager partnerRequestManager,
    InstitutionTypeManager institutionTypeManager, LocElementManager locElementManager) {
    super(config);
    this.partnerRequestManager = partnerRequestManager;
    this.institutionTypeManager = institutionTypeManager;
    this.locElementManager = locElementManager;
    // TODO Auto-generated constructor stub
  }


  @Override
  public String execute() throws Exception {
    success = true;
    try {
      PartnerRequest partnerRequest = partnerRequestManager.getPartnerRequestById(Long.parseLong(requestID));
      if (name != null && !name.isEmpty()) {
        partnerRequest.setPartnerName(name);
      } else {
        partnerRequest.setPartnerName("");
      }
      if (acronym != null && !acronym.isEmpty()) {
        partnerRequest.setAcronym(acronym);
      } else {
        partnerRequest.setAcronym("");
      }
      if (webPage != null && !webPage.isEmpty()) {
        partnerRequest.setWebPage(webPage);
      } else {
        partnerRequest.setWebPage("");
      }
      if (type != null && !type.isEmpty()) {
        Long typeID = Long.parseLong(type);
        if (typeID != null && typeID != 0) {
          InstitutionType institutionType = institutionTypeManager.getInstitutionTypeById(typeID);
          partnerRequest.setInstitutionType(institutionType);
        }
      }
      if (country != null && !country.isEmpty()) {
        LocElement locElement = locElementManager.getLocElementByISOCode(country);
        partnerRequest.setLocElement(locElement);
      }
      partnerRequest.setModifiedBy(this.getCurrentUser());
      partnerRequestManager.savePartnerRequest(partnerRequest);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      success = false;
    }
    return SUCCESS;
  }


  public String getAcronym() {
    return acronym;
  }


  public String getCountry() {
    return country;
  }


  public String getName() {
    return name;
  }


  public String getRequestID() {
    return requestID;
  }


  public String getType() {
    return type;
  }


  public String getWebPage() {
    return webPage;
  }


  public boolean isSuccess() {
    return success;
  }

  @Override
  public void prepare() throws Exception {
    success = true;
    try {
      Map<String, Object> parameters = this.getParameters();
      requestID = StringUtils.trim(((String[]) parameters.get(APConstants.PARTNER_REQUEST_ID))[0]);
      name = StringUtils.trim(((String[]) parameters.get(APConstants.INSTITUTION_NAME))[0]);
      acronym = StringUtils.trim(((String[]) parameters.get("institutionAcronym"))[0]);
      webPage = StringUtils.trim(((String[]) parameters.get("institutionWebPage"))[0]);
      type = StringUtils.trim(((String[]) parameters.get(APConstants.INSTITUTION_TYPE_REQUEST_ID))[0]);
      country = StringUtils.trim(((String[]) parameters.get(APConstants.COUNTRY_REQUEST_ID))[0]);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      success = false;
    }
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setRequestID(String requestID) {
    this.requestID = requestID;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }


  public void setType(String type) {
    this.type = type;
  }


  public void setWebPage(String webPage) {
    this.webPage = webPage;
  }


}

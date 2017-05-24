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
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class InstitutionsByTypeAndCountryAction extends BaseAction {


  private static final long serialVersionUID = -4856767006897553998L;


  private List<Map<String, Object>> institutions;

  private long institutionTypeID;

  private long countryID;

  private InstitutionTypeManager institutionTypeManager;
  private LocElementManager locElementManager;
  private InstitutionManager institutionManager;


  @Inject
  public InstitutionsByTypeAndCountryAction(APConfig config, InstitutionTypeManager institutionTypeManager,
    LocElementManager locElementManager, InstitutionManager institutionManager) {
    super(config);
    this.institutionTypeManager = institutionTypeManager;
    this.locElementManager = locElementManager;
    this.institutionManager = institutionManager;

  }

  @Override
  public String execute() throws Exception {

    LocElement locElement = locElementManager.getLocElementById(countryID);
    InstitutionType institutionType = institutionTypeManager.getInstitutionTypeById(institutionTypeID);

    institutions = new ArrayList<>();
    Map<String, Object> institution;
    List<Institution> institutionsByTypeAndCountry =
      institutionManager.findAll().stream().filter(i -> i.isActive()).collect(Collectors.toList());


    if (institutionTypeID == -1 && locElement != null) {
      for (Institution i : institutionsByTypeAndCountry.stream().filter(i -> i.isActive())
        .collect(Collectors.toList())) {
        institution = new HashMap<>();
        institution.put("id", i.getId());
        institution.put("name", i.getComposedName());
        institutions.add(institution);
      }

      return SUCCESS;
    }

    if (countryID == -1 && institutionType != null) {
      for (Institution i : institutionsByTypeAndCountry.stream()
        .filter(i -> i.isActive() && i.getInstitutionType().equals(institutionType)).collect(Collectors.toList())) {
        institution = new HashMap<>();
        institution.put("id", i.getId());
        institution.put("name", i.getComposedName());
        institutions.add(institution);
      }

      return SUCCESS;
    }

    if (institutionTypeID == -1 && countryID == -1) {
      for (Institution i : institutionsByTypeAndCountry) {
        institution = new HashMap<>();
        institution.put("id", i.getId());
        institution.put("name", i.getComposedName());
        institutions.add(institution);
      }

      return SUCCESS;
    }


    return SUCCESS;
  }

  public List<Map<String, Object>> getInstitutions() {
    return institutions;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    institutionTypeID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.INSTITUTION_TYPE_REQUEST_ID))[0]));
    if (parameters.get(APConstants.COUNTRY_REQUEST_ID) != null) {
      countryID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.COUNTRY_REQUEST_ID))[0]));
    }
  }

  public void setInstitutions(List<Map<String, Object>> institutions) {
    this.institutions = institutions;
  }


}

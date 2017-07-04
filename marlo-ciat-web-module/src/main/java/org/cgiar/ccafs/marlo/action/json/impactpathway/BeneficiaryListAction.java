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

package org.cgiar.ccafs.marlo.action.json.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConfig;
import org.cgiar.ccafs.marlo.data.manager.ICenterBeneficiaryTypeManager;
import org.cgiar.ccafs.marlo.data.model.CenterBeneficiary;
import org.cgiar.ccafs.marlo.data.model.CenterBeneficiaryType;
import org.cgiar.ccafs.marlo.utils.APConstants;

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
public class BeneficiaryListAction extends BaseAction {


  private static final long serialVersionUID = 2977363867019388402L;

  private List<Map<String, Object>> beneficiaries;

  private String message;

  private long beneficiaryTypeID;

  private ICenterBeneficiaryTypeManager beneficiaryTypeService;

  @Inject
  public BeneficiaryListAction(APConfig config, ICenterBeneficiaryTypeManager beneficiaryTypeService) {
    super(config);
    this.beneficiaryTypeService = beneficiaryTypeService;
  }

  @Override
  public String execute() throws Exception {

    beneficiaries = new ArrayList<>();

    Map<String, Object> beneficiary;

    CenterBeneficiaryType beneficiaryType = beneficiaryTypeService.getBeneficiaryTypeById(beneficiaryTypeID);

    if (beneficiaryType != null) {

      CenterBeneficiary nonSpec = beneficiaryType.getBeneficiaries().stream()
        .filter(b -> b.isActive() && b.getName().equals("Non Specific")).collect(Collectors.toList()).get(0);
      beneficiary = new HashMap<>();
      beneficiary.put("id", nonSpec.getId());
      beneficiary.put("name", nonSpec.getName());
      beneficiaries.add(beneficiary);

      for (CenterBeneficiary b : beneficiaryType.getBeneficiaries().stream()
        .filter(b -> b.isActive() && !b.getName().equals("Non Specific")).collect(Collectors.toList())) {

        beneficiary = new HashMap<>();
        beneficiary.put("id", b.getId());
        beneficiary.put("name", b.getName());

        beneficiaries.add(beneficiary);

      }

    }

    return SUCCESS;
  }

  public List<Map<String, Object>> getBeneficiaries() {
    return beneficiaries;
  }

  public long getBeneficiaryTypeID() {
    return beneficiaryTypeID;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    beneficiaryTypeID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.BENEFICIARY_TYPE_ID))[0]));
  }

  public void setBeneficiaries(List<Map<String, Object>> beneficiaries) {
    this.beneficiaries = beneficiaries;
  }

  public void setBeneficiaryTypeID(long beneficiaryTypeID) {
    this.beneficiaryTypeID = beneficiaryTypeID;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}

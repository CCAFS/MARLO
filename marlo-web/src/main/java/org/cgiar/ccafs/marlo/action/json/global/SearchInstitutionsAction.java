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

package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sebastián Amariles - CIAT/CCAFS
 */
public class SearchInstitutionsAction extends BaseAction {


  private static final long serialVersionUID = 281018603716118132L;


  private static Logger LOG = LoggerFactory.getLogger(SearchInstitutionsAction.class);

  // Managers
  private InstitutionManager institutionManager;

  // Parameters
  private String queryParameter;
  private List<Map<String, Object>> institutions;
  private int withPPA;
  private int onlyPPA;


  @Inject
  public SearchInstitutionsAction(APConfig config, InstitutionManager institutionManager) {
    super(config);
    this.institutionManager = institutionManager;
  }

  @Override
  public String execute() throws Exception {
    // Nothing to do here yet.
    return SUCCESS;
  }

  public List<Map<String, Object>> getInstitutions() {
    return institutions;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    // queryParameter = StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]);
    // withPPA = Integer.parseInt(StringUtils.trim(((String[]) parameters.get(APConstants.WITH_PPA_PARAMETER))[0]));
    // onlyPPA = Integer.parseInt(StringUtils.trim(((String[]) parameters.get(APConstants.ONLY_PPA_PARAMETER))[0]));

    Map<String, Parameter> parameters = this.getParameters();
    queryParameter = StringUtils.trim(parameters.get(APConstants.QUERY_PARAMETER).getMultipleValues()[0]);
    withPPA = Integer.parseInt(StringUtils.trim(parameters.get(APConstants.WITH_PPA_PARAMETER).getMultipleValues()[0]));
    onlyPPA = Integer.parseInt(StringUtils.trim(parameters.get(APConstants.ONLY_PPA_PARAMETER).getMultipleValues()[0]));

  }

  /**
   * Search an institution in the database
   * 
   * @return SUCCESS if the search was successfully made.
   * @throws Exception if some error appear.
   */
  public String search() throws Exception {
    List<Institution> institutions =
      institutionManager.searchInstitution(queryParameter, withPPA, onlyPPA, this.getCrpID().longValue());

    this.institutions = new ArrayList<>();
    for (Institution institution : institutions) {
      Map<String, Object> institutionMap = new HashMap<>();
      institutionMap.put("id", institution.getId());
      institutionMap.put("composedName", institution.getComposedName());
      institutionMap.put("acronym", institution.getAcronym());
      institutionMap.put("name", institution.getName());
      institutionMap.put("type", institution.getInstitutionType().getName());
      // Add locations to a set to show it separated by comma
      Set<String> locHash = new TreeSet<String>();
      for (InstitutionLocation il : institution.getInstitutionsLocations()) {
        locHash.add(il.getLocElement().getIsoAlpha2());
      }
      institutionMap.put("countries", StringUtils.join(locHash, ','));
      institutionMap.put("isPPA", institution.isPPA(this.getCrpID(), this.getActualPhase()));
      this.institutions.add(institutionMap);
    }


    LOG.info("The search of institutions by '{}' was made successfully.", queryParameter);
    return SUCCESS;
  }

  public void setInstitutions(List<Map<String, Object>> institutions) {
    this.institutions = institutions;
  }

}

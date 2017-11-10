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
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SimilarStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Garcia - CIAT/CCAFS
 */
public class SearchInstitutionsNameAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = -2906049699515120588L;

  private static Logger LOG = LoggerFactory.getLogger(SearchInstitutionsNameAction.class);

  // Managers
  private InstitutionManager institutionManager;

  // Parameters
  private String name;
  private List<Map<String, Object>> institutions;


  @Inject
  public SearchInstitutionsNameAction(APConfig config, InstitutionManager institutionManager) {
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
    // name = StringUtils.trim(((String[]) parameters.get(APConstants.INSTITUTION_NAME))[0]);

    Map<String, Parameter> parameters = this.getParameters();
    name = StringUtils.trim(parameters.get(APConstants.INSTITUTION_NAME).getMultipleValues()[0]);


  }

  /**
   * Search an institution in the database
   * 
   * @return SUCCESS if the search was successfully made.
   * @throws Exception if some error appear.
   */
  public String search() throws Exception {
    SimilarStringUtils similarUtils = new SimilarStringUtils();
    List<Institution> institutions = institutionManager.findAll().stream()
      .filter(c -> similarUtils.similarity(name, c.getName()) >= 0.7).collect(Collectors.toList());

    this.institutions = new ArrayList<>();
    for (Institution institution : institutions) {
      Map<String, Object> institutionMap = new HashMap<>();
      institutionMap.put("id", institution.getId());
      institutionMap.put("composedName", institution.getComposedName());
      institutionMap.put("acronym", institution.getAcronym());
      institutionMap.put("name", institution.getName());
      institutionMap.put("type", institution.getInstitutionType().getName());


      institutionMap.put("isPPA", institution.isPPA(this.getCrpID()));
      this.institutions.add(institutionMap);
    }


    LOG.info("The search of institutions by '{}' was made successfully.", name);
    return SUCCESS;
  }

  public void setInstitutions(List<Map<String, Object>> institutions) {
    this.institutions = institutions;
  }

}

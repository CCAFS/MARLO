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
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
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
public class PersonsbyPartnerAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 3674950424554096761L;


  private long partnerID;


  private ProjectPartnerManager projectPartnerManager;


  private List<Map<String, Object>> persons;

  @Inject
  public PersonsbyPartnerAction(APConfig config, ProjectPartnerManager projectPartnerManager) {
    super(config);
    this.projectPartnerManager = projectPartnerManager;
  }

  @Override
  public String execute() throws Exception {
    persons = new ArrayList<>();
    Map<String, Object> person;
    ProjectPartner projectPartner = projectPartnerManager.getProjectPartnerById(partnerID);
    for (ProjectPartnerPerson partnerPerson : projectPartner.getProjectPartnerPersons().stream()
      .filter(c -> c.isActive()).collect(Collectors.toList())) {
      person = new HashMap<String, Object>();
      person.put("id", partnerPerson.getId());
      person.put("user", partnerPerson.getComposedCompleteName());
      persons.add(person);
    }
    return SUCCESS;

  }


  public List<Map<String, Object>> getPersons() {
    return persons;
  }


  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    // partnerID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PARTNER_ID))[0]));

    Map<String, Parameter> parameters = this.getParameters();
    partnerID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PARTNER_ID).getMultipleValues()[0]));
  }


  public void setPersons(List<Map<String, Object>> persons) {
    this.persons = persons;
  }


}

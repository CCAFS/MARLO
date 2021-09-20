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

package org.cgiar.ccafs.marlo.action.json.home;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.PolicyHomeDTO;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.dto.GraphCountDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class PolicyGraphsAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -7201590026439955641L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(PolicyGraphsAction.class);

  // Variables
  private List<PolicyHomeDTO> policies;

  // Graph results
  private List<GraphCountDTO> byPolicyType;
  private List<GraphCountDTO> byPolicyLevel;

  @Inject
  public PolicyGraphsAction(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {
    this.byPolicyType = new ArrayList<>();
    this.byPolicyLevel = new ArrayList<>();

    this.policies.stream().collect(Collectors.groupingBy(p -> p.getPolicyType()))
      .forEach((k, v) -> byPolicyType.add(new GraphCountDTO(k, (long) v.size())));
    this.policies.stream()
      .sorted((p1, p2) -> String.CASE_INSENSITIVE_ORDER.compare(p1.getPolicyLevel(), p2.getPolicyLevel()))
      .collect(Collectors.groupingBy(p -> p.getPolicyLevel()))
      .forEach((k, v) -> byPolicyLevel.add(new GraphCountDTO(k, (long) v.size())));

    return SUCCESS;
  }

  public List<GraphCountDTO> getByPolicyLevel() {
    return byPolicyLevel;
  }


  public List<GraphCountDTO> getByPolicyType() {
    return byPolicyType;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void prepare() throws Exception {
    try {
      this.policies = (List<PolicyHomeDTO>) this.getSession().get(APConstants.USER_POLICIES);
      if (policies == null) {
        policies = Collections.emptyList();
      }
    } catch (Exception e) {
      e.printStackTrace();
      this.policies = Collections.emptyList();
    }
  }

  public void setByPolicyLevel(List<GraphCountDTO> byPolicyLevel) {
    this.byPolicyLevel = byPolicyLevel;
  }

  public void setByPolicyType(List<GraphCountDTO> byPolicyType) {
    this.byPolicyType = byPolicyType;
  }
}

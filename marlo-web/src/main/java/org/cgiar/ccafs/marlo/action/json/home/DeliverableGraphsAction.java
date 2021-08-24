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
import org.cgiar.ccafs.marlo.data.dto.DeliverableHomeDTO;
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

public class DeliverableGraphsAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -5773793792616802636L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(DeliverableGraphsAction.class);

  // Variables
  private List<DeliverableHomeDTO> deliverables;

  // Graph results
  private List<GraphCountDTO> byDeliverableType;
  private List<GraphCountDTO> byDeliverableStatus;
  private List<GraphCountDTO> byDeliverableOpenAccess;

  @Inject
  public DeliverableGraphsAction(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {
    this.byDeliverableType = new ArrayList<>();
    this.byDeliverableStatus = new ArrayList<>();
    this.byDeliverableOpenAccess = new ArrayList<>();

    this.deliverables.stream().collect(Collectors.groupingBy(d -> d.getDeliverableType()))
      .forEach((k, v) -> byDeliverableType.add(new GraphCountDTO(k, (long) v.size())));
    this.deliverables.stream().collect(Collectors.groupingBy(d -> d.getStatus()))
      .forEach((k, v) -> byDeliverableStatus.add(new GraphCountDTO(k, (long) v.size())));
    this.deliverables.stream().collect(Collectors.groupingBy(d -> d.getIsOpenAccess()))
      .forEach((k, v) -> byDeliverableOpenAccess.add(new GraphCountDTO(k, (long) v.size())));

    return SUCCESS;
  }

  public List<GraphCountDTO> getByDeliverableOpenAccess() {
    return byDeliverableOpenAccess;
  }

  public List<GraphCountDTO> getByDeliverableStatus() {
    return byDeliverableStatus;
  }

  public List<GraphCountDTO> getByDeliverableType() {
    return byDeliverableType;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void prepare() throws Exception {
    try {
      this.deliverables = (List<DeliverableHomeDTO>) this.getSession().get(APConstants.USER_DELIVERABLES);
      if (deliverables == null) {
        deliverables = Collections.emptyList();
      }
    } catch (Exception e) {
      e.printStackTrace();
      this.deliverables = Collections.emptyList();
    }
  }

  public void setByDeliverableOpenAccess(List<GraphCountDTO> byDeliverableOpenAccess) {
    this.byDeliverableOpenAccess = byDeliverableOpenAccess;
  }

  public void setByDeliverableStatus(List<GraphCountDTO> byDeliverableStatus) {
    this.byDeliverableStatus = byDeliverableStatus;
  }

  public void setByDeliverableType(List<GraphCountDTO> byDeliverableType) {
    this.byDeliverableType = byDeliverableType;
  }
}

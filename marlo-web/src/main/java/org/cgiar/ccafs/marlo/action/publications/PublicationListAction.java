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


package org.cgiar.ccafs.marlo.action.publications;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class PublicationListAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -5176367401132626314L;
  private Crp loggedCrp;
  private CrpManager crpManager;
  private long deliverableID;
  private DeliverableManager deliverableManager;

  @Inject
  public PublicationListAction(APConfig config, CrpManager crpManager, DeliverableManager deliverableManager) {

    super(config);
    this.deliverableManager = deliverableManager;
    this.crpManager = crpManager;

  }


  @Override
  public String add() {

    Deliverable deliverable = new Deliverable();
    deliverable.setYear(this.getCurrentCycleYear());
    deliverable.setCreatedBy(this.getCurrentUser());
    deliverable.setModifiedBy(this.getCurrentUser());
    deliverable.setModificationJustification("New publication created");
    deliverable.setActive(true);
    deliverable.setActiveSince(new Date());
    deliverable.setCrp(loggedCrp);
    deliverable.setCreateDate(new Date());
    deliverable.setIsPublication(true);

    deliverableID = deliverableManager.saveDeliverable(deliverable);

    if (deliverableID > 0) {
      return SUCCESS;
    }

    return INPUT;
  }

  @Override
  public String delete() {

    Map<String, Object> parameters = this.getParameters();
    deliverableID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID))[0]));


    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);

    if (deliverable != null) {
      deliverableManager.deleteDeliverable(deliverableID);
      this.addActionMessage("message:" + this.getText("deleting.success"));
    }
    return SUCCESS;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    try {

      loggedCrp.setDeliverablesList(loggedCrp.getDeliverables().stream()
        .filter(c -> c.getIsPublication() != null && c.getIsPublication().booleanValue() && c.isActive())
        .collect(Collectors.toList()));
    } catch (Exception e) {

    }

  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }
}

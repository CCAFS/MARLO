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

package org.cgiar.ccafs.marlo.action.center.capdev;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CapdevSupportingDocsManager;
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.manager.ICenterDeliverableManager;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class CapdevSupportingDocsAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private CapacityDevelopment capdev;
  private long capdevID;
  private long projectID;
  private long supportingDocID;
  private List<CenterDeliverable> deliverables;

  private CapdevSupportingDocsManager capdevsupportingDocsService;
  private ICapacityDevelopmentService capdevService;
  private ICenterDeliverableManager centerDeliverableSErvice;

  @Inject
  public CapdevSupportingDocsAction(APConfig config, CapdevSupportingDocsManager capdevsupportingDocsService,
    ICapacityDevelopmentService capdevService, ICenterDeliverableManager centerDeliverableSErvice) {
    super(config);
    this.capdevsupportingDocsService = capdevsupportingDocsService;
    this.capdevService = capdevService;
    this.centerDeliverableSErvice = centerDeliverableSErvice;
  }

  @Override
  public String add() {
    CenterDeliverable supportingDoc = new CenterDeliverable();
    capdev = capdevService.getCapacityDevelopmentById(capdevID);
    supportingDoc.setCapdev(capdev);
    supportingDoc.setActive(true);
    supportingDoc.setActiveSince(new Date());
    supportingDoc.setCreatedBy(this.getCurrentUser());
    supportingDoc.setModifiedBy(this.getCurrentUser());
    supportingDocID = centerDeliverableSErvice.saveDeliverable(supportingDoc);

    return SUCCESS;
  }


  @Override
  public String delete() {
    long supportingDocID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter("supportingDocID")));
    CenterDeliverable supportingDoc = centerDeliverableSErvice.getDeliverableById(supportingDocID);
    supportingDoc.setActive(false);
    supportingDoc.setModifiedBy(this.getCurrentUser());
    centerDeliverableSErvice.saveDeliverable(supportingDoc);
    return SUCCESS;
  }


  public CapacityDevelopment getCapdev() {
    return capdev;
  }


  public long getCapdevID() {
    return capdevID;
  }


  public List<CenterDeliverable> getDeliverables() {
    return deliverables;
  }


  public long getProjectID() {
    return projectID;
  }


  public long getSupportingDocID() {
    return supportingDocID;
  }

  @Override
  public void prepare() throws Exception {
    try {
      capdevID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CAPDEV_ID)));
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_ID)));
    } catch (final Exception e) {
      capdevID = -1;
      projectID = 0;
    }
    capdev = capdevService.getCapacityDevelopmentById(capdevID);


    if (capdev.getDeliverables() != null) {
      deliverables =
        new ArrayList<>(capdev.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList()));
    }

  }


  public void setCapdev(CapacityDevelopment capdev) {
    this.capdev = capdev;
  }


  public void setCapdevID(long capdevID) {
    this.capdevID = capdevID;
  }


  public void setDeliverables(List<CenterDeliverable> deliverables) {
    this.deliverables = deliverables;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setSupportingDocID(long supportingDocID) {
    this.supportingDocID = supportingDocID;
  }


}
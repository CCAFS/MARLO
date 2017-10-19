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
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.CapdevSupportingDocs;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
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
  private final CapdevSupportingDocsManager capdevsupportingDocsService;
  private final ICapacityDevelopmentService capdevService;

  @Inject
  public CapdevSupportingDocsAction(APConfig config, CapdevSupportingDocsManager capdevsupportingDocsService,
    ICapacityDevelopmentService capdevService) {
    super(config);
    this.capdevsupportingDocsService = capdevsupportingDocsService;
    this.capdevService = capdevService;
  }

  @Override
  public String add() {
    final CapdevSupportingDocs capdevSupportingDocs = new CapdevSupportingDocs();
    capdev = capdevService.getCapacityDevelopmentById(capdevID);
    capdevSupportingDocs.setCapacityDevelopment(capdev);
    capdevSupportingDocs.setActive(true);
    capdevSupportingDocs.setActiveSince(new Date());
    capdevSupportingDocs.setCreatedBy(this.getCurrentUser());
    capdevSupportingDocs.setModifiedBy(this.getCurrentUser());
    supportingDocID = capdevsupportingDocsService.saveCapdevSupportingDocs(capdevSupportingDocs);

    return SUCCESS;
  }


  @Override
  public String delete() {
    final long supportingDocID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter("supportingDocID")));
    final CapdevSupportingDocs capdevSupportingDocs =
      capdevsupportingDocsService.getCapdevSupportingDocsById(supportingDocID);
    capdevSupportingDocs.setActive(false);
    capdevSupportingDocs.setModifiedBy(this.getCurrentUser());
    capdevsupportingDocsService.saveCapdevSupportingDocs(capdevSupportingDocs);
    return SUCCESS;
  }


  public CapacityDevelopment getCapdev() {
    return capdev;
  }


  public long getCapdevID() {
    return capdevID;
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
    if (capdev.getCapdevSupportingDocs() != null) {
      if (!capdev.getCapdevSupportingDocs().isEmpty()) {
        final List<CapdevSupportingDocs> documentesDB = new ArrayList<>(
          capdev.getCapdevSupportingDocs().stream().filter(d -> d.isActive()).collect(Collectors.toList()));
        Collections.sort(documentesDB, (r1, r2) -> r1.getId().compareTo(r2.getId()));
        capdev.setCapdevSupportingDocs(new HashSet<CapdevSupportingDocs>(documentesDB));
      }
    }
  }


  public void setCapdev(CapacityDevelopment capdev) {
    this.capdev = capdev;
  }

  public void setCapdevID(long capdevID) {
    this.capdevID = capdevID;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setSupportingDocID(long supportingDocID) {
    this.supportingDocID = supportingDocID;
  }


}

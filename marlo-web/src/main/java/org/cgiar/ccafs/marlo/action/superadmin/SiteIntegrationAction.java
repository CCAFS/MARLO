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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.SrfCrossCuttingIssueManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloManager;
import org.cgiar.ccafs.marlo.data.model.SrfCrossCuttingIssue;
import org.cgiar.ccafs.marlo.data.model.SrfSlo;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 */
public class SiteIntegrationAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;


  private HashMap<Long, String> idoList;


  private final SrfSloManager srfSloManager;

  private final SrfCrossCuttingIssueManager srfCrossCuttingIssueManager;

  private List<SrfSlo> slosList;

  private List<SrfCrossCuttingIssue> srfCrossCuttingIssues;


  @Inject
  public SiteIntegrationAction(APConfig config, SrfSloManager srfSloManager,
    SrfCrossCuttingIssueManager srfCrossCuttingIssueManager) {
    super(config);
    this.srfSloManager = srfSloManager;
    this.srfCrossCuttingIssueManager = srfCrossCuttingIssueManager;
  }


  public HashMap<Long, String> getIdoList() {
    return idoList;
  }


  public List<SrfSlo> getSlosList() {
    return slosList;
  }


  public List<SrfCrossCuttingIssue> getSrfCrossCuttingIssues() {
    return srfCrossCuttingIssues;
  }


  @Override
  public void prepare() throws Exception {

    slosList = srfSloManager.findAll();

    srfCrossCuttingIssues = srfCrossCuttingIssueManager.findAll();

    if (this.isHttpPost()) {
      slosList.clear();
    }

  }


  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }


  public void setIdoList(HashMap<Long, String> idoList) {
    this.idoList = idoList;
  }


  public void setSlosList(List<SrfSlo> slosList) {
    this.slosList = slosList;
  }


  @Override
  public void validate() {
    if (save) {

    }
  }

}
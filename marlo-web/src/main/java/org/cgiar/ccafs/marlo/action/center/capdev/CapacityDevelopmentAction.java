/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MiLE).
 * MiLE is free software: you can redistribute it and/or modify
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
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class CapacityDevelopmentAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private CapacityDevelopment capdev;

  private List<CapacityDevelopment> capDevs = new ArrayList<CapacityDevelopment>();

  private final ICapacityDevelopmentService capdevService;

  private long capdevID;
  private int capdevCategory;

  @Inject
  public CapacityDevelopmentAction(APConfig config, ICapacityDevelopmentService capdevService) {
    super(config);
    this.capdevService = capdevService;

  }


  @Override
  public String add() {

    capdevCategory = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter("capdevCategory")));
    System.out.println("capdevCategory del add-->" + capdevCategory);
    capdev = new CapacityDevelopment();
    capdev.setCategory(capdevCategory);
    capdev.setActive(true);
    capdev.setUsersByCreatedBy(this.getCurrentUser());
    capdevID = capdevService.saveCapacityDevelopment(capdev);
    System.out.println("capDevID -->" + capdevID);
    if (capdevID > 0) {
      return SUCCESS;
    } else {
      return NOT_FOUND;
    }


  }


  public CapacityDevelopment getCapdev() {
    return capdev;
  }


  public int getCapdevCategory() {
    return capdevCategory;
  }


  public long getCapdevID() {
    return capdevID;
  }


  public List<CapacityDevelopment> getCapDevs() {
    return capDevs;
  }


  public String list() {
    return SUCCESS;
  }


  @Override
  public void prepare() throws Exception {
    capDevs = capdevService.findAll();
  }


  @Override
  public String save() {
    return super.save();
  }


  public void setCapdev(CapacityDevelopment capdev) {
    this.capdev = capdev;
  }


  public void setCapdevCategory(int capdevCategory) {
    this.capdevCategory = capdevCategory;
  }


  public void setCapdevID(long capdevID) {
    this.capdevID = capdevID;
  }


  public void setCapDevs(List<CapacityDevelopment> capDevs) {
    this.capDevs = capDevs;
  }


}

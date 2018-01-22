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

package org.cgiar.ccafs.marlo.action.center.json.capdev;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.ocs.model.ResourceInfoOCS;
import org.cgiar.ccafs.marlo.ocs.ws.MarloOcsClient;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

public class ResourceOCSServiceAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String resourceID;
  private MarloOcsClient ocsClient;
  private ResourceInfoOCS json;

  @Inject
  public ResourceOCSServiceAction(APConfig config, MarloOcsClient ocsClient) {
    super(config);
    this.ocsClient = ocsClient;
  }

  @Override
  public String execute() throws Exception {
    json = ocsClient.getHRInformation(resourceID);
    return SUCCESS;
  }

  public ResourceInfoOCS getJson() {
    return json;
  }


  public String getResourceID() {
    return resourceID;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    resourceID = StringUtils.trim(parameters.get(APConstants.CAPDEV_PARTICIPANT_CODE_SYNC).getMultipleValues()[0]);
  }


  public void setJson(ResourceInfoOCS json) {
    this.json = json;
  }


  public void setResourceID(String resourceID) {
    this.resourceID = resourceID;
  }


}

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

package org.cgiar.ccafs.marlo.action;

import org.cgiar.ccafs.marlo.ocs.model.AgreementOCS;
import org.cgiar.ccafs.marlo.ocs.ws.MarloOcsClient;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * Action to Test the webservices that MARLO call it (OCS in special), if the service can not sends a positive response
 * struts force to send a 404 httpheader code.
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class TestOCSAction extends BaseAction {


  private static final long serialVersionUID = 1511557973574400249L;


  // OCS Agreement Servcie Class
  private MarloOcsClient ocsClient;
  private AgreementOCS agreement;

  // parameter to fail the service whit intention
  boolean fail;

  @Inject
  public TestOCSAction(APConfig config, MarloOcsClient ocsClient) {
    super(config);
    this.ocsClient = ocsClient;
  }

  @Override
  public String execute() throws Exception {

    // Check the fail parameter
    if (!fail) {
      // Search an existing OCS code.
      agreement = ocsClient.getagreement("D124");
      if (agreement != null) {
        return SUCCESS;
      } else {
        return INPUT;
      }
    } else {
      return INPUT;
    }
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    try {
      this.fail = Boolean.valueOf(StringUtils.trim(((String[]) parameters.get("fail"))[0]));
    } catch (Exception e) {
      this.fail = false;
    }
  }


}

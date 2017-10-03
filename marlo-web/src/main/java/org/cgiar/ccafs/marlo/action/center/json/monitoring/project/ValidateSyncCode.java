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

package org.cgiar.ccafs.marlo.action.center.json.monitoring.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.ocs.model.AgreementOCS;
import org.cgiar.ccafs.marlo.ocs.ws.MarloOcsClient;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ValidateSyncCode extends BaseAction {


  private static final long serialVersionUID = 1176979417490820917L;


  // Sync Variables
  private long syncTypeID;

  private String syncCode;

  // OCS Agreement Servcie Class
  private MarloOcsClient ocsClient;
  private AgreementOCS agreement;

  private ProjectManager projectManager;
  // return value Map
  private Map<String, Object> message;

  @Inject
  public ValidateSyncCode(APConfig config, ProjectManager projectManager, MarloOcsClient ocsClient) {
    super(config);
    this.projectManager = projectManager;
    this.ocsClient = ocsClient;
  }

  @Override
  public String execute() throws Exception {

    message = new HashMap<>();
    message.put("code", syncCode);

    switch (Math.toIntExact(syncTypeID)) {
      case 1:
        agreement = ocsClient.getagreement(syncCode);
        if (agreement != null) {
          message.put("status", true);
        } else {
          message.put("status", false);
        }
        break;

      case 2:
        long projectID = Long.parseLong(syncCode);
        Project project = projectManager.getProjectById(projectID);
        if (project != null) {
          message.put("status", true);
        } else {
          message.put("status", false);
        }
        break;

      default:
        break;
    }

    return SUCCESS;
  }

  public Map<String, Object> getMessage() {
    return message;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    syncTypeID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.CENTER_PROJECT_SYNC_TYPE))[0]));
    syncCode = StringUtils.trim(((String[]) parameters.get(APConstants.CENTER_PROJECT_SYNC_CODE))[0]);

    if (syncTypeID == 2) {
      if (syncCode.toUpperCase().contains("P")) {
        syncCode = syncCode.toUpperCase().replaceFirst("P", "");
      }
    }

  }

  public void setMessage(Map<String, Object> message) {
    this.message = message;
  }

}

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


package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.SafeguardsManager;
import org.cgiar.ccafs.marlo.data.model.Safeguards;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveSafeguardFileAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(RemoveSafeguardFileAction.class);
  private Map<String, Object> save;
  private Long safeguardId;
  private SafeguardsManager safeguardsManager;

  @Inject
  public RemoveSafeguardFileAction(APConfig config, SafeguardsManager safeguardsManager) {
    super(config);
    this.safeguardsManager = safeguardsManager;
  }

  @Override
  public String execute() throws Exception {
    // @param = safeguardID

    save = new HashMap<String, Object>();
    if (safeguardId != null) {

      Safeguards safeguard = new Safeguards();

      // get existing object from database
      try {
        safeguard = safeguardsManager.getSafeguardsById(safeguardId);
        if (safeguard != null && safeguard.getId() != null) {
          if (safeguard.getFile() != null) {
            safeguard.setFile(null);
          }
          if (safeguard.getLink() != null) {
            safeguard.setLink(null);
          }
        }
      } catch (Exception e) {
        logger.error("unable to get existing Feedback comment object from DB", e);
      }

      safeguard = safeguardsManager.saveSafeguards(safeguard);

      if (safeguard.getId() != null) {
        save.put("save", true);
        save.put("id", safeguard.getId());
      } else {
        save.put("save", false);
      }
    } else {
      save.put("save", false);
    }
    return SUCCESS;
  }

  public Map<String, Object> getSave() {
    return save;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();

    try {
      if (parameters.get(APConstants.SAFEGUARD_REQUEST_ID).isDefined()) {
        safeguardId = Long.parseLong(
          StringUtils.trim(StringUtils.trim(parameters.get(APConstants.SAFEGUARD_REQUEST_ID).getMultipleValues()[0])));
      }
    } catch (Exception e) {
      logger.error("unable to get safeguardId", e);
    }
  }

  public void setSave(Map<String, Object> save) {
    this.save = save;
  }

}

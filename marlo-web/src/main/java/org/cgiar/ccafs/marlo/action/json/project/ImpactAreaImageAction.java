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
import org.cgiar.ccafs.marlo.data.manager.ImpactAreaManager;
import org.cgiar.ccafs.marlo.data.model.ImpactArea;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImpactAreaImageAction extends BaseAction {

  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(ImpactAreaImageAction.class);
  private Map<String, Object> image;
  private Long impactAreaID;
  private ImpactAreaManager impactAreaManager;

  @Inject
  public ImpactAreaImageAction(APConfig config, ImpactAreaManager impactAreaManager) {
    super(config);
    this.impactAreaManager = impactAreaManager;
  }

  @Override
  public String execute() throws Exception {
    // @param = sectionName

    image = new HashMap<String, Object>();
    if (impactAreaID != null) {
      ImpactArea impactArea = new ImpactArea();
      // get existing object from database
      try {
        impactArea = impactAreaManager.getImpactAreaById(impactAreaID);
        if (impactArea != null && impactArea.getIcon() != null) {
          String imagePath = "global/images/impactAreas/" + impactArea.getIcon();
          image.put("adsoluteURL", this.getBaseUrl() + "/" + imagePath);
          image.put("relativeURL", imagePath);
        }
      } catch (Exception e) {
        logger.error("unable to get imagen path", e);
      }
    }

    return SUCCESS;
  }

  public Map<String, Object> getImage() {
    return image;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    try {
      if (parameters.get(APConstants.PARTNER_REQUEST_ID).isDefined()) {
        String value = StringUtils.trim(parameters.get(APConstants.PARTNER_REQUEST_ID).getMultipleValues()[0]);
        if (StringUtils.isNumeric(value)) {
          impactAreaID = Long.parseLong(value);
        } else {
          logger.error("The value is not a valid number: " + value);
        }
      }
    } catch (NumberFormatException e) {
      logger.error("Unable to convert to Long", e);
    } catch (Exception e) {
      logger.error("An unexpected error occurred", e);
    }
  }

  public void setImage(Map<String, Object> image) {
    this.image = image;
  }
}

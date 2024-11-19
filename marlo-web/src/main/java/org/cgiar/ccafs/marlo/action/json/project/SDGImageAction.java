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
import org.cgiar.ccafs.marlo.data.manager.SDGContributionManager;
import org.cgiar.ccafs.marlo.data.manager.SdgManager;
import org.cgiar.ccafs.marlo.data.model.SDGContribution;
import org.cgiar.ccafs.marlo.data.model.Sdg;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SDGImageAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(SDGImageAction.class);
  private Map<String, Object> image;
  private Long sdgContributionID;
  private SDGContributionManager sdgContributionManager;
  private SdgManager sdgManager;

  @Inject
  public SDGImageAction(APConfig config, SDGContributionManager sdgContributionManager, SdgManager sdgManager) {
    super(config);
    this.sdgContributionManager = sdgContributionManager;
    this.sdgManager = sdgManager;
  }

  @Override
  public String execute() throws Exception {
    // @param = sectionName

    image = new HashMap<String, Object>();
    if (sdgContributionID != null) {
      SDGContribution sdgContribution = new SDGContribution();
      Sdg sdg = new Sdg();
      // get existing object from database
      try {
        sdgContribution = sdgContributionManager.getSDGContributionById(sdgContributionID);
        if (sdgContribution != null && sdgContribution.getSdg() != null && sdgContribution.getSdg().getId() != null) {
          sdg = sdgManager.getSDGById(sdgContribution.getSdg().getId());

          if (sdg != null && sdg.getIcon() != null) {
            String imagePath = "global/images/sdg/" + sdg.getIcon();
            image.put("adsoluteURL", this.getBaseUrl() + "/" + imagePath);
            image.put("relativeURL", imagePath);
          }
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
          sdgContributionID = Long.parseLong(value);
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

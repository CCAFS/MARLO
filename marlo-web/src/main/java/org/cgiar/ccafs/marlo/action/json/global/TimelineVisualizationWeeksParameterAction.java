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

package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TimelineVisualizationWeeksParameterAction extends BaseAction {


  private static final long serialVersionUID = 8027160696406597679L;
  public static Logger LOG = LoggerFactory.getLogger(TimelineVisualizationWeeksParameterAction.class);

  private Map<String, String> parameter;

  @Inject
  public TimelineVisualizationWeeksParameterAction(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {

    parameter = new HashMap<>();

    int weeks = 0;
    if (APConstants.CRP_TIMELINE_WEEK_PARAMETER_VISUALIZATION != null
      && this.getSession().get(APConstants.CRP_TIMELINE_WEEK_PARAMETER_VISUALIZATION) != null) {
      try {
        weeks =
          Integer.parseInt(this.getSession().get(APConstants.CRP_TIMELINE_WEEK_PARAMETER_VISUALIZATION).toString());
      } catch (NumberFormatException e) {
        LOG.error("Error getting timeline parameter visualizacion value to int: " + e);
      }
    } else {
      LOG.error("CRP_TIMELINE_WEEK_PARAMETER_VISUALIZATION is null or not found in session.");
    }

    parameter.put("weeks", weeks + "");

    return SUCCESS;
  }

  public Map<String, String> getParameter() {
    return parameter;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
  }

  public void setParameter(Map<String, String> parameter) {
    this.parameter = parameter;
  }
}


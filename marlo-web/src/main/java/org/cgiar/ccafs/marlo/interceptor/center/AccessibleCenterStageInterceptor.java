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

package org.cgiar.ccafs.marlo.interceptor.center;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;

import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Interceptor to check the active Marlo Sections with the namespace of the action to execute
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Héctor Fabio Tobón R.
 */
public class AccessibleCenterStageInterceptor extends AbstractInterceptor {

  private static final long serialVersionUID = 3723021484076686914L;

  private static final Logger LOG = LoggerFactory.getLogger(AccessibleCenterStageInterceptor.class);

  private GlobalUnit loggedCenter;

  private final CustomParameterManager customParameterManager;

  public AccessibleCenterStageInterceptor(CustomParameterManager customParameterManager) {
    this.customParameterManager = customParameterManager;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    LOG.debug("=> AccessibleCenterStageInterceptor");

    String stageName = ServletActionContext.getActionMapping().getNamespace();
    Map<String, Object> session = invocation.getInvocationContext().getSession();
    loggedCenter = (GlobalUnit) session.get(APConstants.SESSION_CRP);
    // Check what section is the user loading and
    // validate if it is active
    if (stageName.startsWith("/centerImpactPathway")) {
      if (Boolean.parseBoolean(this.sectionActive(APConstants.CENTER_IMPACT_PATHWAY_ACTIVE))) {
        return invocation.invoke();
      } else {
        return BaseAction.NOT_AUTHORIZED;
      }
    } else if (stageName.startsWith("/monitoring")) {

      return invocation.invoke();

    } else if (stageName.startsWith("/centerSummaries")) {
      if (Boolean.parseBoolean(this.sectionActive(APConstants.CENTER_SUMMARIES_ACTIVE))) {
        return invocation.invoke();
      } else {
        return BaseAction.NOT_AUTHORIZED;
      }
    } else {
      return invocation.invoke();
    }
  }

  public String sectionActive(String section) {

    CustomParameter customParameter =
      customParameterManager.getCustomParameterByParameterKeyAndGlobalUnitId(section, loggedCenter.getId());

    if (customParameter == null) {
      return "false";
    } else {
      return customParameter.getValue();
    }
  }
}

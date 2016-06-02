/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.interceptor;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.utils.APConfig;

import com.google.inject.Inject;
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
public class AccessibleStageInterceptor extends AbstractInterceptor {

  private static final long serialVersionUID = 3723021484076686914L;

  private static final Logger LOG = LoggerFactory.getLogger(AccessibleStageInterceptor.class);

  private APConfig config;

  @Inject
  public AccessibleStageInterceptor(APConfig config) {
    this.config = config;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    LOG.debug("=> AccessibleStageInterceptor");
    String stageName = ServletActionContext.getActionMapping().getNamespace();
    // Check what section is the user loading and
    // validate if it is active
    if (stageName.startsWith("/admin")) {
      if (config.isAdminActive()) {
        return invocation.invoke();
      } else {
        return BaseAction.NOT_AUTHORIZED;
      }
    } else {
      return invocation.invoke();
    }
  }
}

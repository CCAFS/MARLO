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

package org.cgiar.ccafs.marlo.interceptor;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.Map;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Answers 404 when the request carries a phaseID that does not belong to the session Global Unit.
 * It runs after ValidSessionCrpInterceptor, so the session already holds the Global Unit named in the URL. A phase of
 * another Global Unit must never reach the action: BaseAction.getActualPhase() and the actions that read the phaseID
 * field directly would otherwise work on, and save into, a phase of a different Global Unit (A2-2606).
 * A request without a phaseID, or with one that is empty or not a number, is left alone: the action falls back to
 * the current phase, as it always did.
 */
public class ValidPhaseInterceptor extends AbstractInterceptor {

  private static final long serialVersionUID = -6203718870462815531L;

  private static final Logger LOG = LoggerFactory.getLogger(ValidPhaseInterceptor.class);

  private final PhaseManager phaseManager;

  @Inject
  public ValidPhaseInterceptor(PhaseManager phaseManager) {
    this.phaseManager = phaseManager;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    Map<String, Object> session = invocation.getInvocationContext().getSession();
    Object sessionCrp = session == null ? null : session.get(APConstants.SESSION_CRP);
    if (!(sessionCrp instanceof GlobalUnit) || ((GlobalUnit) sessionCrp).getId() == null) {
      // No Global Unit in the session (an unlogged page, for instance): there is nothing to compare against.
      return invocation.invoke();
    }
    Long sessionCrpID = ((GlobalUnit) sessionCrp).getId();

    HttpParameters parameters = invocation.getInvocationContext().getParameters();
    if (parameters == null || !parameters.contains(APConstants.PHASE_ID)) {
      return invocation.invoke();
    }
    Parameter phaseParameter = parameters.get(APConstants.PHASE_ID);
    String[] values = phaseParameter == null ? null : phaseParameter.getMultipleValues();
    if (values == null) {
      return invocation.invoke();
    }

    for (String value : values) {
      long phaseID = NumberUtils.toLong(StringUtils.trim(value), 0L);
      if (phaseID == 0L) {
        // Empty or not a number: the templates render the param with no value whenever their phase has no id.
        continue;
      }
      Phase phase = phaseManager.getPhaseById(phaseID);
      Long phaseCrpID = phase == null || phase.getCrp() == null ? null : phase.getCrp().getId();
      if (!sessionCrpID.equals(phaseCrpID)) {
        LOG.info("The {} {} does not belong to the session Global Unit {}, so the request is answered as not found",
          APConstants.PHASE_ID, phaseID, sessionCrpID);
        return BaseAction.NOT_FOUND;
      }
    }
    return invocation.invoke();
  }

}

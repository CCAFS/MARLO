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
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.User;

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
 * Answers 404 when the request carries a phaseID the user must not work with, so that no action reads or writes a
 * phase of a Global Unit it was not meant for (A2-2606). A phaseID is accepted only when:
 * <ol>
 * <li>the phase exists and has a Global Unit;</li>
 * <li>the URL names no Global Unit ({crp}/ prefix), or names the phase's own Global Unit. This blocks a URL that mixes
 * one Global Unit with the phase of another;</li>
 * <li>the phase's Global Unit is the session one, or one the user is an active member of. A tab still open on another
 * Global Unit keeps working: its autosave, section validation and uploads are flat URLs that never switch the session,
 * so comparing them with the session alone would reject them.</li>
 * </ol>
 * A session without a Global Unit (an unlogged visitor) is not checked, nor is a request without a phaseID or with one
 * that is empty or not a number: the action falls back to the current phase, as it always did.
 * The URL prefix is compared as text with the phase's acronym and never sent to a query: it is user input.
 */
public class ValidPhaseInterceptor extends AbstractInterceptor {

  private static final long serialVersionUID = -6203718870462815531L;

  private static final Logger LOG = LoggerFactory.getLogger(ValidPhaseInterceptor.class);

  private final PhaseManager phaseManager;

  private final CrpUserManager crpUserManager;

  @Inject
  public ValidPhaseInterceptor(PhaseManager phaseManager, CrpUserManager crpUserManager) {
    this.phaseManager = phaseManager;
    this.crpUserManager = crpUserManager;
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

    Object sessionUser = session.get(APConstants.SESSION_USER);
    Long userID = sessionUser instanceof User ? ((User) sessionUser).getId() : null;
    String urlCrpAcronym = this.urlGlobalUnitAcronym(invocation.getInvocationContext().getActionName());

    for (String value : values) {
      long phaseID = NumberUtils.toLong(StringUtils.trim(value), 0L);
      if (phaseID == 0L) {
        // Empty or not a number: the templates render the param with no value whenever their phase has no id.
        continue;
      }
      Phase phase = phaseManager.getPhaseById(phaseID);
      GlobalUnit phaseCrp = phase == null ? null : phase.getCrp();
      if (phaseCrp == null || phaseCrp.getId() == null) {
        LOG.info("The {} {} does not exist or has no Global Unit, so the request is answered as not found",
          APConstants.PHASE_ID, phaseID);
        return BaseAction.NOT_FOUND;
      }
      if (urlCrpAcronym != null && !urlCrpAcronym.equalsIgnoreCase(phaseCrp.getAcronym())) {
        LOG.info("The {} {} belongs to Global Unit {}, not to the one in the URL, so the request is answered as not "
          + "found", APConstants.PHASE_ID, phaseID, phaseCrp.getId());
        return BaseAction.NOT_FOUND;
      }
      if (!sessionCrpID.equals(phaseCrp.getId())
        && (userID == null || !crpUserManager.existActiveCrpUser(userID, phaseCrp.getId()))) {
        LOG.info("The {} {} belongs to Global Unit {}, which user {} is not an active member of, so the request is "
          + "answered as not found", APConstants.PHASE_ID, phaseID, phaseCrp.getId(), userID);
        return BaseAction.NOT_FOUND;
      }
    }
    return invocation.invoke();
  }

  /**
   * @param actionName the action name Struts matched, such as "AICCRA/description" or "autosaveWriter"
   * @return the Global Unit acronym the URL starts with, or null for a flat action name
   */
  private String urlGlobalUnitAcronym(String actionName) {
    if (actionName == null) {
      return null;
    }
    int separator = actionName.indexOf('/');
    if (separator <= 0) {
      return null;
    }
    return actionName.substring(0, separator);
  }

}

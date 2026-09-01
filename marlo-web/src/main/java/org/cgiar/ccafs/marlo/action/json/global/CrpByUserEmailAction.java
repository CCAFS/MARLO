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
import org.cgiar.ccafs.marlo.action.json.project.FlaghshipsByCrpAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.CognitoAuthSpecificity;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CHG-COGNITO-AUTH-001-T10: step 1 of the login wizard. Resolves an email to the Global Units the account
 * belongs to and whether each one has the Cognito flow enabled, so the client can compose
 * {@code mode = user.isCgiarUser && card.cognitoEnabled} before step 3 is ever rendered (design.md 4, 5).
 * <p>
 * <b>Two structural fixes, both required by design.md 4:</b>
 * <ol>
 * <li>{@code user} is now built exactly once, independently of how many Global Units the account belongs
 * to. The original code built it <b>inside</b> the {@code for (GlobalUnit crp : crps)} loop, so a user
 * belonging to zero Global Units always got {@code user == null} even though the account exists.</li>
 * <li>{@code cognitoEnabled} is resolved <b>per Global Unit</b>, through the same {@link
 * CognitoAuthSpecificity} resolver {@code CognitoLoginAction} (T08) uses -- never a single scalar on
 * {@code user}. Hoisting it to one value would make a user belonging to units with different flag states
 * unable to see two different answers, which is exactly the defect MIG-001's "both paths coexist"
 * scenario forbids.</li>
 * </ol>
 * <p>
 * <b>An email matching no {@code users} row discloses nothing</b> (FN-001's third scenario, R-D3): neither
 * {@code user.isCgiarUser} nor any {@code crps[]} entry is produced, so the response cannot be used to
 * infer whether an unknown email would have authenticated through Cognito or locally.
 *
 * @author Christian Garcia - CIAT/CCAFS
 */
public class CrpByUserEmailAction extends BaseAction {

  /**
   *
   */
  private static final long serialVersionUID = -976200901679526774L;
  private final Logger logger = LoggerFactory.getLogger(FlaghshipsByCrpAction.class);
  private List<Map<String, Object>> crps;
  private Map<String, Object> user;
  private String userEmail;
  private GlobalUnitManager crpManager;
  private UserManager userManager;
  private CustomParameterManager customParameterManager;
  private ParameterManager parameterManager;

  // @Inject
  public CrpByUserEmailAction(APConfig config, GlobalUnitManager crpManager, UserManager userManager,
    CustomParameterManager customParameterManager, ParameterManager parameterManager) {
    super(config);
    this.crpManager = crpManager;
    this.userManager = userManager;
    this.customParameterManager = customParameterManager;
    this.parameterManager = parameterManager;
  }


  @Override
  public String execute() throws Exception {
    this.crps = new ArrayList<Map<String, Object>>();

    User usrDB = this.userManager.getUserByEmail(this.userEmail);
    if (usrDB == null) {
      usrDB = this.userManager.getUserByUsername(this.userEmail);
    }

    if (usrDB == null) {
      // FN-001 "Email not found" / R-D3: nothing here may disclose whether an unknown email would have
      // been CGIAR or local. `user` stays null and `crps` stays the empty list initialized above --
      // neither carries any Global Unit membership or auth-type information.
      return SUCCESS;
    }

    this.user = new HashMap<String, Object>();
    this.user.put("name", usrDB.getComposedCompleteName());
    this.user.put("agree", usrDB.getAgreeTerms());
    this.user.put("isCgiarUser", usrDB.isCgiarUser());

    List<GlobalUnit> globalUnits = this.crpManager.crpUsers(this.userEmail);
    globalUnits.sort(Comparator.comparing(GlobalUnit::getAcronym));

    for (GlobalUnit crp : globalUnits) {
      try {
        Map<String, Object> crpMap = new HashMap<String, Object>();
        crpMap.put("id", crp.getId());
        crpMap.put("name", crp.getName());
        crpMap.put("acronym", crp.getAcronym());
        crpMap.put("type", crp.getGlobalUnitType().getName());
        crpMap.put("idType", crp.getGlobalUnitType().getId());
        // Per-unit, resolved through the shared resolver (PS-16) -- never a single scalar on `user`.
        crpMap.put("cognitoEnabled",
          CognitoAuthSpecificity.isActiveFor(crp, this.customParameterManager, this.parameterManager));

        this.crps.add(crpMap);
      } catch (Exception e) {
        this.logger.error("unable to add flagship to crps list", e);
        /**
         * Original code swallows the exception and didn't even log it. Now we at least log it,
         * but we need to revisit to see if we should continue processing or re-throw the exception.
         */
      }
    }
    return SUCCESS;

  }


  public List<Map<String, Object>> getCrps() {
    return crps;
  }


  public Map<String, Object> getUser() {
    return user;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    userEmail = StringUtils.trim(parameters.get(APConstants.USER_EMAIL).getMultipleValues()[0]);
  }

  public void setCrps(List<Map<String, Object>> flagships) {
    this.crps = flagships;
  }


  public void setUser(Map<String, Object> user) {
    this.user = user;
  }


}

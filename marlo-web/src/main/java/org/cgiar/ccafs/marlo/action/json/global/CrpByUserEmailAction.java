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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
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

  @Inject
  public CrpByUserEmailAction(APConfig config, GlobalUnitManager crpManager, UserManager userManager) {
    super(config);
    this.crpManager = crpManager;
    this.userManager = userManager;
  }


  @Override
  public String execute() throws Exception {
    crps = new ArrayList<Map<String, Object>>();
    Map<String, Object> crpMap;
    List<GlobalUnit> crps = crpManager.crpUsers(userEmail);
    for (GlobalUnit crp : crps) {
      try {
        crpMap = new HashMap<String, Object>();
        crpMap.put("id", crp.getId());
        crpMap.put("name", crp.getName());
        crpMap.put("acronym", crp.getAcronym());
        crpMap.put("type", crp.getGlobalUnitType().getName());

        this.crps.add(crpMap);
      } catch (Exception e) {
        logger.error("unable to add flagship to crps list", e);
        /**
         * Original code swallows the exception and didn't even log it. Now we at least log it,
         * but we need to revisit to see if we should continue processing or re-throw the exception.
         */
      }
      user = new HashMap<>();
      User usrDB = userManager.getUserByEmail(userEmail);
      if (usrDB != null) {
        user.put("name", usrDB.getComposedCompleteName());
        user.put("password", usrDB.getPassword());
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

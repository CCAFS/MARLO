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
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

public class LiasonUsersByInstitutionsAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private List<Map<String, Object>> liasonsUsers;

  private Long liasonIntitutionId;
  private LiaisonUserManager liasonUserManager;

  @Inject
  public LiasonUsersByInstitutionsAction(APConfig config, LiaisonUserManager liasonUserManager) {
    super(config);
    this.liasonUserManager = liasonUserManager;
  }

  @Override
  public String execute() throws Exception {
    liasonsUsers = new ArrayList<Map<String, Object>>();
    Map<String, Object> liasonsUser;
    List<LiaisonUser> liaisonUsers = liasonUserManager.getLiasonUsersByInstitutionId(liasonIntitutionId);
    for (LiaisonUser liaisonUser : liaisonUsers) {
      try {
        liasonsUser = new HashMap<String, Object>();
        liasonsUser.put("id", liaisonUser.getId());
        liasonsUser.put("description", liaisonUser.getComposedName());
        liasonsUser.put("active", liaisonUser.isActive());
        this.liasonsUsers.add(liasonsUser);
      } catch (Exception e) {

      }
    }
    return SUCCESS;

  }

  public List<Map<String, Object>> getLiasonsUsers() {
    return liasonsUsers;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();

    liasonIntitutionId =
      // Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.LIASON_INSTITUTION_ID))[0]));
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.LIASON_INSTITUTION_ID).getMultipleValues()[0]));
  }

  public void setLiasonsUsers(List<Map<String, Object>> liasonsUsers) {
    this.liasonsUsers = liasonsUsers;
  }


}

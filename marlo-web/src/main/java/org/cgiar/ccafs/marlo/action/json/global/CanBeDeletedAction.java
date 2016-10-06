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
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class CanBeDeletedAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 7102029488497292435L;

  private String canBeDeleted;

  private long id;
  @Inject
  private LiaisonUserManager liaisonUserManager;
  @Inject
  private UserRoleManager userRoleManager;
  private String className;


  @Inject
  public CanBeDeletedAction(APConfig config) {
    super(config);

  }

  @Override
  public String execute() throws Exception {
    Class clazz = Class.forName(className);
    if (clazz == UserRole.class) {
      UserRole userRole = userRoleManager.getUserRoleById(id);
      long cuId = Long.parseLong((String) this.getSession().get(APConstants.CRP_CU));
      List<LiaisonUser> liaisonUsers = liaisonUserManager.findAll().stream()
        .filter(c -> c.getUser().getId().longValue() == userRole.getUser().getId().longValue()
          && c.getLiaisonInstitution().getId().longValue() == cuId)
        .collect(Collectors.toList());
      canBeDeleted = "true";
      for (LiaisonUser liaisonUser : liaisonUsers) {
        if (!liaisonUser.getProjects().isEmpty()) {
          canBeDeleted = "false";
        }
      }
    }

    return SUCCESS;
  }

  public String getCanBeDeleted() {
    return canBeDeleted;
  }

  @Override
  public void prepare() throws Exception {

    id = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.ID)));
    className = StringUtils.trim(this.getRequest().getParameter(APConstants.CLASS_NAME));

  }

  public void setCanBeDeleted(String canBeDeleted) {
    this.canBeDeleted = canBeDeleted;
  }

}

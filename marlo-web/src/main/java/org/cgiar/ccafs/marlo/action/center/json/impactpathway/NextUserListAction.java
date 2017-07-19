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

package org.cgiar.ccafs.marlo.action.center.json.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ICenterNextuserTypeManager;
import org.cgiar.ccafs.marlo.data.model.CenterNextuserType;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class NextUserListAction extends BaseAction {


  private static final long serialVersionUID = -6010866345601586878L;


  private ICenterNextuserTypeManager nextUserService;

  private List<Map<String, Object>> nextUsers;

  private String message;

  private long nextUserID;

  @Inject
  public NextUserListAction(APConfig config, ICenterNextuserTypeManager nextUserService) {
    super(config);
    this.nextUserService = nextUserService;
  }

  @Override
  public String execute() throws Exception {

    nextUsers = new ArrayList<>();

    Map<String, Object> nextUser;

    CenterNextuserType nextUserType = nextUserService.getNextuserTypeById(nextUserID);

    if (nextUserType != null) {
      List<CenterNextuserType> nextUserChilds = new ArrayList<>(
        nextUserType.getNextuserTypes().stream().filter(nu -> nu.isActive()).collect(Collectors.toList()));

      for (CenterNextuserType nu : nextUserChilds) {

        nextUser = new HashMap<>();
        nextUser.put("id", nu.getId());
        nextUser.put("name", nu.getName());

        nextUsers.add(nextUser);

      }
    }


    return SUCCESS;
  }

  public String getMessage() {
    return message;
  }

  public List<Map<String, Object>> getNextUsers() {
    return nextUsers;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    nextUserID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.NEXT_USER_ID))[0]));
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setNextUsers(List<Map<String, Object>> nextUsers) {
    this.nextUsers = nextUsers;
  }

}

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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class GuestUsersAction extends BaseAction {


  private static final long serialVersionUID = 6860177996446505143L;


  private UserManager userManager;

  private CrpManager crpManager;


  private User user;

  private boolean cigarUser;


  private List<Crp> crps;

  @Inject
  public GuestUsersAction(APConfig config, UserManager userManager, CrpManager crpManager) {
    super(config);
    this.userManager = userManager;
    this.crpManager = crpManager;
  }

  public List<Crp> getCrps() {
    return crps;
  }

  public User getUser() {
    return user;
  }

  public boolean isCigarUser() {
    return cigarUser;
  }

  @Override
  public void prepare() throws Exception {

    crps = new ArrayList<>(
      crpManager.findAll().stream().filter(c -> c.isActive() && c.isMarlo()).collect(Collectors.toList()));


  }

  @Override
  public String save() {

    if (true) {

      User newUser = new User();


    }

    return SUCCESS;
  }

  public void setCigarUser(boolean cigarUser) {
    this.cigarUser = cigarUser;
  }

  public void setCrps(List<Crp> crps) {
    this.crps = crps;
  }

  public void setUser(User user) {
    this.user = user;
  }

}

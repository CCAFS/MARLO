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

package org.cgiar.ccafs.marlo.action.center.capdev;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.utils.APConfig;

import org.cgiar.ciat.auth.LDAPService;
import org.cgiar.ciat.auth.LDAPUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class ContactPersonAction extends BaseAction {

  private static final long serialVersionUID = 1L;

  public static String getADFilter(String criteria) {
    final String ldapFilterString = "(&(|" + "(sAMAccountName=*" + criteria + "*)" + "(cn=*" + criteria + "*)" + "(sn=*"
      + criteria + "*)" + "(mailNickname=*" + criteria + "*)" + "(givenName=*" + criteria + "*))"
      + "(&(!(objectClass=computer))" + "(!(objectClass=contact))" + "(!(objectClass=printQueue))"
      + "(!(objectClass=group))" + "(!(objectClass=publicFolder))" + "(!(objectClass=connectionPoint))"
      + "(!(objectClass=container))" + "(!(objectClass=classStore))" + "(!(objectClass=trustedDomain))"
      + "(!(objectClass=linkTrackOMTEntry))" + "(!(objectClass=nTFRSReplicaSet))"
      + "(!(objectClass=foreignSecurityPrincipal))" + "(!(objectClass=msExchDynamicDistributionList))"
      + "(!(isCriticalSystemObject=TRUE))" + "(!(showInAdvancedViewOnly=TRUE))" + "(!(sAMAccountName=ldapapp))"
      + "(!(sAMAccountName=ldapuser))" + "(!(cn=*shadow*))" + "(!(cn=*systemMailbox*))" + "(!(cn=EUQ_CIATEX*))"
      + "(!(cn=exchanonymous))" + "(!(cn=exchconn))" + "(!(cn=*webpc*))" + "(!(cn=shareciat))"
      + "(!(cn=diskless micros*))" + "(!(cn=VIVIENDA (CIAT)))" + "(!(cn=casino))" + "(!(adminCount=1))))";
    return ldapFilterString;
  }


  private List<Map<String, Object>> users;


  @Inject
  public ContactPersonAction(APConfig config) {
    super(config);
    // TODO Auto-generated constructor stub
  }


  @Override
  public String execute() throws Exception {
    // TODO Auto-generated method stub
    return super.execute();
  }


  public List<Map<String, Object>> getUsers() {
    return users;
  }


  @Override
  public void prepare() throws Exception {
    // TODO Auto-generated method stub
    super.prepare();
  }


  public String searchADUser() throws Exception {
    System.out.println("searchADUser");
    final Map<String, Object> parameters = this.getParameters();
    final LDAPService service = new LDAPService();
    final String queryParameter = StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]);
    System.out.println("antes de la consulta");
    final List<LDAPUser> users = service.searchUsers(queryParameter);
    this.users = new ArrayList<>();

    System.out.println("users.size() " + users.size());

    int idUser = 0;
    for (final LDAPUser user : users) {
      idUser++;
      final Map<String, Object> userMap = new HashMap<>();
      userMap.put("idUser", idUser);
      userMap.put("firstName", user.getFirstName());
      userMap.put("lastName", user.getLastName());
      userMap.put("email", user.getEmail());
      this.users.add(userMap);

    }
    return SUCCESS;


  }

  public void setUsers(List<Map<String, Object>> users) {
    this.users = users;
  }


}

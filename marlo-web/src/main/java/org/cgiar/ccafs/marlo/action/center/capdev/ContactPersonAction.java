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
import org.cgiar.ccafs.marlo.data.manager.AdUserManager;
import org.cgiar.ccafs.marlo.data.model.AdUser;
import org.cgiar.ccafs.marlo.utils.APConfig;

import org.cgiar.ciat.auth.ADConexion;
import org.cgiar.ciat.auth.LDAPService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class ContactPersonAction extends BaseAction {

  private static final long serialVersionUID = 1L;

  private List<Map<String, Object>> users;
  private AdUserManager adUsermanager;


  @Inject
  public ContactPersonAction(APConfig config, AdUserManager adUsermanager) {
    super(config);
    this.adUsermanager = adUsermanager;
  }


  @Override
  public String execute() throws Exception {
    return super.execute();
  }


  public String getADFilter(String criteria) {
    String ldapFilterString = "(&(|" + "(sAMAccountName=*" + criteria + "*)" + "(cn=*" + criteria + "*)" + "(sn=*"
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


  public List<Map<String, Object>> getUsers() {
    return users;
  }


  @Override
  public void prepare() throws Exception {
    super.prepare();
  }

  public String searchADUser() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    LDAPService service = new LDAPService();
    String queryParameter = StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]);
    String genericUser = APConstants.GENERICUSER_AD;
    String genericPassword = APConstants.GENERICPASSWORD_AD;
    String hostName = APConstants.HOSTNAME_AD;
    String port = APConstants.PORT_AD;

    ADConexion adConection = new ADConexion(genericUser, genericPassword, hostName, Integer.parseInt(port));

    queryParameter = queryParameter.trim();
    // List<LDAPUser> ad_users = adConection.searchUsers(this.getADFilter(queryParameter),
    // "OU=CIATHUB,DC=CGIARAD,DC=ORG");

    List<AdUser> ad_users = adUsermanager.searchUsers(queryParameter);

    this.users = new ArrayList<>();
    int idUser = 0;
    for (AdUser user : ad_users) {
      idUser++;
      Map<String, Object> userMap = new HashMap<>();
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

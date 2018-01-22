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

package org.cgiar.ccafs.marlo.quartz;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AdUserManager;
import org.cgiar.ccafs.marlo.data.model.AdUser;
import org.cgiar.ccafs.marlo.utils.AuditLogContext;
import org.cgiar.ccafs.marlo.utils.AuditLogContextProvider;

import org.cgiar.ciat.auth.ADConexion;
import org.cgiar.ciat.auth.LDAPUser;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Named
public class ADUsersJob implements Job {

  public static Logger LOG = LoggerFactory.getLogger(ADUsersJob.class);


  private final AdUserManager adUsermanager;
  private final SessionFactory sessionFactory;

  @Inject
  public ADUsersJob(AdUserManager adUsermanager, SessionFactory sessionFactory) {
    super();
    this.adUsermanager = adUsermanager;
    this.sessionFactory = sessionFactory;
  }


  public String deleteSpecialCaracter(String string) {
    for (int i = 0; i < string.length(); i++) {
      if (string.charAt(i) == (char) 39) {
        StringBuilder sb = new StringBuilder(string);
        sb.deleteCharAt(i);
        string = sb.toString();
      }
    }

    return string;
  }


  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {

    sessionFactory.getCurrentSession().beginTransaction();

    String genericUser = APConstants.GENERICUSER_AD;
    String genericPassword = APConstants.GENERICPASSWORD_AD;
    String hostName = APConstants.HOSTNAME_AD;
    String port = APConstants.PORT_AD;

    ADConexion adConection = new ADConexion(genericUser, genericPassword, hostName, Integer.parseInt(port));
    List<LDAPUser> users = adConection.searchUsers("(sAMAccountName=*)", "DC=CGIARAD,DC=ORG");

    for (LDAPUser user : users) {
      if (user != null) {
        AuditLogContextProvider.push(new AuditLogContext());
        if (user.getEmail() != null) {
          if (!user.getEmail().equals("")) {
            try {
              AdUser adUser = adUsermanager.findByUserEmail(this.deleteSpecialCaracter(user.getEmail()));
              if (adUser == null) {
                adUser = new AdUser();
                adUser.setLogin(user.getLogin());
                adUser.setFirstName(user.getFirstName());
                adUser.setMiddleName(user.getMiddleName());
                adUser.setLastName(user.getLastName());
                adUser.setEmail(user.getEmail());
                adUser.setActive(true);
                adUser.setActiveSince(new Date());
                adUser.setCreatedBy(null);
                adUser.setModifiedBy(null);
                adUsermanager.saveAdUser(adUser);
              }
            } catch (Exception e) {
              LOG.error("Could not save entity for user login : " + user.getLogin(), e);
            }
          }
        }

      }
    }
    sessionFactory.getCurrentSession().getTransaction().commit();


  }

}
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
import org.cgiar.ccafs.marlo.utils.AuditLogContext;
import org.cgiar.ccafs.marlo.utils.AuditLogContextProvider;

import org.cgiar.ciat.auth.ADConexion;
import org.cgiar.ciat.auth.LDAPUser;

import java.util.List;

import com.google.inject.Injector;
import org.hibernate.SessionFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ADUsersJob implements Job {


  public static Logger LOG = LoggerFactory.getLogger(ADUsersJob.class);
  private AdUserManager adUsermanager;

  private Injector injector;
  private SessionFactory sessionFactory;


  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    System.out.println("Struts 2.3.4 + Quartz 2.1.5");
    SchedulerContext schedulerContext = null;
    try {
      schedulerContext = context.getScheduler().getContext();
    } catch (SchedulerException e1) {
      e1.printStackTrace();
    }
    injector = (Injector) schedulerContext.get("injector");
    sessionFactory = injector.getInstance(SessionFactory.class);
    sessionFactory.getCurrentSession().beginTransaction();

    adUsermanager = injector.getInstance(AdUserManager.class);


    String genericUser = APConstants.GENERICUSER_AD;
    String genericPassword = APConstants.GENERICPASSWORD_AD;
    String hostName = APConstants.HOSTNAME_AD;
    String port = APConstants.PORT_AD;

    ADConexion adConection = new ADConexion(genericUser, genericPassword, hostName, Integer.parseInt(port));
    List<LDAPUser> users = adConection.searchUsers("(sAMAccountName=*)", "DC=CGIARAD,DC=ORG");
    // LDAPService service = new LDAPService();
    // List<LDAPUser> users = service.searchUsers("(sAMAccountName=*)");
    // LDAPUser user = service.searchUserByEmail("l.o.gonzalez@cgiar.org");
    // LDAPUser user = adConection.searchUserByEmail("l.o.gonzalez@cgiar.org");
    System.out.println("=======> users size== " + users.size());


    for (LDAPUser user : users) {
      if (user != null) {
        AuditLogContextProvider.push(new AuditLogContext());
        System.out.println("Login ===>" + user.getLogin());
        // try {
        // AdUser adUser = adUsermanager.findByUserLogin(user.getLogin());
        // if (adUser == null) {
        // adUser = new AdUser();
        // adUser.setLogin(user.getLogin());
        // adUser.setFirstName(user.getFirstName());
        // adUser.setMiddleName(user.getMiddleName());
        // adUser.setLastName(user.getLastName());
        // adUser.setEmail(user.getEmail());
        // adUser.setActive(true);
        // adUser.setActiveSince(new Date());
        // adUser.setCreatedBy(null);
        // adUser.setModifiedBy(null);
        // adUsermanager.saveAdUser(adUser);
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // LOG.error("Could not save entity!" + user.getLogin());
        // }
      }
    }
    sessionFactory.getCurrentSession().getTransaction().commit();


  }

}

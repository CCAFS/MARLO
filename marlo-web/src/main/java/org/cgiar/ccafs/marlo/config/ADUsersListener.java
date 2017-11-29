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

package org.cgiar.ccafs.marlo.config;

import org.cgiar.ccafs.marlo.quartz.ADUsersJob;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.inject.Injector;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class ADUsersListener implements ServletContextListener {

  @Override
  public void contextDestroyed(ServletContextEvent arg0) {

  }

  @Override
  public void contextInitialized(ServletContextEvent arg0) {

    Injector injector = (Injector) arg0.getServletContext().getAttribute(APGuiceContextListener.KEY_NAME);
    JobDetail job = JobBuilder.newJob(ADUsersJob.class).withIdentity("anyJobName", "group1").build();

    try {
      Trigger trigger = TriggerBuilder.newTrigger().withIdentity("anyTriggerName", "group1")
        .withSchedule(CronScheduleBuilder.cronSchedule("0 30 01 * * ?")).build();

      Scheduler scheduler = new StdSchedulerFactory().getScheduler();
      scheduler.getContext().put("injector", injector);

      scheduler.start();
      scheduler.scheduleJob(job, trigger);

    } catch (SchedulerException e) {
      e.printStackTrace();
    }

  }

}

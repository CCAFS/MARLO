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

package org.cgiar.ccafs.marlo;

import org.cgiar.ccafs.marlo.config.AutowiringSpringBeanJobFactory;
import org.cgiar.ccafs.marlo.quartz.ADUsersJob;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
public class MarloQuartzConfiguration {

  @Bean
  public JobDetailFactoryBean jobDetail() {
    JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
    jobDetailFactory.setJobClass(ADUsersJob.class);
    jobDetailFactory.setName("anyJobName");
    jobDetailFactory.setGroup("group1");
    jobDetailFactory.setDescription("Invoking ADUsersJob service...");
    jobDetailFactory.setDurability(true);
    return jobDetailFactory;
  }

  @Bean
  public SchedulerFactoryBean scheduler(Trigger trigger, JobDetail job) {
    SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
    schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));

    schedulerFactory.setJobFactory(this.springBeanJobFactory());
    schedulerFactory.setJobDetails(job);
    schedulerFactory.setTriggers(trigger);
    return schedulerFactory;
  }

  @Bean
  public SpringBeanJobFactory springBeanJobFactory() {
    AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
    return jobFactory;
  }

  @Bean
  public CronTriggerFactoryBean trigger(JobDetail jobDetail) {
    CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
    trigger.setJobDetail(jobDetail);
    trigger.setName("anyTriggerName");
    trigger.setGroup("group1");
    trigger.setCronExpression("0 30 01 * * ?");
    return trigger;
  }


}
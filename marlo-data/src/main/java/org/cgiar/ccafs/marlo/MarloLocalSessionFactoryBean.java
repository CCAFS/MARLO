/**
 * 
 */
package org.cgiar.ccafs.marlo;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cache.spi.RegionFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

/**
 * Custom session factory bean extended from {@link LocalSessionFactoryBean}. This implementation overrides the method
 * buildSessionFactory to provide Hibernate with own region factory.
 * 
 * @author Alexander Filigrana - Premize S.A.S
 * @since 4.4.0
 * @see {@link RegionFactory}
 */
public class MarloLocalSessionFactoryBean extends LocalSessionFactoryBean {

  private RegionFactory regionFactory;

  @Override
  protected SessionFactory buildSessionFactory(LocalSessionFactoryBuilder sfb) {

    StandardServiceRegistryBuilder serviceRegistryBuilder = sfb.getStandardServiceRegistryBuilder();
    serviceRegistryBuilder.addService(RegionFactory.class, regionFactory);

    return sfb.buildSessionFactory();
  }

  @Required
  public void setRegionFactory(RegionFactory regionFactory) {
    this.regionFactory = regionFactory;
  }

}
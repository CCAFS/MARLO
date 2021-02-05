package org.cgiar.ccafs.marlo.data.dao.mysql;

import javax.inject.Inject;
import javax.inject.Named;
import org.cgiar.ccafs.marlo.data.dao.RestApiAuditlogDAO;
import org.cgiar.ccafs.marlo.data.model.RestApiAuditlog;
import org.hibernate.SessionFactory;

/**
 *
 * @author tonyshikali
 */
@Named
public class RestApiAuditlogMySQLDAO extends AbstractMarloDAO<RestApiAuditlog, Long> implements RestApiAuditlogDAO{
    
  @Inject
    public RestApiAuditlogMySQLDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

  @Override
    public void logThis(RestApiAuditlog restApiAuditLog) {
        super.saveEntity(restApiAuditLog);
    }
    

}

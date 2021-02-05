package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.RestApiAuditlog;

/**
 *
 * @author tonyshikali
 */
public interface RestApiAuditlogManager {
    
  public void logApiCall(RestApiAuditlog restApiAuditlog);

}

package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.RestApiAuditlog;

/**
 *
 * @author tonyshikali
 */
public interface RestApiAuditlogDAO {
    
    public void logThis(RestApiAuditlog restApiAuditLog);
}

package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.data.dao.RestApiAuditlogDAO;
import org.cgiar.ccafs.marlo.data.manager.RestApiAuditlogManager;
import org.cgiar.ccafs.marlo.data.model.RestApiAuditlog;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author tonyshikali
 */
@Named
public class RestApiAuditlogImpl implements RestApiAuditlogManager {

  private final RestApiAuditlogDAO restApiuditLogDAO;

  @Inject
  public RestApiAuditlogImpl(RestApiAuditlogDAO restApiuditLogDAO) {
    this.restApiuditLogDAO = restApiuditLogDAO;
  }

  @Override
  public void logApiCall(RestApiAuditlog restApiAuditlog) {
    restApiuditLogDAO.logThis(restApiAuditlog);
  }

}

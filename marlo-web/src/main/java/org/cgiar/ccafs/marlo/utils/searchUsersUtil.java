package org.cgiar.ccafs.marlo.utils;

import org.cgiar.ciat.auth.LDAPService;
import org.cgiar.ciat.auth.LDAPUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class searchUsersUtil {

  private static final Logger LOG = LoggerFactory.getLogger(searchUsersUtil.class);

  public static void main(String[] args) {

    /** LDAP USERS */

    LDAPUser user = null;
    try {
      user = new LDAPService().searchUserByEmail("@cgiar.org");
    } catch (Exception e) {
      user = null;
    }

    if (user != null) {

      LOG.info("First name : {}", user.getFirstName());
      LOG.info("Last name : {}", user.getLastName());
      LOG.info("User Login : {}", user.getLogin().toLowerCase());
      LOG.info("User Email : {}", user.getEmail().toLowerCase());
      LOG.info("User Status : {}", user.getAttributes().get("userAccountControl"));

      // Microsoft statuses in:
      // https://support.microsoft.com/en-us/help/305144/how-to-use-useraccountcontrol-to-manipulate-user-account-properties
    } else {
      LOG.info("User is NULL - Not available for CGIAR Login");
    }
  }
}
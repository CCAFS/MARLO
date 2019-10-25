package org.cgiar.ccafs.marlo.utils;

import org.cgiar.ciat.auth.LDAPService;
import org.cgiar.ciat.auth.LDAPUser;

public class searchUsersUtil {

  public static void main(String[] args) {

    /** LDAP USERS */

    LDAPUser user = null;
    try {
      user = new LDAPService().searchUserByEmail("email@cgiar");
    } catch (Exception e) {
      user = null;
    }

    if (user != null) {

      System.out.println("First name : " + user.getFirstName());
      System.out.println("Last name : " + user.getLastName());
      System.out.println("User Login : " + user.getLogin().toLowerCase());
      System.out.println("User Email : " + user.getEmail().toLowerCase());
    } else {
      System.out.println("User is NULL - Not available for CGIAR Login");
    }
  }
}
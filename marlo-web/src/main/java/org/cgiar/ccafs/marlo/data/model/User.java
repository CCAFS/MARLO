/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.data.model;
// Generated May 13, 2016 3:37:30 PM by Hibernate Tools 3.4.0.CR1


import java.util.Date;

/**
 * Users object, represents the Users table of the Database model.
 * 
 * @author Hernán David Carvajal
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class User implements java.io.Serializable {


  private Long id;
  private String firstName;
  private String lastName;
  private String username;
  private String email;
  private String password;
  private boolean isCcafsUser;
  private Long createdBy;
  private boolean isActive;
  private Date lastLogin;

  /**
   * create a default user object
   */
  public User() {
  }


  /**
   * create a new user object with the following parameters.
   * 
   * @param email - user email
   * @param password - user password access
   * @param isCcafsUser - indicator if is a ccafs user
   * @param isActive - indicator if the user is active in the system
   */
  public User(String email, String password, boolean isCcafsUser, boolean isActive) {
    this.email = email;
    this.password = password;
    this.isCcafsUser = isCcafsUser;
    this.isActive = isActive;
  }

  /**
   * create a new user object with the following parameters.
   * 
   * @param firstName - user first name
   * @param lastName - user last name
   * @param username - login username for the user
   * @param email - user email
   * @param password - user password access
   * @param isCcafsUser - indicator if is a ccafs user
   * @param createdBy - user creator
   * @param isActive - indicator if the user is active in the system
   * @param lastLogin - date of the last login of the user
   */
  public User(String firstName, String lastName, String username, String email, String password, boolean isCcafsUser,
    Long createdBy, boolean isActive, Date lastLogin) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.username = username;
    this.email = email;
    this.password = password;
    this.isCcafsUser = isCcafsUser;
    this.createdBy = createdBy;
    this.isActive = isActive;
    this.lastLogin = lastLogin;
  }

  public Long getCreatedBy() {
    return this.createdBy;
  }

  public String getEmail() {
    return this.email;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public Long getId() {
    return this.id;
  }

  public Date getLastLogin() {
    return this.lastLogin;
  }

  public String getLastName() {
    return this.lastName;
  }

  public String getPassword() {
    return this.password;
  }

  public String getUsername() {
    return this.username;
  }

  public boolean isActive() {
    return this.isActive;
  }

  public boolean isCcafsUser() {
    return this.isCcafsUser;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setIsActive(boolean isActive) {
    this.isActive = isActive;
  }

  public void setIsCcafsUser(boolean isCcafsUser) {
    this.isCcafsUser = isCcafsUser;
  }

  public void setLastLogin(Date lastLogin) {
    this.lastLogin = lastLogin;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUsername(String username) {
    this.username = username;
  }


}


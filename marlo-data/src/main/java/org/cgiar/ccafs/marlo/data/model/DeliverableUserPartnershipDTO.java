package org.cgiar.ccafs.marlo.data.model;

import java.util.List;

/**
 * @author Hermes Jimenez - CIAT/CCAFS
 */
public class DeliverableUserPartnershipDTO implements java.io.Serializable {


  private static final long serialVersionUID = 8456549948490064852L;

  private Institution institution;
  private List<User> users;


  public Institution getInstitution() {
    return institution;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setInstitution(Institution institution) {
    this.institution = institution;
  }


  public void setUsers(List<User> users) {
    this.users = users;
  }


  @Override
  public String toString() {
    return "DeliverableUserPartnershipDTO [Institution=" + this.getInstitution().getId() + "]";
  }


}


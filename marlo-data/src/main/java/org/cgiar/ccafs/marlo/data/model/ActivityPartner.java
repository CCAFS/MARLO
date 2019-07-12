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


package org.cgiar.ccafs.marlo.data.model;

public class ActivityPartner {

  private int id;
  private Institution partner;
  private String contactName;
  private String contactEmail;
  private String contribution;

  public ActivityPartner() {

  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ActivityPartner) {
      ActivityPartner a = (ActivityPartner) obj;
      return a.getId() == this.id;
    }
    return false;
  }

  public String getComposedName() {
    String composedName = contactName;
    if (contactEmail != null && !contactEmail.isEmpty()) {
      composedName += " <" + contactEmail + "> ";
    }
    return composedName;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public String getContactName() {
    return contactName;
  }

  public String getContribution() {
    return contribution;
  }

  public int getId() {
    return id;
  }

  public Institution getPartner() {
    return partner;
  }

  @Override
  public int hashCode() {
    return this.id;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public void setContribution(String contribution) {
    this.contribution = contribution;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setPartner(Institution partner) {
    this.partner = partner;
  }

  @Override
  public String toString() {
    return "ActivityPartner [id=" + id + ", contactName=" + contactName + ", contactEmail=" + contactEmail + "]";
  }


}

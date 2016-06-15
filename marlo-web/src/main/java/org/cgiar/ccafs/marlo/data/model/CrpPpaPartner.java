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

public class CrpPpaPartner implements java.io.Serializable {


  private static final long serialVersionUID = 9208364810110651075L;


  private Long id;

  private Institution institution;


  private Crp crp;

  public CrpPpaPartner() {
  }

  public CrpPpaPartner(Institution institution, Crp crp) {
    this.institution = institution;
    this.crp = crp;
  }

  public Crp getCrp() {
    return crp;
  }

  public Long getId() {
    return this.id;
  }

  public Institution getInstitution() {
    return institution;
  }

  public void setCrp(Crp crp) {
    this.crp = crp;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setInstitution(Institution institution) {
    this.institution = institution;
  }

}


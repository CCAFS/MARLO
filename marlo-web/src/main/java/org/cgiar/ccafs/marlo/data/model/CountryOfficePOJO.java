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

import java.util.List;


/**
 * CountryOfficePOJO: This is a class used in SystemAdmin that send to front an institution and the partner requests
 * related with the institution
 * 
 * @author avalencia
 * @date Oct 18, 2017
 * @time 3:17:19 PM
 */
public class CountryOfficePOJO implements java.io.Serializable {

  private static final long serialVersionUID = -3560403045129320555L;

  private Institution institution;
  private List<PartnerRequest> partnerRequest;
  /**
   * Created to store the ids (separated by ,) of countryOffice selected
   * 
   * @author avalencia - CCAFS
   * @date Oct 19, 2017
   * @time 8:21:19 AM
   */
  private String ids;


  public CountryOfficePOJO() {
  }


  public CountryOfficePOJO(Institution institution, List<PartnerRequest> partnerRequest) {
    this.institution = institution;
    this.partnerRequest = partnerRequest;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    CountryOfficePOJO other = (CountryOfficePOJO) obj;
    if (institution == null) {
      if (other.institution != null) {
        return false;
      }
    } else if (!institution.equals(other.institution)) {
      return false;
    }
    return true;
  }

  public String getIds() {
    return ids;
  }


  public Institution getInstitution() {
    return institution;
  }


  public List<PartnerRequest> getPartnerRequest() {
    return partnerRequest;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((institution == null) ? 0 : institution.hashCode());
    return result;
  }


  public void setIds(String ids) {
    this.ids = ids;
  }


  public void setInstitution(Institution institution) {
    this.institution = institution;
  }

  public void setPartnerRequest(List<PartnerRequest> partnerRequest) {
    this.partnerRequest = partnerRequest;
  }

}

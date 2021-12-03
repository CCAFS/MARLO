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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.model;


public class InstitutionDictionary extends MarloAuditableEntity implements java.io.Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Institution institution;
  private InstitutionSource source;
  private String sourceId;
  private String sourceName;

  public Institution getInstitution() {
    return institution;
  }

  public InstitutionSource getSource() {
    return source;
  }

  public String getSourceId() {
    return sourceId;
  }

  public String getSourceName() {
    return sourceName;
  }

  public void setInstitution(Institution institution) {
    this.institution = institution;
  }

  public void setSource(InstitutionSource source) {
    this.source = source;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }


}

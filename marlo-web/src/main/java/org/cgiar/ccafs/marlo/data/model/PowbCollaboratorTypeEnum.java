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

/**
 * @author Andrés Valencia - CIAT/CCAFS
 */
public enum PowbCollaboratorTypeEnum {

  CRPPlatform("1", "CRP/Platform"), NonCgiar("2", "Non-CGIAR Collaborator");

  /**
   * Look for the PowbCollaboratorTypeEnum with id
   * 
   * @param id the id to search
   * @return Object PowbCollaboratorTypeEnum if no exist null
   */
  public static PowbCollaboratorTypeEnum getValue(int id) {
    PowbCollaboratorTypeEnum[] lst = PowbCollaboratorTypeEnum.values();
    for (PowbCollaboratorTypeEnum powbCollaboratorTypeEnum : lst) {
      if (powbCollaboratorTypeEnum.getId().equals(String.valueOf(id))) {
        return powbCollaboratorTypeEnum;
      }
    }
    return null;
  }

  private String name;

  private String id;


  private PowbCollaboratorTypeEnum(String id, String name) {
    this.id = id;
    this.name = name;
  }


  public String getId() {
    return id;
  }


  public String getName() {
    return name;
  }


  public void setId(String id) {
    this.id = id;
  }


}

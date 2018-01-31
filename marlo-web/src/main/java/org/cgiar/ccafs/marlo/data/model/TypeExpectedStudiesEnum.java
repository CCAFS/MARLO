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
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public enum TypeExpectedStudiesEnum {

  OUTCOMECASESTUDY(1, "Outcome Case study"), IMPACTCASESTUDY(2, "Impact Case study"),
  IMPACTASSESMENT(3, "Impact assessment"), ADOPTIONSTUDY(4, "Adoption Study"),
  CRP_PTF(5, "CRP/PTF Commissioned -Evaluation"), EVAULATION(6, "Evaluation"), REVIEW(7, "Review"),
  LEARNING(8, "Learning"), OTHER(9, "Any other, please specify");

  /**
   * Look for the ProjectStatusEnum with id
   * 
   * @param id the id to search
   * @return Object ProjectStatusEnum if no exist null
   */
  public static TypeExpectedStudiesEnum getValue(int id) {
    TypeExpectedStudiesEnum[] lst = TypeExpectedStudiesEnum.values();
    for (TypeExpectedStudiesEnum projectStatusEnum : lst) {
      if (projectStatusEnum.getId() == id) {
        return projectStatusEnum;
      }
    }
    return null;
  }


  private String type;


  private int id;


  private TypeExpectedStudiesEnum(int id, String type) {
    this.id = id;
    this.type = type;
  }


  public int getId() {
    return id;
  }


  public String getType() {
    return type;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setType(String type) {
    this.type = type;
  }


}

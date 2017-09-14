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

/**
 * 
 */
package org.cgiar.ccafs.marlo.data.model;

import java.io.Serializable;


/**
 * Modified by @author nmatovu last on Oct 9, 2016
 */
public class CenterAllTypes implements Serializable {

  private static final long serialVersionUID = 8700833701800021858L;

  private Long id;
  /**
   * The class or entity to which this type belongs.
   */
  private String className;
  /**
   * The type of record
   */
  private String typeName;


  /**
   * 
   */
  public CenterAllTypes() {
    super();
    // TODO Auto-generated constructor stub
  }


  /**
   * @param className
   * @param typeName
   */
  public CenterAllTypes(String className, String typeName) {
    super();
    this.className = className;
    this.typeName = typeName;
  }


  /**
   * @return the className
   */
  public String getClassName() {
    return className;
  }


  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @return the typeName
   */
  public String getTypeName() {
    return typeName;
  }

  /**
   * @param className the className to set
   */
  public void setClassName(String className) {
    this.className = className;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @param typeName the typeName to set
   */
  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  @Override
  public String toString() {
    return "CenterAllTypes [id=" + id + ", className=" + className + ", typeName=" + typeName + "]";
  }


}

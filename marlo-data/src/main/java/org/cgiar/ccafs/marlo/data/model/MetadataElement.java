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

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class MetadataElement implements Serializable, IAuditLog {

  private static final long serialVersionUID = 5792114060838051002L;
  @Expose
  private Integer id;
  @Expose
  private String schema;
  @Expose
  private String element;
  @Expose
  private String qualifier;
  @Expose
  private String econdedName;
  @Expose
  private String status;
  @Expose
  private String vocabulary;
  @Expose
  private String definitation;

  private Set<DeliverableMetadataElement> deliverableMetadataElements = new HashSet<DeliverableMetadataElement>(0);

  public MetadataElement() {
  }

  public MetadataElement(String schema, String element, String qualifier, String econdedName, String status,
    String vocabulary, String definitation, Set<DeliverableMetadataElement> deliverableMetadataElements) {
    this.schema = schema;
    this.element = element;
    this.qualifier = qualifier;
    this.econdedName = econdedName;
    this.status = status;
    this.vocabulary = vocabulary;
    this.definitation = definitation;
    this.deliverableMetadataElements = deliverableMetadataElements;
  }

  public String getDefinitation() {
    return definitation;
  }


  public Set<DeliverableMetadataElement> getDeliverableMetadataElements() {
    return deliverableMetadataElements;
  }


  public String getEcondedName() {
    return econdedName;
  }

  public String getElement() {
    return element;
  }


  @Override
  public Integer getId() {
    return id;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  @Override
  public String getModificationJustification() {

    return "";
  }


  @Override
  public User getModifiedBy() {
    User u = new User();
    u.setId(new Long(3));
    return u;
  }


  public String getQualifier() {
    return qualifier;
  }


  public String getSchema() {
    return schema;
  }


  public String getStatus() {
    return status;
  }

  public String getVocabulary() {
    return vocabulary;
  }

  @Override
  public boolean isActive() {

    return true;
  }

  public void setDefinitation(String definitation) {
    this.definitation = definitation;
  }

  public void setDeliverableMetadataElements(Set<DeliverableMetadataElement> deliverableMetadataElements) {
    this.deliverableMetadataElements = deliverableMetadataElements;
  }

  public void setEcondedName(String econdedName) {
    this.econdedName = econdedName;
  }

  public void setElement(String element) {
    this.element = element;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setQualifier(String qualifier) {
    this.qualifier = qualifier;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setVocabulary(String vocabulary) {
    this.vocabulary = vocabulary;
  }

}

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


package org.cgiar.ccafs.marlo.ocs.ws.client;

public class TWsMarloPla implements java.io.Serializable {

  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
    new org.apache.axis.description.TypeDesc(TWsMarloPla.class, true);

  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloPla"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("agreementId");
    elemField.setXmlName(new javax.xml.namespace.QName("", "agreementId"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("description");
    elemField.setXmlName(new javax.xml.namespace.QName("", "description"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("partner");
    elemField.setXmlName(new javax.xml.namespace.QName("", "partner"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("partnertext");
    elemField.setXmlName(new javax.xml.namespace.QName("", "partnertext"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("plaId");
    elemField.setXmlName(new javax.xml.namespace.QName("", "plaId"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
  }

  /**
   * Get Custom Deserializer
   */
  public static org.apache.axis.encoding.Deserializer getDeserializer(java.lang.String mechType,
    java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
    return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
  }

  /**
   * Get Custom Serializer
   */
  public static org.apache.axis.encoding.Serializer getSerializer(java.lang.String mechType, java.lang.Class _javaType,
    javax.xml.namespace.QName _xmlType) {
    return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
  }

  /**
   * Return type metadata object
   */
  public static org.apache.axis.description.TypeDesc getTypeDesc() {
    return typeDesc;
  }

  private java.lang.String agreementId;

  private java.lang.String description;


  private java.lang.String partner;


  private java.lang.String partnertext;


  private java.lang.String plaId;


  private java.lang.Object __equalsCalc = null;


  private boolean __hashCodeCalc = false;


  public TWsMarloPla() {
  }


  public TWsMarloPla(java.lang.String agreementId, java.lang.String description, java.lang.String partner,
    java.lang.String partnertext, java.lang.String plaId) {
    this.agreementId = agreementId;
    this.description = description;
    this.partner = partner;
    this.partnertext = partnertext;
    this.plaId = plaId;
  }


  @Override
  public synchronized boolean equals(java.lang.Object obj) {
    if (!(obj instanceof TWsMarloPla)) {
      return false;
    }
    TWsMarloPla other = (TWsMarloPla) obj;
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    boolean _equals;
    _equals = true
      && ((this.agreementId == null && other.getAgreementId() == null)
        || (this.agreementId != null && this.agreementId.equals(other.getAgreementId())))
      && ((this.description == null && other.getDescription() == null)
        || (this.description != null && this.description.equals(other.getDescription())))
      && ((this.partner == null && other.getPartner() == null)
        || (this.partner != null && this.partner.equals(other.getPartner())))
      && ((this.partnertext == null && other.getPartnertext() == null)
        || (this.partnertext != null && this.partnertext.equals(other.getPartnertext())))
      && ((this.plaId == null && other.getPlaId() == null)
        || (this.plaId != null && this.plaId.equals(other.getPlaId())));
    __equalsCalc = null;
    return _equals;
  }


  /**
   * Gets the agreementId value for this TWsMarloPla.
   * 
   * @return agreementId
   */
  public java.lang.String getAgreementId() {
    return agreementId;
  }


  /**
   * Gets the description value for this TWsMarloPla.
   * 
   * @return description
   */
  public java.lang.String getDescription() {
    return description;
  }

  /**
   * Gets the partner value for this TWsMarloPla.
   * 
   * @return partner
   */
  public java.lang.String getPartner() {
    return partner;
  }

  /**
   * Gets the partnertext value for this TWsMarloPla.
   * 
   * @return partnertext
   */
  public java.lang.String getPartnertext() {
    return partnertext;
  }

  /**
   * Gets the plaId value for this TWsMarloPla.
   * 
   * @return plaId
   */
  public java.lang.String getPlaId() {
    return plaId;
  }

  @Override
  public synchronized int hashCode() {
    if (__hashCodeCalc) {
      return 0;
    }
    __hashCodeCalc = true;
    int _hashCode = 1;
    if (this.getAgreementId() != null) {
      _hashCode += this.getAgreementId().hashCode();
    }
    if (this.getDescription() != null) {
      _hashCode += this.getDescription().hashCode();
    }
    if (this.getPartner() != null) {
      _hashCode += this.getPartner().hashCode();
    }
    if (this.getPartnertext() != null) {
      _hashCode += this.getPartnertext().hashCode();
    }
    if (this.getPlaId() != null) {
      _hashCode += this.getPlaId().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }

  /**
   * Sets the agreementId value for this TWsMarloPla.
   * 
   * @param agreementId
   */
  public void setAgreementId(java.lang.String agreementId) {
    this.agreementId = agreementId;
  }

  /**
   * Sets the description value for this TWsMarloPla.
   * 
   * @param description
   */
  public void setDescription(java.lang.String description) {
    this.description = description;
  }

  /**
   * Sets the partner value for this TWsMarloPla.
   * 
   * @param partner
   */
  public void setPartner(java.lang.String partner) {
    this.partner = partner;
  }

  /**
   * Sets the partnertext value for this TWsMarloPla.
   * 
   * @param partnertext
   */
  public void setPartnertext(java.lang.String partnertext) {
    this.partnertext = partnertext;
  }

  /**
   * Sets the plaId value for this TWsMarloPla.
   * 
   * @param plaId
   */
  public void setPlaId(java.lang.String plaId) {
    this.plaId = plaId;
  }

}

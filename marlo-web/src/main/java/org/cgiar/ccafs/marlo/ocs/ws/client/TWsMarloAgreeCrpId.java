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

public class TWsMarloAgreeCrpId implements java.io.Serializable {

  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
    new org.apache.axis.description.TypeDesc(TWsMarloAgreeCrpId.class, true);

  static {
    typeDesc
      .setXmlType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloAgreeCrpId"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("agreementId");
    elemField.setXmlName(new javax.xml.namespace.QName("", "agreementId"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("crp");
    elemField.setXmlName(new javax.xml.namespace.QName("", "crp"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("crpText");
    elemField.setXmlName(new javax.xml.namespace.QName("", "crpText"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("percentage");
    elemField.setXmlName(new javax.xml.namespace.QName("", "percentage"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
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


  private java.lang.String crp;


  private java.lang.String crpText;


  private java.lang.Double percentage;


  private java.lang.Object __equalsCalc = null;


  private boolean __hashCodeCalc = false;


  public TWsMarloAgreeCrpId() {
  }


  public TWsMarloAgreeCrpId(java.lang.String agreementId, java.lang.String crp, java.lang.String crpText,
    java.lang.Double percentage) {
    this.agreementId = agreementId;
    this.crp = crp;
    this.crpText = crpText;
    this.percentage = percentage;
  }


  @Override
  public synchronized boolean equals(java.lang.Object obj) {
    if (!(obj instanceof TWsMarloAgreeCrpId)) {
      return false;
    }
    TWsMarloAgreeCrpId other = (TWsMarloAgreeCrpId) obj;
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
      && ((this.crp == null && other.getCrp() == null) || (this.crp != null && this.crp.equals(other.getCrp())))
      && ((this.crpText == null && other.getCrpText() == null)
        || (this.crpText != null && this.crpText.equals(other.getCrpText())))
      && ((this.percentage == null && other.getPercentage() == null)
        || (this.percentage != null && this.percentage.equals(other.getPercentage())));
    __equalsCalc = null;
    return _equals;
  }

  /**
   * Gets the agreementId value for this TWsMarloAgreeCrpId.
   * 
   * @return agreementId
   */
  public java.lang.String getAgreementId() {
    return agreementId;
  }

  /**
   * Gets the crp value for this TWsMarloAgreeCrpId.
   * 
   * @return crp
   */
  public java.lang.String getCrp() {
    return crp;
  }

  /**
   * Gets the crpText value for this TWsMarloAgreeCrpId.
   * 
   * @return crpText
   */
  public java.lang.String getCrpText() {
    return crpText;
  }

  /**
   * Gets the percentage value for this TWsMarloAgreeCrpId.
   * 
   * @return percentage
   */
  public java.lang.Double getPercentage() {
    return percentage;
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
    if (this.getCrp() != null) {
      _hashCode += this.getCrp().hashCode();
    }
    if (this.getCrpText() != null) {
      _hashCode += this.getCrpText().hashCode();
    }
    if (this.getPercentage() != null) {
      _hashCode += this.getPercentage().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }

  /**
   * Sets the agreementId value for this TWsMarloAgreeCrpId.
   * 
   * @param agreementId
   */
  public void setAgreementId(java.lang.String agreementId) {
    this.agreementId = agreementId;
  }

  /**
   * Sets the crp value for this TWsMarloAgreeCrpId.
   * 
   * @param crp
   */
  public void setCrp(java.lang.String crp) {
    this.crp = crp;
  }

  /**
   * Sets the crpText value for this TWsMarloAgreeCrpId.
   * 
   * @param crpText
   */
  public void setCrpText(java.lang.String crpText) {
    this.crpText = crpText;
  }

  /**
   * Sets the percentage value for this TWsMarloAgreeCrpId.
   * 
   * @param percentage
   */
  public void setPercentage(java.lang.Double percentage) {
    this.percentage = percentage;
  }

}

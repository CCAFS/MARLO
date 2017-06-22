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

public class TWsMarloAgreeCountryId implements java.io.Serializable {

  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
    new org.apache.axis.description.TypeDesc(TWsMarloAgreeCountryId.class, true);

  static {
    typeDesc
      .setXmlType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloAgreeCountryId"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("agreementId");
    elemField.setXmlName(new javax.xml.namespace.QName("", "agreementId"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("country");
    elemField.setXmlName(new javax.xml.namespace.QName("", "country"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("countrytext");
    elemField.setXmlName(new javax.xml.namespace.QName("", "countrytext"));
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


  private java.lang.String country;


  private java.lang.String countrytext;


  private java.lang.Double percentage;


  private java.lang.Object __equalsCalc = null;


  private boolean __hashCodeCalc = false;


  public TWsMarloAgreeCountryId() {
  }


  public TWsMarloAgreeCountryId(java.lang.String agreementId, java.lang.String country, java.lang.String countrytext,
    java.lang.Double percentage) {
    this.agreementId = agreementId;
    this.country = country;
    this.countrytext = countrytext;
    this.percentage = percentage;
  }


  @Override
  public synchronized boolean equals(java.lang.Object obj) {
    if (!(obj instanceof TWsMarloAgreeCountryId)) {
      return false;
    }
    TWsMarloAgreeCountryId other = (TWsMarloAgreeCountryId) obj;
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
      && ((this.country == null && other.getCountry() == null)
        || (this.country != null && this.country.equals(other.getCountry())))
      && ((this.countrytext == null && other.getCountrytext() == null)
        || (this.countrytext != null && this.countrytext.equals(other.getCountrytext())))
      && ((this.percentage == null && other.getPercentage() == null)
        || (this.percentage != null && this.percentage.equals(other.getPercentage())));
    __equalsCalc = null;
    return _equals;
  }

  /**
   * Gets the agreementId value for this TWsMarloAgreeCountryId.
   * 
   * @return agreementId
   */
  public java.lang.String getAgreementId() {
    return agreementId;
  }

  /**
   * Gets the country value for this TWsMarloAgreeCountryId.
   * 
   * @return country
   */
  public java.lang.String getCountry() {
    return country;
  }

  /**
   * Gets the countrytext value for this TWsMarloAgreeCountryId.
   * 
   * @return countrytext
   */
  public java.lang.String getCountrytext() {
    return countrytext;
  }

  /**
   * Gets the percentage value for this TWsMarloAgreeCountryId.
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
    if (this.getCountry() != null) {
      _hashCode += this.getCountry().hashCode();
    }
    if (this.getCountrytext() != null) {
      _hashCode += this.getCountrytext().hashCode();
    }
    if (this.getPercentage() != null) {
      _hashCode += this.getPercentage().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }

  /**
   * Sets the agreementId value for this TWsMarloAgreeCountryId.
   * 
   * @param agreementId
   */
  public void setAgreementId(java.lang.String agreementId) {
    this.agreementId = agreementId;
  }

  /**
   * Sets the country value for this TWsMarloAgreeCountryId.
   * 
   * @param country
   */
  public void setCountry(java.lang.String country) {
    this.country = country;
  }

  /**
   * Sets the countrytext value for this TWsMarloAgreeCountryId.
   * 
   * @param countrytext
   */
  public void setCountrytext(java.lang.String countrytext) {
    this.countrytext = countrytext;
  }

  /**
   * Sets the percentage value for this TWsMarloAgreeCountryId.
   * 
   * @param percentage
   */
  public void setPercentage(java.lang.Double percentage) {
    this.percentage = percentage;
  }

}

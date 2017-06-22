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

public class TWsMarloPlaCountry implements java.io.Serializable {

  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
    new org.apache.axis.description.TypeDesc(TWsMarloPlaCountry.class, true);

  static {
    typeDesc
      .setXmlType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloPlaCountry"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("id");
    elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
    elemField
      .setXmlType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloPlaCountryId"));
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

  private org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountryId id;
  private java.lang.Object __equalsCalc = null;

  private boolean __hashCodeCalc = false;

  public TWsMarloPlaCountry() {
  }

  public TWsMarloPlaCountry(org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountryId id) {
    this.id = id;
  }

  @Override
  public synchronized boolean equals(java.lang.Object obj) {
    if (!(obj instanceof TWsMarloPlaCountry)) {
      return false;
    }
    TWsMarloPlaCountry other = (TWsMarloPlaCountry) obj;
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
    _equals =
      true && ((this.id == null && other.getId() == null) || (this.id != null && this.id.equals(other.getId())));
    __equalsCalc = null;
    return _equals;
  }

  /**
   * Gets the id value for this TWsMarloPlaCountry.
   * 
   * @return id
   */
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountryId getId() {
    return id;
  }

  @Override
  public synchronized int hashCode() {
    if (__hashCodeCalc) {
      return 0;
    }
    __hashCodeCalc = true;
    int _hashCode = 1;
    if (this.getId() != null) {
      _hashCode += this.getId().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }

  /**
   * Sets the id value for this TWsMarloPlaCountry.
   * 
   * @param id
   */
  public void setId(org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountryId id) {
    this.id = id;
  }

}

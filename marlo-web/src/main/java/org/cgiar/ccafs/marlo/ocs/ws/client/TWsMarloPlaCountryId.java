/**
 * TWsMarloPlaCountryId.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.cgiar.ccafs.marlo.ocs.ws.client;

public class TWsMarloPlaCountryId  implements java.io.Serializable {
    private java.lang.String country;

    private java.lang.String countryText;

    private java.lang.Double percentage;

    private java.lang.String plaId;

    public TWsMarloPlaCountryId() {
    }

    public TWsMarloPlaCountryId(
           java.lang.String country,
           java.lang.String countryText,
           java.lang.Double percentage,
           java.lang.String plaId) {
           this.country = country;
           this.countryText = countryText;
           this.percentage = percentage;
           this.plaId = plaId;
    }


    /**
     * Gets the country value for this TWsMarloPlaCountryId.
     * 
     * @return country
     */
    public java.lang.String getCountry() {
        return country;
    }


    /**
     * Sets the country value for this TWsMarloPlaCountryId.
     * 
     * @param country
     */
    public void setCountry(java.lang.String country) {
        this.country = country;
    }


    /**
     * Gets the countryText value for this TWsMarloPlaCountryId.
     * 
     * @return countryText
     */
    public java.lang.String getCountryText() {
        return countryText;
    }


    /**
     * Sets the countryText value for this TWsMarloPlaCountryId.
     * 
     * @param countryText
     */
    public void setCountryText(java.lang.String countryText) {
        this.countryText = countryText;
    }


    /**
     * Gets the percentage value for this TWsMarloPlaCountryId.
     * 
     * @return percentage
     */
    public java.lang.Double getPercentage() {
        return percentage;
    }


    /**
     * Sets the percentage value for this TWsMarloPlaCountryId.
     * 
     * @param percentage
     */
    public void setPercentage(java.lang.Double percentage) {
        this.percentage = percentage;
    }


    /**
     * Gets the plaId value for this TWsMarloPlaCountryId.
     * 
     * @return plaId
     */
    public java.lang.String getPlaId() {
        return plaId;
    }


    /**
     * Sets the plaId value for this TWsMarloPlaCountryId.
     * 
     * @param plaId
     */
    public void setPlaId(java.lang.String plaId) {
        this.plaId = plaId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TWsMarloPlaCountryId)) return false;
        TWsMarloPlaCountryId other = (TWsMarloPlaCountryId) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.country==null && other.getCountry()==null) || 
             (this.country!=null &&
              this.country.equals(other.getCountry()))) &&
            ((this.countryText==null && other.getCountryText()==null) || 
             (this.countryText!=null &&
              this.countryText.equals(other.getCountryText()))) &&
            ((this.percentage==null && other.getPercentage()==null) || 
             (this.percentage!=null &&
              this.percentage.equals(other.getPercentage()))) &&
            ((this.plaId==null && other.getPlaId()==null) || 
             (this.plaId!=null &&
              this.plaId.equals(other.getPlaId())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCountry() != null) {
            _hashCode += getCountry().hashCode();
        }
        if (getCountryText() != null) {
            _hashCode += getCountryText().hashCode();
        }
        if (getPercentage() != null) {
            _hashCode += getPercentage().hashCode();
        }
        if (getPlaId() != null) {
            _hashCode += getPlaId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TWsMarloPlaCountryId.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloPlaCountryId"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("country");
        elemField.setXmlName(new javax.xml.namespace.QName("", "country"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countryText");
        elemField.setXmlName(new javax.xml.namespace.QName("", "countryText"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("plaId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "plaId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

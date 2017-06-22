/**
 * TWsMarloAgreeCountryId.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.cgiar.ciat.abw.control.logic;

public class TWsMarloAgreeCountryId  implements java.io.Serializable {
    private java.lang.String agreementId;

    private java.lang.String country;

    private java.lang.String countrytext;

    private java.lang.Double percentage;

    public TWsMarloAgreeCountryId() {
    }

    public TWsMarloAgreeCountryId(
           java.lang.String agreementId,
           java.lang.String country,
           java.lang.String countrytext,
           java.lang.Double percentage) {
           this.agreementId = agreementId;
           this.country = country;
           this.countrytext = countrytext;
           this.percentage = percentage;
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
     * Sets the agreementId value for this TWsMarloAgreeCountryId.
     * 
     * @param agreementId
     */
    public void setAgreementId(java.lang.String agreementId) {
        this.agreementId = agreementId;
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
     * Sets the country value for this TWsMarloAgreeCountryId.
     * 
     * @param country
     */
    public void setCountry(java.lang.String country) {
        this.country = country;
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
     * Sets the countrytext value for this TWsMarloAgreeCountryId.
     * 
     * @param countrytext
     */
    public void setCountrytext(java.lang.String countrytext) {
        this.countrytext = countrytext;
    }


    /**
     * Gets the percentage value for this TWsMarloAgreeCountryId.
     * 
     * @return percentage
     */
    public java.lang.Double getPercentage() {
        return percentage;
    }


    /**
     * Sets the percentage value for this TWsMarloAgreeCountryId.
     * 
     * @param percentage
     */
    public void setPercentage(java.lang.Double percentage) {
        this.percentage = percentage;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TWsMarloAgreeCountryId)) return false;
        TWsMarloAgreeCountryId other = (TWsMarloAgreeCountryId) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.agreementId==null && other.getAgreementId()==null) || 
             (this.agreementId!=null &&
              this.agreementId.equals(other.getAgreementId()))) &&
            ((this.country==null && other.getCountry()==null) || 
             (this.country!=null &&
              this.country.equals(other.getCountry()))) &&
            ((this.countrytext==null && other.getCountrytext()==null) || 
             (this.countrytext!=null &&
              this.countrytext.equals(other.getCountrytext()))) &&
            ((this.percentage==null && other.getPercentage()==null) || 
             (this.percentage!=null &&
              this.percentage.equals(other.getPercentage())));
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
        if (getAgreementId() != null) {
            _hashCode += getAgreementId().hashCode();
        }
        if (getCountry() != null) {
            _hashCode += getCountry().hashCode();
        }
        if (getCountrytext() != null) {
            _hashCode += getCountrytext().hashCode();
        }
        if (getPercentage() != null) {
            _hashCode += getPercentage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TWsMarloAgreeCountryId.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloAgreeCountryId"));
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

/**
 * TWsMarloAgreeCrpId.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.cgiar.ccafs.marlo.ocs.ws.client;

public class TWsMarloAgreeCrpId  implements java.io.Serializable {
    private java.lang.String agreementId;

    private java.lang.String crp;

    private java.lang.String crpText;

    private java.lang.Double percentage;

    public TWsMarloAgreeCrpId() {
    }

    public TWsMarloAgreeCrpId(
           java.lang.String agreementId,
           java.lang.String crp,
           java.lang.String crpText,
           java.lang.Double percentage) {
           this.agreementId = agreementId;
           this.crp = crp;
           this.crpText = crpText;
           this.percentage = percentage;
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
     * Sets the agreementId value for this TWsMarloAgreeCrpId.
     * 
     * @param agreementId
     */
    public void setAgreementId(java.lang.String agreementId) {
        this.agreementId = agreementId;
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
     * Sets the crp value for this TWsMarloAgreeCrpId.
     * 
     * @param crp
     */
    public void setCrp(java.lang.String crp) {
        this.crp = crp;
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
     * Sets the crpText value for this TWsMarloAgreeCrpId.
     * 
     * @param crpText
     */
    public void setCrpText(java.lang.String crpText) {
        this.crpText = crpText;
    }


    /**
     * Gets the percentage value for this TWsMarloAgreeCrpId.
     * 
     * @return percentage
     */
    public java.lang.Double getPercentage() {
        return percentage;
    }


    /**
     * Sets the percentage value for this TWsMarloAgreeCrpId.
     * 
     * @param percentage
     */
    public void setPercentage(java.lang.Double percentage) {
        this.percentage = percentage;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TWsMarloAgreeCrpId)) return false;
        TWsMarloAgreeCrpId other = (TWsMarloAgreeCrpId) obj;
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
            ((this.crp==null && other.getCrp()==null) || 
             (this.crp!=null &&
              this.crp.equals(other.getCrp()))) &&
            ((this.crpText==null && other.getCrpText()==null) || 
             (this.crpText!=null &&
              this.crpText.equals(other.getCrpText()))) &&
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
        if (getCrp() != null) {
            _hashCode += getCrp().hashCode();
        }
        if (getCrpText() != null) {
            _hashCode += getCrpText().hashCode();
        }
        if (getPercentage() != null) {
            _hashCode += getPercentage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TWsMarloAgreeCrpId.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloAgreeCrpId"));
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

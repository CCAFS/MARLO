/**
 * FileTransfer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.cgiar.ccafs.marlo.ocs.ws.client;

public class FileTransfer  implements java.io.Serializable {
    private byte[] docBytes;

    private java.lang.String docName;

    public FileTransfer() {
    }

    public FileTransfer(
           byte[] docBytes,
           java.lang.String docName) {
           this.docBytes = docBytes;
           this.docName = docName;
    }


    /**
     * Gets the docBytes value for this FileTransfer.
     * 
     * @return docBytes
     */
    public byte[] getDocBytes() {
        return docBytes;
    }


    /**
     * Sets the docBytes value for this FileTransfer.
     * 
     * @param docBytes
     */
    public void setDocBytes(byte[] docBytes) {
        this.docBytes = docBytes;
    }


    /**
     * Gets the docName value for this FileTransfer.
     * 
     * @return docName
     */
    public java.lang.String getDocName() {
        return docName;
    }


    /**
     * Sets the docName value for this FileTransfer.
     * 
     * @param docName
     */
    public void setDocName(java.lang.String docName) {
        this.docName = docName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FileTransfer)) return false;
        FileTransfer other = (FileTransfer) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.docBytes==null && other.getDocBytes()==null) || 
             (this.docBytes!=null &&
              java.util.Arrays.equals(this.docBytes, other.getDocBytes()))) &&
            ((this.docName==null && other.getDocName()==null) || 
             (this.docName!=null &&
              this.docName.equals(other.getDocName())));
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
        if (getDocBytes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDocBytes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDocBytes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDocName() != null) {
            _hashCode += getDocName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FileTransfer.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "fileTransfer"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("docBytes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "docBytes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("docName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "docName"));
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

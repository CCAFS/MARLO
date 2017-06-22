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

public class TWsMarloAgree implements java.io.Serializable {

  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
    new org.apache.axis.description.TypeDesc(TWsMarloAgree.class, true);

  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloAgree"));
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
    elemField.setFieldName("donor");
    elemField.setXmlName(new javax.xml.namespace.QName("", "donor"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("donorText");
    elemField.setXmlName(new javax.xml.namespace.QName("", "donorText"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("endDate");
    elemField.setXmlName(new javax.xml.namespace.QName("", "endDate"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("extentionDate");
    elemField.setXmlName(new javax.xml.namespace.QName("", "extentionDate"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("fundingType");
    elemField.setXmlName(new javax.xml.namespace.QName("", "fundingType"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("grantAmount");
    elemField.setXmlName(new javax.xml.namespace.QName("", "grantAmount"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("objectives");
    elemField.setXmlName(new javax.xml.namespace.QName("", "objectives"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("researcher");
    elemField.setXmlName(new javax.xml.namespace.QName("", "researcher"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("researcherText");
    elemField.setXmlName(new javax.xml.namespace.QName("", "researcherText"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("shortName");
    elemField.setXmlName(new javax.xml.namespace.QName("", "shortName"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("startDate");
    elemField.setXmlName(new javax.xml.namespace.QName("", "startDate"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("status");
    elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
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

  private java.lang.String donor;

  private java.lang.String donorText;

  private java.lang.String endDate;

  private java.lang.String extentionDate;

  private java.lang.String fundingType;

  private java.lang.Double grantAmount;

  private java.lang.String objectives;

  private java.lang.String researcher;

  private java.lang.String researcherText;


  private java.lang.String shortName;


  private java.lang.String startDate;


  private java.lang.String status;


  private java.lang.Object __equalsCalc = null;


  private boolean __hashCodeCalc = false;


  public TWsMarloAgree() {
  }


  public TWsMarloAgree(java.lang.String agreementId, java.lang.String description, java.lang.String donor,
    java.lang.String donorText, java.lang.String endDate, java.lang.String extentionDate, java.lang.String fundingType,
    java.lang.Double grantAmount, java.lang.String objectives, java.lang.String researcher,
    java.lang.String researcherText, java.lang.String shortName, java.lang.String startDate, java.lang.String status) {
    this.agreementId = agreementId;
    this.description = description;
    this.donor = donor;
    this.donorText = donorText;
    this.endDate = endDate;
    this.extentionDate = extentionDate;
    this.fundingType = fundingType;
    this.grantAmount = grantAmount;
    this.objectives = objectives;
    this.researcher = researcher;
    this.researcherText = researcherText;
    this.shortName = shortName;
    this.startDate = startDate;
    this.status = status;
  }


  @Override
  public synchronized boolean equals(java.lang.Object obj) {
    if (!(obj instanceof TWsMarloAgree)) {
      return false;
    }
    TWsMarloAgree other = (TWsMarloAgree) obj;
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
      && ((this.donor == null && other.getDonor() == null)
        || (this.donor != null && this.donor.equals(other.getDonor())))
      && ((this.donorText == null && other.getDonorText() == null)
        || (this.donorText != null && this.donorText.equals(other.getDonorText())))
      && ((this.endDate == null && other.getEndDate() == null)
        || (this.endDate != null && this.endDate.equals(other.getEndDate())))
      && ((this.extentionDate == null && other.getExtentionDate() == null)
        || (this.extentionDate != null && this.extentionDate.equals(other.getExtentionDate())))
      && ((this.fundingType == null && other.getFundingType() == null)
        || (this.fundingType != null && this.fundingType.equals(other.getFundingType())))
      && ((this.grantAmount == null && other.getGrantAmount() == null)
        || (this.grantAmount != null && this.grantAmount.equals(other.getGrantAmount())))
      && ((this.objectives == null && other.getObjectives() == null)
        || (this.objectives != null && this.objectives.equals(other.getObjectives())))
      && ((this.researcher == null && other.getResearcher() == null)
        || (this.researcher != null && this.researcher.equals(other.getResearcher())))
      && ((this.researcherText == null && other.getResearcherText() == null)
        || (this.researcherText != null && this.researcherText.equals(other.getResearcherText())))
      && ((this.shortName == null && other.getShortName() == null)
        || (this.shortName != null && this.shortName.equals(other.getShortName())))
      && ((this.startDate == null && other.getStartDate() == null)
        || (this.startDate != null && this.startDate.equals(other.getStartDate())))
      && ((this.status == null && other.getStatus() == null)
        || (this.status != null && this.status.equals(other.getStatus())));
    __equalsCalc = null;
    return _equals;
  }


  /**
   * Gets the agreementId value for this TWsMarloAgree.
   * 
   * @return agreementId
   */
  public java.lang.String getAgreementId() {
    return agreementId;
  }


  /**
   * Gets the description value for this TWsMarloAgree.
   * 
   * @return description
   */
  public java.lang.String getDescription() {
    return description;
  }


  /**
   * Gets the donor value for this TWsMarloAgree.
   * 
   * @return donor
   */
  public java.lang.String getDonor() {
    return donor;
  }


  /**
   * Gets the donorText value for this TWsMarloAgree.
   * 
   * @return donorText
   */
  public java.lang.String getDonorText() {
    return donorText;
  }


  /**
   * Gets the endDate value for this TWsMarloAgree.
   * 
   * @return endDate
   */
  public java.lang.String getEndDate() {
    return endDate;
  }


  /**
   * Gets the extentionDate value for this TWsMarloAgree.
   * 
   * @return extentionDate
   */
  public java.lang.String getExtentionDate() {
    return extentionDate;
  }


  /**
   * Gets the fundingType value for this TWsMarloAgree.
   * 
   * @return fundingType
   */
  public java.lang.String getFundingType() {
    return fundingType;
  }


  /**
   * Gets the grantAmount value for this TWsMarloAgree.
   * 
   * @return grantAmount
   */
  public java.lang.Double getGrantAmount() {
    return grantAmount;
  }


  /**
   * Gets the objectives value for this TWsMarloAgree.
   * 
   * @return objectives
   */
  public java.lang.String getObjectives() {
    return objectives;
  }


  /**
   * Gets the researcher value for this TWsMarloAgree.
   * 
   * @return researcher
   */
  public java.lang.String getResearcher() {
    return researcher;
  }


  /**
   * Gets the researcherText value for this TWsMarloAgree.
   * 
   * @return researcherText
   */
  public java.lang.String getResearcherText() {
    return researcherText;
  }


  /**
   * Gets the shortName value for this TWsMarloAgree.
   * 
   * @return shortName
   */
  public java.lang.String getShortName() {
    return shortName;
  }


  /**
   * Gets the startDate value for this TWsMarloAgree.
   * 
   * @return startDate
   */
  public java.lang.String getStartDate() {
    return startDate;
  }


  /**
   * Gets the status value for this TWsMarloAgree.
   * 
   * @return status
   */
  public java.lang.String getStatus() {
    return status;
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
    if (this.getDonor() != null) {
      _hashCode += this.getDonor().hashCode();
    }
    if (this.getDonorText() != null) {
      _hashCode += this.getDonorText().hashCode();
    }
    if (this.getEndDate() != null) {
      _hashCode += this.getEndDate().hashCode();
    }
    if (this.getExtentionDate() != null) {
      _hashCode += this.getExtentionDate().hashCode();
    }
    if (this.getFundingType() != null) {
      _hashCode += this.getFundingType().hashCode();
    }
    if (this.getGrantAmount() != null) {
      _hashCode += this.getGrantAmount().hashCode();
    }
    if (this.getObjectives() != null) {
      _hashCode += this.getObjectives().hashCode();
    }
    if (this.getResearcher() != null) {
      _hashCode += this.getResearcher().hashCode();
    }
    if (this.getResearcherText() != null) {
      _hashCode += this.getResearcherText().hashCode();
    }
    if (this.getShortName() != null) {
      _hashCode += this.getShortName().hashCode();
    }
    if (this.getStartDate() != null) {
      _hashCode += this.getStartDate().hashCode();
    }
    if (this.getStatus() != null) {
      _hashCode += this.getStatus().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }


  /**
   * Sets the agreementId value for this TWsMarloAgree.
   * 
   * @param agreementId
   */
  public void setAgreementId(java.lang.String agreementId) {
    this.agreementId = agreementId;
  }


  /**
   * Sets the description value for this TWsMarloAgree.
   * 
   * @param description
   */
  public void setDescription(java.lang.String description) {
    this.description = description;
  }


  /**
   * Sets the donor value for this TWsMarloAgree.
   * 
   * @param donor
   */
  public void setDonor(java.lang.String donor) {
    this.donor = donor;
  }


  /**
   * Sets the donorText value for this TWsMarloAgree.
   * 
   * @param donorText
   */
  public void setDonorText(java.lang.String donorText) {
    this.donorText = donorText;
  }


  /**
   * Sets the endDate value for this TWsMarloAgree.
   * 
   * @param endDate
   */
  public void setEndDate(java.lang.String endDate) {
    this.endDate = endDate;
  }

  /**
   * Sets the extentionDate value for this TWsMarloAgree.
   * 
   * @param extentionDate
   */
  public void setExtentionDate(java.lang.String extentionDate) {
    this.extentionDate = extentionDate;
  }

  /**
   * Sets the fundingType value for this TWsMarloAgree.
   * 
   * @param fundingType
   */
  public void setFundingType(java.lang.String fundingType) {
    this.fundingType = fundingType;
  }

  /**
   * Sets the grantAmount value for this TWsMarloAgree.
   * 
   * @param grantAmount
   */
  public void setGrantAmount(java.lang.Double grantAmount) {
    this.grantAmount = grantAmount;
  }

  /**
   * Sets the objectives value for this TWsMarloAgree.
   * 
   * @param objectives
   */
  public void setObjectives(java.lang.String objectives) {
    this.objectives = objectives;
  }

  /**
   * Sets the researcher value for this TWsMarloAgree.
   * 
   * @param researcher
   */
  public void setResearcher(java.lang.String researcher) {
    this.researcher = researcher;
  }

  /**
   * Sets the researcherText value for this TWsMarloAgree.
   * 
   * @param researcherText
   */
  public void setResearcherText(java.lang.String researcherText) {
    this.researcherText = researcherText;
  }

  /**
   * Sets the shortName value for this TWsMarloAgree.
   * 
   * @param shortName
   */
  public void setShortName(java.lang.String shortName) {
    this.shortName = shortName;
  }

  /**
   * Sets the startDate value for this TWsMarloAgree.
   * 
   * @param startDate
   */
  public void setStartDate(java.lang.String startDate) {
    this.startDate = startDate;
  }

  /**
   * Sets the status value for this TWsMarloAgree.
   * 
   * @param status
   */
  public void setStatus(java.lang.String status) {
    this.status = status;
  }

}

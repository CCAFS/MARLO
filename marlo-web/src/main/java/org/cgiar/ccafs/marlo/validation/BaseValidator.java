package org.cgiar.ccafs.marlo.validation;


import org.cgiar.ccafs.marlo.utils.APConfig;

import javax.mail.internet.InternetAddress;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BaseValidator {

  private static final Logger LOG = LoggerFactory.getLogger(BaseValidator.class);

  @Inject
  protected APConfig config;
  protected StringBuilder validationMessage;
  protected StringBuilder missingFields;


  @Inject
  public BaseValidator() {
    validationMessage = new StringBuilder();
    missingFields = new StringBuilder();
  }

  protected void addMessage(String message) {
    validationMessage.append("<p> - ");
    validationMessage.append(message);
    validationMessage.append("</p>");
  }

  /**
   * This method add a missing field separated by a semicolon (;).
   * 
   * @param field is the name of the field.
   */
  protected void addMissingField(String field) {
    if (missingFields.length() != 0) {
      missingFields.append(";");
    }
    missingFields.append(field);
  }

  protected boolean isValidEmail(String email) {
    if (email != null) {
      try {
        InternetAddress internetAddress = new InternetAddress(email);
        internetAddress.validate();
        return true;
      } catch (javax.mail.internet.AddressException e) {
        email = (email == null) ? "" : email;
        LOG.debug("Email address was invalid: " + email);
      }
    }
    return false;
  }

  protected boolean isValidNumber(String number) {
    if (this.isValidString(number)) {
      try {
        Double.parseDouble(number);
        // If is a number the code comes to here.
        return true;
      } catch (NumberFormatException e) {
        // if not a number.
        return false;
      }
    }
    return false;
  }

  /**
   * This method validates that the string received is not null and is not empty.
   * 
   * @param string
   * @return true if the string is valid. False otherwise.
   */
  protected boolean isValidString(String string) {
    if (string != null) {
      return !string.trim().isEmpty();
    }
    return false;
  }

  /**
   * This method counts the number of words in a given text.
   * 
   * @param text is some text to be validated.
   * @return the number of words.
   */
  protected int wordCount(String text) {
    text = text.trim();
    return text.isEmpty() ? 0 : text.split("\\s+").length;
  }
}

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

package org.cgiar.ccafs.marlo.rest.errors;

import org.cgiar.ccafs.marlo.logging.ErrorNotificationThrottle;
import org.cgiar.ccafs.marlo.logging.LogContext;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Controller advice to translate the server side exceptions to client-friendly
 * json structures.
 */
@ControllerAdvice
public class ExceptionTranslator {

  // Every handler logs the error it handles, with the HTTP status it answers (see logHandledError)
  private static final Logger LOG = LoggerFactory.getLogger(ExceptionTranslator.class);

  private final SendMailS sendMail;

  private final APConfig config;

  @Inject
  public ExceptionTranslator(SendMailS sendMail, APConfig config) {
    this.sendMail = sendMail;
    this.config = config;
  }

  @ExceptionHandler(MARLOFieldValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDTO MARLOFieldsValiadtion(MARLOFieldValidationException ex) {
    this.logHandledError(HttpStatus.BAD_REQUEST, ex);
    return ex.getErrorDTO();
  }

  @ExceptionHandler(AuthorizationException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorDTO processAuthorizationException(final RuntimeException ex) {
    // This one doesn't get logged by our aspectj logger due to Shiro's
    // aspects being applied first.
    LogContext.putStatusCode(HttpStatus.UNAUTHORIZED.value());
    LOG.error("AuthorizationException - user does does not have correct permissions");
    return new ErrorDTO(ErrorConstants.ERR_ACCESS_DENIED, ErrorConstants.SEVERITY_ERROR,
      "Please contact to MARLOSupport@cgiar.org to request permissions");
  }

  @ExceptionHandler({IllegalArgumentException.class, DataIntegrityViolationException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDTO processBadRequest(final RuntimeException ex) {
    this.logHandledError(HttpStatus.BAD_REQUEST, ex);
    return new ErrorDTO(ErrorConstants.ERR_VALIDATION, ErrorConstants.SEVERITY_ERROR, ex.getMessage());
  }

  @ExceptionHandler(ConcurrencyFailureException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public ErrorDTO processConcurencyError(ConcurrencyFailureException ex) {
    this.logHandledError(HttpStatus.CONFLICT, ex);
    return new ErrorDTO(ErrorConstants.ERR_CONCURRENCY_FAILURE);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDTO processConstraintViolations(final ConstraintViolationException e) {
    this.logHandledError(HttpStatus.BAD_REQUEST, e);

    StringBuilder errorMessageBuilder = new StringBuilder();

    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
      if (errorMessageBuilder.length() > 0) {
        errorMessageBuilder.append(", ");
      }
      errorMessageBuilder.append(violation.getInvalidValue()).append(": ").append(violation.getMessage());
    }

    return new ErrorDTO(ErrorConstants.ERR_VALIDATION, ErrorConstants.SEVERITY_ERROR, errorMessageBuilder.toString());
  }

  /**
   * Logs a handled REST error with the HTTP status the handler answers, which is put in the log context first.
   * <ul>
   * <li>5xx is a server failure: ERROR, with the stack trace.</li>
   * <li>429 is DEBUG: it answers a client that is flooding the API, and logging each one would flood the log too.</li>
   * <li>401 and 404 are routine (an expired token, a record that does not exist): INFO.</li>
   * <li>Any other 4xx: WARN.</li>
   * </ul>
   * A 4xx is logged with the exception class only, never its message, because the message can echo back the values
   * the client sent. Logging must never stop the handler from answering, so any failure here is swallowed.
   *
   * @param status the status declared by the handler's @ResponseStatus
   * @param ex the exception being handled
   */
  private void logHandledError(HttpStatus status, Exception ex) {
    if (status == null) {
      return;
    }
    try {
      LogContext.putStatusCode(status.value());
      String exceptionType = ex != null ? ex.getClass().getName() : null;
      if (status.is5xxServerError()) {
        // The catch-all also receives client-input errors whose message echoes the request, so strip its line breaks
        Throwable loggableException = LogContext.withoutLineBreaks(ex);
        LOG.error("REST request failed with {} {}", status.value(), status.getReasonPhrase(), loggableException);
        this.notifySupport(status, ex);
      } else if (status == HttpStatus.TOO_MANY_REQUESTS) {
        LOG.debug("REST request throttled with {} {}: {}", status.value(), status.getReasonPhrase(), exceptionType);
      } else if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.NOT_FOUND) {
        LOG.info("REST request answered {} {}: {}", status.value(), status.getReasonPhrase(), exceptionType);
      } else {
        LOG.warn("REST request rejected with {} {}: {}", status.value(), status.getReasonPhrase(), exceptionType);
      }
    } catch (RuntimeException e) {
      LOG.debug("Could not log the handled REST error", e);
    }
  }

  /**
   * Emails a REST 5xx to the support team through the same mail and throttle as the Struts unhandled exceptions
   * (UnhandledExceptionAction), so a repeated failure is emailed once an hour. Like there, the email is sent whenever
   * the request has a Global Unit, and otherwise only in production. A 4xx is never emailed: most are legitimate
   * answers, such as a record that does not exist. The stack trace goes to this internal email and the log only, never
   * to the client. Any failure here is swallowed, so the handler still answers.
   *
   * @param status the 5xx status the handler answers
   * @param ex the exception being handled
   */
  private void notifySupport(HttpStatus status, Exception ex) {
    try {
      if (sendMail == null || config == null || ex == null || ex instanceof ClientAbortException) {
        return;
      }
      String globalUnit = LogContext.get(LogContext.TOOL_NAME);
      if (globalUnit == null && !config.isProduction()) {
        return;
      }
      String route = LogContext.get(LogContext.CONTROLLER_AFFECTED);
      String keyRoute = routeForKey(RequestContextHolder.getRequestAttributes(), route);
      int suppressedRepeats = ErrorNotificationThrottle.shared()
        .register(ErrorNotificationThrottle.key(globalUnit, keyRoute, ex, String.valueOf(status.value())));
      if (suppressedRepeats == ErrorNotificationThrottle.SUPPRESSED) {
        LOG.info("The same REST error was already reported within the hour; no email sent");
        return;
      }

      StringWriter stackTrace = new StringWriter();
      ex.printStackTrace(new PrintWriter(stackTrace));
      String globalUnitName = globalUnit != null ? globalUnit : "MARLO";
      String subject = "REST exception occurred in " + globalUnitName + " - "
        + (route != null ? route : "unknown route") + " - " + status.value();

      StringBuilder message = new StringBuilder();
      message.append("A REST request failed with " + status.value() + " " + status.getReasonPhrase() + ".</br>");
      message.append("This exception occurs in the server: " + config.getBaseUrl() + "</br></br>");
      message.append("<b>Global Unit: </b>" + globalUnitName + ".</br>");
      String userId = LogContext.get(LogContext.USER_ID);
      if (userId != null) {
        message.append("<b>User id: </b>" + userId + ".</br>");
      }
      String userName = LogContext.get(LogContext.USER_NAME);
      if (userName != null) {
        message.append("<b>User: </b>" + StringEscapeUtils.escapeHtml4(userName) + ".</br>");
      }
      message.append(ErrorNotificationThrottle.correlationHtml(suppressedRepeats));
      message.append("</br><b>Exception message: </b></br><pre>");
      message.append(StringEscapeUtils.escapeHtml4(stackTrace.toString()) + "</pre>");

      sendMail.send(config.getEmailNotification(), null, config.getEmailNotification(), subject, message.toString(),
        null, null, null, true);
      LOG.info("The platform has sent a message reporting a REST exception");
    } catch (RuntimeException e) {
      LOG.debug("Could not email the REST error to the support team", e);
    }
  }

  /**
   * Returns the route that identifies a REST error for deduplication: the mapping pattern of the handler that failed
   * (/{CGIAREntity}/progresstowards/{id}), not the path that was called (/AICCRA/progresstowards/7). Otherwise every id
   * of a broken endpoint would count as a different error, and be emailed once each.
   *
   * @param attributes the attributes of the current request, may be null
   * @param route the request path, used when no mapping pattern is known
   * @return the mapping pattern, or the given route; never throws
   */
  static String routeForKey(RequestAttributes attributes, String route) {
    try {
      if (attributes != null) {
        Object pattern =
          attributes.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (pattern instanceof String && !((String) pattern).isEmpty()) {
          return (String) pattern;
        }
      }
    } catch (RuntimeException e) {
      LOG.debug("Could not read the mapping pattern of the failed REST request", e);
    }
    return route;
  }

  private ErrorDTO processFieldErrors(List<FieldError> fieldErrors) {
    ErrorDTO dto = new ErrorDTO(ErrorConstants.ERR_VALIDATION);

    for (FieldError fieldError : fieldErrors) {
      dto.add(fieldError.getObjectName(), fieldError.getField(), fieldError.getCode());
    }

    return dto;
  }

  @ExceptionHandler(HttpMessageNotWritableException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorDTO processHttpMessageNotWritable(HttpMessageNotWritableException ex) {
    this.logHandledError(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    return new ErrorDTO(ErrorConstants.ERR_INTERNAL_SERVER, ErrorConstants.SEVERITY_ERROR, ex.getMessage());
  }

  @ExceptionHandler({NullPointerException.class, IllegalStateException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDTO processInternalServerError(final RuntimeException ex) {
    this.logHandledError(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    return new ErrorDTO(ErrorConstants.ERR_INTERNAL_SERVER, ErrorConstants.SEVERITY_ERROR, ex.getMessage());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ErrorDTO processMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
    this.logHandledError(HttpStatus.METHOD_NOT_ALLOWED, exception);
    return new ErrorDTO(ErrorConstants.ERR_METHOD_NOT_SUPPORTED, ErrorConstants.SEVERITY_ERROR, exception.getMessage());
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDTO processNotFoundException(final NotFoundException ex) {
    this.logHandledError(HttpStatus.NOT_FOUND, ex);
    return new ErrorDTO(ex.getCode() + " - " + ex.getDescription());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorDTO processRemainingExceptions(Exception ex) {
    this.logHandledError(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    return new ErrorDTO(ErrorConstants.ERR_INTERNAL_SERVER, ErrorConstants.SEVERITY_ERROR, ex.getMessage());
  }

  @ExceptionHandler({ResourceConflictException.class, ResourceAlreadyExistsException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.CONFLICT)
  protected ErrorDTO processResourceAlreadyExists(final RuntimeException ex) {
    this.logHandledError(HttpStatus.CONFLICT, ex);
    return new ErrorDTO(ErrorConstants.ERR_RESOURCE_ALREADY_EXISTS, ErrorConstants.SEVERITY_ERROR, ex.getMessage());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDTO processResourceNotFoundException(final RuntimeException ex) {
    this.logHandledError(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    return new ErrorDTO(ErrorConstants.ERR_TOO_MANY_REQUEST, ErrorConstants.SEVERITY_ERROR, ex.getMessage());
  }


  @ExceptionHandler(ThrottlingException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
  protected ErrorDTO processThrottlingException(final RuntimeException ex) {
    this.logHandledError(HttpStatus.TOO_MANY_REQUESTS, ex);
    return new ErrorDTO(ErrorConstants.ERR_RESOURCE_ALREADY_EXISTS, ErrorConstants.SEVERITY_ERROR, ex.getMessage());
  }

  @ExceptionHandler(UnauthenticatedException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorDTO processUnauthenticedException(UnauthenticatedException e) {
    this.logHandledError(HttpStatus.UNAUTHORIZED, e);
    return new ErrorDTO(ErrorConstants.ERR_ACCESS_DENIED, ErrorConstants.SEVERITY_ERROR,
      "Please check your username and password");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDTO processValidationError(MethodArgumentNotValidException ex) {
    this.logHandledError(HttpStatus.BAD_REQUEST, ex);
    BindingResult result = ex.getBindingResult();
    List<FieldError> fieldErrors = result.getFieldErrors();

    return this.processFieldErrors(fieldErrors);
  }


}

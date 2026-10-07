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

import org.cgiar.ccafs.marlo.logging.LogContext;

import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

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

/**
 * Controller advice to translate the server side exceptions to client-friendly
 * json structures.
 */
@ControllerAdvice
public class ExceptionTranslator {

  // Every handler logs the error it handles, with the HTTP status it answers (see logHandledError)
  private static final Logger LOG = LoggerFactory.getLogger(ExceptionTranslator.class);

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

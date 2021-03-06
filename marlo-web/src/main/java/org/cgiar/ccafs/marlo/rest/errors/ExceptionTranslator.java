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

  // This is just for exceptions that don't get processed by the LoggingAspect
  // (e.g. UnauthorizedException)
  private static final Logger LOG = LoggerFactory.getLogger(ExceptionTranslator.class);

  @ExceptionHandler(MARLOFieldValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDTO MARLOFieldsValiadtion(MARLOFieldValidationException ex) {
    return ex.getErrorDTO();
  }

  @ExceptionHandler(AuthorizationException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorDTO processAuthorizationException(final RuntimeException ex) {
    // This one doesn't get logged by our aspectj logger due to Shiro's
    // aspects being applied first.
    LOG.error("AuthorizationException - user does does not have correct permissions");
    return new ErrorDTO(ErrorConstants.ERR_ACCESS_DENIED, ErrorConstants.SEVERITY_ERROR,
      "Please contact to MARLOSupport@cgiar.org to request permissions");
  }

  @ExceptionHandler({IllegalArgumentException.class, DataIntegrityViolationException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDTO processBadRequest(final RuntimeException ex) {
    return new ErrorDTO(ErrorConstants.ERR_VALIDATION, ErrorConstants.SEVERITY_ERROR, ex.getMessage());
  }

  @ExceptionHandler(ConcurrencyFailureException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public ErrorDTO processConcurencyError(ConcurrencyFailureException ex) {
    return new ErrorDTO(ErrorConstants.ERR_CONCURRENCY_FAILURE);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDTO processConstraintViolations(final ConstraintViolationException e) {

    StringBuilder errorMessageBuilder = new StringBuilder();

    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
      if (errorMessageBuilder.length() > 0) {
        errorMessageBuilder.append(", ");
      }
      errorMessageBuilder.append(violation.getInvalidValue()).append(": ").append(violation.getMessage());
    }

    return new ErrorDTO(ErrorConstants.ERR_VALIDATION, ErrorConstants.SEVERITY_ERROR, errorMessageBuilder.toString());
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
    return new ErrorDTO(ErrorConstants.ERR_INTERNAL_SERVER, ErrorConstants.SEVERITY_ERROR, ex.getMessage());
  }

  @ExceptionHandler({NullPointerException.class, IllegalStateException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDTO processInternalServerError(final RuntimeException ex) {
    return new ErrorDTO(ErrorConstants.ERR_INTERNAL_SERVER, ErrorConstants.SEVERITY_ERROR, ex.getMessage());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ErrorDTO processMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
    return new ErrorDTO(ErrorConstants.ERR_METHOD_NOT_SUPPORTED, ErrorConstants.SEVERITY_ERROR, exception.getMessage());
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDTO processNotFoundException(final NotFoundException ex) {
    return new ErrorDTO(ex.getCode() + " - " + ex.getDescription());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorDTO processRemainingExceptions(Exception ex) {
    return new ErrorDTO(ErrorConstants.ERR_INTERNAL_SERVER, ErrorConstants.SEVERITY_ERROR, ex.getMessage());
  }

  @ExceptionHandler({ResourceConflictException.class, ResourceAlreadyExistsException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.CONFLICT)
  protected ErrorDTO processResourceAlreadyExists(final RuntimeException ex) {
    return new ErrorDTO(ErrorConstants.ERR_RESOURCE_ALREADY_EXISTS, ErrorConstants.SEVERITY_ERROR, ex.getMessage());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDTO processResourceNotFoundException(final RuntimeException ex) {
    return new ErrorDTO(ErrorConstants.ERR_TOO_MANY_REQUEST, ErrorConstants.SEVERITY_ERROR, ex.getMessage());
  }


  @ExceptionHandler(ThrottlingException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
  protected ErrorDTO processThrottlingException(final RuntimeException ex) {
    return new ErrorDTO(ErrorConstants.ERR_RESOURCE_ALREADY_EXISTS, ErrorConstants.SEVERITY_ERROR, ex.getMessage());
  }

  @ExceptionHandler(UnauthenticatedException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorDTO processUnauthenticedException(UnauthenticatedException e) {
    return new ErrorDTO(ErrorConstants.ERR_ACCESS_DENIED, ErrorConstants.SEVERITY_ERROR,
      "Please check your username and password");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDTO processValidationError(MethodArgumentNotValidException ex) {
    BindingResult result = ex.getBindingResult();
    List<FieldError> fieldErrors = result.getFieldErrors();

    return this.processFieldErrors(fieldErrors);
  }


}

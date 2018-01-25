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
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 */
@ControllerAdvice
public class ExceptionTranslator {


  @ExceptionHandler(AuthorizationException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorDTO processAuthorizationException(final RuntimeException ex) {
    return new ErrorDTO(ErrorConstants.ERR_ACCESS_DENIED,
      "Please contact the crp administrator to request permissions");
  }

  @ExceptionHandler({IllegalArgumentException.class, DataIntegrityViolationException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDTO processBadRequest(final RuntimeException ex) {
    return new ErrorDTO(ErrorConstants.ERR_VALIDATION, ex.getMessage());
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

    return new ErrorDTO(ErrorConstants.ERR_VALIDATION, errorMessageBuilder.toString());
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
    return new ErrorDTO(ErrorConstants.ERR_INTERNAL_SERVER, ex.getMessage());
  }

  @ExceptionHandler({NullPointerException.class, IllegalStateException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDTO processInternalServerError(final RuntimeException ex) {
    return new ErrorDTO(ErrorConstants.ERR_INTERNAL_SERVER, ex.getMessage());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ErrorDTO processMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
    return new ErrorDTO(ErrorConstants.ERR_METHOD_NOT_SUPPORTED, exception.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorDTO processRemainingExceptions(Exception ex) {
    return new ErrorDTO(ErrorConstants.ERR_INTERNAL_SERVER, ex.getMessage());
  }

  @ExceptionHandler({ResourceConflictException.class, ResourceAlreadyExistsException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.CONFLICT)
  protected ErrorDTO processResourceAlreadyExists(final RuntimeException ex) {
    return new ErrorDTO(ErrorConstants.ERR_RESOURCE_ALREADY_EXISTS, ex.getMessage());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDTO processResourceNotFoundException(final RuntimeException ex) {
    return new ErrorDTO(ErrorConstants.ERR_INTERNAL_SERVER, ex.getMessage());
  }

  @ExceptionHandler(UnauthenticatedException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorDTO processUnauthenticedException(UnauthenticatedException e) {
    return new ErrorDTO(ErrorConstants.ERR_ACCESS_DENIED, "Please check your username and password");
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

package com.sman20.berlinclock.web;

import com.sman20.berlinclock.domain.InvalidTimeException;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class InvalidTimeAdvice extends ResponseEntityExceptionHandler {
  private static final URI INVALID_INPUT = URI.create("urn:berlin-clock:invalid-input");

  @ExceptionHandler(InvalidTimeException.class)
  public ProblemDetail invalidTime(InvalidTimeException exception) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    problem.setType(INVALID_INPUT);
    return problem;
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException exception, HttpHeaders headers,
      HttpStatusCode status, WebRequest request) {
    ProblemDetail problem = exception.getBody();
    problem.setType(INVALID_INPUT);
    return handleExceptionInternal(exception, problem, headers, status, request);
  }
}

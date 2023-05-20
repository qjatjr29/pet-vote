package numble.pet.vote.common.exception;

import java.util.List;
import numble.pet.vote.common.presentation.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
    ErrorCode errorCode = e.getErrorCode();
    List<ErrorField> errorFieldList = e.getErrorFieldList();
    ErrorResponse errorResponse = ErrorResponse.from(errorCode, errorFieldList);
    return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getStatusCode()));
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {

    return ResponseEntity
        .badRequest()
        .body(ErrorResponse.fromBindingResult(ErrorCode.INVALID_INPUT_VALUE, ex.getBindingResult()));
  }
}

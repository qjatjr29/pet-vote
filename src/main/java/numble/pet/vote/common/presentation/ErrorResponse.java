package numble.pet.vote.common.presentation;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import numble.pet.vote.common.exception.ErrorCode;
import numble.pet.vote.common.exception.ErrorField;
import org.springframework.validation.BindingResult;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

  private String errorCode;
  private String message;
  private int statusCode;
  private List<ErrorField> errorFieldList;

  private ErrorResponse(final ErrorCode errorCode, final List<ErrorField> errorFieldList) {
    this.statusCode = errorCode.getStatusCode();
    this.errorCode = errorCode.getErrorCode();
    this.message = errorCode.getMessage();
    this.errorFieldList = errorFieldList;
  }

  public static ErrorResponse fromBindingResult(final ErrorCode errorCode, final BindingResult bindingResult) {
    return new ErrorResponse(errorCode, ErrorField.of(bindingResult));
  }

  public static ErrorResponse from(final ErrorCode errorCode, final List<ErrorField> errorFieldList) {
    return new ErrorResponse(errorCode, errorFieldList);
  }

}

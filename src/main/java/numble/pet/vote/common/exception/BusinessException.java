package numble.pet.vote.common.exception;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessException extends RuntimeException {

  private ErrorCode errorCode;
  private List<ErrorField> errorFieldList;

  public BusinessException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public BusinessException(ErrorCode errorCode, ErrorField errorField) {
    this.errorCode = errorCode;
    this.errorFieldList = new ArrayList<>();
    errorFieldList.add(errorField);
  }

  public BusinessException(ErrorCode errorCode, List<ErrorField> errorFieldList) {
    this.errorCode = errorCode;
    this.errorFieldList = errorFieldList;
  }

}

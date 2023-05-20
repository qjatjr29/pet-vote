package numble.pet.vote.common.exception;

import java.util.List;

public class BadRequestException extends BusinessException {

  public BadRequestException(ErrorCode errorCode) {
    super(errorCode);
  }

  public BadRequestException(ErrorCode errorCode, ErrorField errorField) {
    super(errorCode, errorField);
  }

  public BadRequestException(ErrorCode errorCode, List<ErrorField> errorFieldList) {
    super(errorCode, errorFieldList);
  }
}

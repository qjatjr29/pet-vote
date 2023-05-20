package numble.pet.vote.common.exception;

import java.util.List;

public class ForbiddenException extends BusinessException {

  public ForbiddenException(ErrorCode errorCode) {
    super(errorCode);
  }

  public ForbiddenException(ErrorCode errorCode, ErrorField errorField) {
    super(errorCode, errorField);
  }

  public ForbiddenException(ErrorCode errorCode, List<ErrorField> errorFieldList) {
    super(errorCode, errorFieldList);
  }
}

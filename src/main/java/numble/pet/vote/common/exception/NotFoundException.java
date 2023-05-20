package numble.pet.vote.common.exception;

import java.util.List;

public class NotFoundException extends BusinessException {

  public NotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }

  public NotFoundException(ErrorCode errorCode, ErrorField errorField) {
    super(errorCode, errorField);
  }

  public NotFoundException(ErrorCode errorCode, List<ErrorField> errorFieldList) {
    super(errorCode, errorFieldList);
  }
}

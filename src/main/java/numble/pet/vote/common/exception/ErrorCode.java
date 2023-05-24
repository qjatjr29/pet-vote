package numble.pet.vote.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

  INVALID_INPUT_VALUE(400, "COMMON-001", "유효성 검증에 실패한 경우"),
  DUPLICATE_INPUT_VALUE(409, "COMMON-002", "중복된 값이 들어온 경우"),
  BAD_REQUEST(400, "COMMON-003", "잘못된 요청이 들어온 경우"),

  FILE_CONVERT_FAIL(401, "AWS-001", "파일 변환에 실패한 경우"),

  UNAUTHORIZED(401, "USER-001", "인증에 실패한 경우"),
  USER_NOT_FOUND(404, "USER-002", "계정을 찾을 수 없는 경우"),
  INVALID_ROLE(403, "USER-003", "권한이 부족한 경우"),
  WRONG_PASSWORD(400, "USER-004", "비밀번호가 잘 못 입력된 경우"),

  PET_NOT_FOUND(404, "PET-001", "찾고자 하는 펫이 없는 경우"),

  EXPIRED_VERIFICATION_TOKEN(403, "AUTH-001", "인증 토큰이 만료된 경우"),
  INVALID_VERIFICATION_TOKEN(403, "AUTH-002", "토큰이 유효하지 않은 경우"),
  CERTIFICATION_TYPE_NOT_MATCH(403, "AUTH-003", "인증 타입이 일치하지 않은 경우"),
  TOKEN_NOT_EXISTS(404, "AUTH-004", "인증 토큰이 존재하지 않는 경우"),

  ALREADY_VOTE(409, "VOTE-001", "이미 투표를 한 경우");

  int statusCode;
  String errorCode;
  String message;

  ErrorCode(int statusCode, String errorCode, String message) {
    this.statusCode = statusCode;
    this.errorCode = errorCode;
    this.message = message;
  }

}

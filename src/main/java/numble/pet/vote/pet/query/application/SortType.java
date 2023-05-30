package numble.pet.vote.pet.query.application;

import java.util.Arrays;

public enum SortType {

  NONE(""),
  이름순("name"),
  최신순("createdAt"),
  인기순("voteCount");

  private String value;

  SortType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static SortType of(String value) {
    return Arrays.stream(SortType.values())
        .filter(s -> s.getValue().equals(value))
        .findFirst()
        .orElse(SortType.NONE);
  }
}

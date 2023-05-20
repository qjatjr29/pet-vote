package numble.pet.vote.pet.command.domain;

public enum Species {

  강아지,
  고양이,
  ETC;

  public static Species of(String species) {
    try {
      return Species.valueOf(species);
    } catch (IllegalArgumentException e) {
      return Species.ETC;
    }
  }

}

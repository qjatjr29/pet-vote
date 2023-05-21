package numble.pet.vote.vote.command.application;

import numble.pet.vote.common.event.Events;
import numble.pet.vote.common.exception.ErrorCode;
import numble.pet.vote.common.exception.NotFoundException;
import numble.pet.vote.pet.command.domain.Pet;
import numble.pet.vote.pet.command.domain.PetRepository;
import numble.pet.vote.vote.command.domain.Vote;
import numble.pet.vote.vote.command.domain.VoteRepository;
import numble.pet.vote.vote.command.domain.VoteSubmittedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteService {

  private final VoteRepository voteRepository;
  private final PetRepository petRepository;

  public VoteService(VoteRepository voteRepository,
      PetRepository petRepository) {
    this.voteRepository = voteRepository;
    this.petRepository = petRepository;
  }

  @Transactional
  public Vote submit(String email, Long petId) {

    // todo: exception 처리
    Pet pet = petRepository.findById(petId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.PET_NOT_FOUND));

    if(isAlreadyVotedEmail(email)) throw new RuntimeException();

    pet.increaseVoteCount();
    petRepository.save(pet);
    Events.raise(new VoteSubmittedEvent(petId));
    Vote newVote = new Vote(email, petId);
    return voteRepository.save(newVote);
  }

  // todo : @Cacheable
  public Boolean isAlreadyVotedEmail(String email) {
    return voteRepository.existsByEmail(email);
  }


}

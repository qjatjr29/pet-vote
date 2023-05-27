package numble.pet.vote.vote.command.application;

import numble.pet.vote.common.event.Events;
import numble.pet.vote.common.exception.ErrorCode;
import numble.pet.vote.common.exception.NotFoundException;
import numble.pet.vote.pet.command.domain.Pet;
import numble.pet.vote.pet.command.domain.PetRepository;
import numble.pet.vote.vote.command.domain.Vote;
import numble.pet.vote.vote.command.domain.VoteCanceledEvent;
import numble.pet.vote.vote.command.domain.VoteRepository;
import numble.pet.vote.vote.command.domain.VoteSubmittedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VoteService {

  private final VoteRepository voteRepository;
  private final PetRepository petRepository;

  public VoteService(VoteRepository voteRepository,
      PetRepository petRepository) {
    this.voteRepository = voteRepository;
    this.petRepository = petRepository;
  }

  public Vote submit(String email, Long petId) {

    Pet pet = petRepository.findById(petId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.PET_NOT_FOUND));

    if(isAlreadyVotedEmail(email)) throw new RuntimeException();

    Events.raise(new VoteSubmittedEvent(petId));
    Vote newVote = new Vote(email, petId);
    return voteRepository.save(newVote);
  }

  public void cancel(String email, Long petId) {

    if(!isAlreadyVotedEmail(email)) throw new NotFoundException(ErrorCode.NOT_FOUND_VOTE_EMAIL);

    Pet pet = petRepository.findById(petId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.PET_NOT_FOUND));

    Events.raise(new VoteCanceledEvent(petId));
    voteRepository.deleteByEmail(email);
  }

  public Boolean isAlreadyVotedEmail(String email) {
    return voteRepository.existsByEmail(email);
  }


}

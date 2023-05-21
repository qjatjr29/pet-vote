package numble.pet.vote.vote.presentation;

import numble.pet.vote.vote.command.application.VoteService;
import numble.pet.vote.vote.command.application.VoteSubmitRequest;
import numble.pet.vote.vote.command.domain.Vote;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vote")
public class VoteController {

  private final VoteService voteService;

  public VoteController(VoteService voteService) {
    this.voteService = voteService;
  }

  @PostMapping("/submit")
  public ResponseEntity<Void> submit(@RequestBody VoteSubmitRequest request) {
    voteService.submit(request.getEmail(), request.getPetId());
    return ResponseEntity.noContent().build();
  }
}
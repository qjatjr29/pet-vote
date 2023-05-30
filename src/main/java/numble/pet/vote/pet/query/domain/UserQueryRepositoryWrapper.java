package numble.pet.vote.pet.query.domain;

import numble.pet.vote.pet.query.application.SortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryRepositoryWrapper {

  Page<PetData> findAllSortedBy(SortType sortType, Pageable pageable);

}

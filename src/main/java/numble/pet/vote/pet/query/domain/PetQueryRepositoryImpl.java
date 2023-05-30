package numble.pet.vote.pet.query.domain;

import com.querydsl.core.types.OrderSpecifier;
import java.util.List;
import numble.pet.vote.pet.query.application.SortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.QuerydslRepositorySupport;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;

public class PetQueryRepositoryImpl extends QuerydslRepositorySupport implements UserQueryRepositoryWrapper{

  private static final QPetData petDataCollection = QPetData.petData;


  public PetQueryRepositoryImpl(MongoOperations operations) {
    super(operations);
  }

  @Override
  public Page<PetData> findAllSortedBy(SortType sortType, Pageable pageable) {
    SpringDataMongodbQuery<PetData> query = from(petDataCollection)
        .orderBy(getSortExpression(sortType));

    long totalCount = query.fetchCount();

    query.limit(pageable.getPageSize()).offset(pageable.getOffset());

    List<PetData> results = query.fetch();

    return new PageImpl<>(results, pageable, totalCount);
  }

  private OrderSpecifier<?> getSortExpression(SortType sortType) {
    switch (sortType) {
      case 이름순:
        return petDataCollection.name.asc();
      case 인기순:
        return petDataCollection.voteCount.desc();
      case 최신순:
        return petDataCollection.createdAt.desc();
      default:
        return petDataCollection.createdAt.desc();
    }
  }

}

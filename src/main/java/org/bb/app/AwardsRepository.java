package org.bb.app;


import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface AwardsRepository extends ReactiveCrudRepository<Award, Integer> {
    Mono<Award> findFirstByCategoryAndNomineeContaining(@Param("category") String category, @Param("nominee")String nominee);
}
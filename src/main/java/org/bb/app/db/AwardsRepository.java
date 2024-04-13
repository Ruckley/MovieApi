package org.bb.app.db;


import org.bb.app.model.Award;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface AwardsRepository extends R2dbcRepository<Award, Integer> {
    Mono<Award> findFirstByCategoryAndNomineeContaining(@Param("category") String category, @Param("nominee") String nominee);

    Mono<Award> findFirstByCategoryAndYearAndNomineeContaining(@Param("category") String category, @Param("year") int year, @Param("nominee") String nominee);

}


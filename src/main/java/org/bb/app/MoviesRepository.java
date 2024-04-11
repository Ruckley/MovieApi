package org.bb.app;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MoviesRepository extends R2dbcRepository<Movie, Integer> {
    Mono<Movie> findFirstByTitleIgnoreCase(@Param("title") String title);
    Mono<Movie> findFirstByTitleIgnoreCaseAndYear(@Param("title") String title, @Param("year") int year);
    @Query("SELECT * FROM (SELECT * FROM movies ORDER BY av_rating DESC LIMIT :limit) AS top_entries ORDER BY year ASC")
    Flux<Movie> findTopNByAvRatingOrderByYearAsc(int limit);
}


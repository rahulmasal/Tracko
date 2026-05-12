package com.tracko.backend.repository;

import com.tracko.backend.model.ScoreCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreCardRepository extends JpaRepository<ScoreCard, Long> {

    Optional<ScoreCard> findByUserIdAndScoreMonthAndScoreYear(Long userId,
                                                               Integer scoreMonth,
                                                               Integer scoreYear);

    List<ScoreCard> findByUserIdOrderByScoreYearDescScoreMonthDesc(Long userId);

    List<ScoreCard> findByScoreMonthAndScoreYear(Integer scoreMonth, Integer scoreYear);

    @Query("SELECT sc FROM ScoreCard sc WHERE sc.scoreYear = :year " +
           "ORDER BY sc.totalScore DESC")
    List<ScoreCard> findByScoreYearOrderByTotalScoreDesc(@Param("year") Integer year);

    @Query("SELECT AVG(sc.totalScore) FROM ScoreCard sc WHERE sc.scoreMonth = :month " +
           "AND sc.scoreYear = :year")
    Double getAverageScoreForMonth(@Param("month") Integer month,
                                   @Param("year") Integer year);
}

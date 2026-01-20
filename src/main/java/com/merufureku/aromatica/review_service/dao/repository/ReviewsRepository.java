package com.merufureku.aromatica.review_service.dao.repository;

import com.merufureku.aromatica.review_service.dao.entity.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long>, JpaSpecificationExecutor<Reviews> {

    boolean existsByUserIdAndFragranceId(Integer userId, Long fragranceId);

    Optional<Reviews> findByIdAndFragranceId(Long id, Long fragranceId);

    Optional<Reviews> findByIdAndUserIdAndFragranceId(Long id, Integer userId, Long fragranceId);

    @Query("SELECT r FROM Reviews r " +
           "WHERE r.fragranceId IN :fragranceIds " +
           "AND r.rating >= :rating " +
           "AND ( :userId IS NULL OR :userId = 0 OR r.userId <> :userId ) " +
           "ORDER BY r.fragranceId")
    List<Reviews> findAllByFragranceIdInAndRatingGreaterThanOrEqualExcludingUser(
            @Param("fragranceIds") Set<Long> fragranceIds,
            @Param("rating") int rating,
            @Param("userId") Integer userId);

    List<Reviews> findByUserIdAndRatingGreaterThanOrderByFragranceId(Integer userId, int rating);
}

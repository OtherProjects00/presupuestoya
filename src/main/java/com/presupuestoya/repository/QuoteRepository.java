package com.presupuestoya.repository;

import com.presupuestoya.domain.Quote;
import com.presupuestoya.domain.QuoteStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface QuoteRepository extends JpaRepository<Quote, Long> {

    @EntityGraph(attributePaths = "client")
    List<Quote> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    @EntityGraph(attributePaths = "client")
    List<Quote> findByOwnerIdAndStatusOrderByCreatedAtDesc(Long ownerId, QuoteStatus status);

    @EntityGraph(attributePaths = "client")
    List<Quote> findTop5ByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    @EntityGraph(attributePaths = {"client", "items"})
    @Query("select q from Quote q where q.id = :id and q.owner.id = :ownerId")
    Optional<Quote> findDetailedByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);

    @EntityGraph(attributePaths = {"client", "items"})
    @Query("select q from Quote q where q.publicTokenHash = :hash")
    Optional<Quote> findPublicByTokenHash(@Param("hash") String hash);

    boolean existsByNumber(String number);

    long countByOwnerId(Long ownerId);

    long countByOwnerIdAndStatus(Long ownerId, QuoteStatus status);

    long countByOwnerIdAndClientId(Long ownerId, Long clientId);

    @Query("select coalesce(sum(q.total), 0) from Quote q where q.owner.id = :ownerId and q.status = :status")
    BigDecimal sumTotalByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") QuoteStatus status);
}

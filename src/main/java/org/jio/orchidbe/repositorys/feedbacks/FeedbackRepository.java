package org.jio.orchidbe.repositorys.feedbacks;

import org.jio.orchidbe.models.feedbacks.Feedbacks;
import org.jio.orchidbe.models.orders.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedbacks, Long> {
    Page<Feedbacks> findAll(Specification<Feedbacks> specification, Pageable pageable);

    Boolean existsByAuction_IdAndUser_Id(Long auctionId,Long userId);
}

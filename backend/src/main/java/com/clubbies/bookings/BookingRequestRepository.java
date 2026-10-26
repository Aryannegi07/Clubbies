package com.clubbies.bookings;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BookingRequestRepository extends JpaRepository<BookingRequest, UUID> {
    List<BookingRequest> findByRequestedByOrderByCreatedAtDesc(UUID userId);
    List<BookingRequest> findByClubIdOrderByCreatedAtDesc(UUID clubId);
    List<BookingRequest> findByStatusOrderByCreatedAtAsc(String status);
}

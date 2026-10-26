package com.clubbies.clubs;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ClubRepository extends JpaRepository<Club, UUID> {
    List<Club> findByActiveTrueOrderByNameAsc();
    boolean existsByNameIgnoreCase(String name);
}

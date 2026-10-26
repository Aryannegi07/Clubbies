package com.clubbies.presidentcup;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, UUID> { }

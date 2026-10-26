package com.clubbies.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PlatformAuditLogRepository extends JpaRepository<PlatformAuditLog, UUID> { }

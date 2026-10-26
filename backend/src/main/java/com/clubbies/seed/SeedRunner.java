package com.clubbies.seed;

import com.clubbies.clubs.Club;
import com.clubbies.clubs.ClubRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SeedRunner implements CommandLineRunner {
    private final ClubRepository clubs;
    public SeedRunner(ClubRepository clubs) { this.clubs = clubs; }
    @Override @Transactional public void run(String... args) {
        seed("IEEE", "IEEE", "A student-led technology community placeholder. Update this description and full name from the administration dashboard.", "Technology", true);
        seed("Aryabhat", "Aryabhat", "A clearly editable placeholder for a mathematics and innovation community. Replace with verified college information when available.", "Innovation", true);
        seed("Wizard", "Wizard", "A clearly editable placeholder for a creative and problem-solving community. Replace with verified college information when available.", "Community", false);
    }
    private void seed(String name, String fullName, String description, String category, boolean recruitmentOpen) {
        if (clubs.existsByNameIgnoreCase(name)) return;
        Club club = new Club(); club.name = name; club.fullName = fullName; club.description = description; club.category = category; club.recruitmentOpen = recruitmentOpen; clubs.save(club);
    }
}

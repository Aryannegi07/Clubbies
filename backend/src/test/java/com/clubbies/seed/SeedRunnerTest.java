package com.clubbies.seed;

import com.clubbies.clubs.Club;
import com.clubbies.clubs.ClubRepository;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SeedRunnerTest {
    @Test
    void seedsExactlyTheRequestedClubsAndIsSafeToRerun() {
        ClubRepository repository = mock(ClubRepository.class);
        when(repository.existsByNameIgnoreCase(any())).thenReturn(false);
        new SeedRunner(repository).run();
        var names = new HashSet<String>();
        var captor = org.mockito.ArgumentCaptor.forClass(Club.class);
        verify(repository, times(3)).save(captor.capture());
        captor.getAllValues().forEach(club -> names.add(club.name));
        assertThat(names).containsExactlyInAnyOrder("IEEE", "Aryabhat", "Wizard");

        reset(repository);
        when(repository.existsByNameIgnoreCase(any())).thenReturn(true);
        new SeedRunner(repository).run();
        verify(repository, never()).save(any());
    }
}

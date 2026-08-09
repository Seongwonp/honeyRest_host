package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.ReservationDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.RoomDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyResourceAccessServiceTest {

    @Mock CompanyService companyService;
    @Mock AccommodationService accommodationService;
    @Mock RoomService roomService;
    @Mock ReservationService reservationService;
    @Mock Authentication authentication;

    private CompanyResourceAccessService accessService;

    @BeforeEach
    void setUp() {
        accessService = new CompanyResourceAccessService(
                companyService, accommodationService, roomService, reservationService);
    }

    @Test
    void resolvesCompanyFromAuthenticatedUser() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("host@example.com");
        when(companyService.getByUserEmail("host@example.com"))
                .thenReturn(CompanyDTO.builder().companyId(7).build());

        assertThat(accessService.currentCompanyId(authentication)).isEqualTo(7);
    }

    @Test
    void rejectsAccommodationOwnedByAnotherCompany() {
        when(accommodationService.getById(30L)).thenReturn(
                AccommodationCreateRequestDTO.builder().accommodationId(30L).companyId(8).build());

        assertThat(accessService.ownsAccommodation(7, 30L)).isFalse();
    }

    @Test
    void acceptsRoomOnlyWhenItsAccommodationBelongsToCompany() {
        when(roomService.getByRoomId(88L)).thenReturn(
                RoomDTO.builder().roomId(88L).accommodationId(30L).build());
        when(accommodationService.getById(30L)).thenReturn(
                AccommodationCreateRequestDTO.builder().accommodationId(30L).companyId(7).build());

        assertThat(accessService.ownsRoom(7, 88L)).isTrue();
        assertThat(accessService.ownsRoom(8, 88L)).isFalse();
    }

    @Test
    void rejectsReservationOwnedByAnotherCompany() {
        when(reservationService.getReservationDetail(101L)).thenReturn(
                ReservationDTO.builder().reservationId(101L).accommodationId(30L).build());
        when(accommodationService.getById(30L)).thenReturn(
                AccommodationCreateRequestDTO.builder().accommodationId(30L).companyId(7).build());

        assertThat(accessService.ownsReservation(8, 101L)).isFalse();
    }

    @Test
    void reservationFormCannotMixRoomAndAccommodation() {
        when(roomService.getByRoomId(88L)).thenReturn(
                RoomDTO.builder().roomId(88L).accommodationId(30L).build());
        when(accommodationService.getById(30L)).thenReturn(
                AccommodationCreateRequestDTO.builder().accommodationId(30L).companyId(7).build());

        ReservationDTO form = ReservationDTO.builder()
                .roomId(88L)
                .accommodationId(31L)
                .build();

        assertThat(accessService.canCreateReservation(7, form)).isFalse();
    }
}

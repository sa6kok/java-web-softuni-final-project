package com.localbandb.localbandb.data.repositories;

import com.localbandb.localbandb.data.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findAllByGuest_UsernameOrderByStartDateAsc(String username);

    List<Reservation> findAllByGuest_Username_AndPastOrderByEndDateAsc(String username, boolean past);

    List<Reservation> findAllByGuest_Username_AndPayedOrderByStartDateAsc(String username, boolean payed);

    List<Reservation> findAllByGuest_Username_AndCanceledOrderByStartDateAsc(String username, boolean canceled);
}

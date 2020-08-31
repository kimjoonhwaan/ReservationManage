package local;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long>{
    List<Reservation> findByHospitalId(String HospitalId);
    Reservation findByScreeningId(Long ScreeningId);
}
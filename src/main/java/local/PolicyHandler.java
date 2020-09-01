package local;

import local.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyHandler{

    @Autowired
    ReservationRepository reservationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverScreeningRequested_ReservationComplete(@Payload Requested requested){

        if(requested.isMe()){
            //  검진 요청으로 인한 예약 확정
            System.out.println("##### 검진 요청으로 인한 예약 확정: " + requested.toJson());
            if(requested.isMe()){
                Reservation temp = new Reservation();
                temp.setStatus("REQUEST_COMPLETED");
                temp.setCustNm(requested.getCustNm());
                temp.setHospitalId(requested.getHospitalId());
                temp.setHospitalNm(requested.getHospitalNm());
                temp.setScreeningId(requested.getId());
                reservationRepository.save(temp);
            }
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverHospitalDeleted_ForcedReservationCancel(@Payload HospitalDeleted hospitalDeleted){

        if(hospitalDeleted.isMe()){
            System.out.println("##### listener ForcedReservationCanceled : " + hospitalDeleted.toJson());
            //  병원일정 삭제로 인한 , 스케쥴 상태 변경
            List<Reservation> list = reservationRepository.findByHospitalId(String.valueOf(hospitalDeleted.getId()));
            for(Reservation temp : list){
                if(!"CANCELED".equals(temp.getStatus())) {
                    temp.setStatus("FORCE_CANCELED");
                    reservationRepository.save(temp);
                }
            }
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCanceled_ReservationCancel(@Payload Canceled canceled){

        if(canceled.isMe()){
            //  검진예약 취소로 인한 취소
            Reservation temp = reservationRepository.findByScreeningId(canceled.getId());
            temp.setStatus("CANCELED");
            reservationRepository.save(temp);

        }
    }

}

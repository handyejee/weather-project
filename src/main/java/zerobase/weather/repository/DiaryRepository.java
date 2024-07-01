package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Diary;

import java.time.LocalDate;
import java.util.List;

@Service
public interface DiaryRepository extends JpaRepository<Diary, Integer> {
    // date 기준으로 그날 일기 가져오기
    List<Diary> findAllByDate(LocalDate date);

    // 특정 일자 기준으로 일기 가져오기
    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    // 수정함수 ( 쿼리에서 LIMIT 1 과 동일한 역할 하도록)
    Diary getFirstByDate(LocalDate date);

    // 삭제 함수
    @Transactional
    void deleteAllByDate(LocalDate date);
}

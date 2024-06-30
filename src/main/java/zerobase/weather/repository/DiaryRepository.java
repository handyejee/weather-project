package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import zerobase.weather.domain.Diary;

@Service
public interface DiaryRepository extends JpaRepository<Diary, Integer> {
}

package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

@Repository
// ORM을 사용할때 사용가능한 메서드들을 JpaRepository가 가지고 있어서 이용하면 되나
public interface JpaMemoRepository extends JpaRepository<Memo, Integer> {

}

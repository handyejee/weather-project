package zerobase.weather.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity(name = "date_weather")
@NoArgsConstructor
public class DateWeather {
    @Id // PK 설정 -> 여러번 수집 해도 pk 이기 때문에 중복수집 x
    private LocalDate date;
    private String weather;
    private String icon;
    private double temperature;
}

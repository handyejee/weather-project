package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional; // 여기 라이브러리에서 가져와야 함
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// open weather map 에서 데이터 받아오기 -> 이거 실행
// 받아온 날씨 데이터 파싱하기
// db에 저장하기
@Service
@Transactional(readOnly = true)
public class DiaryService {
    @Value("${openweathermap.key}")
    private String apiKey; // 미리 지정한 api key 값 가져옴
    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;
    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) { // 생성자 만들기
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    /*
    데이터 삽입 테스트 용
    @Scheduled(cron = "0/5 * * * * *")
     */

    // api 스케줄링으로 데이터 저장
    @Transactional
    @Scheduled(cron = "0 0 1  * * *")
     // 매일 한시에 데이터 save
    public void saveWeatherDate(){
        dateWeatherRepository.save(getWeatherFromApi());
    }

    /*
    @Transactional(readOnly = true)
    수정해야 하는 기능에 readOnly = true 옵션을 주면 SQLException으로 수정불가능하다는 에러가 난다
    */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        logger.info("started to create diary");

        // 날씨 데이터 가져오기(API 에서 가져오기? or DB에서 가져오기?) -> API에서 가져오기
        DateWeather dateWeather = getDateWeather(date);

        /* 파싱된 데이터 + 읽기 값 db에 저장하기
         매번 다이어리를 쓸때마다 값을 파싱하고 가져오는 불필요한 과정 생략
         매일 API 에서 값을 가지고 와 저장했기 때문에 유료기능인 특정 기간 이상의 날씨를 조회하는 기능도 유저에게 제공가능
        */
        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);
        nowDiary.setDate(date);
        diaryRepository.save(nowDiary);
        logger.info("end to create diary");

    }

    private DateWeather getWeatherFromApi(){
        // open weather map 에서 데이터 받아오기
        String weatherData = getWeatherString();
        System.out.println(weatherData);// 반환된 결과값 출력(json 형태로)

        // 받아온 날씨 데이터 json 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherData);
        DateWeather dateWeather = new DateWeather();

        //domain에서 선언한 값 setter 사용해 가져오기
        dateWeather.setDate(LocalDate.now()); // 오늘 날씨 가져오기
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parsedWeather.get("temp"));

        return dateWeather;
    }

    // 날씨 데이터 가지고 오기
    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        if(dateWeatherListFromDB.isEmpty()){
            // 새로 api에서 날씨 정보를 가져와야 한다
            // 현재 날씨를 통해 가지고 오기로 함
            return getWeatherFromApi();
        } else {
            return  dateWeatherListFromDB.get(0);
        }
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) { // date 기준으로 그날 일기 가져오기
        logger.debug("read diary");
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    // 일기 수정
    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text); // 새로운 input text 가져오기
        diaryRepository.save(nowDiary); // save는 table에 덮어쓰는것
    }

    // 일기 삭제
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }

    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;
        try {
            //url 객체
            URL url = new URL(apiUrl);
            // API URL을 http 형식으로 연결
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //connection 열기
            connection.setRequestMethod("GET"); // 요청
            int responseCode = connection.getResponseCode(); // 응답코드 받음

            BufferedReader br; //bufferedReader로 응답 결과 읽어옴
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream())); //성공결과
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream())); //오류결과
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine); // 결과값 stringbuilder에 넣어주기
            }
            br.close();
            return response.toString();

        } catch (Exception e) {
            return "failed to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString); //JSON Object로 넘겨줌
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<>();
        // main 이랑 weather jsonObject 로 만들어줌
        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        /*
          "weather":[{"id":502,"main":"Rain","description":"heavy intensity rain","icon":"10n"}]
           대괄호로 된 json 형식은 jsonArray -> jsonObject 로 두번 변환해줘야 한다
         */
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        return resultMap;
    }


}

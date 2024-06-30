package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zerobase.weather.domain.Diary;
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
public class DiaryService {
    @Value("${openweathermap.key}")
    private String apiKey; // 미리 지정한 api key 값 가져옴
    private final DiaryRepository diaryRepository;

    public DiaryService(DiaryRepository diaryRepository) { // 생성자 만들기
        this.diaryRepository = diaryRepository;
    }

    public void createDiary(LocalDate date, String text) {
        // open weather map 에서 데이터 받아오기
        String weatherData = (getWeatherString());
        System.out.println(weatherData);// 반환된 결과값 출력(json 형태로)

        // 받아온 날씨 데이터 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherData);

        // 파싱된 데이터 + 읽기 값 db에 저장하기
        Diary nowDiary = new Diary();
        nowDiary.setWeather(parsedWeather.get("main").toString());
        nowDiary.setIcon(parsedWeather.get("icon").toString());
        nowDiary.setTemperature((Double) parsedWeather.get("temp"));
        nowDiary.setText(text);
        nowDiary.setDate(date);
        diaryRepository.save(nowDiary);
    }

    public List<Diary> readDiary(LocalDate date){ // date 기준으로 그날 일기 가져오기
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate){
        return diaryRepository.findAllByDateBetween(startDate, endDate);
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

    private Map<String, Object> parseWeather(String jsonString){
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try{
            jsonObject = (JSONObject) jsonParser.parse(jsonString); //JSON Object로 넘겨줌
        }catch (ParseException e) {
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

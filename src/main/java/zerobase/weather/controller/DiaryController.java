package zerobase.weather.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController // 상태코드 지정할 수 있는 RestController
public class DiaryController {
    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }


    // client와 맡닿아 있음 : controller - service -domain
    // 1. 날씨 일기 작성하는 api - path 지정하기
    @Tag(name = "날씨일기 작성 API", description = "날씨 일기 작성을 도와주는 API 입니다")
    @PostMapping("/create/diary")
    void createDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody String text) {
        // @RequestParam : 날짜를 create/diary/date?=20240630 이런식으로 파라미터로 보내게 됨
        // body 값으로 보낼 String 형식의 text
        diaryService.createDiary(date, text);
    }

    // 일기를 날짜에 따라 조회
    @Tag(name = "날씨일기 단건조회 API", description = "날씨 일기를 선택한 날짜별로 조회하는 API입니다.")
    @GetMapping("/read/diary")
    List<Diary> readDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return diaryService.readDiary(date);
    }

    // 일기 날짜 범위에 따라 조회
    @Tag(name = "날씨일기 조회 API", description = "선택한 기간중의 모든 일기 데이터를 가져옵니다")
    @Schema(description = "날짜 형식 : yyyy-MM-dd", example = "2024-07-01")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return diaryService.readDiaries(startDate, endDate);
    }

    // 일기 수정 api
    @Tag(name = "날씨일기 수정 API", description = "날씨 일기 내용을 수정하는 API 입니다")
    @PutMapping("/update/diary")
    void updateDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody String text

    ) {
        diaryService.updateDiary(date, text);
    }

    @Tag(name = "날씨일기 삭제 API", description = "작성한 날씨일기를 삭제할 수 있는 API 입니다")
    @DeleteMapping("/delete/diary")
    void deleteDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
    {
        diaryService.deleteDiary(date);
    }

}

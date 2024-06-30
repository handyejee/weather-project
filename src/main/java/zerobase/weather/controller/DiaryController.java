package zerobase.weather.controller;

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
    @PostMapping("/create/diary")
    void createDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody String text) {
        // @RequestParam : 날짜를 create/diary/date?=20240630 이런식으로 파라미터로 보내게 됨
        // body 값으로 보낼 String 형식의 text
        diaryService.createDiary(date, text);
    }


    // 일기를 날짜에 따라 조회
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return diaryService.readDiary(date);
    }

    // 일기 날짜 범위에 따라 조회(
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return diaryService.readDiaries(startDate, endDate);
    }
}

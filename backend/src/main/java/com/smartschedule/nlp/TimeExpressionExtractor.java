package com.smartschedule.nlp;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TimeExpressionExtractor {

    public LocalDateTime extractStartTime(String text) {
        Result result = extract(text);
        return result != null ? result.startTime : null;
    }

    public LocalDateTime extractEndTime(String text) {
        Result result = extract(text);
        return result != null ? result.endTime : null;
    }

    public Result extract(String text) {
        LocalDate date = null;
        LocalTime time = null;
        LocalTime endTime = null;

        date = extractDate(text);
        if (date == null) date = LocalDate.now().plusDays(1);

        time = extractTime(text);
        endTime = extractEndTimeFromText(text);

        if (time == null) time = LocalTime.of(9, 0);
        if (endTime == null) endTime = time.plusHours(1);

        return new Result(LocalDateTime.of(date, time), LocalDateTime.of(date, endTime));
    }

    private LocalDate extractDate(String text) {
        LocalDate today = LocalDate.now();

        if (Pattern.compile("今天|今日").matcher(text).find()) return today;
        if (Pattern.compile("明天|明日").matcher(text).find()) return today.plusDays(1);
        if (Pattern.compile("后天|后日").matcher(text).find()) return today.plusDays(2);
        if (Pattern.compile("昨天|昨日").matcher(text).find()) return today.minusDays(1);

        Matcher dayAfterMatcher = Pattern.compile("(\\d+)天后").matcher(text);
        if (dayAfterMatcher.find()) return today.plusDays(Integer.parseInt(dayAfterMatcher.group(1)));

        Matcher weekMatcher = Pattern.compile("下周(周|星期)?([一二三四五六日1-6天])").matcher(text);
        if (weekMatcher.find()) {
            String day = weekMatcher.group(2);
            DayOfWeek dow = parseDayOfWeek(day);
            return today.with(TemporalAdjusters.next(dow)).plusWeeks(1);
        }

        Matcher thisWeekMatcher = Pattern.compile("本周(周|星期)?([一二三四五六日1-6天])").matcher(text);
        if (thisWeekMatcher.find()) {
            String day = thisWeekMatcher.group(2);
            DayOfWeek dow = parseDayOfWeek(day);
            return today.with(TemporalAdjusters.nextOrSame(dow));
        }

        Matcher cnMonthDayMatcher = Pattern.compile(
                "(十[一二]?|[一二三四五六七八九])月" +
                "(三十[一]?|二十[一二三四五六七八九]?|十[一二三四五六七八九]?|[一二三四五六七八九])[日号]"
        ).matcher(text);
        if (cnMonthDayMatcher.find()) {
            int month = chineseNumToInt(cnMonthDayMatcher.group(1));
            int day = chineseNumToInt(cnMonthDayMatcher.group(2));
            if (month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                return LocalDate.of(today.getYear(), month, day);
            }
        }

        Matcher monthDayMatcher = Pattern.compile("(\\d{1,2})月(\\d{1,2})[日号]").matcher(text);
        if (monthDayMatcher.find()) {
            int month = Integer.parseInt(monthDayMatcher.group(1));
            int day = Integer.parseInt(monthDayMatcher.group(2));
            return LocalDate.of(today.getYear(), month, day);
        }

        Matcher dateNumMatcher = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})").matcher(text);
        if (dateNumMatcher.find()) {
            return LocalDate.of(
                    Integer.parseInt(dateNumMatcher.group(1)),
                    Integer.parseInt(dateNumMatcher.group(2)),
                    Integer.parseInt(dateNumMatcher.group(3)));
        }

        return null;
    }

    private LocalTime extractTime(String text) {
        Matcher matcher = Pattern.compile("(上午|下午|晚上|凌晨)?\\s*(\\d{1,2})[点时：:](\\d{0,2})?").matcher(text);
        if (matcher.find()) {
            int hour = Integer.parseInt(matcher.group(2));
            int minute = matcher.group(3) != null && !matcher.group(3).isEmpty()
                    ? Integer.parseInt(matcher.group(3)) : 0;
            String period = matcher.group(1);

            if ("下午".equals(period) && hour < 12) hour += 12;
            if ("晚上".equals(period)) {
                if (hour < 12) hour += 12;
            }
            if ("凌晨".equals(period) && hour == 12) hour = 0;

            return LocalTime.of(hour, minute);
        }

        matcher = Pattern.compile("(\\d{1,2}):(\\d{2})").matcher(text);
        if (matcher.find()) {
            return LocalTime.of(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        }

        return null;
    }

    private LocalTime extractEndTimeFromText(String text) {
        List<LocalTime> times = new ArrayList<>();

        Matcher matcher = Pattern.compile("(上午|下午|晚上|凌晨)?\\s*(\\d{1,2})[点时：:](\\d{0,2})?").matcher(text);
        while (matcher.find()) {
            int hour = Integer.parseInt(matcher.group(2));
            int minute = matcher.group(3) != null && !matcher.group(3).isEmpty()
                    ? Integer.parseInt(matcher.group(3)) : 0;
            String period = matcher.group(1);

            if ("下午".equals(period) && hour < 12) hour += 12;
            if ("晚上".equals(period) && hour < 12) hour += 12;
            if ("凌晨".equals(period) && hour == 12) hour = 0;

            times.add(LocalTime.of(hour, minute));
        }

        if (times.size() >= 2) return times.get(1);

        return null;
    }

    private int chineseNumToInt(String chinese) {
        if (chinese == null || chinese.isEmpty()) return 0;
        int result = 0;
        if (chinese.startsWith("三十")) {
            result = 30;
            chinese = chinese.substring(2);
        } else if (chinese.startsWith("二十")) {
            result = 20;
            chinese = chinese.substring(2);
        } else if (chinese.startsWith("十")) {
            result = 10;
            chinese = chinese.substring(1);
        }
        return result + switch (chinese) {
            case "一" -> 1; case "二" -> 2; case "三" -> 3;
            case "四" -> 4; case "五" -> 5; case "六" -> 6;
            case "七" -> 7; case "八" -> 8; case "九" -> 9;
            default -> 0;
        };
    }

    private DayOfWeek parseDayOfWeek(String day) {
        return switch (day) {
            case "一", "1" -> DayOfWeek.MONDAY;
            case "二", "2" -> DayOfWeek.TUESDAY;
            case "三", "3" -> DayOfWeek.WEDNESDAY;
            case "四", "4" -> DayOfWeek.THURSDAY;
            case "五", "5" -> DayOfWeek.FRIDAY;
            case "六", "6" -> DayOfWeek.SATURDAY;
            case "日", "天", "7" -> DayOfWeek.SUNDAY;
            default -> DayOfWeek.MONDAY;
        };
    }

    public static class Result {
        public final LocalDateTime startTime;
        public final LocalDateTime endTime;

        public Result(LocalDateTime startTime, LocalDateTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
}
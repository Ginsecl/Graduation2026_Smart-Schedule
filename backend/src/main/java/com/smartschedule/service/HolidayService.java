package com.smartschedule.service;

import com.smartschedule.common.HolidayInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Service
public class HolidayService {

    public List<HolidayInfo> getHolidays(int year) {
        List<HolidayInfo> holidays = new ArrayList<>();

        if (year == 2025) {
            holidays.add(new HolidayInfo(LocalDate.of(2025, 1, 1), "元旦", "法定假日"));
            holidays.add(new HolidayInfo(LocalDate.of(2025, 1, 28), "除夕", "法定假日"));
            holidays.add(new HolidayInfo(LocalDate.of(2025, 1, 29), "春节", "法定假日"));
            holidays.add(new HolidayInfo(LocalDate.of(2025, 4, 5), "清明节", "法定假日"));
            holidays.add(new HolidayInfo(LocalDate.of(2025, 5, 1), "劳动节", "法定假日"));
            holidays.add(new HolidayInfo(LocalDate.of(2025, 5, 31), "端午节", "法定假日"));
            holidays.add(new HolidayInfo(LocalDate.of(2025, 10, 1), "国庆节", "法定假日"));
            holidays.add(new HolidayInfo(LocalDate.of(2025, 10, 6), "中秋节", "法定假日"));
            holidays.add(new HolidayInfo(LocalDate.of(2025, 2, 14), "情人节", "节日"));
            holidays.add(new HolidayInfo(LocalDate.of(2025, 5, 11), "母亲节", "节日"));
            holidays.add(new HolidayInfo(LocalDate.of(2025, 6, 15), "父亲节", "节日"));
            holidays.add(new HolidayInfo(LocalDate.of(2025, 7, 1), "建党节", "纪念日"));
            holidays.add(new HolidayInfo(LocalDate.of(2025, 8, 1), "建军节", "纪念日"));
            holidays.add(new HolidayInfo(LocalDate.of(2025, 8, 29), "七夕", "节日"));
            holidays.add(new HolidayInfo(LocalDate.of(2025, 9, 10), "教师节", "纪念日"));
            holidays.add(new HolidayInfo(LocalDate.of(2025, 12, 25), "圣诞节", "节日"));
        } else if (year == 2026) {
            holidays.add(new HolidayInfo(LocalDate.of(2026, 1, 1), "元旦", "法定假日"));
            holidays.add(new HolidayInfo(LocalDate.of(2026, 2, 17), "春节", "法定假日"));
            holidays.add(new HolidayInfo(LocalDate.of(2026, 4, 5), "清明节", "法定假日"));
            holidays.add(new HolidayInfo(LocalDate.of(2026, 5, 1), "劳动节", "法定假日"));
            holidays.add(new HolidayInfo(LocalDate.of(2026, 6, 19), "端午节", "法定假日"));
            holidays.add(new HolidayInfo(LocalDate.of(2026, 9, 25), "中秋节", "法定假日"));
            holidays.add(new HolidayInfo(LocalDate.of(2026, 10, 1), "国庆节", "法定假日"));
            holidays.add(new HolidayInfo(LocalDate.of(2026, 2, 14), "情人节", "节日"));
            holidays.add(new HolidayInfo(LocalDate.of(2026, 5, 10), "母亲节", "节日"));
            holidays.add(new HolidayInfo(LocalDate.of(2026, 6, 21), "父亲节", "节日"));
            holidays.add(new HolidayInfo(LocalDate.of(2026, 7, 1), "建党节", "纪念日"));
            holidays.add(new HolidayInfo(LocalDate.of(2026, 8, 1), "建军节", "纪念日"));
            holidays.add(new HolidayInfo(LocalDate.of(2026, 8, 29), "七夕", "节日"));
            holidays.add(new HolidayInfo(LocalDate.of(2026, 9, 10), "教师节", "纪念日"));
            holidays.add(new HolidayInfo(LocalDate.of(2026, 12, 25), "圣诞节", "节日"));
        }

        return holidays;
    }
}
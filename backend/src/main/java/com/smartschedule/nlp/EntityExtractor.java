package com.smartschedule.nlp;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EntityExtractor {

    public String extractTitle(String text) {
        String[] prefixes = {
                "创建", "新建", "添加", "安排", "定", "订",
                "明天", "今天", "下周", "本周",
                "有一个", "有个", "一个"
        };

        String result = text;
        for (String prefix : prefixes) {
            if (result.startsWith(prefix)) {
                result = result.substring(prefix.length());
            }
        }

        result = result.replaceAll("(上午|下午|晚上|凌晨)?\\s*\\d{1,2}[点时：:]\\d{0,2}", "");
        result = result.replaceAll("到\\s*(上午|下午|晚上|凌晨)?\\s*\\d{1,2}[点时：:]\\d{0,2}", "");
        result = result.replaceAll("\\d{1,2}月\\d{1,2}[日号]", "");
        result = result.replaceAll("[在的]\\s*\\S+室", "");
        result = result.replaceAll("[和与跟同]\\s*\\S+", "");
        result = result.replaceAll("提醒|通知|别忘记|别忘了", "");
        result = result.trim();

        if (result.isEmpty()) {
            result = "未命名日程";
        }

        return result;
    }

    public List<String> extractParticipants(String text) {
        List<String> participants = new ArrayList<>();

        Matcher matcher = Pattern.compile("[和与跟同]\\s*([A-Za-z\\u4e00-\\u9fa5]{2,4})").matcher(text);
        while (matcher.find()) {
            String name = matcher.group(1);
            if (isLikelyPerson(name)) {
                participants.add(name);
            }
        }

        return participants;
    }

    public String extractLocation(String text) {
        Matcher matcher = Pattern.compile("[在的于]\\s*([\\u4e00-\\u9fa5\\d]+(?:室|会议室|办公室|大厅|咖啡厅|咖啡店|餐厅))").matcher(text);
        if (matcher.find()) return matcher.group(1);

        matcher = Pattern.compile("地点[：:]\\s*([\\u4e00-\\u9fa5\\d]+)").matcher(text);
        if (matcher.find()) return matcher.group(1);

        return null;
    }

    public Integer extractImportance(String text) {
        if (Pattern.compile("重要|紧急|赶紧").matcher(text).find()) return 5;
        if (Pattern.compile("比较重要").matcher(text).find()) return 4;
        if (Pattern.compile("无所谓|随便|不重要").matcher(text).find()) return 1;
        return null;
    }

    private boolean isLikelyPerson(String name) {
        return Pattern.compile("^[\\u4e00-\\u9fa5]{2,4}$").matcher(name).matches()
                || Pattern.compile("^[A-Z][a-z]+$").matcher(name).matches();
    }
}
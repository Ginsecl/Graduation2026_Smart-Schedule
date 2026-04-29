package com.smartschedule.nlp;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class IntentClassifier {

    private static final Map<Pattern, String> INTENT_PATTERNS = new LinkedHashMap<>();
    private static final Map<Pattern, String> TYPE_PATTERNS = new LinkedHashMap<>();

    static {
        INTENT_PATTERNS.put(Pattern.compile("新建|创建|新增|添加|安排|定|订|预约|建立"), "CREATE");
        INTENT_PATTERNS.put(Pattern.compile("查看|查询|今天|明天|本周|下周|这周|有什么|有哪些|显示|列出"), "QUERY");
        INTENT_PATTERNS.put(Pattern.compile("修改|改|变更|调整|推迟|提前|延后|延期|更新"), "MODIFY");
        INTENT_PATTERNS.put(Pattern.compile("取消|删除|移除|撤|不要了"), "DELETE");

        TYPE_PATTERNS.put(Pattern.compile("开会|会议|例会|周会|晨会|讨论|汇报|评审|review"), "MEETING");
        TYPE_PATTERNS.put(Pattern.compile("任务|待办|todo|完成|作业"), "TASK");
        TYPE_PATTERNS.put(Pattern.compile("截止|deadline|ddl|到期|最后期限|截至"), "DEADLINE");
        TYPE_PATTERNS.put(Pattern.compile("生日|birthday|诞辰"), "BIRTHDAY");
        TYPE_PATTERNS.put(Pattern.compile("出差|旅行|旅游|飞机|火车|航班|出行"), "TRAVEL");
        TYPE_PATTERNS.put(Pattern.compile("运动|健身|跑步|游泳|瑜伽|锻炼|学习|看书|阅读|购物|买东西"), "PERSONAL");
    }

    public String classifyIntent(String text) {
        for (Map.Entry<Pattern, String> entry : INTENT_PATTERNS.entrySet()) {
            if (entry.getKey().matcher(text).find()) {
                return entry.getValue();
            }
        }
        return "CREATE";
    }

    public String classifyType(String text) {
        for (Map.Entry<Pattern, String> entry : TYPE_PATTERNS.entrySet()) {
            if (entry.getKey().matcher(text).find()) {
                return entry.getValue();
            }
        }
        return "OTHER";
    }

    public double calculateConfidence(String text) {
        int matches = 0;
        int total = INTENT_PATTERNS.size() + TYPE_PATTERNS.size();
        for (Pattern p : INTENT_PATTERNS.keySet()) {
            if (p.matcher(text).find()) matches++;
        }
        for (Pattern p : TYPE_PATTERNS.keySet()) {
            if (p.matcher(text).find()) matches++;
        }
        double ratio = (double) matches / total;
        return 0.3 + ratio * 0.7;
    }
}
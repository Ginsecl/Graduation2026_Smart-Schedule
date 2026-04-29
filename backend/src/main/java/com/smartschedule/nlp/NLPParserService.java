package com.smartschedule.nlp;

import com.smartschedule.dto.NlpParseRequest;
import com.smartschedule.dto.NlpParseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NLPParserService {

    private final IntentClassifier intentClassifier;
    private final TimeExpressionExtractor timeExtractor;
    private final EntityExtractor entityExtractor;

    @Value("${nlp.confidence-threshold:0.7}")
    private double confidenceThreshold;

    public NlpParseResult parse(NlpParseRequest request) {
        String text = request.getText();

        NlpParseResult result = new NlpParseResult();
        result.setRawText(text);

        String intent = intentClassifier.classifyIntent(text);
        result.setIntent(intent);

        String type = intentClassifier.classifyType(text);
        result.setType(type);

        TimeExpressionExtractor.Result timeResult = timeExtractor.extract(text);
        result.setStartTime(timeResult.startTime);
        result.setEndTime(timeResult.endTime);

        result.setTitle(entityExtractor.extractTitle(text));

        result.setLocation(entityExtractor.extractLocation(text));

        List<String> participants = entityExtractor.extractParticipants(text);
        result.setParticipants(participants);

        Integer importance = entityExtractor.extractImportance(text);
        result.setImportance(importance);

        double confidence = intentClassifier.calculateConfidence(text);
        result.setConfidence(confidence);

        result.setNeedsConfirmation(confidence < confidenceThreshold);

        return result;
    }
}
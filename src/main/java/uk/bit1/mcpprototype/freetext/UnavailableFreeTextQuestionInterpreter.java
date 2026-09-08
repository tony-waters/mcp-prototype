package uk.bit1.mcpprototype.freetext;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ConditionalOnMissingBean(FreeTextQuestionInterpreter.class)
class UnavailableFreeTextQuestionInterpreter implements FreeTextQuestionInterpreter {

    @Override
    public boolean isConfigured() {
        return false;
    }

    @Override
    public FreeTextQuestionIntent interpret(String question) {
        return new FreeTextQuestionIntent(null, Optional.empty(), true);
    }
}

package uk.bit1.mcpprototype.freetext;

public interface FreeTextQuestionInterpreter {

    boolean isConfigured();

    FreeTextQuestionIntent interpret(String question);
}

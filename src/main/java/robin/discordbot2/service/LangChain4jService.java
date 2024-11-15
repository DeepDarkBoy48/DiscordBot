package robin.discordbot2.service;

import robin.discordbot2.entity.AiMessageFormat;

public interface LangChain4jService {
    byte[] deepDarkAiHTMLFigure(String id, AiMessageFormat aiMessageFormat);

    String deepdarkaiText(String id, AiMessageFormat aiMessageFormat);

    String deepdarkaiImage(String id,AiMessageFormat aiMessageFormat);

    String deepDarkTreadAiChat(String id,AiMessageFormat aiMessageFormat);

    String embediframe(String id,AiMessageFormat aiMessageFormat);

    String aisearch(String id,AiMessageFormat aiMessageFormat);
}

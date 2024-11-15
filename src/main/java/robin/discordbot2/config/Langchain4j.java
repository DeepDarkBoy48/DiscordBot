package robin.discordbot2.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import robin.discordbot2.entity.AiMessageFormat;

@Configuration
public class Langchain4j {
    private final Dotenv dotenv = Dotenv.load();

    public String getOpenaiToken(){
        return dotenv.get("openaiToken");
    }
    public String getDiscordToken(){
        return dotenv.get("discordToken");
    }

    //AiAssistantHTmLFigure
    @Bean
    public ChatLanguageModel chatLanguageModelHTMLFigure() {
        return OpenAiChatModel.builder()
                .apiKey(getOpenaiToken())
                .modelName("gpt-4o-mini")
                .build();
    }
    public interface AiAssistantFigure {
        @SystemMessage("Output the answer according to the information. " +
                "If the information is in Chinese, it needs to be translated into English and then the answer needs to be output. " +
                "The output answer should be wrapped in HTML tags, and creativity is needed to enhance the visual effect, and some styles such as colour changes and font sizes should be used. " +
                "The font size of English should be set to Arial. Output more content each time." +
                " For example, if the user enters ‘ai rankings’, a detailed table should be given, including a comparison of various parameters.")
        String chat(@MemoryId Object userId, @UserMessage("AiMessageFormat") AiMessageFormat aiMessageFormat);
    }
    @Bean
    public AiAssistantFigure deepDarkAiHTMLFigure() {
        return AiServices.builder(AiAssistantFigure.class)
                .chatLanguageModel(chatLanguageModelHTMLFigure())
                .build();
    }

    //AiAssistantText
    @Bean
    public ChatLanguageModel chatLanguageModelText() {
        return OpenAiChatModel.builder()
                .apiKey(getOpenaiToken())
                .modelName("gpt-4o-mini")
                .build();
    }
    public interface AiAssistantText {
        @SystemMessage("根据message信息输出答案，并把输出的答案用markdown包装，如大标题，代码块，加粗等")
        String chat(@MemoryId Object userId, @UserMessage("AiMessageFormat") AiMessageFormat aiMessageFormat);
    }
    @Bean
    public AiAssistantText deepDarkAi() {
        return AiServices.builder(AiAssistantText.class)
                .chatLanguageModel(chatLanguageModelText())
                .build();
    }

    //论坛ai
    public interface AiAssistantThreadText {
        @SystemMessage("根据message信息输出答案，并把输出的答案用markdown包装，如大标题，代码块，加粗等")
        String chat(@MemoryId Object userId, @UserMessage("AiMessageFormat") AiMessageFormat aiMessageFormat);
    }

    public interface embed {
        @SystemMessage("根据用户提供的url输出嵌入代码。以下是输出实例：" +
                "1.YouTube用嵌入，用户户输入https://www.youtube.com/embed/w-TT5M6Ax_k?si=NryDdn03edu90Yq7" +
                "（用户可能会输入其他的内容，但是只要识别到是YouTube就获取这个链接，将该链接填入下方代码块的src）" +
                "<div style=\"display: flex; justify-content: center; align-items: center; max-width: 800px; width: 100%; aspect-ratio: 16/9; margin: 0 auto;\">\n" +
                "    <iframe \n" +
                "        style=\"width: 100%; height: 100%;\"\n" +
                "        src=\"\"\n" +
                "        title=\"YouTube video player\" \n" +
                "        frameborder=\"0\" \n" +
                "        allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\"\n" +
                "        referrerpolicy=\"strict-origin-when-cross-origin\" \n" +
                "        allowfullscreen>\n" +
                "    </iframe>\n" +
                "</div>" +
                "2.qq音乐" +
                "用户输入songid如127570280，那么将这个id填入下方代码块songid中" +
                "<iframe frameborder=\"no\" border=\"0\" marginwidth=\"0\" marginheight=\"0\" width=320 height=65\n" +
                "src=\"https://i.y.qq.com/n2/m/outchain/player/index.html?songid=127570280&songtype=0\"></iframe>" +
                "最后输出代码块给用户,用markdonw格式包装")
        String chat(@MemoryId Object userId, @UserMessage("AiMessageFormat") AiMessageFormat aiMessageFormat);
    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(30)
                .build();
    }
    @Bean
    public AiAssistantThreadText deepDarkTreadAi(ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(AiAssistantThreadText.class)
                .chatLanguageModel(chatLanguageModelText())
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }

    @Bean
    public embed embed(ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(embed.class)
                .chatLanguageModel(chatLanguageModelText())
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }

    //ai search tavily
    @Bean
    public ChatLanguageModel chatLanguageModel4o() {
        return OpenAiChatModel.builder()
                .apiKey(getOpenaiToken())
                .modelName("gpt-4o")
                .build();
    }
    public interface aiSearchTavily {
        @SystemMessage("message为网络搜索后的资料，根据message输出一篇文章，包含message提供的网址链接，行文要有层次，如果message是英文就翻译成中文再输出报告。最后一段标题为相关图片：链接1，链接2，注意图片链接直接输出链接即可，最后结尾写上斜体加粗文字（AI服务由crispy frog提供）")
        String chat(@MemoryId Object userId, @UserMessage("AiMessageFormat") AiMessageFormat aiMessageFormat);
    }
    @Bean
    public aiSearchTavily aiSearchTavily() {
        return AiServices.builder(aiSearchTavily.class)
                .chatLanguageModel(chatLanguageModel4o())
                .build();
    }
}

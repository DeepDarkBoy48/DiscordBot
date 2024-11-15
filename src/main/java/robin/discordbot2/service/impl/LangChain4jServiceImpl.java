package robin.discordbot2.service.impl;


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;
import gui.ava.html.image.generator.HtmlImageGenerator;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import robin.discordbot2.config.Langchain4j;
import robin.discordbot2.entity.AiMessageFormat;
import robin.discordbot2.service.LangChain4jService;
import robin.discordbot2.utils.html2picture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class LangChain4jServiceImpl implements LangChain4jService {
    @Autowired
    private Langchain4j.AiAssistantFigure  deepDarkAiHTMLFigure;
    @Autowired
    private Langchain4j.AiAssistantText deepDarkAiText;
    @Autowired
    private Langchain4j.AiAssistantThreadText deepDarkThreadAi;
    @Autowired
    private Langchain4j.embed embed;
    @Autowired
    private Langchain4j.aiSearchTavily aiSearchTavily;

    private final Dotenv dotenv = Dotenv.load();
    public String getOpenaiToken(){
        return dotenv.get("openaiToken");
    }
    public String getDiscordToken(){
        return dotenv.get("discordToken");
    }

    public ImageModel chatLanguageModelImage = OpenAiImageModel.builder()
                .apiKey(getOpenaiToken())
                .modelName("DALL·E 2")
                .build();


    @Override
    public byte[] deepDarkAiHTMLFigure(String id, AiMessageFormat aiMessageFormat) {
        String deepDarkResult = deepDarkAiHTMLFigure.chat(id, aiMessageFormat);
//        html2picture.htmlTransferImage(deepDarkResult);
        HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
        // 加载 HTML 内容
        imageGenerator.setSize(new Dimension(800,600));
        imageGenerator.loadHtml(deepDarkResult);
        // 保存图片到指定路径
//        imageGenerator.saveAsImage("output.png");
        // 生成 BufferedImage
        BufferedImage bufferedImage = imageGenerator.getBufferedImage();
        // 将 BufferedImage 转换为 byte[]
        // 使用 ByteArrayOutputStream 将 BufferedImage 转换为 byte[]
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", baos);  // 将图片写入字节流，格式为 PNG
            return baos.toByteArray();  // 返回字节数组
        } catch (IOException e) {
            e.printStackTrace();
            return null;  // 处理异常，返回 null
        }
    }

    @Override
    public String deepdarkaiText(String id, AiMessageFormat aiMessageFormat) {
        String deepDarkResult = deepDarkAiText.chat(id, aiMessageFormat);
        return deepDarkResult;
    }

    @Override
    public String deepdarkaiImage(String id, AiMessageFormat aiMessageFormat) {
        String message = aiMessageFormat.getMessage();
        Response<Image> response = chatLanguageModelImage.generate(message);
        return response.content().url().toString();
    }

    @Override
    public String deepDarkTreadAiChat(String id, AiMessageFormat aiMessageFormat) {
        String deepDarkResult = deepDarkThreadAi.chat(id,aiMessageFormat);
        return deepDarkResult;
    }

    @Override
    public String embediframe(String id, AiMessageFormat aiMessageFormat) {
        String embedResult = embed.chat(id, aiMessageFormat);
        return embedResult;
    }

    @Override
    public String aisearch(String id, AiMessageFormat aiMessageFormat) {
        Map<String, Object> requestData = new HashMap<>();
        String url = "https://api.tavily.com/search";
        String apiKey = "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";
        boolean include_images = true;
        boolean include_images_descriptions = true;
        String search_depth = "advanced";
        String query = aiMessageFormat.getMessage();

        // 构建请求数据
        JSONObject data = new JSONObject();
        data.put("api_key", apiKey);
        data.put("query", query);
        data.put("include_images", include_images);
        data.put("include_images_descriptions", include_images_descriptions);
        data.put("search_depth", search_depth);

        // 发送 POST 请求
        String response = HttpUtil.post(url, data.toString());
        AiMessageFormat aiMessageFormat1 = new AiMessageFormat();
        aiMessageFormat1.setMessage(response);
        String aisearchResult = aiSearchTavily.chat(id, aiMessageFormat1);
        return aisearchResult;
    }
}

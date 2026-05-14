package com.stu.helloserver.service.impl;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.stu.helloserver.dto.ChatRequestDTO;
import com.stu.helloserver.dto.ChatResponseVO;
import com.stu.helloserver.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private static final String REDIS_KEY_PREFIX = "chat:session:";
    private static final int MAX_ROUNDS = 3;

    private final ChatClient chatClient;
    private final StringRedisTemplate stringRedisTemplate;

    public ChatServiceImpl(ChatClient.Builder chatClientBuilder,
                           StringRedisTemplate stringRedisTemplate) {
        this.chatClient = chatClientBuilder
                .defaultSystem("你是一名专业、友好、简洁的中文智能助手，请帮助用户。")
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                .build()
                )
                .build();
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public ChatResponseVO chat(ChatRequestDTO requestDTO) {
        String sessionId = requestDTO.getSessionId();
        String message = requestDTO.getMessage();
        String redisKey = REDIS_KEY_PREFIX + sessionId;

        List<String> records = stringRedisTemplate.opsForList().range(redisKey, 0, -1);

        String finalPrompt;
        if (records != null && !records.isEmpty()) {
            String historyText = String.join("\n", records);
            finalPrompt = """
                    以下是历史对话:
                    %s
                    当前用户问题:
                    %s
                    """.formatted(historyText, message);
        } else {
            finalPrompt = message;
        }

        String answer = chatClient.prompt(finalPrompt)
                .call()
                .content();

        String recordText = "用户: " + message + "\n" + "助手: " + answer;
        stringRedisTemplate.opsForList().rightPush(redisKey, recordText);

        Long size = stringRedisTemplate.opsForList().size(redisKey);
        if (size != null && size > MAX_ROUNDS) {
            stringRedisTemplate.opsForList().trim(redisKey, size - MAX_ROUNDS, size - 1);
        }

        return new ChatResponseVO(message, answer);
    }
}

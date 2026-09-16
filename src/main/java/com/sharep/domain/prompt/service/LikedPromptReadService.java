package com.sharep.domain.prompt.service;

import com.sharep.domain.prompt.domain.PromptLike;
import com.sharep.domain.prompt.domain.repository.PromptLikeRepository;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.prompt.presentation.dto.response.PromptAllResponse;
import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.global.error.exception.CustomException;
import com.sharep.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikedPromptReadService {
    private final PromptRepository promptRepository;
    private final PromptLikeRepository promptLikeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PromptAllResponse> execute(Long userId) {

        List<PromptAllResponse> promptResponse;

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USERID_NOT_FOUND));

        List<PromptLike> promptLikes = promptLikeRepository.findAllByUser(user);

        promptResponse = promptLikes.stream()
                .map(promptLike -> {
                        return new PromptAllResponse(promptLike.getPrompt(), true);
                })
                .toList();

        return promptResponse;
    }
}

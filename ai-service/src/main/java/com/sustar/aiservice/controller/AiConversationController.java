package com.sustar.aiservice.controller;

import com.sustar.aiservice.dto.AiConversationDTO;
import com.sustar.aiservice.query.AiConversationQuery;
import com.sustar.aiservice.service.AiConversationService;
import com.sustar.aiservice.vo.AiConversationVO;
import com.sustar.aiservice.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI对话控制器
 * 处理对话相关的HTTP请求
 */
@RestController
@RequestMapping("/api/ai/conversation")
@RequiredArgsConstructor
public class AiConversationController {

    private final AiConversationService aiConversationService;

    @GetMapping("/{id}")
    public Result<AiConversationVO> getConversation(@PathVariable Long id) {

        AiConversationDTO dto = aiConversationService.getConversationById(id);
        AiConversationVO vo = convertToVO(dto);
        return Result.success(vo);
    }

    @GetMapping("/list")
    public Result<List<AiConversationVO>> listConversations(AiConversationQuery query) {
        List<AiConversationDTO> dtoList = aiConversationService.listConversations(query);
        List<AiConversationVO> voList = dtoList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @PostMapping
    public Result<Long> createConversation(@RequestBody AiConversationDTO dto) {
        Long id = aiConversationService.createConversation(dto);
        return Result.success("创建成功", id);
    }

    @PutMapping("/{id}")
    public Result<Void> updateConversation(@PathVariable Long id, @RequestBody AiConversationDTO dto) {
        aiConversationService.updateConversation(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteConversation(@PathVariable Long id) {
        aiConversationService.deleteConversation(id);
        return Result.success();
    }

    private AiConversationVO convertToVO(AiConversationDTO dto) {
        AiConversationVO vo = new AiConversationVO();
        BeanUtils.copyProperties(dto, vo);
        return vo;
    }
}

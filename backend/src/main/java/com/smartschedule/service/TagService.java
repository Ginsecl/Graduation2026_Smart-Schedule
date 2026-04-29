package com.smartschedule.service;

import com.smartschedule.dto.ScheduleTagDTO;
import com.smartschedule.entity.ScheduleTag;
import com.smartschedule.repository.ScheduleTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final ScheduleTagRepository tagRepository;

    @Transactional
    public ScheduleTagDTO create(Long userId, ScheduleTagDTO dto) {
        tagRepository.findByUserIdAndName(userId, dto.getName())
                .ifPresent(t -> { throw new RuntimeException("标签已存在"); });

        ScheduleTag tag = new ScheduleTag();
        tag.setUserId(userId);
        tag.setName(dto.getName());
        if (dto.getColor() != null) tag.setColor(dto.getColor());
        if (dto.getIcon() != null) tag.setIcon(dto.getIcon());
        tag = tagRepository.save(tag);
        return ScheduleTagDTO.fromTag(tag);
    }

    public List<ScheduleTagDTO> listByUser(Long userId) {
        return tagRepository.findByUserId(userId)
                .stream().map(ScheduleTagDTO::fromTag).collect(Collectors.toList());
    }

    @Transactional
    public ScheduleTagDTO update(Long userId, Long tagId, ScheduleTagDTO dto) {
        ScheduleTag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("标签不存在"));
        if (!tag.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此标签");
        }
        if (dto.getName() != null) tag.setName(dto.getName());
        if (dto.getColor() != null) tag.setColor(dto.getColor());
        if (dto.getIcon() != null) tag.setIcon(dto.getIcon());
        tag = tagRepository.save(tag);
        return ScheduleTagDTO.fromTag(tag);
    }

    @Transactional
    public void delete(Long userId, Long tagId) {
        ScheduleTag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("标签不存在"));
        if (!tag.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此标签");
        }
        tagRepository.delete(tag);
    }
}
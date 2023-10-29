package ru.practicum.comments.service;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CreatedCommentDto;
import ru.practicum.comments.dto.UpdatedCommentDto;

import java.util.List;

public interface CommentsService {
    CommentDto createComment(Long userId, CreatedCommentDto createdCommentDto);

    List<CommentDto> getAllUserComments(Long userId);

    CommentDto updateComment(Long userId, Long commentId, UpdatedCommentDto updatedCommentDto);

    void deleteComment(Long userId, Long commentId);

    List<CommentDto> getAllEventComments(Long eventId, Integer from, Integer size);

    CommentDto getCommentById(Long commentId);
}

package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CreatedCommentDto;
import ru.practicum.comments.dto.UpdatedCommentDto;
import ru.practicum.comments.service.CommentsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class CommentsPrivateController {

    private final CommentsService commentsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long userId,
                                    @Valid @RequestBody CreatedCommentDto createdCommentDto) {
        return commentsService.createComment(userId, createdCommentDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllUserComments(@PathVariable Long userId) {
        return commentsService.getAllUserComments(userId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @RequestBody UpdatedCommentDto updatedCommentDto) {
        return commentsService.updateComment(userId, commentId, updatedCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        commentsService.deleteComment(userId, commentId);
    }
}

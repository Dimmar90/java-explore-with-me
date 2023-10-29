package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentMapper;
import ru.practicum.comments.dto.CreatedCommentDto;
import ru.practicum.comments.dto.UpdatedCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentsRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentsServiceImpl implements CommentsService {

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final CommentsRepository commentsRepository;

    @Override
    public CommentDto createComment(Long userId, CreatedCommentDto createdCommentDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));

        Event event = eventRepository.findById(createdCommentDto.getEvent())
                .orElseThrow(() -> new NotFoundException("Событие с id: " + createdCommentDto.getEvent() + " не найдено"));

        Comment commentToAdd = Comment.builder()
                .created(LocalDateTime.now())
                .text(createdCommentDto.getText())
                .author(user)
                .event(event)
                .build();

        commentsRepository.save(commentToAdd);

        log.info("Added new comment: {}", createdCommentDto);

        return CommentMapper.getCommentDto(commentToAdd);
    }

    @Override
    public List<CommentDto> getAllUserComments(Long userId) {
        List<CommentDto> userComments = new ArrayList<>();

        if (!userRepository.existsUserById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }

        for (Comment comment : commentsRepository.findAllByAuthor_Id(userId)) {
            userComments.add(CommentMapper.getCommentDto(comment));
        }

        log.info("Get all comments by user with id: {}", userId);
        return userComments;
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdatedCommentDto updatedCommentDto) {
        if (!userRepository.existsUserById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }

        Comment comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id: " + commentId + " не найден"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("User can't update comment: wrong author id");
        }

        comment.setText(updatedCommentDto.getText());

        commentsRepository.save(comment);

        log.info("Comment with id: {} updated by user: {}", commentId, userId);

        return CommentMapper.getCommentDto(comment);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        if (!userRepository.existsUserById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }

        Comment comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id: " + commentId + " не найден"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь с id: " + userId + " не является автором комментария с id: " + commentId);
        }

        log.info("Deleted comment with id: {} by user with id: {}", commentId, userId);

        commentsRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getAllEventComments(Long eventId, Integer from, Integer size) {
        List<CommentDto> eventComments = new ArrayList<>();

        Pageable pageable = PageRequest.of(from / size, size);

        if (!eventRepository.existsEventById(eventId)) {
            throw new NotFoundException("Событие с id: " + eventId + " не найдено");
        }

        for (Comment comment : commentsRepository.findAllByEventIdOrderById(eventId, pageable)) {
            eventComments.add(CommentMapper.getCommentDto(comment));
        }

        log.info("Get all comments of event with id : {}", eventId);
        return eventComments;
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id: " + commentId + " не найден"));

        log.info("Get comment by id: {}", commentId);

        return CommentMapper.getCommentDto(comment);
    }
}
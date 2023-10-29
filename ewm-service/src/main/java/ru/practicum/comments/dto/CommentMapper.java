package ru.practicum.comments.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.comments.model.Comment;
import ru.practicum.events.dto.EventMapper;
import ru.practicum.users.dto.UserMapper;

@UtilityClass
public class CommentMapper {

    public static CommentDto getCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .event(EventMapper.getEventShortDto(comment.getEvent()))
                .author(UserMapper.getUserShortDto(comment.getAuthor()))
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }
}

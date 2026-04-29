package com.smartschedule.service;

import com.smartschedule.dto.TodoDTO;
import com.smartschedule.entity.Todo;
import com.smartschedule.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public TodoDTO create(Long userId, TodoDTO dto) {
        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setTitle(dto.getTitle());
        todo.setImportant(dto.getImportant() != null ? dto.getImportant() : false);
        todo.setCompleted(false);
        todo = todoRepository.save(todo);
        return TodoDTO.fromTodo(todo);
    }

    @Transactional
    public TodoDTO update(Long userId, Long todoId, TodoDTO dto) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("待办事项不存在"));
        if (!todo.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此待办事项");
        }
        if (dto.getTitle() != null) {
            todo.setTitle(dto.getTitle());
        }
        if (dto.getImportant() != null) {
            todo.setImportant(dto.getImportant());
        }
        todo = todoRepository.save(todo);
        return TodoDTO.fromTodo(todo);
    }

    @Transactional
    public void delete(Long userId, Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("待办事项不存在"));
        if (!todo.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此待办事项");
        }
        todoRepository.delete(todo);
    }

    public List<TodoDTO> list(Long userId) {
        return todoRepository.findByUserIdOrderByImportantDescCreatedAtDesc(userId)
                .stream().map(TodoDTO::fromTodo).collect(Collectors.toList());
    }

    @Transactional
    public TodoDTO toggleComplete(Long userId, Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("待办事项不存在"));
        if (!todo.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此待办事项");
        }
        todo.setCompleted(!todo.getCompleted());
        todo = todoRepository.save(todo);
        return TodoDTO.fromTodo(todo);
    }
}

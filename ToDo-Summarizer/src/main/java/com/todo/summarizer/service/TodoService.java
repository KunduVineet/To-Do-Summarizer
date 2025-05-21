package com.todo.summarizer.service;

import com.todo.summarizer.model.Todo;
import com.todo.summarizer.repository.TodoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    //create to-do
    @Transactional
    public Todo createTodo(Todo todo){
        todo.setCreatedOn(LocalDateTime.now());
        todo.setStatus("Pending");
        return todoRepository.save(todo);
    }

    // Read all to-dos
    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    // Read a single to-do by ID
    public Optional<Todo> getTodoById(Long id) {
        return todoRepository.findById(id);
    }

    //update To-do
    public Todo updateTodo(Long id, Todo updatetodo ){
        Optional<Todo> existingTodo = todoRepository.findById(id);
        if(existingTodo.isPresent()){
            Todo todo = existingTodo.get();
            todo.setTitle(updatetodo.getTitle());
            todo.setDescription(updatetodo.getDescription());
            todo.setStatus(updatetodo.getStatus());
            return todoRepository.save(todo);
        } else{
            throw new RuntimeException("Todo not found with id"+ id);
        }
    }

    //delete To-do
    public Todo deleteTodo(Long id){
        if(todoRepository.existsById(id)){
            todoRepository.deleteById(id);
        }   else {
            throw new RuntimeException("Todo not found with id"+ id);
        }
        return null;
    }

    public List<Todo> getPendingTodos() {
        return todoRepository.findByStatus("pending");
    }
}

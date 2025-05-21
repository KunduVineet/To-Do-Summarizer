package com.todo.summarizer.controller;

import com.todo.summarizer.model.Todo;
import com.todo.summarizer.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    //get all todos
    @GetMapping
    public List<Todo> getAllTodos(){
        return todoService.getAllTodos();
    }

    //create all todos
    @PostMapping
    public Todo createTodo(@RequestBody Todo todo){
        return todoService.createTodo(todo);
    }

    //delete all todos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable long id){
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    //update all todos
    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable long id, @RequestBody Todo todo){
        return todoService.updateTodo(id, todo);
    }
}

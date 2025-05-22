import React, { useState, useEffect } from 'react';
import axios from 'axios';
import TodoForm from './components/TodoForm';
import TodoList from './components/TodoList';
import './App.css';

function App() {
  const [todos, setTodos] = useState([]);
  const [message, setMessage] = useState('');

  // Fetch all to-dos on mount
  useEffect(() => {
    fetchTodos();
  }, []);

  const fetchTodos = async () => {
    try {
      const response = await axios.get('http://localhost:8080/todos');
      setTodos(response.data);
      setMessage('');
    } catch (error) {
      setMessage('Error fetching todos: ' + error.message);
    }
  };

  const addTodo = async (todo) => {
    try {
      const response = await axios.post('http://localhost:8080/todos', todo);
      setTodos([...todos, response.data]);
      setMessage('Todo added successfully.');
    } catch (error) {
      setMessage('Error adding todo: ' + error.message);
    }
  };

  const deleteTodo = async (id) => {
    try {
      await axios.delete(`http://localhost:8080/todos/${id}`);
      setTodos(todos.filter((todo) => todo.id !== id));
      setMessage('Todo deleted successfully.');
    } catch (error) {
      setMessage('Error deleting todo: ' + error.message);
    }
  };

  const summarizeTodos = async () => {
    try {
      const response = await axios.post('http://localhost:8080/todos/summary', {});
      setMessage(response.data);
    } catch (error) {
      setMessage('Error summarizing todos: ' + error.message);
    }
  };

  return (
    <div className="App">
      <h1>Todo Summary Assistant</h1>
      <TodoForm addTodo={addTodo} />
      <TodoList todos={todos} deleteTodo={deleteTodo} />
      <button className="summarize-btn" onClick={summarizeTodos}>
        Summarize Todos
      </button>
      {message && <p className="message">{message}</p>}
    </div>
  );
}

export default App;
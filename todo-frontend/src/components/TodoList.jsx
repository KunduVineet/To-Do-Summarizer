import React from 'react';

function TodoList({ todos, deleteTodo }) {
  return (
    <ul className="todo-list">
      {todos.length === 0 && <li>No todos available.</li>}
      {todos.map((todo) => (
        <li key={todo.id} className="todo-item">
          <div>
            <strong>{todo.title}</strong>
            {todo.description && <p>{todo.description}</p>}
            <span>Status: {todo.status}</span>
          </div>
          <button onClick={() => deleteTodo(todo.id)}>Delete</button>
        </li>
      ))}
    </ul>
  );
}

export default TodoList;
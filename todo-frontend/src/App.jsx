import React, { useState, useEffect } from "react";
import axios from "axios";
import "./App.css"; // Assuming you have a CSS file for styles

export default function HomePage() {
  const [todos, setTodos] = useState([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");

  useEffect(() => {
    fetchTodos();
  }, []);

  const fetchTodos = async () => {
    const response = await axios.get("http://localhost:8080/todos");
    setTodos(response.data);
  };

  const addTodo = async () => {
    if (!title.trim() || !description.trim()) return;
    const newTodo = {
      title,
      description,
      status: "Pending",
      createdOn: new Date().toISOString()
    };
    const response = await axios.post("http://localhost:8080/todos", newTodo);
    setTodos([...todos, response.data]);
    setTitle("");
    setDescription("");
  };

  const deleteTodo = async (id) => {
    await axios.delete(`http://localhost:8080/todos/${id}`);
    setTodos(todos.filter((todo) => todo.id !== id));
  };

  const toggleStatus = async (todo) => {
    const updatedTodo = {
      ...todo,
      status: todo.status === "Completed" ? "Pending" : "Completed"
    };
    const response = await axios.put(`http://localhost:8080/todos/${todo.id}`, updatedTodo);
    setTodos(todos.map(t => t.id === todo.id ? response.data : t));
  };

  return (
    <div className="min-h-screen flex flex-col bg-gray-200 text-gray-800 w-full">
      <header className="w-full p-4 shadow-md bg-white sticky top-0 z-50">
        <div className="max-w-7xl mx-auto flex justify-between items-center">
          <h1 className="text-3xl md:text-4xl font-bold text-indigo-600">To-Do Summarizer</h1>
        </div>
      </header>

      <main className="flex-1 w-full max-w-7xl mx-auto px-4 py-8">
        <h2 className="text-2xl md:text-3xl font-semibold mb-6 text-center">Manage Your Tasks</h2>

        <div className="flex flex-col sm:flex-row gap-4 mb-8 justify-center">
          <input
            type="text"
            placeholder="Title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="flex-1 p-3 border rounded shadow-sm"
          />
          <input
            type="text"
            placeholder="Description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="flex-1 p-3 border rounded shadow-sm"
          />
          <button
            onClick={addTodo}
            className="bg-indigo-600 text-white px-5 py-3 rounded shadow hover:bg-indigo-700"
          >
            Add
          </button>
        </div>

        <ul className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {todos.map((todo) => (
            <li
              key={todo.id}
              className="p-4 bg-white shadow-md rounded-lg space-y-2 flex flex-col justify-between"
            >
              <div>
                <h3 className="text-lg font-semibold text-indigo-700">{todo.title}</h3>
                <p className="text-sm text-gray-600">{todo.description}</p>
                <p className={`text-sm mt-1 font-medium ${todo.status === "Completed" ? "text-green-600" : "text-yellow-600"}`}>
                  Status: {todo.status}
                </p>
              </div>
              <div className="flex justify-end space-x-4 mt-4">
                <button
                  onClick={() => toggleStatus(todo)}
                  className="text-blue-600 hover:underline"
                >
                  Toggle Status
                </button>
                <button
                  onClick={() => deleteTodo(todo.id)}
                  className="text-red-600 hover:underline"
                >
                  Delete
                </button>
              </div>
            </li>
          ))}
        </ul>
      </main>

      <footer className="bg-white text-gray-700 mt-auto py-6 border-t shadow-inner">
        <div className="max-w-4xl mx-auto px-4 text-center">
          <p>To-Do Summarizer App © 2025</p>
          <p>Email: support@todosummarizer.app</p>
        </div>
      </footer>
    </div>
  );
}
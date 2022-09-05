package com.example.fighting.Calendar;

public class Note {
    int _id;
    String todo;
    String date_todo;

    public Note(int _id, String todo, String date_todo){
        this._id = _id;
        this.todo = todo;
        this.date_todo = date_todo;
    }

    public String getDate_todo() {
        return date_todo;
    }

    public void setDate_todo(String date_todo) {
        this.date_todo = date_todo;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }
}

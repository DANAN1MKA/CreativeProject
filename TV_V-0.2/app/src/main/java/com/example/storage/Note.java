package com.example.storage;

public class Note {
    public int id;
    public String name;
    public String login;
    public String pass;
    public String url;
    public String description;

    public Note(){}

    public Note(int _id, String _name, String _login, String _pass, String _url, String _description){
        id = _id;
        name = _name;
        login = _login;
        pass = _pass;
        url =_url;
        description =_description;
    }
}

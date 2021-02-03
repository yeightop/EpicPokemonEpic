package com.example.pokedexrev0;

public class Pokemon {
    private String name;
    private int number;
    private String url;

    public Pokemon(String name, String url) {
        this.name = name;
        this.url = url;

    }

    public Pokemon(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public String getUrl() {return url;}

}

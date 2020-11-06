package com.example.iotkitchen;


//Class model to hold the current step information
class Step {
    private String ingredient;
    private int number;  //What is this for??? I do not know...
    private String procedure;
    private int weight;
    private String unit;

    public Step() {}  // Needed for Firebase

    public Step(String ingredient, int number, String procedure, int weight, String unit) {
        this.ingredient = ingredient;
        this.number = number;
        this.procedure = procedure;
        this.weight = weight;
        this.unit = unit;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}

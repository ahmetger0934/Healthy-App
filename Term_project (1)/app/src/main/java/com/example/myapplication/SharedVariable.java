package com.example.myapplication;

public class SharedVariable {
    private static SharedVariable instance;
    private float variable;

    private SharedVariable() {

    }

    public static SharedVariable getInstance() {
        if (instance == null) {
            instance = new SharedVariable();
        }
        return instance;
    }

    public float getVariable() {
        return variable;
    }

    public void setVariable(float variable) {
        this.variable = variable;
    }
}
package com.zero.dee.steps;

public class Data {
//    private String _username;
//    private String password;
    private String session_steps;
    private String all_steps;


    public Data( ){

    }

    public Data(String Allsteps, String sessionSteps ){

        this.session_steps = sessionSteps;
        this.all_steps = Allsteps;
    }

    public void setSession_steps(String session_steps) {
        this.session_steps = session_steps;
    }

    public String getSession_steps() {
        return session_steps;
    }



    public void set_AllSteps(String newSteps) {
        this.session_steps = session_steps;
    }

    public String get_Allsteps() {
        return all_steps;
    }
}

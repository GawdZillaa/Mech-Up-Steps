package com.zero.dee.steps;

import java.io.Serializable;

/**
 * Created by Detrich on 3/18/2018.
 */

public class UserData implements Serializable{


    UserData CurrentUser;

    private String UserName;
    private int UserID;
    private  int UserSteps;

    public UserData(String usrnm)
    {
        UserName = usrnm;
    }

    public String getUsername()
    {
        return UserName;
    }

}

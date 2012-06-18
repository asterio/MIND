/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

import java.io.Serializable;

/**
 *
 * @author Asterio
 */
public class UserSession implements Serializable
{
    private String schoolId;
    private String name;
    private boolean logged;
    
    public UserSession(String schoolId, String name, boolean logged)
    {
        this.schoolId = schoolId;
        this.name = name;
        this.logged = logged;
    }
    public String getSchoolId()
    {
        return schoolId;
    }
    public String getName()
    {
        return name;
    }
    public boolean isLogged()
    {
        return logged;
    }
    public void setLogged(boolean logged)
    {
        this.logged = logged;
    }
}

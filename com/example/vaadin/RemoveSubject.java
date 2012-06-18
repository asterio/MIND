/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import hibernate.Hibernate;

/**
 *
 * @author Asterio
 */
public class RemoveSubject extends CustomComponent implements ClickListener
{
    private UserSession us = null;
    
    private Panel panel = null;
    
    private Select selectSubject = null;
    
    private Button execute = null;
    
    private Label result = null;
    
    private Window parentWindow = null;
    
    private Hibernate hibernate = null;
    private Notification notification = new Notification("");
    
    public RemoveSubject(Hibernate hibernate, Window parentWindow,UserSession us) 
    {
        this.us = us;
        this.parentWindow = parentWindow;
        this.hibernate = hibernate;
        
        init();
        
        this.setCompositionRoot(panel);
    }
    
    private void init()
    {
        panel = new Panel();
        selectSubject = new Select();
        execute = new Button("Wykonaj");
        result = new Label("");
        
        GridLayout horizontal = new GridLayout(2,2);
        horizontal.addComponent(new Label("Wybierz przedmiot:"));
        horizontal.addComponent(selectSubject);
        panel.addComponent(horizontal);
        
        panel.addComponent(execute);
        
        execute.addListener(this);
    }

    @Override
    public void buttonClick(ClickEvent event) 
    {
        if(selectSubject.getValue() != null)
        {
            result.setValue(selectSubject.getValue());
        }
        else
        {
            result.setValue("Nie wypełniono wszystkich pól.");
        }
    }    
}

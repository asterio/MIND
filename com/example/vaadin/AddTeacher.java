/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import hibernate.Hibernate;

/**
 *
 * @author Asterio
 */
public class AddTeacher extends CustomComponent implements ClickListener
{
    private UserSession us = null;
    
    private Panel panel = null;
    
    private TextField name = null;
    private TextField surname = null;
    
    private Button add = null;
    
    private Window parentWindow = null;
    
    private Label result = null;
    
    private Hibernate hibernate = null;
    private Notification notification = new Notification("");
    

    public AddTeacher(Hibernate hibernate, Window parentWindow,UserSession us) 
    {
        this.us = us;
        this.parentWindow = parentWindow;
        this.hibernate = hibernate;
        init();
        this.setCompositionRoot(panel);
    }
    
    private void init()
    {
        name = new TextField();
        surname = new TextField();
        add = new Button("Dodaj");
        panel = new Panel();
        result = new Label("");
        
        GridLayout gridLayout = new GridLayout(2,2);
        gridLayout.addComponent(new Label("Imię: "));
        gridLayout.addComponent(name);
        gridLayout.addComponent(new Label("Nazwisko: "));
        gridLayout.addComponent(surname);
        panel.addComponent(gridLayout);
        
        panel.addComponent(add);
        
        add.addListener(this);  
        
    }
    @Override
    public void buttonClick(ClickEvent event) 
    {
        if(!name.getValue().toString().isEmpty() && !surname.getValue().toString().isEmpty())
        {
            boolean res = hibernate.addTeacher(name.getValue().toString(), surname.getValue().toString(),us.getSchoolId());
            if(res == true)
            {
                notification.setCaption("Dodano nauczyciela do bazy danych.");
                notification.setDelayMsec(MyApplication.delay);
                parentWindow.showNotification(notification);
                name.setValue("");
                surname.setValue("");
            }
            else
            {
                parentWindow.showNotification("Błąd w dodawaniu.",Window.Notification.TYPE_ERROR_MESSAGE);
            }
        }
        else
        {
            parentWindow.showNotification("Nie wypełniono wszystkich pól!",Window.Notification.TYPE_ERROR_MESSAGE);
        }        
    }
}

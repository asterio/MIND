/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import dbclass.SchoolTeacher;
import hibernate.Hibernate;
import java.util.Iterator;

/**
 *
 * @author Asterio
 */
public class AddSubject extends CustomComponent implements ClickListener
{
    private UserSession us = null;
    
    private Panel panel = null;
    
    private Window parentWindow = null;
    
    private Button execute = null;
    
    private Label result = null;
    
    private Hibernate hibernate = null;
    
    private TextField subjectName = null;
    private Notification notification = new Notification("");
    
    public AddSubject(Hibernate hibernate, Window parentWindow,UserSession us)
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
        execute = new Button("Dodaj");
        result = new Label("");
        subjectName = new TextField();
        
        GridLayout gridLayout = new GridLayout(2,2);
        gridLayout.addComponent(new Label("Podaj nazwę przedmiotu:"));
        gridLayout.addComponent(subjectName);
        panel.addComponent(gridLayout);
        panel.addComponent(execute);
        
        execute.addListener(this);
    }

    @Override
    public void buttonClick(ClickEvent event) 
    {
        if(subjectName.getValue() != null && !subjectName.getValue().toString().isEmpty())
        {
            boolean res = hibernate.addSubject(subjectName.getValue().toString());
            if(res == true)
            {
                //parentWindow.showNotification("Dodano przedmiot do bazy danych.",Window.Notification.TYPE_HUMANIZED_MESSAGE);
                notification.setCaption("Dodano przedmiot do bazy danych.");
                notification.setDelayMsec(MyApplication.delay);
                parentWindow.showNotification(notification);
                subjectName.setValue("");
            }
            else
            {
                parentWindow.showNotification("Błąd w dodawaniu do bazy danych!",Window.Notification.TYPE_ERROR_MESSAGE);
            }
            //result.setValue(subjectName.getValue() + " " + selectTeacher.getValue());
        }
        else
        {
            parentWindow.showNotification("Nie wypełniono wszystkich pól!",Window.Notification.TYPE_ERROR_MESSAGE);
            //result.setValue("Nie wypełniono wszystkich pól.");
        }
    }

}

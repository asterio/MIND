/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import dbclass.SchoolClass;
import hibernate.Hibernate;
import java.util.Iterator;

/**
 *
 * @author Asterio
 */
public class RemoveClass extends CustomComponent implements ClickListener
{
    private UserSession us = null;
    
    private Panel panel = null;
    
    private Label result = null;
    
    private Button execute = null;
    
    private Window parentWindow = null;
    
    private Select selectClass = null;
    
    private Hibernate hibernate = null;
    private Notification notification = new Notification("");
    
    public RemoveClass(Hibernate hibernate, Window parentWindow,UserSession us)
    {
        this.us = us;
        this.parentWindow = parentWindow;
        this.hibernate = hibernate;
        init();
        initSelectClass();
        this.setCompositionRoot(panel);
    }
    
    private void init()
    {
        panel = new Panel();
        result = new Label("");
        execute = new Button("Wykonaj");
        selectClass = new Select();
        
        GridLayout horizontal = new GridLayout(2,2);
        horizontal.addComponent(new Label("Wybierz klasę:"));
        horizontal.addComponent(selectClass);
        panel.addComponent(horizontal);
        
        panel.addComponent(execute);
               
        execute.addListener(this);
    }
    
    private void initSelectClass()
    {
        selectClass.removeAllItems();
        for(Iterator iter = hibernate.getClasses(us.getSchoolId()).iterator();iter.hasNext();)
        {
            Object nextItem = iter.next();
            if(nextItem instanceof SchoolClass)
            {
                SchoolClass sc = (SchoolClass) nextItem;
                selectClass.addItem(sc.getClassName());
            }
        }
    }

    @Override
    public void buttonClick(ClickEvent event) 
    {
        if(selectClass.getValue() != null)
        {
            boolean res = hibernate.removeClass(selectClass.getValue().toString(),us.getSchoolId());
            if(res == true)
            {
                initSelectClass();
                //parentWindow.showNotification("Usunięto poprawnie.",Window.Notification.TYPE_HUMANIZED_MESSAGE);
                notification.setCaption("Usunięto poprawnie.");
                notification.setDelayMsec(MyApplication.delay);
                parentWindow.showNotification(notification);
                //result.setValue(selectClass.getValue());
            }
            else
            {
                //result.setValue("Błąd w dodawaniu do bazy danych");
                parentWindow.showNotification("Błąd w dodawaniu.",Window.Notification.TYPE_ERROR_MESSAGE);
            }
        }
        else
        {
            //result.setValue("Nie wybrano klasy.");
            parentWindow.showNotification("Nie wybrano klasy.",Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }
}

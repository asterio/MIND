/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import dbclass.SchoolClass;
import hibernate.Hibernate;
import java.util.Iterator;

/**
 *
 * @author Asterio
 */
public class RemoveTimetable extends CustomComponent
{
    private UserSession us = null;
    
    private Panel panel = null;
    
    private Select selectClass = null;
    private Select selectDay = null;
    
    private CheckBox checkBox = null;
    
    private Label result = null;
    private Label labelDay = null;
    
    private Button execute = null;
    
    private Window parentWindow = null;
    
    private Hibernate hibernate = null;
    private Notification notification = new Notification("");
    
    public RemoveTimetable(Hibernate hibernate, Window parentWindow,UserSession us)
    {
        this.us = us;
        this.parentWindow = parentWindow;
        this.hibernate = hibernate;
        init();
        initSelectDay();
        initSelectClass();
        this.setCompositionRoot(panel);
    }
    private void init()
    {
        panel = new Panel();
        selectClass = new Select();
        selectDay = new Select();
        checkBox = new CheckBox();
        result = new Label("");
        labelDay = new Label("Wybierz dzień:");
        execute = new Button("Wykonaj");
        
        selectDay.setVisible(false);
        labelDay.setVisible(false);
        
        GridLayout horizontal = new GridLayout(2,3);
        horizontal.addComponent(new Label("Wybierz klasę:"));
        horizontal.addComponent(selectClass);
        horizontal.addComponent(new Label("Konkretny dzień:"));
        horizontal.addComponent(checkBox);
        horizontal.addComponent(labelDay);
        horizontal.addComponent(selectDay);
        panel.addComponent(horizontal);
        
        panel.addComponent(execute);
        
        checkBox.addListener(new ValueChangeListener() 
        {

            @Override
            public void valueChange(ValueChangeEvent event) 
            {
                selectDay.setVisible(checkBox.booleanValue());
                labelDay.setVisible(checkBox.booleanValue());
            }
        });
        checkBox.setImmediate(true);
        
        execute.addListener(new Button.ClickListener() 
        {

            //Usuwa wszystkie zajęcia dla wszystkich klas z poniedziałku
            @Override
            public void buttonClick(ClickEvent event) 
            {
                if(selectClass.getValue() != null)
                {
                    if(!selectClass.getValue().toString().isEmpty())
                    {
                        if(selectDay.isVisible() && selectDay.getValue() != null)
                        {
                            boolean res = hibernate.removeTimetable(hibernate.getClassId(selectClass.getValue().toString(),us.getSchoolId()), selectDay.getValue().toString());
                            if(res == true)
                            {
                                //parentWindow.showNotification("Usunięto plan dla klasy w wybranym dniu.",Window.Notification.TYPE_HUMANIZED_MESSAGE);
                                notification.setCaption("Usunięto plan dla klasy w dniu " + selectDay.getItemCaption(selectDay.getValue()));
                                notification.setDelayMsec(MyApplication.delay);
                                parentWindow.showNotification(notification);
                                //result.setValue(selectClass.getValue() + " " + selectDay.getValue());
                            }
                            else
                            {
                                parentWindow.showNotification("Błąd w dodawaniu.",Window.Notification.TYPE_ERROR_MESSAGE);
                                //result.setValue("Błąd w dodawaniu do bazy danych");
                            }
                        }
                        else
                        {
                            boolean res = hibernate.removeTimetable(hibernate.getClassId(selectClass.getValue().toString(),us.getSchoolId()), "-1");
                            if(res == true)
                            {
                                parentWindow.showNotification("Usunięto plan dla klasy.",Window.Notification.TYPE_HUMANIZED_MESSAGE);
                                //result.setValue(selectClass.getValue());
                            }
                            else
                            {
                                parentWindow.showNotification("Błąd w dodawaniu.",Window.Notification.TYPE_ERROR_MESSAGE);
                                //result.setValue("Błąd w dodawaniu do bazy danych");
                            }
                        }
                    }
                }
                else
                {
                    parentWindow.showNotification("Nie wypełniono wszystkich pól!",Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
    }
    private void initSelectDay()
    {
        Object item = selectDay.addItem();
        selectDay.setItemCaption(item, "Poniedziałek");
        item = selectDay.addItem();
        selectDay.setItemCaption(item, "Wtorek");
        item = selectDay.addItem();
        selectDay.setItemCaption(item, "Środa");
        item = selectDay.addItem();
        selectDay.setItemCaption(item, "Czwartek");
        item = selectDay.addItem();
        selectDay.setItemCaption(item, "Piątek");
    }
    private void initSelectClass()
    {
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
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import dbclass.SchoolTeacher;
import hibernate.Hibernate;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Asterio
 */
public class AddClass extends CustomComponent implements ClickListener
{
    private Window parentWindow = null;
    
    private Panel panel = null;
    
    private Select selectTeacher = null;
    
    private TextField className = null;
    
    private Button add = null;
    
    private Label result = null;
    
    private Hibernate hibernate = null;
    
    private String newClassId = "";
    private String newClassName = "";
    
    private UserSession us = null;
    
    private Select selectNumber = null;
    
    private Notification notification = new Notification("");
    
    
    public AddClass(Hibernate hibernate, Window parentWindow,UserSession us)
    {
        this.us = us;
        this.hibernate = hibernate;
        this.parentWindow = parentWindow;
        init();
        initSelectMenu();
        initSelectNumber();
        this.setCompositionRoot(panel);
    }
    
    private void init()
    {
        panel = new Panel();
        selectTeacher = new Select();
        selectNumber = new Select();
        className = new TextField();
        add = new Button("Dodaj");
        result = new Label("");
        
        GridLayout gridLayout = new GridLayout(2,4);
        
        gridLayout.addComponent(new Label("Rok: "));
        gridLayout.addComponent(selectNumber);
        gridLayout.addComponent(new Label("Nazwa klasy: "));
        gridLayout.addComponent(className);
        gridLayout.addComponent(new Label("Wychowawca: "));
        gridLayout.addComponent(selectTeacher);
        gridLayout.addComponent(new Label("Podgląd nazwy klasy:"));
        gridLayout.addComponent(result);
        panel.addComponent(gridLayout);
        
        panel.addComponent(add);
        
        className.setTextChangeEventMode(TextChangeEventMode.EAGER);
        
        className.addListener(new TextChangeListener()
        {

            @Override
            public void textChange(TextChangeEvent event) 
            {
                if(event.getText() != null && selectNumber.getValue() != null)
                {
                    setResultLabel(selectNumber.getValue().toString(),event.getText());
                }
                else
                {
                    setResultLabel("","");
                }
            }
            
        });
        selectNumber.setImmediate(true);
        
        
        selectNumber.addListener(new Property.ValueChangeListener() 
        {

            @Override
            public void valueChange(ValueChangeEvent event) 
            {
                if(event.getProperty().getValue() != null && className != null)
                {
                    newClassId = event.getProperty().getValue().toString();
                    setResultLabel(event.getProperty().getValue().toString(),className.getValue().toString());
                }
                else
                {
                    setResultLabel("","");
                }
                                
            }
        });
        
        selectTeacher.setImmediate(true);
        selectTeacher.addListener(new ValueChangeListener()
        {

            @Override
            public void valueChange(ValueChangeEvent event) 
            {
                if(selectTeacher.getValue() != null)
                {
                    boolean res = hibernate.isTutor(hibernate.getTeacherId(selectTeacher.getValue().toString()));
                    if(res == true)
                    {
                        add.setEnabled(false);
                        add.setDescription(selectTeacher.getValue() + " jest już wychowcą innej klasy.");
                    }
                    else
                    {
                        add.setEnabled(true);
                        add.setDescription("");
                    }
                }
            }
            
        });
        
        
        add.addListener(this);
    }
    
    private void setResultLabel(String classId, String className)
    {
        if(className != null && !classId.isEmpty() && classId != null)
        {
            result.setValue(classId + " " + className);
        }
    }

    @Override
    public void buttonClick(ClickEvent event) 
    {
        if(selectTeacher.getValue() != null && selectNumber.getValue() != null)
        {
            if(!className.getValue().toString().isEmpty() && !selectTeacher.getValue().toString().isEmpty())
            {
                boolean res = hibernate.addClass(selectNumber.getValue().toString()+ " " + className.getValue().toString(), hibernate.getTeacherId(selectTeacher.getValue().toString()),us.getSchoolId());
                if(res == true)
                {
                    //parentWindow.showNotification("Dodano klasę do bazy danych.",Window.Notification.TYPE_HUMANIZED_MESSAGE|Window.Notification.DELAY_FOREVER);
                    notification.setCaption("Dodano klasę do bazy danych.");
                    notification.setDelayMsec(MyApplication.delay);
                    parentWindow.showNotification(notification);
                    className.setValue("");
                    selectTeacher.unselect(selectTeacher.getValue());
                    selectNumber.unselect(selectNumber.getValue());
                    //result.setValue(className.getValue() + " " + selectTeacher.getValue());
                }
                else
                {
                    parentWindow.showNotification("Błąd w dodawaniu.",Window.Notification.TYPE_ERROR_MESSAGE);
                    //result.setValue("Błąd w dodawaniu do bazy danych");
                }
            }
        }
        else
        {
            parentWindow.showNotification("Nie wypełniono wszystkich pól!",Window.Notification.TYPE_ERROR_MESSAGE);
            
            //result.setValue("Nie wypełniono wszystkich pól.");
        }
    }
    
    private void initSelectNumber()
    {
        Object item = selectNumber.addItem();
        selectNumber.setItemCaption(item,"1");
        item = selectNumber.addItem();
        selectNumber.setItemCaption(item, "2");
        item = selectNumber.addItem();
        selectNumber.setItemCaption(item,"3");
        item = selectNumber.addItem();
        selectNumber.setItemCaption(item, "4");
    }
    
    private void initSelectMenu()
    {
        for(Iterator iter = hibernate.getTeachers(us.getSchoolId()).iterator(); iter.hasNext();)
        {
            Object nextItem = iter.next();
            if(nextItem instanceof SchoolTeacher)
            {
                SchoolTeacher st = (SchoolTeacher) nextItem;
                selectTeacher.addItem(st.getTeacherName() + " " + st.getTeacherSurname());
            }            
        }
    }    
}

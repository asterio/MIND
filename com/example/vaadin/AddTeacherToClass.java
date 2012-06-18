/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import dbclass.SchoolClass;
import dbclass.SchoolTeacher;
import hibernate.Hibernate;
import java.util.Iterator;

/**
 *
 * @author Asterio
 */
public class AddTeacherToClass extends CustomComponent
{
    private Panel panel = null;
    
    private Hibernate hibernate = null;
    
    private Button execute = null;
    
    private Window parentWindow = null;
    
    private UserSession us = null;
    
    private Label teacherInfo = null;
    private Label classInfo = null;
    
    private Select selectClass = null;
    private Select selectTeacher = null;
    
    private boolean resultClass = false;
    private boolean resultTeacher = false;
    
    private Notification notification = new Notification("");
   
    
    public AddTeacherToClass(Hibernate hibernate, Window parentWindow, UserSession us)
    {
        this.parentWindow = parentWindow;
        this.hibernate = hibernate;
        this.us = us;
        
        init();
        initTeacherListener();
        initClassListener();
        this.setCompositionRoot(panel);
    }
    
    private void initClassListener()
    {
        selectClass.addListener(new ValueChangeListener()
        {
            @Override
            public void valueChange(ValueChangeEvent event) 
            {
                if(selectClass.getValue() != null)
                {
                    
                    SchoolTeacher st = hibernate.hasTutor(hibernate.getClassId(selectClass.getValue().toString(), us.getSchoolId()));
                    if(st != null)
                    {
                        classInfo.setValue("Obecny wychowawca: " + st.getTeacherName() + " " + st.getTeacherSurname());
                        
                    }
                    else
                    {
                        classInfo.setValue(selectClass.getValue() + " nie ma wychowawcy");
                    }
                    execute.setEnabled(!resultTeacher);
                }
            }            
        });
    }
    private void initTeacherListener()
    {
        selectTeacher.addListener(new ValueChangeListener()
        {
            @Override
            public void valueChange(ValueChangeEvent event) 
            {
                if(selectTeacher.getValue() != null)
                {
                    resultTeacher = hibernate.isTutor(hibernate.getTeacherId(selectTeacher.getValue().toString()));
                    if(resultTeacher == true)
                    {
                        teacherInfo.setValue(selectTeacher.getValue() + " jest już wychowawcą klasy");
                    }
                    else
                    {
                        teacherInfo.setValue(selectTeacher.getValue() + " nie jest wychowawcą żadnej klasy");
                    }
                    execute.setEnabled(!resultTeacher);
                }
            }   
        });
    }
    
    private void init()
    {
        panel = new Panel();
        selectClass = new Select();
        selectTeacher = new Select();
        execute = new Button("Zamień");
        execute.setEnabled(false);
        
        selectTeacher.setImmediate(true);
        selectClass.setImmediate(true);
        
        teacherInfo = new Label("");
        classInfo = new Label("");
        
        GridLayout layout = new GridLayout(3,2);
        layout.addComponent(new Label("Wybierz klasę:"));
        layout.addComponent(selectClass);
        layout.addComponent(classInfo);
        
        layout.addComponent(new Label("Wybierz nauczyciela:"));
        layout.addComponent(selectTeacher);
        layout.addComponent(teacherInfo);
        panel.addComponent(layout);
        
        panel.addComponent(execute);
        
        execute.addListener(new Button.ClickListener() 
        {

            @Override
            public void buttonClick(ClickEvent event) 
            {
                if(selectClass.getValue() != null && selectTeacher.getValue() != null)
                {
                    boolean res = hibernate.addTeacherToClass(hibernate.getClassId(selectClass.getValue().toString(),us.getSchoolId()), hibernate.getTeacherId(selectTeacher.getValue().toString()));
                    if(res == true)
                    {
                        notification.setCaption("Dodano wychowawcę do klasy.");
                        notification.setDelayMsec(MyApplication.delay);
                        parentWindow.showNotification(notification);
                        selectTeacher.unselect(selectTeacher.getValue());
                        selectClass.unselect(selectClass.getValue());
                        teacherInfo.setValue("");
                        classInfo.setValue("");
                    }
                    else
                    {
                        parentWindow.showNotification("Błąd w dodawaniu do bazy danych", Window.Notification.TYPE_ERROR_MESSAGE);
                    }
                }
                else
                {
                    parentWindow.showNotification("Nie wybrano wszystkich pól!", Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
        initSelectTeacher();
        initSelectClass();
    }
    
    private void initSelectTeacher()
    {
        for(Iterator iter = hibernate.getTeachers(us.getSchoolId()).iterator(); iter.hasNext();)
        {
            SchoolTeacher st = (SchoolTeacher) iter.next();
            selectTeacher.addItem(st.getTeacherName() + " " + st.getTeacherSurname());            
        }
    }
    private void initSelectClass()
    {
        for(Iterator iter = hibernate.getClasses(us.getSchoolId()).iterator(); iter.hasNext();)
        {
            SchoolClass sc = (SchoolClass) iter.next();
            selectClass.addItem(sc.getClassName());
        }
    }
    
}

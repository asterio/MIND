/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import dbclass.SchoolSubject;
import dbclass.SchoolTeacher;
import hibernate.Hibernate;
import java.util.Iterator;

/**
 *
 * @author Asterio
 */
public class AddTeacherToSubject extends CustomComponent implements ClickListener
{
    private UserSession us = null;
    
    private Window parentWindow = null;
    
    private Hibernate hibernate = null;
    
    private Panel panel = null;
    
    private Select selectTeacher = null;
    private Select selectSubject = null;
    
    private Button execute = null;
    
    private Notification notification = new Notification("");
    
    public AddTeacherToSubject(Hibernate hibernate, Window parentWindow,UserSession us)
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
        execute = new Button("Wykonaj");
        selectTeacher = new Select();
        selectSubject = new Select();
        initSelectTeacher();
        initSelectSubject();
        
        GridLayout horizontal = new GridLayout(2,2);
        horizontal.addComponent(new Label("Wybierz przedmiot:"));
        horizontal.addComponent(selectSubject);
        
        horizontal.addComponent(new Label("Wybierz nauczyciela:"));
        horizontal.addComponent(selectTeacher);
        
        panel.addComponent(horizontal);
        panel.addComponent(execute);
        
        execute.addListener(this);
        
        execute.setImmediate(true);
    }
    
    private void initSelectTeacher()
    {
        for(Iterator iter = hibernate.getTeachers(us.getSchoolId()).iterator(); iter.hasNext();)
        {
            SchoolTeacher st = (SchoolTeacher) iter.next();
            selectTeacher.addItem(st.getTeacherName() + " " + st.getTeacherSurname());
        }
    }
    
    private void initSelectSubject()
    {
        for(Iterator iter = hibernate.getSubjects().iterator(); iter.hasNext();)
        {
            SchoolSubject ss = (SchoolSubject) iter.next();
            selectSubject.addItem(ss.getSubjectName());
        }
    }

    @Override
    public void buttonClick(ClickEvent event) 
    {
        if(selectSubject.getValue() != null && selectTeacher.getValue() != null)
        {
            boolean res = hibernate.addTeacherToSubject(hibernate.getTeacherId(selectTeacher.getValue().toString()),hibernate.getSubjectId(selectSubject.getValue().toString(), us.getSchoolId()));
            if(res == true)
            {
               notification.setCaption("Dodano nauczyciela do przedmiotu.");
                notification.setDelayMsec(MyApplication.delay);
                parentWindow.showNotification(notification);
                selectTeacher.unselect(selectTeacher.getValue());
                selectSubject.unselect(selectSubject.getValue());
            }
            else
            {
                parentWindow.showNotification("Błąd w dodawaniu do bazy danych!", Window.Notification.TYPE_ERROR_MESSAGE);
            }
        }
        else
        {
            parentWindow.showNotification("Nie wybrano wszystkich pól!", Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }
}

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
public class RemoveTeacher extends CustomComponent implements ClickListener
{
    private UserSession us = null;
    
    private Select selectNewTeacher = new Select();
    
    private Panel panel = null;
    
    private Select selectTeacher = null;
    
    private Button execute = null;
   
    private Label result = null;
    
    private Window parentWindow = null;
    
    private Hibernate hibernate = null;
    
    private Window newWindow = null;
    private Notification notification = new Notification("");
    
    private boolean isActive = false;
    
    public RemoveTeacher(Hibernate hibernate, Window parentWindow,UserSession us)
    {
        this.us = us;
        this.parentWindow = parentWindow;
        this.hibernate = hibernate;
        init();
        initSelectTeacher();
        this.setCompositionRoot(panel);
    }
    
    private void init()
    {
        panel = new Panel();
        selectTeacher = new Select();
        execute = new Button("Wykonaj");
        
        result = new Label("");
        
        execute.addListener(this);
        
        GridLayout horizontal = new GridLayout(2,2);
        horizontal.addComponent(new Label("Wybierz nauczyciela:"));
        horizontal.addComponent(selectTeacher);
        panel.addComponent(horizontal);
       
        panel.addComponent(execute);
        
    }
    
    private void initNewWindow()
    {
        newWindow = new Window();
        newWindow.setCaption("Usuwanie następującego nauczyciela: " + selectTeacher.getValue());
        newWindow.setHeight("200px");
        newWindow.setWidth("500px");
        newWindow.setClosable(false);
        newWindow.setResizable(false);
        
        parentWindow.addWindow(newWindow);
        Button button = new Button("Wykonaj");
        Button cancel = new Button("Anuluj");
        initNewSelectTeacher();
        
        selectTeacher.setEnabled(false);
        
        newWindow.addComponent(new Label("Wybierz nowego nauczyciela:"));
        newWindow.addComponent(selectNewTeacher);
        newWindow.addComponent(button);
        newWindow.addComponent(cancel);
        
        cancel.addListener(new ClickListener()
        {
            @Override
            public void buttonClick(ClickEvent event) 
            {
                initSelectTeacher();
                parentWindow.removeWindow(newWindow);
                isActive = false;
                selectTeacher.setEnabled(true);
            }
            
        });
        
        button.addListener(new ClickListener()
        {
            @Override
            public void buttonClick(ClickEvent event) 
            {
                boolean res = hibernate.changeTeacher(hibernate.getTeacherId(selectTeacher.getValue().toString()),hibernate.getTeacherId(selectNewTeacher.getValue().toString()),us.getSchoolId());
                if(res == true)
                {
                    res = hibernate.removeTeacher(hibernate.getTeacherId(selectTeacher.getValue().toString()));
                    if(res == true)
                    {
                        //parentWindow.showNotification("Usunięto poprawnie.",Window.Notification.TYPE_HUMANIZED_MESSAGE);
                        notification.setCaption("Usunięto poprawnie.");
                        notification.setDelayMsec(MyApplication.delay);
                        parentWindow.showNotification(notification);
                    }
                    else
                    {
                        parentWindow.showNotification("Błąd w dodawaniu.",Window.Notification.TYPE_ERROR_MESSAGE);
                    }
                }
                else
                {
                    parentWindow.showNotification("Nie udało się zamienić nauczycieli.",Window.Notification.TYPE_ERROR_MESSAGE);
                }
                //System.out.println(selectNewTeacher.getValue().toString() + " " + selectTeacher.getValue().toString());
                initSelectTeacher();
                parentWindow.removeWindow(newWindow);
                isActive = false;
                selectTeacher.setEnabled(true);
            }
        });
        //horizontal.addComponent();
    }
    
    private void initSelectTeacher()
    {
        selectTeacher.removeAllItems();
        for(Iterator iter = hibernate.getTeachers(us.getSchoolId()).iterator();iter.hasNext();)
        {
            Object nextItem = iter.next();
            if(nextItem instanceof SchoolTeacher)
            {
                SchoolTeacher st = (SchoolTeacher) nextItem;
                selectTeacher.addItem(st.getTeacherName() + " " + st.getTeacherSurname());
            }
        }
    }
    private void initNewSelectTeacher()
    {
        selectNewTeacher.removeAllItems();
        isActive = true;
        for(Iterator iter = hibernate.getTeachers(us.getSchoolId()).iterator();iter.hasNext();)
        {
            Object nextItem = iter.next();
            if(nextItem instanceof SchoolTeacher)
            {                                
                SchoolTeacher st = (SchoolTeacher) nextItem;
                if(!(selectTeacher.getValue().toString().equals(st.getTeacherName() + " " + st.getTeacherSurname())))
                {
                    selectNewTeacher.addItem(st.getTeacherName() + " " + st.getTeacherSurname());
                }
            }
        }
    }
    

    @Override
    public void buttonClick(ClickEvent event) 
    {
        if(selectTeacher.getValue() != null)
        {
            if(isActive == false)
            {
                initNewWindow();
            }
        }
    }
}

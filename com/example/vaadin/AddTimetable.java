/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import dbclass.SchoolClass;
import dbclass.SchoolLessonNumber;
import dbclass.SchoolSubject;
import dbclass.SchoolTeacher;
import hibernate.Hibernate;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Asterio
 */
public class AddTimetable extends CustomComponent
{
    private UserSession us = null;
    
    private VerticalLayout vertical = null;
    
    private Button button = null;
    
    private Select selectDay = null;
    private Select selectClass = null;
    
    private Hibernate hibernate = null;
    
    private SplitPanel splitter = null;
    
    private Window parentWindow = null;
    
    private Panel panel = null;
    
    private Table table = new Table();
    
    private Notification notification = new Notification("");
    
    public AddTimetable(Hibernate hibernate, Window parentWindow,UserSession us)
    {
        this.us = us;
        this.parentWindow = parentWindow;
        this.hibernate = hibernate;
        
        init();
        
        table.addContainerProperty("Numer lekcji ",String.class,null);
        table.addContainerProperty("Godziny ",String.class,null);
        table.addContainerProperty("Przedmiot ",Select.class,null);
        table.addContainerProperty("Nauczyciel ",Select.class,null);
        table.addContainerProperty("Sala ", TextField.class,null);
        table.addContainerProperty("Informacje ", String.class,null);
        
        
        int i = 0;
        for(Iterator iter = hibernate.getSchoolLessonNumbers(us.getSchoolId()).iterator(); iter.hasNext();)
        {
            SchoolLessonNumber sln = (SchoolLessonNumber) iter.next();
            
            Select selectSubject = initSelectMenu(hibernate.getSubjects().iterator());
            //Select selectTeacher = initSelectTeacher(hibernate.getTeachers("1").iterator());
            Select selectTeacher = new Select();
            SelectListener listener = new SelectListener(selectSubject, selectTeacher);
            selectSubject.addListener(listener);
            selectSubject.setImmediate(true);
            
            
            table.addItem(new Object[] {sln.getId().getLessonNumberId().toString(),sln.getStartTime()+"-"+sln.getEndTime(),selectSubject,selectTeacher,new TextField(),new Label("")},i);
            i++;
        }
        vertical.addComponent(button);
        vertical.setHeight(80, Sizeable.UNITS_PERCENTAGE);
        
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(new Label("Wybierz dzień:"));
        layout.addComponent(selectDay);
        panel.addComponent(layout);
        layout = new HorizontalLayout();
        layout.addComponent(new Label("Wybierz klasę:"));
        layout.addComponent(selectClass);
        panel.addComponent(layout);
        panel.addComponent(table);
        panel.addComponent(vertical);
        panel.setHeight(100, Sizeable.UNITS_PERCENTAGE);
        
        this.setCompositionRoot(panel);
    }

    
    private void init()
    {
        splitter = new SplitPanel();
        vertical = new VerticalLayout();
        panel = new Panel();
        button = new Button("Zapisz");
        selectDay = new Select();
        selectClass = new Select();
        initSelectClass();
        initSelectDay();

        selectDay.setImmediate(true); 
        
        button.addListener(new Button.ClickListener() 
        {

            @Override
            public void buttonClick(ClickEvent event) 
            {
                if(selectClass.getValue() != null && selectDay.getValue() != null)
                {
                    for(Iterator iter = table.getItemIds().iterator(); iter.hasNext();)
                    {
                        Item item = table.getItem(iter.next());

                        for(Iterator iter1 = item.getItemPropertyIds().iterator(); iter1.hasNext();)
                        {
                            Property id = item.getItemProperty(iter1.next());
                            iter1.next();
                            Property subjectId = item.getItemProperty(iter1.next());
                            Property teacherId = item.getItemProperty(iter1.next());
                            Property room = item.getItemProperty(iter1.next());
                            Property output = item.getItemProperty(iter1.next());

                            try
                            {                            
                                if(teacherId.getValue() != null && subjectId.getValue() != null && room.getValue() != null)
                                {
                                    if(!teacherId.getValue().toString().isEmpty() && !subjectId.getValue().toString().isEmpty() && !room.getValue().toString().isEmpty())
                                    {
                                        boolean res = hibernate.addLesson(us.getSchoolId(), id.getValue().toString(), hibernate.getClassId(selectClass.getValue().toString(),us.getSchoolId()), hibernate.getTeacherId(teacherId.getValue().toString()), hibernate.getSubjectId(subjectId.getValue().toString(), us.getSchoolId()), selectDay.getValue().toString(), room.getValue().toString());
                                        if(res == true)
                                        {
                                            output.setValue("Dodano wpis.");
                                        }
                                        else
                                        {
                                            output.setValue("Podany wpis już istnieje!");
                                        }
                                    }
                                    else
                                    {
                                        output.setValue("Nie wypełniono wszystkich pól!");
                                    }
                                }
                                else
                                {
                                    parentWindow.showNotification("Nie wypełniono wszystkich pól.", Window.Notification.TYPE_ERROR_MESSAGE);
                                }                           
                            }
                            catch(Exception e)
                            {

                            }
                        }
                    }
                }
                else
                {
                    parentWindow.showNotification("Nie wypełniono wszystkich pól.", Window.Notification.TYPE_ERROR_MESSAGE);
                }                
            }
        });
        button.setImmediate(true);
    }
    
    private void initSelectClass()
    {
        for(Iterator iter = hibernate.getClasses(us.getSchoolId()).iterator(); iter.hasNext();)
        {
            SchoolClass sc = (SchoolClass) iter.next();
            selectClass.addItem(sc.getClassName());
        }
    }
    
    private Select initSelectTeacher(Iterator iter)
    {
        Select select = new Select();
        
        for(;iter.hasNext();)
        {
            Object nextItem = iter.next();
            if(nextItem instanceof SchoolTeacher)
            {
                SchoolTeacher st = (SchoolTeacher) nextItem;
                select.addItem(st.getTeacherName() + " " + st.getTeacherSurname());
            }
        }
        return select;
    }
    
    private Select initSelectMenu(Iterator iter)
    {
        Select select = new Select();
        
        for(;iter.hasNext();)
        {
            Object nextItem = iter.next();
            if(nextItem instanceof SchoolSubject)
            {
                SchoolSubject ss = (SchoolSubject) nextItem;
                select.addItem(ss.getSubjectName());
            }
        }
        
        return select;
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
    
    class SelectListener implements ValueChangeListener
    {
        private Select listenerMenu = null;
        private Select changeMenu = null;

        public SelectListener(Select listenerMenu, Select changeMenu)
        {
            this.listenerMenu = listenerMenu;
            this.changeMenu = changeMenu;
        }
        @Override
        public void valueChange(ValueChangeEvent event) 
        {
            changeMenu.removeAllItems();
            StringBuilder str = new StringBuilder();
            str.append("Prowadzący:" + "<ul>");
            List result = hibernate.getTeachers(us.getSchoolId(),event.getProperty().getValue().toString());
            for(Iterator iter = result.iterator();iter.hasNext();)
            {
                SchoolTeacher st = (SchoolTeacher) iter.next();
                str.append("<li>").append(st.getTeacherName()).append(" ").append(st.getTeacherSurname()).append("</li>");
                changeMenu.addItem(st.getTeacherName() + " " + st.getTeacherSurname());
                //System.out.println(st.getTeacherName() + " " + st.getTeacherSurname());
            }
            changeMenu.setDescription(str.toString());
        }
    }    
}



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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import dbclass.SchoolClass;
import dbclass.SchoolLesson;
import dbclass.SchoolSubject;
import dbclass.SchoolTeacher;
import hibernate.Hibernate;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Asterio
 */
public class EditTimetable extends CustomComponent
{
    private UserSession us = null;
    
    private Panel panel = null;
    
    private Select selectClass = null;
    private Select selectDay = null;
    
    private Table table = null;
    
    private Button execute = null;
    
    private Hibernate hibernate = null;
    
    private Window parentWindow = null;
    
    private Notification notification = new Notification("");
    
    public EditTimetable(Hibernate hibernate, Window parentWindow,UserSession us)
    {
        this.us = us;
        this.hibernate = hibernate;
        this.parentWindow = parentWindow;
        init();
        
        this.setCompositionRoot(panel);
    }
    
    private void init()
    {
        panel = new Panel();
        table = new Table();
        selectClass = new Select();
        selectClass.setImmediate(true);
        execute = new Button("Wykonaj");
        initSelectClass();
        selectClass.addListener(new Property.ValueChangeListener() 
        {
            @Override
            public void valueChange(ValueChangeEvent event) 
            {
                if(selectClass.getValue() != null && selectDay.getValue() != null)
                {
                    changeValueClass(hibernate.getClassId(selectClass.getValue().toString(), us.getSchoolId()),Integer.parseInt(selectDay.getValue().toString()));
                }
            }
        });
        selectDay = new Select();
        selectDay.setImmediate(true);
        initSelectDay();
        selectDay.addListener(new Property.ValueChangeListener() 
        {
            @Override
            public void valueChange(ValueChangeEvent event) 
            {
                if(selectClass.getValue() != null && selectDay.getValue() != null)
                {
                    changeValueClass(hibernate.getClassId(selectClass.getValue().toString(), us.getSchoolId()),Integer.parseInt(selectDay.getValue().toString()));
                }
            }
        });
        
        table.addContainerProperty("Numer lekcji", String.class, null);
        table.addContainerProperty("Godziny", String.class, null);
        table.addContainerProperty("Przedmiot",Select.class,null);
        table.addContainerProperty("Nauczyciel",Select.class,null);
        table.addContainerProperty("Sala",String.class,null);
        table.addContainerProperty("Zapisz",Button.class,null);
        table.addContainerProperty("Usuń",Button.class,null);
        
        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.addComponent(new Label("Wybierz dzień:"));
        horizontal.addComponent(selectDay);
        panel.addComponent(horizontal);
        
        horizontal = new HorizontalLayout();
        horizontal.addComponent(new Label("Wybierz klasę:"));
        horizontal.addComponent(selectClass);
        panel.addComponent(horizontal);
        panel.addComponent(table);
    }
    
    private void changeValueClass(Integer classId, Integer dayId)
    {
        int i = 1;
        table.removeAllItems();
        for(int j = 0; j < hibernate.getSchoolLessonNumbers(us.getSchoolId()).size(); j++)
        {
            
            SchoolLesson sl = hibernate.getLesson(classId.toString(),dayId, i);
            
            if(sl != null)
            {
                Button r = new Button("Usuń");
                RemoveListener remove = new RemoveListener(r);
                r.addListener(remove);
                Button b = new Button("Zapisz");
                SaveListener save = new SaveListener(b);
                b.addListener(save);
                
                Select selectSubject = null;
                
                if(sl.getSchoolSubject() != null)
                {
                    selectSubject = initSelectSubject(sl.getSchoolSubject().getSubjectName());
                }
                else
                {
                    selectSubject = initSelectSubject("Brak");
                }
                
                Select selectTeacher = null;
                
                if(sl.getSchoolTeacher() != null)
                {
                    selectTeacher = initSelectTeacher(sl.getSchoolTeacher().getTeacherName() + " " + sl.getSchoolTeacher().getTeacherSurname(),sl.getSchoolSubject().getSubjectName());
                }
                else
                {
                    selectTeacher = initSelectTeacher("Brak",sl.getSchoolSubject().getSubjectName());
                }
                addDescription(sl.getSchoolSubject().getSubjectName(),selectTeacher);
                
                SelectListener listener = new SelectListener(selectSubject, selectTeacher);
                selectSubject.addListener(listener);
                selectSubject.setImmediate(true);
                
                Object []items = new Object[] {i,sl.getSchoolLessonNumber().getStartTime() + "-" + sl.getSchoolLessonNumber().getEndTime(),selectSubject,selectTeacher,sl.getRoom(),b, r};
                save.setItemValue(items);
                remove.setItemValue(items);
                table.addItem(items,i);
            }
            i++;
        }
    }
    
    private Select initSelectTeacher(String teacherName,String subjectName)
    {
        Select newSelect = new Select();
        
        newSelect.addItem(teacherName);
        newSelect.select(teacherName);
        
        for(Iterator iter = hibernate.getTeachers(us.getSchoolId(),subjectName ).iterator(); iter.hasNext();)
        {
            SchoolTeacher st = (SchoolTeacher) iter.next();
            if(!(st.getTeacherName() + " " + st.getTeacherSurname()).equals(teacherName))
            {
                newSelect.addItem(st.getTeacherName() + " " + st.getTeacherSurname());
            }
        }
        return newSelect;
    }
    
    private Select initSelectSubject(String subjectName)
    {
        Select newSelect = new Select();
        newSelect.addItem(subjectName);
        newSelect.select(subjectName);
        for(Iterator iter = hibernate.getSubjects().iterator(); iter.hasNext();)
        {
            SchoolSubject ss = (SchoolSubject) iter.next();
            if(!ss.getSubjectName().equals(subjectName))
            {
                newSelect.addItem(ss.getSubjectName());
            }
        }        
        return newSelect;
    }
    
    private void initSelectClass()
    {
        for(Iterator iter = hibernate.getClasses(us.getSchoolId()).iterator(); iter.hasNext();)
        {
            SchoolClass sc = (SchoolClass) iter.next();
            selectClass.addItem(sc.getClassName());
        }
    }
    
    private void addDescription(String subjectName, Select changeMenu)
    {
        StringBuilder str = new StringBuilder();
        str.append("Prowadzący:" + "<ul>");
        List result = hibernate.getTeachers(us.getSchoolId(),subjectName);
        for(Iterator iter = result.iterator();iter.hasNext();)
        {
            SchoolTeacher st = (SchoolTeacher) iter.next();
            str.append("<li>").append(st.getTeacherName()).append(" ").append(st.getTeacherSurname()).append("</li>");
            changeMenu.addItem(st.getTeacherName() + " " + st.getTeacherSurname());
        }
        changeMenu.setDescription(str.toString());
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
            changeMenu.unselect(changeMenu.getVisibleItemIds());
            changeMenu.removeAllItems();
            StringBuilder str = new StringBuilder();
            str.append("Prowadzący:" + "<ul>");
            List result = hibernate.getTeachers(us.getSchoolId(),event.getProperty().getValue().toString());
            for(Iterator iter = result.iterator();iter.hasNext();)
            {
                SchoolTeacher st = (SchoolTeacher) iter.next();
                str.append("<li>").append(st.getTeacherName()).append(" ").append(st.getTeacherSurname()).append("</li>");
                changeMenu.addItem(st.getTeacherName() + " " + st.getTeacherSurname());
            }
            changeMenu.setDescription(str.toString());
        }

    }
    
    class RemoveListener implements ClickListener
    {
        private Button b = null;
        private Object[] item = null;
        public RemoveListener(Button b)
        {
            this.b = b;
        }
        
        public void setItemValue(Object[] item)
        {
            this.item = item;
        }

        @Override
        public void buttonClick(ClickEvent event) 
        {
            if(selectClass.getValue() != null && selectDay.getValue() != null)
            {
                Integer lessonNumber =  (Integer) item[0];
                
                boolean res = hibernate.removeLesson(lessonNumber, us.getSchoolId() ,hibernate.getClassId(selectClass.getValue().toString(),us.getSchoolId()), selectDay.getValue().toString() );
                if(res == true)
                {
                    //parentWindow.showNotification("Usunięto.",Window.Notification.TYPE_WARNING_MESSAGE);
                    notification.setCaption("Usunięto.");
                    notification.setDelayMsec(MyApplication.delay);
                    parentWindow.showNotification(notification);
                    changeValueClass(hibernate.getClassId(selectClass.getValue().toString(), us.getSchoolId()),Integer.parseInt(selectDay.getValue().toString()));
                }
                else
                {
                    parentWindow.showNotification("Nie udało się usunąć.",Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
            else
            {
                parentWindow.showNotification("Nie wybrano dnia lub klasy!");
            }
        }
    }
    
    class SaveListener implements ClickListener
    {
        private Button b = null;
        private Object[] item = null;
        public SaveListener(Button b)
        {
            this.b = b;
        }
        public SaveListener(Button b, Object[] item)
        {
            this.b = b;
            this.item = item;
        }
        @Override
        public void buttonClick(ClickEvent event) 
        {
            if(selectClass.getValue() != null && selectDay.getValue() != null)
            {                
                Integer lessonNumber = (Integer) item[0];
                String subjectName = item[2].toString();
                String teacherName = item[3].toString();
                String room = item[4].toString();
                
                boolean res = false;
                if(!subjectName.equals("Brak") && !subjectName.isEmpty() && !teacherName.equals("Brak") && !teacherName.isEmpty())
                {
                    res = hibernate.updateLesson(lessonNumber, us.getSchoolId(), hibernate.getClassId(selectClass.getValue().toString(),us.getSchoolId()), selectDay.getValue().toString(), hibernate.getTeacherId(teacherName), hibernate.getSubjectId(subjectName, us.getSchoolId()), room);
                }
                
                if(res == true)
                {
                    //parentWindow.showNotification("Zapisano.",Window.Notification.TYPE_HUMANIZED_MESSAGE);
                    notification.setCaption("Zapisano.");
                    notification.setDelayMsec(MyApplication.delay);
                    parentWindow.showNotification(notification);
                }
                else
                {
                    parentWindow.showNotification("Błąd w zapisywaniu.",Window.Notification.TYPE_ERROR_MESSAGE);
                }                
            }
            else
            {
                parentWindow.showNotification("Nie wybrano dnia lub klasy!");
            }
        }
        public void setItemValue(Object[] item)
        {
            this.item = item;
        }        
    }
}
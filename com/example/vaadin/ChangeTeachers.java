/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

/**
 *
 * @author Asterio
 */
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import dbclass.SchoolSubjectTeacher;
import dbclass.SchoolTeacher;
import hibernate.Hibernate;
import java.util.Iterator;
import java.util.Set;

public class ChangeTeachers extends CustomComponent implements ClickListener
{
    private UserSession us = null;
    
    private Panel panel = null;
    
    private Button execute = null;
    
    private CheckBox withSubject = null;
    private CheckBox withClass = null;
    
    private Label classLabel = null;
    private Label infoSubject = new Label("Wybierz przedmiot:");
    private Label infoClass = new Label("Obecna klasa: ");
    
    private Select selectSubject = null;    
    private Select oldTeacher = null;
    private Select newTeacher = null;
    
    private Window parentWindow = null;
    
    private Label labelResult = null;
    
    private OptionGroup optionGroup = null;
    
    private Hibernate hibernate = null;
    private Notification notification = new Notification("");
    
    public ChangeTeachers(Hibernate hibernate, Window parentWindow,UserSession us)
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
        newTeacher = new Select();
        oldTeacher = new Select();
        labelResult = new Label("");
        execute = new Button("Wykonaj");
        withSubject = new CheckBox();
        withClass = new CheckBox();
        selectSubject = new Select();
        optionGroup = new OptionGroup("");
        optionGroup.addItem("Wszystkie przedmioty");
        optionGroup.addItem("Wybrany przedmiot");
        optionGroup.addItem("Żadnego");
        optionGroup.select("Żadnego");
        
        selectSubject.setVisible(false);
        infoSubject.setVisible(false);
        infoClass.setVisible(false);
        
        GridLayout horizontal = new GridLayout(4,5);
        horizontal.addComponent(new Label("Wybierz nauczyciela do zmiany:"));
        horizontal.addComponent(oldTeacher);
        horizontal.addComponent(new Label(""));
        horizontal.addComponent(new Label(""));
        horizontal.addComponent(new Label("Nowy nauczyciel:"));
        horizontal.addComponent(newTeacher);
        horizontal.addComponent(new Label(""));
        horizontal.addComponent(new Label(""));

        //horizontal.addComponent(new Label("Konkretny przedmiot"));
        horizontal.addComponent(new Label("Zamień:"));
        horizontal.addComponent(optionGroup);
        //horizontal.addComponent(withSubject);
        horizontal.addComponent(infoSubject);
        horizontal.addComponent(selectSubject);
        horizontal.addComponent(new Label("Zmień wychowawcę:"));
        horizontal.addComponent(withClass);
        horizontal.addComponent(infoClass);
        horizontal.addComponent(new Label(""));
        
        //horizontal.addComponent(new Label("Zamień:"));
        //horizontal.addComponent(optionGroup);
        panel.addComponent(horizontal);
        initSelectTeacher();
        initCheckClass();
        initCheckSubject();
        initOldTeacherListener();
        initNewTeacherListener();
        initOptionGroupListener();
        
        panel.addComponent(execute);
        panel.addComponent(labelResult);
        
        execute.addListener(this);
    }
    
    private void initOptionGroupListener()
    {
        optionGroup.setImmediate(true);
        optionGroup.addListener(new ValueChangeListener()
        {
            @Override
            public void valueChange(ValueChangeEvent event) 
            {
                if(event.getProperty().getValue().toString().equals("Wybrany przedmiot"))
                {
                    selectSubject.setVisible(true);
                    infoSubject.setVisible(true);
                }
                else
                {
                    selectSubject.setVisible(false);
                    infoSubject.setVisible(false);
                }
            }            
        });
    }
    
    private void initNewTeacherListener()
    {
        newTeacher.setImmediate(true);
        newTeacher.addListener(new ValueChangeListener()
        {
            @Override
            public void valueChange(ValueChangeEvent event) 
            {
                changeNewTeacher();
            }
        });
    }
    
    private void changeNewTeacher()
    {
        if(withClass.booleanValue() == true)
        {
            if(newTeacher.getValue() != null)
            {
                boolean res = hibernate.isTutor(hibernate.getTeacherId(newTeacher.getValue().toString()));
                if(res == true)
                {
                    execute.setEnabled(false);
                    execute.setDescription(newTeacher.getValue() + " jest już wychowcą innej klasy.");
                }
                else
                {
                    execute.setEnabled(true);
                    execute.setDescription("");
                }
            }
        }
        else
        {
            execute.setEnabled(true);
            execute.setDescription("");
        }
    }
    
    private void initOldTeacherListener()
    {
        oldTeacher.setImmediate(true);
        oldTeacher.addListener(new ValueChangeListener() 
        {
            @Override
            public void valueChange(ValueChangeEvent event) 
            {
                if(event.getProperty().getValue() != null)
                {
                    changeSelectSubject(hibernate.getTeacherSubjects(hibernate.getTeacherId(event.getProperty().getValue().toString())));
                    initSelectNewTeacher(event.getProperty().getValue().toString());
                    changeInfoClass();
                }
            }
        });
    }
    private void initSelectNewTeacher(String oldTeacher) 
    {
        newTeacher.removeAllItems();
        for(Iterator iter = hibernate.getTeachers(us.getSchoolId()).iterator();iter.hasNext();)
        {
            Object nextItem = iter.next();
            if(nextItem instanceof SchoolTeacher)
            {
                SchoolTeacher st = (SchoolTeacher) nextItem;  
                if(!oldTeacher.equals(st.getTeacherName() + " " + st.getTeacherSurname()))
                {
                    newTeacher.addItem(st.getTeacherName() + " " + st.getTeacherSurname());
                }
            }
        }
    }
    
    private void changeSelectSubject(Set items)
    {
        selectSubject.removeAllItems();
        for(Iterator iter = items.iterator(); iter.hasNext();)
        {
            SchoolSubjectTeacher sst = (SchoolSubjectTeacher) iter.next();
            selectSubject.addItem(sst.getSchoolSubject().getSubjectName());
        }
    }
    
    private void initCheckClass()
    {
        withClass.setImmediate(true);
        withClass.addListener(new ValueChangeListener() 
        {

            @Override
            public void valueChange(ValueChangeEvent event) 
            {
                infoClass.setVisible(withClass.booleanValue());
                changeInfoClass();
                changeNewTeacher();
            }
        });
    }
    
    private void changeInfoClass()
    {
        if(withClass.booleanValue() == true && oldTeacher.getValue() != null)
        {
            String result = hibernate.getClassName(hibernate.getTeacherId(oldTeacher.getValue().toString()));
            if(result != null)
            {
                infoClass.setValue("Obecna klasa: " + result);
            }
            else
            {
                infoClass.setValue("Obecna klasa: brak");
            }
        }
    }
    
    private void initCheckSubject()
    {
        withSubject.setImmediate(true);
        withSubject.addListener(new ValueChangeListener() 
        {
            @Override
            public void valueChange(ValueChangeEvent event) 
            {
                selectSubject.setVisible(withSubject.booleanValue());
                infoSubject.setVisible(withSubject.booleanValue());
                if(withSubject.booleanValue() == false)
                {
                    selectSubject.unselect(selectSubject.getValue());                    
                }
            }
        });
    }
    
    

    @Override
    public void buttonClick(ClickEvent event) 
    {
        
        if(oldTeacher.getValue() != null && newTeacher.getValue() != null)
        {
            boolean res = false;
            String result = "";
            Integer classId = null;
            Integer subjectId = null;
            Integer oldTeacherId = hibernate.getTeacherId(oldTeacher.getValue().toString());

            if(withClass.booleanValue() == true)
            {
                classId = hibernate.getClassIdFromTeacher(oldTeacherId);
                //res = hibernate.switchTeachers(oldTeacherId,hibernate.getTeacherId(newTeacher.getValue().toString()), us.getSchoolId(), hibernate.getClassIdFromTeacher(oldTeacherId), hibernate.getSubjectId(selectSubject.getValue().toString(), us.getSchoolId()));
            }
            if(optionGroup.getValue() != null)
            {
                if(optionGroup.getValue().toString().equals("Wybrany przedmiot"))
                {
                    if(selectSubject.getValue() != null)
                    {
                        subjectId = hibernate.getSubjectId(selectSubject.getValue().toString(), us.getSchoolId());
                    }
                }
                else if(optionGroup.getValue().toString().equals("Wszystkie przedmioty"))
                {
                    subjectId = -1;
                }
            }

            res = hibernate.switchTeachers(oldTeacherId,hibernate.getTeacherId(newTeacher.getValue().toString()), us.getSchoolId(), classId , subjectId);
            
            if(res == true)
            {
                if(subjectId == null)
                {
                    notification.setCaption("Nie wybrano przedmiotów do zamiany");
                }
                else
                {
                    notification.setCaption("Zamieniono nauczycieli." +"<br />" + result);
                }
                
                notification.setDelayMsec(MyApplication.delay);                
                parentWindow.showNotification(notification);
            }
            else
            {
                parentWindow.showNotification("Błąd w dodawaniu.",Window.Notification.TYPE_ERROR_MESSAGE);
            }
            
            
            /*
             * dla checkboxa
             * 
            if(selectSubject.getValue() != null && withClass.booleanValue() == true && withSubject.booleanValue() == true)
            {
                res = hibernate.switchTeachers(oldTeacherId,hibernate.getTeacherId(newTeacher.getValue().toString()), us.getSchoolId(), hibernate.getClassIdFromTeacher(oldTeacherId), hibernate.getSubjectId(selectSubject.getValue().toString(), us.getSchoolId()));
                System.out.println("OldTeacher: " + oldTeacherId + " NewTeacher: " + hibernate.getTeacherId(newTeacher.getValue().toString()) + " ClassId: " + hibernate.getClassIdFromTeacher(oldTeacherId) + " SubjectId: " + hibernate.getSubjectId(selectSubject.getValue().toString(), us.getSchoolId()));
                result = "Zmieniono podany przedmiot: " + selectSubject.getValue() + "<br />" + "Zmieniono wychowawcę";
            }
            else if(selectSubject.getValue() != null && withSubject.booleanValue() == true)
            {
                System.out.println("OldTeacher: " + oldTeacherId + " NewTeacher: " + hibernate.getTeacherId(newTeacher.getValue().toString()) + " SubjectId: " + hibernate.getSubjectId(selectSubject.getValue().toString(), us.getSchoolId()));
                res = hibernate.switchTeachers(oldTeacherId,hibernate.getTeacherId(newTeacher.getValue().toString()), us.getSchoolId(), null, hibernate.getSubjectId(selectSubject.getValue().toString(), us.getSchoolId()));
                result = "Zmieniono podany przedmiot: " + selectSubject.getValue();
            }
            else if(withClass.booleanValue() == true)
            {
                System.out.println("OldTeacher: " + oldTeacherId + " NewTeacher: " + hibernate.getTeacherId(newTeacher.getValue().toString()) + " ClassId: " + hibernate.getClassIdFromTeacher(oldTeacherId) );
                res = hibernate.switchTeachers(oldTeacherId,hibernate.getTeacherId(newTeacher.getValue().toString()), us.getSchoolId(), hibernate.getClassIdFromTeacher(oldTeacherId), null);
                result = "Zmieniono wszystkie przedmioty <br />" + "Zmieniono wychowawcę";
            }
            else
            {
                System.out.println("OldTeacher: " + oldTeacherId + " NewTeacher: " + hibernate.getTeacherId(newTeacher.getValue().toString()));
                res = hibernate.switchTeachers(oldTeacherId,hibernate.getTeacherId(newTeacher.getValue().toString()), us.getSchoolId(), null, null);
                result = "Zmieniono wszystkie przedmioty";
            }
            if(res == true)
            {
                notification.setCaption("Zamieniono nauczycieli." +"<br />" + result);
                notification.setDelayMsec(MyApplication.delay);                
                parentWindow.showNotification(notification);
            }
            else
            {
                parentWindow.showNotification("Błąd w dodawaniu.",Window.Notification.TYPE_ERROR_MESSAGE);
            }*/
        }
        else
        {
            parentWindow.showNotification("Nie wypełniono wszystkich pól.",Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }
    private void initSelectTeacher()
    {
        for(Iterator iter = hibernate.getTeachers(us.getSchoolId()).iterator();iter.hasNext();)
        {
            Object nextItem = iter.next();
            if(nextItem instanceof SchoolTeacher)
            {
                SchoolTeacher st = (SchoolTeacher) nextItem;                
                oldTeacher.addItem(st.getTeacherName() + " " + st.getTeacherSurname());
            }
        }
    }
}
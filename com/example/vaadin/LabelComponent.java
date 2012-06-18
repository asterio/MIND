/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;

/**
 *
 * @author Asterio
 */
public final class LabelComponent extends CustomComponent
{
    private Label id = null;
    private Label hour = null;
    private Label output = null;
    private Select teacherSelect = null;
    private Select subjectSelect = null;
    private TextField room = null;
    private HorizontalLayout layout = null;
    
    private Panel panel;
    
    public LabelComponent(String idVal, String hourVal, Select teacherValue, Select subjectValue)
    {
        panel = new Panel();
        layout = new HorizontalLayout();
        id = new Label();
        id.setValue(idVal);
        hour = new Label();
        hour.setValue("\tGodziny: " + hourVal);
        output = new Label("");
        room = new TextField();
        teacherSelect = new Select();
        teacherSelect = teacherValue;
        teacherSelect.setDescription("Wybierz nauczyciela");
        subjectSelect = new Select();
        subjectSelect = subjectValue;
        subjectSelect.setDescription("Wybierz przedmiot");
        room.setDescription("Podaj salę");
        
        panel.addComponent(layout);
        layout.setWidth(Sizeable.SIZE_UNDEFINED,0);
        layout.addComponent(new Label("Numer lekcji:"));
        layout.addComponent(id);
        layout.addComponent(hour);
        layout.addComponent(teacherSelect);
        layout.addComponent(subjectSelect);
        layout.addComponent(room);
        layout.addComponent(output);
        setCompositionRoot(panel);
        
        
    }
    
    public void setLabelOutput(String value)
    {
        output.setValue(value);
    }
    
    public String getId()
    {
        if(id != null)
        {
            return id.getValue().toString();
        }
        return null;
    }
    public String getRoom()
    {
        if(room != null)
        {
            return room.getValue().toString();
        }
        return null;
    }
    public String getTeacher()
    {
        if(teacherSelect != null)
        {
            if(teacherSelect.getValue() != null)
            {
                return teacherSelect.getValue().toString();
            }
        }
        return null;
    }
    public String getSubject()
    {
        if(subjectSelect != null)
        {
            if(subjectSelect.getValue() != null)
            {
                return subjectSelect.getValue().toString();
            }
        }
        return null;
    }
    
    /*public void initValues()
    {
        List result = hibernate.getLesson("1");
        SchoolLesson sl = (SchoolLesson) result.get(0);
        id.setValue(sl.getSchoolLessonNumber().getId().getLessonNumberId());
        hour.setValue(sl.getSchoolLessonNumber().getStartTime() + "-" + sl.getSchoolLessonNumber().getEndTime());
        room.setValue(sl.getRoom());
        
        for(Iterator iter = hibernate.getTeachers("1").iterator();iter.hasNext();)
        {
            SchoolTeacher st = (SchoolTeacher) iter.next();
            Object itemId = teacherSelect.addItem();
            teacherSelect.setItemCaption(itemId, st.getTeacherName() + " " + st.getTeacherSurname());            
        }
    }*/

    
    
}

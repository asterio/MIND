/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

import com.vaadin.ui.*;
import dbclass.SchoolSubjectTeacher;
import dbclass.SchoolTeacher;
import hibernate.Hibernate;
import java.util.Iterator;
import java.util.Set;

public class ShowTeachers extends CustomComponent
{
    private UserSession us = null;
    
    private Panel panel = null;
    
    private Hibernate hibernate = null;
    
    private Table table = null;
    
    public ShowTeachers(Hibernate hibernate,UserSession us)
    {
        this.us = us;
        panel = new Panel();
        panel.setWidth("80%");
        panel.setHeight("80%");
        table = new Table();
        
        this.hibernate = hibernate;
        
        table.addContainerProperty("Nazwisko", String.class, null);
        table.addContainerProperty("Imię",String.class, null);
        table.addContainerProperty("Prowadzone przedmioty",String.class, null);
        initTable();
        panel.addComponent(table);
        
        this.setCompositionRoot(panel);
    }
    private void initTable()
    {
        Set teachers = hibernate.getTeachers(us.getSchoolId());
        int i = 0;
        for(Iterator iter = teachers.iterator(); iter.hasNext();)
        {
            SchoolTeacher st = (SchoolTeacher) iter.next();
            StringBuilder sb = new StringBuilder();
            for(Iterator iter1 = st.getSchoolSubjectTeachers().iterator(); iter1.hasNext();)
            {
                SchoolSubjectTeacher sst = (SchoolSubjectTeacher) iter1.next();
                sb.append(sst.getSchoolSubject().getSubjectName()).append(" ");
            }
            table.addItem(new Object[] {st.getTeacherName(),st.getTeacherSurname(),sb}, i);
            i++;
        }
    }
}

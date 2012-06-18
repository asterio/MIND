/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

import com.vaadin.ui.*;
import dbclass.SchoolClass;
import hibernate.Hibernate;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Asterio
 */
class ShowClasses extends CustomComponent 
{
    private UserSession us = null;
    
    private Panel panel = null;
    
    private Hibernate hibernate = null;
    
    private Table table = null;
    
    public ShowClasses(Hibernate hibernate,UserSession us) 
    {
        this.us = us;
        this.hibernate = hibernate;
        panel = new Panel();
        table = new Table();
        
        table.addContainerProperty("Nazwa klasy", String.class, null);
        table.addContainerProperty("Wychowawca", String.class, "Brak");
        initTable();
        panel.addComponent(table);
        panel.setWidth("80%");
        panel.setHeight("80%");
        this.setCompositionRoot(panel);
    }
    
    private void initTable()
    {
        List classes = hibernate.getClasses(us.getSchoolId());
        int i = 0;
        for(Iterator iter = classes.iterator(); iter.hasNext();)
        {
            SchoolClass sc = (SchoolClass) iter.next();
            if(sc.getSchoolTeacher() != null)
            {
                table.addItem(new Object[] {sc.getClassName(), sc.getSchoolTeacher().getTeacherName() + " " + sc.getSchoolTeacher().getTeacherSurname()}, i);
            }
            else
            {
                table.addItem(new Object[] {sc.getClassName(),"Brak"}, i);
            }
            i++;
        }
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

import com.vaadin.ui.*;
import dbclass.SchoolSubject;
import hibernate.Hibernate;
import java.util.Iterator;

/**
 *
 * @author Asterio
 */
public class ShowSubjects extends CustomComponent
{
    private UserSession us = null;
    
    private Panel panel = null;
    
    private Table table = null;
    
    private Hibernate hibernate = null;
    
    public ShowSubjects(Hibernate hibernate,UserSession us)
    {
        this.us = us;
        this.hibernate = hibernate;
        
        init();
        
        this.setCompositionRoot(panel);
    }
    
    private void init()
    {
        panel = new Panel();
        table = new Table();
        table.addContainerProperty("Nazwa przedmiotu", String.class, null);
        initTable();
        
        panel.addComponent(table);
        
        
    }
    private void initTable()
    {
        int i = 0;
        for(Iterator iter = hibernate.getSubjects().iterator(); iter.hasNext();)
        {
            SchoolSubject ss = (SchoolSubject) iter.next();
            table.addItem(new Object[] {ss.getSubjectName() },i);
            i++;
        }
    }
}

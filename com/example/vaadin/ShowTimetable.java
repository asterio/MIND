/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.vaadin;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import dbclass.SchoolLesson;
import hibernate.Hibernate;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Asterio
 */
public class ShowTimetable extends CustomComponent
{
    private UserSession us = null;
    
    private TabSheet tabSheet = null;

    private SplitPanel horizontal = null;
    
    private Panel treePanel = null;

    private VerticalLayout poniedzialek = null;
    private VerticalLayout wtorek = null;
    private VerticalLayout sroda = null;
    private VerticalLayout czwartek = null;
    private VerticalLayout piatek = null;
    
    private String prevItem = "";

    private Tree tree = null;
    
    private Table timetable = null;

    private Label labelLessons = null;

    private Panel panel = new Panel();
    
    private Hibernate hibernate = null;

    public ShowTimetable(Hibernate hibernate,UserSession us)
    {
        this.us = us;
        this.hibernate = hibernate;
        tabSheet = new TabSheet();
        horizontal = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
        
        timetable = new Table();
        timetable.setWidth("100%");
        timetable.setHeight("100%");
        timetable.addContainerProperty("Numer lekcji",String.class,null);
        timetable.addContainerProperty("Godziny",String.class,null);
        timetable.addContainerProperty("Przedmiot",String.class,null);
        timetable.addContainerProperty("Sala",String.class,null);
        timetable.addContainerProperty("Nauczyciel",String.class,null);

        labelLessons = new Label("");

        poniedzialek = new VerticalLayout();
        wtorek = new VerticalLayout();
        sroda = new VerticalLayout();
        czwartek = new VerticalLayout();
        piatek = new VerticalLayout();

        tabSheet.addTab(poniedzialek,"Poniedziałek");
        tabSheet.addTab(wtorek,"Wtorek");
        tabSheet.addTab(sroda,"Środa");
        tabSheet.addTab(czwartek,"Czwartek");
        tabSheet.addTab(piatek,"Piątek");
        
        
        
        tabSheet.setHeight("600px");
        tabSheet.setWidth("100%");
        
        poniedzialek.addComponent(labelLessons);
        poniedzialek.setCaption("1");
        wtorek.setCaption("2");
        sroda.setCaption("3");
        czwartek.setCaption("4");
        piatek.setCaption("5");

        wtorek.addComponent(labelLessons);
        sroda.addComponent(labelLessons);
        czwartek.addComponent(labelLessons);
        piatek.addComponent(labelLessons);


        tabSheet.addListener(new TabSheet.SelectedTabChangeListener() 
        {

            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) 
            {
                if((TabSheet) event.getSource() != null && tree.getValue() != null)
                {
                    switch(Integer.parseInt(((TabSheet)event.getSource()).getSelectedTab().getCaption()))
                    {
                        case 1:
                            poniedzialek.addComponent(timetable);
                            break;
                        case 2:
                            wtorek.addComponent(timetable);
                            break;
                        case 3:
                            sroda.addComponent(timetable);
                            break;
                        case 4:
                            czwartek.addComponent(timetable);
                            break;
                        case 5:
                            piatek.addComponent(timetable);
                            break;
                    }
                    changeTab(((TabSheet)event.getSource()).getSelectedTab().getCaption(),tree.getValue().toString());
                }
            }
        });

        initTree();

        horizontal.setFirstComponent(treePanel);
        horizontal.setSecondComponent(tabSheet);
        horizontal.setLocked(true);
        horizontal.setSplitPosition(20,Sizeable.UNITS_PERCENTAGE);
        

        panel.setContent(horizontal);
        panel.setWidth("100%");
        horizontal.setWidth("100%");
        horizontal.setHeight(100, Sizeable.UNITS_PERCENTAGE);

        setCompositionRoot(panel);
    }

    private void changeTab(String dayId, String className)
    {
        Integer classId = hibernate.getClassId(className, us.getSchoolId());
        
        if(classId != null)
        {
            timetable.removeAllItems();
            List result = hibernate.getDayTimetable(dayId, ""+classId);
            if(result != null)
            {
                for(Iterator iter = result.iterator(); iter.hasNext();)
                {
                    String teacherSub = "Brak";
                    String subjectSub = "Brak";
                    SchoolLesson sl = (SchoolLesson) iter.next();
                    
                    if(sl.getSchoolSubject() != null)
                    {
                        subjectSub = sl.getSchoolSubject().getSubjectName();
                    }
                    
                    if(sl.getSchoolTeacher() != null)
                    {
                        teacherSub = sl.getSchoolTeacher().getTeacherName() + " " + sl.getSchoolTeacher().getTeacherSurname();
                    }
                    
                    timetable.addItem(new Object[] {sl.getSchoolLessonNumber().getId().getLessonNumberId().toString(),sl.getSchoolLessonNumber().getStartTime().toString()+"-"+sl.getSchoolLessonNumber().getEndTime().toString(),subjectSub,sl.getRoom().toString(),teacherSub}, new Integer(sl.getSchoolLessonNumber().getId().getLessonNumberId()));        
                
                }
            }
        }
    }

    private void initTree()
    {
        treePanel = new Panel();
        treePanel.addComponent(new Label("Wybierz klasę"));
        
        tree = new Tree("");
        tree.setHeight("600px");
        treePanel.addComponent(tree);

        for(Iterator iter = hibernate.getAllClasses(us.getSchoolId()).iterator(); iter.hasNext();)
        {
            List items = (List) iter.next();
            if(items.size() > 0)
            {
                String mainItem = items.get(0).toString().split(" ")[0];
                tree.addItem(mainItem);
                for(Iterator iter1 = items.iterator(); iter1.hasNext();)
                {
                    String subItem = (String) iter1.next();
                    tree.addItem(subItem);
                    tree.setParent(subItem,mainItem);
                    tree.setChildrenAllowed(subItem,false);
                }
            }
        }
        
        tree.addListener(new ItemClickListener()
        {

            @Override
            public void itemClick(ItemClickEvent event) 
            {
                if(event.getItem() != null)
                {
                    if(tree.isExpanded(event.getItemId()))
                    {
                        tree.collapseItem(event.getItemId());
                    }
                    else
                    {
                        tree.expandItem(event.getItemId());
                    }
                }
            }
            
        });
        
        tree.addListener(new Property.ValueChangeListener() 
        {
            @Override
            public void valueChange(ValueChangeEvent event) 
            {
                timetable.removeAllItems();
                
                if(event.getProperty().getValue() != null)
                {
                    
                    if(tabSheet.getSelectedTab().getCaption() != null && event.getProperty().getValue() != null)
                    {
                        switch(Integer.parseInt(tabSheet.getSelectedTab().getCaption()))
                        {
                            case 1:
                                poniedzialek.addComponent(timetable);
                                break;
                            case 2:
                                wtorek.addComponent(timetable);
                                break;
                            case 3:
                                sroda.addComponent(timetable);
                                break;
                            case 4:
                                czwartek.addComponent(timetable);
                                break;
                            case 5:
                                piatek.addComponent(timetable);
                                break;
                        }
                        changeTab(tabSheet.getSelectedTab().getCaption(),event.getProperty().getValue().toString());
                    }
                }
            }
        });
        tree.setImmediate(true);
    }
}
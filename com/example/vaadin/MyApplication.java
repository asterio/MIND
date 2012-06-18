/*
 * MyApplication.java
 *
 * Created on 20 luty 2012, 09:18
 */
 
package com.example.vaadin;           

import com.vaadin.Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.*;
import com.vaadin.data.*;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;
import hibernate.Hibernate;
/** 
 *
 * @author Asterio
 * @version 
 */



public class MyApplication extends Application 
{
    private Hibernate hibernate = new Hibernate();
    
    private Label label = new Label("");    
    
    private Window teachersWindow = null;
    private Window classesWindow = null;
    private Window tabWindow = null;
    
    private VerticalLayout vertical = null;
    
    private AddClass addClass = null;
    private AddSubject addSubject = null;
    private AddTeacher addTeacher = null;
    private AddTimetable addTimetable = null;
    
    private RemoveClass removeClass = null;
    private RemoveSubject removeSubject = null;
    private RemoveTeacher removeTeacher = null;
    private RemoveTimetable removeTimetable = null;
    
    public static int delay = 2000;
    
    private ShowClasses showClasses = null;
    private ShowSubjects showSubjects = null;
    private ShowTimetable showTimetable = null;
    private Notification n = null;
    
    private Window mainWindow = null;
    
    private String prevItem = "";
    
    private Button logout = null;
    
    private SplitPanel splitter = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
    
    private UserSession us = null;
    
    private void initTreePanel()
    {
        Panel treePanel = new Panel();
        
        logout = new Button("Wyloguj");
        
        logout.addListener(new Button.ClickListener()
        {

            @Override
            public void buttonClick(ClickEvent event) 
            {
                getMainWindow().getApplication().close();
                us = null;
            }
            
        });
        treePanel.addComponent(new Label("Zalogowany jako: " + us.getName()));
        treePanel.addComponent(logout);       
        
        
        final Tree tree = new Tree("AdminPanel");
        Object [][] treeItems = new Object[][]
        {
            new Object[] {"Plan zajęć","Wyświetl plan","Dodaj plan","Edytuj plan","Usuń plan"},
            new Object[] {"Klasy","Wykaz klas","Dodaj klasę","Dodaj wychowawcę","Usuń klasę"},
            new Object[] {"Nauczyciele","Wykaz nauczycieli","Dodaj nauczyciela","Zamień nauczycieli","Usuń nauczyciela"},
            new Object[] {"Przedmioty","Wykaz przedmiotów","Dodaj przedmiot","Przypisz nauczyciela"}
        };
        for(int i = 0; i < treeItems.length; i++)
        {
            String mainItem = (String) (treeItems[i][0]);
            tree.addItem(mainItem);
            if(treeItems[i].length == 1)
            {
                tree.setChildrenAllowed(mainItem, false);
            }
            else
            {
                for(int j = 1; j < treeItems[i].length; j++)
                {
                    String subItem = (String) treeItems[i][j];
                    tree.addItem(subItem);
                    if(subItem.equals("Przypisz..."))
                    {
                     //   tree.setItemDescriptionGenerator(new ItemDescriptionGenerator( "Przypisz nauczyciela do przedmiotu"));
                    }
                    
                    tree.setParent(subItem, mainItem);
                    tree.setChildrenAllowed(subItem, false);
                }
            }                    
        }
        treePanel.addComponent(tree);
        
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
                if(event.getProperty().getValue() != null)
                {
                    if(event.getProperty().getValue().toString().equals("Wyświetl plan"))
                    {
                        splitter.setSecondComponent(new ShowTimetable(hibernate,us));
                    }
                    else if(event.getProperty().getValue().toString().equals("Wykaz nauczycieli"))
                    {
                        splitter.setSecondComponent(new ShowTeachers(hibernate,us));                 
                    }
                    else if(event.getProperty().getValue().toString().equals("Wykaz klas"))
                    {
                        splitter.setSecondComponent(new ShowClasses(hibernate,us));
                    }
                    else if(event.getProperty().getValue().toString().equals("Dodaj plan"))
                    {
                        splitter.setSecondComponent(new AddTimetable(hibernate,mainWindow,us));
                    }
                    else if(event.getProperty().getValue().toString().equals("Dodaj nauczyciela"))
                    {
                        splitter.setSecondComponent(new AddTeacher(hibernate,mainWindow,us));
                    }
                    else if(event.getProperty().getValue().toString().equals("Dodaj klasę"))
                    {
                        splitter.setSecondComponent(new AddClass(hibernate,mainWindow,us));
                    }
                    else if(event.getProperty().getValue().toString().equals("Usuń plan"))
                    {
                        splitter.setSecondComponent(new RemoveTimetable(hibernate,mainWindow,us));
                    }
                    else if(event.getProperty().getValue().toString().equals("Usuń klasę"))
                    {
                        splitter.setSecondComponent(new RemoveClass(hibernate,mainWindow,us));
                    }
                    else if(event.getProperty().getValue().toString().equals("Usuń nauczyciela"))
                    {
                        splitter.setSecondComponent(new RemoveTeacher(hibernate,mainWindow,us));
                    }
                    else if(event.getProperty().getValue().toString().equals("Wykaz przedmiotów"))
                    {
                        splitter.setSecondComponent(new ShowSubjects(hibernate,us));
                    }
                    else if(event.getProperty().getValue().toString().equals("Dodaj przedmiot"))
                    {
                        splitter.setSecondComponent(new AddSubject(hibernate,mainWindow,us));
                    }
                    else if(event.getProperty().getValue().toString().equals("Zamień nauczycieli"))
                    {
                        splitter.setSecondComponent(new ChangeTeachers(hibernate,mainWindow,us));
                    }
                    else if(event.getProperty().getValue().toString().equals("Edytuj plan"))
                    {
                        splitter.setSecondComponent(new EditTimetable(hibernate,mainWindow,us));
                    }
                    else if(event.getProperty().getValue().toString().equals("Przypisz nauczyciela"))
                    {
                        splitter.setSecondComponent(new AddTeacherToSubject(hibernate,mainWindow,us));
                    }
                    else if(event.getProperty().getValue().toString().equals("Dodaj wychowawcę"))
                    {
                        splitter.setSecondComponent(new AddTeacherToClass(hibernate,mainWindow,us));
                    }
                }
            }
        });
        tree.setImmediate(true);
       
        splitter.setFirstComponent(treePanel);
        splitter.setSecondComponent(new Label(""));
    }
    
    @Override
    public void init() 
    {
        n = new Notification("Witaj, poprawnie zalogowano");
        n.setDelayMsec(delay);
	mainWindow = new Window("StudentBox");
        
        mainWindow.setContent(splitter);
        splitter.setLocked(true);
        splitter.setSplitPosition(20, Sizeable.UNITS_PERCENTAGE);
        
        initSession();
        

	setMainWindow(mainWindow);
        
        mainWindow.addListener(new CloseListener()
        {

            @Override
            public void windowClose(CloseEvent e) 
            {
                getMainWindow().getApplication().close();
                us = null;
            }
            
        });
    }
    private void initSession()
    {
        if(us == null)
        {
            splitter.setFirstComponent(new Label("Musisz się zalogować, aby mieć dostęp."));
            LoginForm loginForm = new LoginForm();
            
            loginForm.setPasswordCaption("Hasło");
            loginForm.setUsernameCaption("Użytkownik");
            loginForm.setLoginButtonCaption("Zaloguj");
            
            final Window loginWindow = new Window();
            loginWindow.setResizable(false);
            loginWindow.setCaption("Logowanie");
            loginWindow.setClosable(false);
            
            loginWindow.setWidth("300px");
            loginWindow.addComponent(loginForm);
            
            loginForm.setImmediate(true);
            
            loginForm.addListener(new LoginListener()
            {
                @Override
                public void onLogin(LoginEvent event) 
                {
                    String username = event.getLoginParameter("username");
                    String pass = event.getLoginParameter("password");
                    
                    String res = hibernate.login(username, pass);
                    
                    
                    //if(username.equals("admin") && pass.equals("admin"))
                    if(res != null)
                    {
                        if(!res.isEmpty())
                        {
                            //loginWindow.showNotification("Witaj, poprawnie zalogowano",Window.Notification.DELAY_FOREVER);
                            us = new UserSession(res,username,true);
                            mainWindow.removeWindow(loginWindow);
                            initTreePanel();
                            
                            mainWindow.showNotification(n);
                        }
                    }                    
                    else
                    {
                        
                        loginWindow.showNotification("Podane dane są nieprawidłowe!<br />","Spróbuj jeszcze raz..." , Window.Notification.TYPE_ERROR_MESSAGE);
                    }
                }                
            });
            
            mainWindow.addWindow(loginWindow);
        }
        else
        {
            
        }
    }
}

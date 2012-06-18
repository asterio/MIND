/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernate;

import dbclass.SchoolLesson;
import dbclass.SchoolSubjectTeacher;
import dbclass.SchoolTeacher;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Asterio
 */
public class Test 
{
    public Hibernate hibernate = null;
    
    public Test()
    {
        hibernate = new Hibernate();
    }
    
    public static void main(String[] args)
    {
        Test t = new Test();
        
        System.out.println(t.hibernate.getAllClasses("1").size());
        
        for(Iterator iter = t.hibernate.getAllClasses("1").iterator(); iter.hasNext();)
        {
            List items = (List) iter.next();
            if(items.size() > 0)
            {
                String mainItem = items.get(0).toString().split(" ")[0];
                System.out.println(mainItem);
                for(Iterator iter1 = items.iterator(); iter1.hasNext();)
                {
                    String subItem = (String) iter1.next();
                    System.out.println("\t" + subItem);
                }
            }
        }
        
        //System.out.println(t.hibernate.getTeacherId("ZYGMUNT NOWAK"));
        
        /*Set teachers = t.hibernate.getTeachers("1");
        for(Iterator iter = teachers.iterator(); iter.hasNext();)
        {
            SchoolTeacher st = (SchoolTeacher) iter.next();
            StringBuilder sb = new StringBuilder();
            for(Iterator iter1 = st.getSchoolSubjectTeachers().iterator(); iter1.hasNext();)
            {
                SchoolSubjectTeacher sst = (SchoolSubjectTeacher) iter1.next();
                sb.append(sst.getSchoolSubject().getSubjectName());
            }
            System.out.println(st.getTeacherName() + "   " + sb);
        }*/
        
        
        /*List result = t.hibernate.getDayTimetable("1", "1");
            
            for(Iterator iter = result.iterator(); iter.hasNext();)
            {
                SchoolLesson sl = (SchoolLesson) iter.next();
                System.out.println(sl.getDayId() + " " + sl.getSchoolClass().getClassName() + " " + sl.getSchoolSubject().getSubjectName());
            }
        */
        /*List result = t.hibernate.getDayTimetable("1", "1");
        for(Iterator iter = result.iterator(); iter.hasNext(); )
        {
            SchoolLesson sl = (SchoolLesson) iter.next();
            System.out.println(sl.getDayId() + " " + sl.getLessonId() + " " + sl.getSchoolClass().getClassName());
        }*/
        
        /*for(Iterator iter = t.hibernate.getLesson("1").iterator(); iter.hasNext();)
        {
            SchoolLesson sl = (SchoolLesson) iter.next();
            System.out.println(sl.getSchoolLessonNumber().getId().getLessonNumberId() + " " + sl.getSchoolClass().getClassName() + " " + sl.getSchoolSubject().getSubjectName());
        }
        System.out.println(t.hibernate.getSchoolLessonNumbers("1").size());*/
    }
    
}

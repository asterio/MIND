/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernate;

import dbclass.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

/**
 *
 * @author Asterio
 * Dodawanie klasy
 * Wykaz klas
 * Usuwanie klas
 */
public class Hibernate 
{
    private Session session = null;
    private Transaction transaction = null;
    
    public Hibernate()
    {
        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();
        transaction.begin();
    }
    
    public boolean removeLesson(Integer lessonId, String schoolId, Integer classId, String dayId)
    {
        try
        {
            
            
            Criteria cr = session.createCriteria(SchoolLesson.class);
            School s = new School();
            s.setSchoolId(new Integer(schoolId));
            
            SchoolLessonNumberId slnId = new SchoolLessonNumberId();
            slnId.setLessonNumberId(lessonId);
            slnId.setSchoolId(new Integer(schoolId));
            
            SchoolLessonNumber sln = new SchoolLessonNumber();
            sln.setId(slnId);
            
            SchoolClass sc = new SchoolClass();
            sc.setClassId(classId);
            
            cr.add(Expression.eq("schoolClass", sc));
            cr.add(Expression.eq("schoolLessonNumber", sln));
            cr.add(Expression.eq("dayId", new Integer(dayId)));
            
            if(cr.list().size() > 0)
            {
                transaction.begin();
                SchoolLesson sl = (SchoolLesson) cr.list().get(0);
                session.delete(sl);
                transaction.commit();
                restart();
                return true;
            }
            
        }
        catch(Exception e)
        {
            
        }
        return false;
    }
    
    public boolean updateLesson(Integer lessonId, String schoolId, Integer classId, String dayId, Integer teacherId, Integer subjectId, String room)
    {
        try
        {
            transaction.begin();
            Criteria cr = session.createCriteria(SchoolLesson.class);
            SchoolLessonNumberId slnId = new SchoolLessonNumberId();
            slnId.setLessonNumberId(new Integer(lessonId));
            slnId.setSchoolId(new Integer(schoolId));
            
            SchoolLessonNumber sln = new SchoolLessonNumber();
            sln.setId(slnId);
            
            SchoolClass sc = new SchoolClass();
            sc.setClassId(classId);
            
            //System.err.println(classId + " " + lessonId + " " + dayId);
            
            cr.add(Expression.eq("schoolClass", sc));
            cr.add(Expression.eq("schoolLessonNumber", sln));
            cr.add(Expression.eq("dayId",new Integer(dayId)));
            
            if(cr.list().size() > 0)
            {
                SchoolLesson sl = (SchoolLesson) cr.list().get(0);
                SchoolSubject ss = new SchoolSubject();
                ss.setSubjectId(new Integer(subjectId));
                SchoolTeacher st = new SchoolTeacher();
                st.setTeacherId(teacherId);
                sl.setRoom(room.toUpperCase());
                sl.setSchoolSubject(ss);
                sl.setSchoolTeacher(st);
                
                session.save(sl);
                
            }
            transaction.commit();
            restart();
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public String login(String name, String pass)
    {
        try
        {
            //transaction.begin();
            Criteria cr = session.createCriteria(SchoolUser.class);
            cr.add(Expression.eq("login", name));
            cr.add(Expression.eq("pass", pass));
            
            if(cr.list().size() > 0)
            {
                return ((SchoolUser)cr.list().get(0)).getSchool().getSchoolId().toString();
            }
            
        }
        catch(Exception e)
        {
            
        }
        return null;
    }
    
    public Integer getClassIdFromTeacher(Integer teacherId)
    {
        try
        {
            Criteria cr = session.createCriteria(SchoolClass.class);
            SchoolTeacher st = new SchoolTeacher();
            st.setTeacherId(teacherId);
            cr.add(Expression.eq("schoolTeacher", st));
            
            if(cr.list().size() > 0)
            {
                SchoolClass sc = (SchoolClass) cr.list().get(0);
                return sc.getClassId();
            }
        }
        catch(Exception e)
        {
            
        }
        return null;
    }
    
    public boolean switchTeachers(Integer oldTeacherId, Integer newTeacherId, String schoolId, Integer classId, Integer subjectId)
    {
        try
        {
            //transaction = session.beginTransaction();
            
            Criteria cr = session.createCriteria(SchoolTeacher.class);
            School s = new School();
            s.setSchoolId(new Integer(schoolId));
            SchoolTeacher newTeacher = new SchoolTeacher();
            newTeacher.setSchool(s);
            newTeacher.setTeacherId(newTeacherId);
            cr.add(Expression.eq("teacherId", oldTeacherId));
            cr.add(Expression.eq("school", s));
            if(cr.list().size() > 0)
            {
                transaction.begin();
                SchoolTeacher st = (SchoolTeacher) cr.list().get(0);
                
                if(classId != null)
                {
                    for(Iterator iter = st.getSchoolClasses().iterator(); iter.hasNext();)
                    {
                        SchoolClass sc = (SchoolClass) iter.next();
                        sc.setSchoolTeacher(newTeacher);
                    }
                }
                
                for(Iterator iter = st.getSchoolLessons().iterator(); iter.hasNext();)
                {
                    SchoolLesson sl = (SchoolLesson) iter.next();
                    if(subjectId != null)
                    {
                        if(sl.getSchoolSubject().getSubjectId() == subjectId)
                        {
                            sl.setSchoolTeacher(newTeacher);
                        }                        
                    }
                    else
                    {
                        sl.setSchoolTeacher(newTeacher);
                    }
                }
                if(subjectId != null)
                {
                    for(Iterator iter = st.getSchoolSubjectTeachers().iterator(); iter.hasNext();)
                    {
                        SchoolSubjectTeacher sst = (SchoolSubjectTeacher) iter.next();
                        if(subjectId == -1)
                        {
                            sst.setSchoolTeacher(newTeacher);
                        }
                        else
                        {
                            if(sst.getSchoolSubject().getSubjectId() == subjectId)
                            {
                                sst.setSchoolTeacher(newTeacher);
                            }
                        }
                    }
                }

                transaction.commit();
                restart();
                return true;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean changeTeacher(Integer oldTeacherId, Integer newTeacherId, String schoolId)
    {
        try
        {
            //transaction = session.beginTransaction();
            
            Criteria cr = session.createCriteria(SchoolTeacher.class);
            School s = new School();
            s.setSchoolId(new Integer(schoolId));
            SchoolTeacher newTeacher = new SchoolTeacher();
            newTeacher.setSchool(s);
            newTeacher.setTeacherId(newTeacherId);
            cr.add(Expression.eq("teacherId", oldTeacherId));
            cr.add(Expression.eq("school", s));
            if(cr.list().size() > 0)
            {
                transaction.begin();
                SchoolTeacher st = (SchoolTeacher) cr.list().get(0);
                
                for(Iterator iter = st.getSchoolClasses().iterator(); iter.hasNext();)
                {
                    SchoolClass sc = (SchoolClass) iter.next();
                    sc.setSchoolTeacher(newTeacher);
                }
                for(Iterator iter = st.getSchoolLessons().iterator(); iter.hasNext();)
                {
                    SchoolLesson sl = (SchoolLesson) iter.next();
                    sl.setSchoolTeacher(newTeacher);
                }
                for(Iterator iter = st.getSchoolSubjectTeachers().iterator(); iter.hasNext();)
                {
                    SchoolSubjectTeacher sst = (SchoolSubjectTeacher) iter.next();
                    sst.setSchoolTeacher(newTeacher);
                }

                transaction.commit();
                restart();
                return true;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public Integer getSubjectId(String subjectName, String schoolId)
    {
        //transaction.begin();
        Criteria cr = session.createCriteria(SchoolSubject.class);
        cr.add(Expression.eq("subjectName", subjectName));
        
        if(cr.list().size() > 0)
        {
            return ((SchoolSubject)cr.list().get(0)).getSubjectId();
        }
        return null;
    }
    
    public Integer getClassId(String className, String schoolId)
    {
        try
        {
           // transaction.begin();
            Criteria cr = session.createCriteria(SchoolClass.class);
            School s = new School();
            s.setSchoolId(new Integer(schoolId));
            cr.add(Expression.eq("school", s));
            cr.add(Expression.eq("className", className));
            
            if(cr.list().size() > 0)
            {
                SchoolClass sc = (SchoolClass) cr.list().get(0);
                return sc.getClassId();
            }
        }
        catch(Exception e)
        {
            
        }
        return null;
    }
    
    /**
     * public List getLesson
     * @param classId
     * @return lista lekcji dla danej klasy
     */
    public List getLesson(String classId)
    {
        //transaction.begin();
        Criteria cr = session.createCriteria(SchoolLesson.class);
        SchoolClass sc = new SchoolClass();
        sc.setClassId(new Integer(classId));
        cr.add(Expression.eq("schoolClass", sc));
        if(cr.list().size() > 0)
        {
            return cr.list();
        }
        
        return null;
    }
    
    public List getTeachers(String schoolId, String subjectName)
    {
        //transaction.begin();
        Criteria cr = session.createCriteria(SchoolSubject.class);
        cr.add(Expression.eq("subjectName", subjectName));
        
        List result = null;
        
        if(cr.list().size() > 0)
        {
            result = new ArrayList();
            SchoolSubject ss = (SchoolSubject) cr.list().get(0);
            
            for(Iterator iter = ss.getSchoolSubjectTeachers().iterator(); iter.hasNext();)
            {
                SchoolSubjectTeacher sst = (SchoolSubjectTeacher) iter.next();
                if(sst.getSchoolTeacher() != null)
                {
                    if(sst.getSchoolTeacher().getSchool().getSchoolId().toString().equals(schoolId))
                    {
                        result.add(sst.getSchoolTeacher());
                    }
                }
            }
            
        }
        return result;
    }
    
    /**
     * public Set getTeachers
     * @param schoolId
     * @return nauczyciele dla danej szkoły
     */
    public Set getTeachers(String schoolId)
    {
        //transaction.begin();
        Criteria cr = session.createCriteria(School.class);
        cr.add(Expression.eq("schoolId", new Integer(schoolId)));
        if(cr.list().size() > 0)
        {
            School s = (School) cr.list().get(0);
            return s.getSchoolTeachers();
        }
        return null;
    }
    
    public List getClasses(String schoolId)
    {
        //transaction.begin();
        Criteria cr = session.createCriteria(SchoolClass.class);
        School s = new School();
        s.setSchoolId(new Integer(schoolId));
        cr.add(Expression.eq("school",s));
        cr.addOrder(Order.asc("className"));
        return cr.list();
    }
    
    public List getSubjects()
    {
        //transaction.begin();
        Criteria cr = session.createCriteria(SchoolSubject.class);
        return cr.list();
    }
    
    public List getSchoolLessonNumbers(String schoolId)
    {
        //transaction.begin();
        Criteria cr = session.createCriteria(SchoolLessonNumber.class);
        SchoolLessonNumberId slnId = new SchoolLessonNumberId();
        slnId.setSchoolId(new Integer(schoolId));
        
        cr.addOrder(Order.asc("id"));
        //cr.add(Expression.eq("id", slnId));
        Integer school = new Integer(schoolId);
        List result = new ArrayList();
        
        for(Iterator iter = cr.list().iterator(); iter.hasNext();)
        {
            SchoolLessonNumber sln = (SchoolLessonNumber) iter.next();
            if(sln.getId().getSchoolId().equals(school))
            {
                result.add(sln);
            }
        }
        return result;
    }
    
    public List getDayTimetable(String dayId, String classId)
    {
        //transaction.begin();
        Criteria cr = session.createCriteria(SchoolLesson.class);
        SchoolClass sc = new SchoolClass();
        sc.setClassId(new Integer(classId));
        cr.add(Expression.eq("schoolClass",sc));
        cr.add(Expression.eq("dayId",new Integer(dayId)));
        cr.addOrder(Order.asc("schoolLessonNumber"));
        
        if(cr.list().size() > 0)
        {
            return cr.list();
        }        
        return null;
    }
    
    
    
    public Integer getTeacherId(String name)
    {
        try
        {
            //transaction.begin();
            StringTokenizer str = new StringTokenizer(name," ");
            String teacherName = str.nextToken();
            String teacherSurname = str.nextToken();
            Criteria cr = session.createCriteria(SchoolTeacher.class);
            cr.add(Expression.eq("teacherName", teacherName));
            cr.add(Expression.eq("teacherSurname",teacherSurname));
            if(cr.list().size() > 0)
            {
                return ((SchoolTeacher)cr.list().get(0)).getTeacherId();
            }
        }
        catch(Exception e)
        {
            
        }
        return null;
    }
    
    public boolean addSubject(String name)
    {
        try
        {
            transaction.begin();
            
            SchoolSubject ss = new SchoolSubject();
            ss.setSubjectName(name.toUpperCase());
            session.save(ss);
            
            transaction.commit();
            restart();
            return true;
        }
        catch(Exception e)
        {
            
        }
        return false;
    }
    
    public boolean addLesson(String schoolId,String lessonNumber,Integer classId, Integer teacherId, Integer subjectId, String dayId, String room)
    {
        try
        {
            transaction.begin();
            
            SchoolLesson sl = new SchoolLesson();
            
            SchoolSubject ss = new SchoolSubject();
            ss.setSubjectId(subjectId);
            
            SchoolClass sc = new SchoolClass();
            sc.setClassId(classId);
            
            SchoolTeacher st = new SchoolTeacher();
            st.setTeacherId(teacherId);
            
            SchoolLessonNumberId slnId = new SchoolLessonNumberId();
            slnId.setSchoolId(new Integer(schoolId));
            slnId.setLessonNumberId(new Integer(lessonNumber));
            
            SchoolLessonNumber sln = new SchoolLessonNumber();
            sln.setId(slnId);
            
            sl.setDayId(new Integer(dayId));
            sl.setRoom(room.toUpperCase());
            sl.setSchoolClass(sc);
            sl.setSchoolLessonNumber(sln);
            sl.setSchoolSubject(ss);
            sl.setSchoolTeacher(st);
            
            session.save(sl);
            transaction.commit();
            restart();
            return true;
            
        }
        catch(Exception e)
        {
            //e.printStackTrace();
        }
        return false;        
    }
    
    public boolean addClass(String className, Integer teacherId, String schoolId)
    {
        try
        {
            transaction.begin();
            SchoolClass sc = new SchoolClass();
            SchoolTeacher st = new SchoolTeacher();
            School s = new School();
            
            s.setSchoolId(new Integer(schoolId));
            st.setTeacherId(teacherId);
           
            sc.setClassName(className.toUpperCase());
            sc.setSchool(s);
            sc.setSchoolTeacher(st);
            session.save(sc);
            transaction.commit();
            restart();
            return true;
        }
        catch(Exception e)
        {
            
        }
        return false;
    }
    
    public boolean addTeacher(String name, String surname, String schoolId)
    {
        try
        {
            transaction.begin();
            SchoolTeacher st = new SchoolTeacher();
            School s = new School();
            s.setSchoolId(new Integer(schoolId));
            st.setSchool(s);
            st.setTeacherName(name.toUpperCase());
            st.setTeacherSurname(surname.toUpperCase());
            session.save(st);
            transaction.commit();
            restart();
            return true;   
        }
        catch(Exception e)
        {
            
        }
        return false;
    }
    
    public boolean removeTimetable(Integer classId, String dayId)
    {
        try
        {
            Criteria cr = session.createCriteria(SchoolLesson.class);
            SchoolClass sc = new SchoolClass();
            sc.setClassId(classId);
            cr.add(Expression.eq("schoolClass",sc));
            if(!dayId.equals("-1"))
            {
                cr.add(Expression.eq("dayId", new Integer(dayId)));
            }
            transaction.begin();
            for(Iterator iter = cr.list().iterator(); iter.hasNext();)
            {
                
                SchoolLesson sl = (SchoolLesson) iter.next();
                session.delete(sl);
            }
            
            transaction.commit();
            restart();
            return true;
        }
        catch(Exception e)
        {
            
        }
        return false;
    }
    
    public boolean removeClass(String className, String schoolId)
    {
        try
        {
            transaction.begin();
            
            School s = new School();
            s.setSchoolId(new Integer(schoolId));
            
            Criteria cr = session.createCriteria(SchoolClass.class);
            cr.add(Expression.eq("className", className));
            cr.add(Expression.eq("school",s));
            
            if(cr.list().size() > 0)
            {
                SchoolClass sc = (SchoolClass) cr.list().get(0);
                for(Iterator iter = sc.getSchoolLessons().iterator();iter.hasNext();)
                {
                    SchoolLesson sl = (SchoolLesson) iter.next();
                    session.delete(sl);
                }
                session.delete(sc);
            }
            
            transaction.commit();
            restart();
            return true;
        }
        catch(Exception e)
        {
            
        }
        return false;
    }
    
    public boolean removeClass(String classId)
    {
        try
        {
            transaction.begin();
            
            Criteria cr = session.createCriteria(SchoolClass.class);
            cr.add(Expression.eq("classId", new Integer(classId)));
            
            if(cr.list().size() > 0)
            {
                SchoolClass sc = (SchoolClass) cr.list().get(0);
                for(Iterator iter = sc.getSchoolLessons().iterator();iter.hasNext();)
                {
                    SchoolLesson sl = (SchoolLesson) iter.next();
                    session.delete(sl);
                }
                session.delete(sc);
            }
            
            transaction.commit();
            restart();
            return true;
        }
        catch(Exception e)
        {
            
        }
        return false;
    }
    
    public boolean removeTeacher(Integer teacherId)
    {
        try
        {
            
            Criteria cr = session.createCriteria(SchoolTeacher.class);
            cr.add(Expression.eq("teacherId", teacherId));
            transaction.begin();
            if(cr.list().size() > 0)
            {
                SchoolTeacher st = (SchoolTeacher) cr.list().get(0);
                for(Iterator iter = st.getSchoolLessons().iterator(); iter.hasNext();)
                {
                    SchoolLesson sl = (SchoolLesson) iter.next();
                    sl.setSchoolTeacher(null);
                }
                for(Iterator iter = st.getSchoolClasses().iterator(); iter.hasNext();)
                {
                    SchoolClass sc = (SchoolClass) iter.next();
                    sc.setSchoolTeacher(null);
                }
                for(Iterator iter = st.getSchoolSubjectTeachers().iterator(); iter.hasNext();)
                {
                    SchoolSubjectTeacher sst = (SchoolSubjectTeacher) iter.next();
                    sst.setSchoolTeacher(null);
                }
                session.delete(st);
                
            }
            transaction.commit();
            restart();
            return true;
        }
        catch(Exception e)
        {
            
        }
        return false;
    }
    public void restart()
    {
        //transaction.rollback();
        transaction = null;

        HibernateUtil.getSessionFactory().close();

        session.disconnect();
        session.close();
        session = null;
        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();

    }
    
    public SchoolLesson getLesson(String classId, int dayName, int lessonNumber)
    {
        
        //transaction = session.beginTransaction();
        //transaction.begin();
        Criteria cr = session.createCriteria(SchoolLesson.class);
        SchoolClass sc = new SchoolClass();
        sc.setClassId(new Integer(classId));
                
        cr.add(Expression.eq("schoolClass", sc));
        cr.add(Expression.eq("dayId", dayName));
        
        SchoolLesson sl = null;
        
        Integer number = new Integer(lessonNumber);
        
        for(Iterator iter = cr.list().iterator(); iter.hasNext();)
        {
            sl = (SchoolLesson) iter.next();
            
            if(sl.getSchoolLessonNumber().getId().getLessonNumberId().equals(number))
            {
                return sl;
            }
        }
        
        return null;
    }
    
    public String getClassName(Integer teacherId)
    {
        try
        {
            Criteria cr = session.createCriteria(SchoolClass.class);
            SchoolTeacher st = new SchoolTeacher();
            st.setTeacherId(teacherId);
            cr.add(Expression.eq("schoolTeacher", st));
            
            if(cr.list().size() > 0)
            {
                SchoolClass sc = (SchoolClass) cr.list().get(0);
                return sc.getClassName();
            }
        }
        catch(Exception e)
        {
            
        }
        return null;
    }
    
    public Set getTeacherSubjects(Integer teacherId)
    {
        try
        {
            Criteria cr = session.createCriteria(SchoolTeacher.class);
            cr.add(Expression.eq("teacherId", teacherId));
            
            if(cr.list().size() > 0)
            {
                SchoolTeacher st = (SchoolTeacher) cr.list().get(0);
                return st.getSchoolSubjectTeachers();
            }
        }
        catch(Exception e)
        {
            
        }
        return null;
    }
    
    public SchoolTeacher hasTutor(Integer classId)
    {
        try
        {
            Criteria cr = session.createCriteria(SchoolClass.class);
            cr.add(Expression.eq("classId", classId));
            
            if(cr.list().size() > 0)
            {
                SchoolClass sc = (SchoolClass) cr.list().get(0);
                if(sc.getSchoolTeacher() != null)
                {
                    return sc.getSchoolTeacher();
                }
            }
        }
        catch(Exception e)
        {
            return null;
        }
        return null;
    }
    
    public boolean isTutor(Integer teacherId)
    {
        try
        {
            Criteria cr = session.createCriteria(SchoolTeacher.class);
            cr.add(Expression.eq("teacherId", teacherId));
            
            if(cr.list().size() > 0)
            {
                SchoolTeacher st = (SchoolTeacher) cr.list().get(0);
                if(st.getSchoolClasses().size() > 0)
                {
                    return true;
                }
            }
        }
        catch(Exception e)
        {
            return true;
        }
        return false;
    }

    public boolean addTeacherToSubject(Integer teacherId,Integer subjectId)
    {
        try
        {
            transaction.begin();
            
            SchoolSubjectTeacher sst = new SchoolSubjectTeacher();
            SchoolTeacher st = new SchoolTeacher();
            st.setTeacherId(teacherId);
            SchoolSubject ss = new SchoolSubject();
            ss.setSubjectId(subjectId);
            sst.setSchoolSubject(ss);
            sst.setSchoolTeacher(st);
            session.save(sst);
            transaction.commit();
            restart();
            return true;
        }
        catch(Exception e)
        {
            
        }
        return false;
    }
    
    public List getAllClasses(String schoolId)
    {
        List result = null;
        try
        {
            Criteria cr = session.createCriteria(SchoolClass.class);
            School s = new School();
            s.setSchoolId(new Integer(schoolId));
            cr.add(Expression.eq("school", s));
            cr.addOrder(Order.asc("className"));
            
            List object1 = new ArrayList();
            List object2 = new ArrayList();
            List object3 = new ArrayList();
            List object4 = new ArrayList();
            
            result = new ArrayList();
            
            
            for(Iterator iter = cr.list().iterator(); iter.hasNext();)
            {
                SchoolClass sc = (SchoolClass) iter.next();
                
                if(sc.getClassName().startsWith("1"))
                {
                    object1.add(sc.getClassName());
                }
                else if(sc.getClassName().startsWith("2"))
                {
                    object2.add(sc.getClassName());
                }
                else if(sc.getClassName().startsWith("3"))
                {
                    object3.add(sc.getClassName());
                }
                else if(sc.getClassName().startsWith("4"))
                {
                    object4.add(sc.getClassName());
                }
            }
            
            result.add(object1);
            result.add(object2);
            result.add(object3);
            result.add(object4);
            return result;
            
        }
        catch(Exception e)
        {
            
        }
        return result;
    }
    
    private void showList(List list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            System.out.println(list.get(i).toString());
        }
    }

    public boolean addTeacherToClass(Integer classId, Integer teacherId) 
    {
        try
        {
            Criteria cr = session.createCriteria(SchoolClass.class);
            cr.add(Expression.eq("classId", classId));
            
            if(cr.list().size() > 0)
            {
                transaction.begin();
                SchoolTeacher st = new SchoolTeacher();
                st.setTeacherId(teacherId);
                
                SchoolClass sc = (SchoolClass) cr.list().get(0);
                sc.setSchoolTeacher(st);
                
                session.update(sc);
                transaction.commit();
                restart();                        
                return true;
            }
        }
        catch(Exception e)
        {
            
        }
        return false;
    }
}

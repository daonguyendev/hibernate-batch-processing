package com.daonguyen;

import com.daonguyen.entity.Employee;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;

import java.util.Iterator;
import java.util.List;

public class EmployeeManager {

    private static SessionFactory factory;

    public static void main(String[] args) {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable e) {
            System.err.println("Failed to create sessionFactory object." + e);
            throw new ExceptionInInitializerError(e);
        }

        EmployeeManager employeeManager = new EmployeeManager();

        // Add few employees in batches
        employeeManager.addEmployees();

        // List of all employees
        employeeManager.listEmployees();
    }

    public void addEmployees() {
        Session session = factory.openSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            for (int i = 0; i < 100000; i++) {
                String fname = "First name " + i;
                String lname = "Last name " + i;
                int salary = i;
                Employee employee = new Employee(fname, lname, salary);
                session.save(employee);

                if (i % 50 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            trans.commit();
        } catch (HibernateException e) {
            if (trans != null)
                trans.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void listEmployees() {
        Session session = factory.openSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            Query query = session.createQuery("FROM Employee");
            query.setCacheable(true);
            query.setCacheRegion("employee");
            List data = query.list();

            for (Iterator iterator = data.iterator(); iterator.hasNext();) {
                Employee employee = (Employee) iterator.next();
                System.out.print("First name: " + employee.getFirstName());
                System.out.print(" | Last name: " + employee.getLastName());
                System.out.println(" | Salary: " + employee.getSalary());
            }

            trans.commit();
        } catch (HibernateException e) {
            if (trans != null)
                trans.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}

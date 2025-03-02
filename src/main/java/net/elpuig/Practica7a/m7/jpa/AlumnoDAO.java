package net.elpuig.Practica7a.m7.jpa;

import net.elpuig.Practica7a.m7.beans.Alumno;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlumnoDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlumnosPU");

    public static List<Alumno> getAllAlumnos() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT a FROM Alumno a", Alumno.class)
                   .getResultList();
        } finally {
            em.close();
        }
    }

    public static Alumno getAlumnoById(int id) {
        EntityManager em = emf.createEntityManager();
        Alumno alumno = em.find(Alumno.class, id);
        em.close();
        return alumno;
    }

    public static boolean save(Alumno alumno) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Verify if the alumno already exists
            Alumno existingAlumno = em.find(Alumno.class, alumno.getId());
            if (existingAlumno != null) {
                // Update the existing alumno
                existingAlumno.setNombre(alumno.getNombre());
                existingAlumno.setCurso(alumno.getCurso());
                em.merge(existingAlumno);
            } else {
                // Create a new alumno
                em.persist(alumno);
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static List<Map<String, String>> executeQueryNative(String sql) {
        EntityManager em = emf.createEntityManager();
        List<Map<String, String>> result = new ArrayList<>();
        
        try {
            Query query = em.createNativeQuery(sql);
            List<Object[]> resultList = query.getResultList();
            
            // Get column names from the native query
            Query columnQuery = em.createNativeQuery("SELECT * FROM (" + sql + ") AS temp LIMIT 0");
            List<String> columnNames = getColumnNames(sql);
            
            // Convert each row to a map of column name -> value
            for (Object[] row : resultList) {
                Map<String, String> rowMap = new HashMap<>();
                for (int i = 0; i < row.length; i++) {
                    String columnName = i < columnNames.size() ? columnNames.get(i) : "Column" + i;
                    rowMap.put(columnName, row[i] != null ? row[i].toString() : "null");
                }
                result.add(rowMap);
            }
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing native query: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    private static List<String> getColumnNames(String sql) {
        // Simple column name extraction for basic SELECT queries
        // This is a simplified approach and may not work for complex queries
        List<String> columnNames = new ArrayList<>();
        
        try {
            String selectPart = sql.toUpperCase().substring(
                sql.toUpperCase().indexOf("SELECT") + 6,
                sql.toUpperCase().indexOf("FROM")
            ).trim();
            
            // Simple case: SELECT * FROM table
            if (selectPart.equals("*")) {
                // For * queries, we'll use generic column names
                return columnNames; // Will be handled by the caller
            }
            
            // Handle regular SELECT col1, col2 FROM table
            String[] cols = selectPart.split(",");
            for (String col : cols) {
                col = col.trim();
                
                // Handle "AS" alias
                if (col.toUpperCase().contains(" AS ")) {
                    col = col.substring(col.toUpperCase().indexOf(" AS ") + 4).trim();
                } else {
                    // Get the last part after any dots or spaces
                    String[] parts = col.split("\\s+|\\.");
                    col = parts[parts.length - 1];
                }
                
                columnNames.add(col);
            }
            
            return columnNames;
        } catch (Exception e) {
            // On failure, return an empty list - we'll handle it in the calling method
            return new ArrayList<>();
        }
    }
}

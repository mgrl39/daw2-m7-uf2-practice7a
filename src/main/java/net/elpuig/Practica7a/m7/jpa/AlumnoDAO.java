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
    
    public static boolean delete(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Alumno alumno = em.find(Alumno.class, id);
            if (alumno != null) {
                em.remove(alumno);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
    
    public static List<Map<String, String>> executeQueryNative(String sql) {
        EntityManager em = emf.createEntityManager();
        List<Map<String, String>> result = new ArrayList<>();
        
        try {
            // Determine if this is JPQL or SQL native query
            boolean isJPQL = sql.trim().toUpperCase().startsWith("SELECT") && 
                             sql.toUpperCase().contains(" FROM ") && 
                             !sql.contains("*");
            
            Query query;
            if (isJPQL) {
                query = em.createQuery(sql);
            } else {
                query = em.createNativeQuery(sql);
            }
            
            List<?> resultList = query.getResultList();
            
            // Handle different result types
            if (!resultList.isEmpty()) {
                Object first = resultList.get(0);
                
                if (first instanceof Object[]) {
                    // Handle array results (multiple columns)
                    List<String> columnNames = getColumnNames(sql);
                    for (Object row : resultList) {
                        Object[] rowArray = (Object[]) row;
                        Map<String, String> rowMap = new HashMap<>();
                        for (int i = 0; i < rowArray.length; i++) {
                            String columnName = i < columnNames.size() ? columnNames.get(i) : "Column" + i;
                            rowMap.put(columnName, rowArray[i] != null ? rowArray[i].toString() : "null");
                        }
                        result.add(rowMap);
                    }
                } else if (first instanceof Alumno) {
                    // Handle entity results
                    for (Object obj : resultList) {
                        Alumno alumno = (Alumno) obj;
                        Map<String, String> rowMap = new HashMap<>();
                        rowMap.put("id", String.valueOf(alumno.getId()));
                        rowMap.put("nombre", alumno.getNombre());
                        rowMap.put("curso", alumno.getCurso());
                        result.add(rowMap);
                    }
                } else {
                    // Handle scalar results (single column)
                    for (Object value : resultList) {
                        Map<String, String> rowMap = new HashMap<>();
                        rowMap.put("result", value != null ? value.toString() : "null");
                        result.add(rowMap);
                    }
                }
            }
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing query: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    private static List<String> getColumnNames(String sql) {
        // Simple column name extraction for basic SELECT queries
        // This is a simplified approach and may not work for complex queries
        List<String> columnNames = new ArrayList<>();
        
        try {
            String upperSql = sql.toUpperCase();
            int selectIndex = upperSql.indexOf("SELECT");
            int fromIndex = upperSql.indexOf("FROM");
            
            if (selectIndex >= 0 && fromIndex > selectIndex) {
                String selectPart = sql.substring(selectIndex + 6, fromIndex).trim();
                
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
                    
                    // Remove any surrounding quotes or backticks
                    if ((col.startsWith("\"") && col.endsWith("\"")) || 
                        (col.startsWith("`") && col.endsWith("`")) || 
                        (col.startsWith("'") && col.endsWith("'"))) {
                        col = col.substring(1, col.length() - 1);
                    }
                    
                    columnNames.add(col);
                }
            }
            
            return columnNames;
        } catch (Exception e) {
            // On failure, return an empty list - we'll handle it in the calling method
            return new ArrayList<>();
        }
    }
}

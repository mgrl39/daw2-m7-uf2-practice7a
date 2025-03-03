package net.elpuig.Practica7a.m7.beans;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TypedQuery;
import net.elpuig.Practica7a.m7.jpa.JPAUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "alumnos")
public class Alumno {
    @Id
    private int id;
    
    @Column(name = "curso")
    private String curso;
    
    @Column(name = "nombre")
    private String nombre;

    // Constructor vacío requerido por JPA
    public Alumno() {
    }
    
    public Alumno(int id, String curso, String nombre) {
        this.id = id;
        this.curso = curso;
        this.nombre = nombre;
    }
    
    public int getId() {
        return id;
    }	
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getCurso() {
        return curso;
    }
    
    public void setCurso(String curso) {
        this.curso = curso;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    /**
     * Carga todos los alumnos de la base de datos utilizando JPQL
     * @return Lista de alumnos
     */
    public static List<Alumno> load() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Alumno> query = em.createQuery("SELECT a FROM Alumno a", Alumno.class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error al cargar alumnos: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    /**
     * Ejecuta una consulta JPQL y devuelve los resultados formateados
     * @param jpql Consulta JPQL a ejecutar
     * @return Lista de mapas con los resultados de la consulta
     */
    public static List<Map<String, String>> executeJPQL(String jpql) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Map<String, String>> result = new ArrayList<>();
        
        try {
            try {
                // Intentamos primero como una consulta tipada de Alumno
                TypedQuery<Alumno> query = em.createQuery(jpql, Alumno.class);
                List<Alumno> resultList = query.getResultList();
                System.out.println(resultList);
                
                for (Alumno alumno : resultList) {
                    Map<String, String> row = new HashMap<>();
                    row.put("id", String.valueOf(alumno.getId()));
                    row.put("nombre", alumno.getNombre());
                    row.put("curso", alumno.getCurso());
                    result.add(row);
                }
            } catch (Exception e) {
                // Si falla como TypedQuery<Alumno>, podría ser una consulta que devuelve otro tipo de datos
                try {
                    List<?> queryResult = em.createQuery(jpql).getResultList();
                    processQueryResults(queryResult, result);
                } catch (Exception e2) {
                    throw new RuntimeException("Error ejecutando consulta JPQL: " + e2.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al ejecutar la consulta: " + e.getMessage(), e);
        } finally {
            em.close();
        }
        
        return result;
    }
    
    /**
     * Mantiene retrocompatibilidad con el método anterior
     */
    public static List<Map<String, String>> executeQuery(String jpql) {
        return executeJPQL(jpql);
    }
    
    // Método auxiliar para procesar resultados de consultas
    private static void processQueryResults(List<?> queryResult, List<Map<String, String>> result) {
        for (Object obj : queryResult) {
            if (obj instanceof Alumno) {
                Alumno alumno = (Alumno) obj;
                Map<String, String> rowMap = new HashMap<>();
                rowMap.put("id", String.valueOf(alumno.getId()));
                rowMap.put("nombre", alumno.getNombre());
                rowMap.put("curso", alumno.getCurso());
                result.add(rowMap);
            } else if (obj instanceof Object[]) {
                Object[] row = (Object[]) obj;
                Map<String, String> rowMap = new HashMap<>();
                for (int i = 0; i < row.length; i++) {
                    // Intentamos obtener nombres de columnas más descriptivos
                    String columnName = "column" + i;
                    if (i == 0) columnName = "id";
                    else if (i == 1) columnName = "nombre";
                    else if (i == 2) columnName = "curso";
                    
                    rowMap.put(columnName, row[i] != null ? row[i].toString() : "null");
                }
                result.add(rowMap);
            } else {
                Map<String, String> rowMap = new HashMap<>();
                rowMap.put("result", obj != null ? obj.toString() : "null");
                result.add(rowMap);
            }
        }
    }
    
    public boolean save() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(this);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al guardar alumno: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
}

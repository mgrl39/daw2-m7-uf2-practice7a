package net.elpuig.Practica7a.m7.beans;

import jakarta.persistence.*;
import net.elpuig.Practica7a.m7.jpa.AlumnoDAO;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "alumnos")
public class Alumno {
    @Id
    private int id;
    @Column(nullable = false)
    private String curso;
    @Column(nullable = false)
    private String nombre;

    public Alumno(int id, String nombre, String curso) {
        this.id = id;
        this.nombre = nombre;
        this.curso = curso;
    }

    public Alumno() {}

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
    
    public static List<Alumno> load() {
        return AlumnoDAO.getAllAlumnos();
    }
        
    public static List<Map<String, String>> executeQuery(String sql) {
        return AlumnoDAO.executeQueryNative(sql);
    }
    
    public boolean save() {
        return AlumnoDAO.save(this);
    }
}

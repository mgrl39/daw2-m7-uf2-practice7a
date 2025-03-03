package net.elpuig.Practica7a.m7.util;

import net.sf.jasperreports.engine.JasperCompileManager;

public class JasperCompiler {
    public static void main(String[] args) {
        try {
            String sourcePath = "src/main/webapp/WEB-INF/informes/alumnos/Alumnos.jrxml";
            String destPath = "src/main/webapp/WEB-INF/informes/alumnos/Alumnos.jasper";
            
            System.out.println("Compilando " + sourcePath + " a " + destPath);
            JasperCompileManager.compileReportToFile(sourcePath, destPath);
            System.out.println("Compilación completada con éxito");
            
        } catch (Exception e) {
            System.err.println("Error al compilar el reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
package net.elpuig.Practica7a.m7.webses;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

public class WebSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        System.out.println("Sesión creada");
        ServletContext context = event.getSession().getServletContext();

        synchronized (context) {
            Integer usuariosConectados = (Integer) context.getAttribute("usuariosConectados");
            if (usuariosConectados == null) {
                usuariosConectados = 0;
            }
            usuariosConectados++;
            context.setAttribute("usuariosConectados", usuariosConectados);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        System.out.println("Sesión destruida");
        ServletContext context = event.getSession().getServletContext();

        synchronized (context) {
            Integer usuariosConectados = (Integer) context.getAttribute("usuariosConectados");
            if (usuariosConectados == null || usuariosConectados == 0) {
                usuariosConectados = 0;
            } else {
                usuariosConectados--;
            }
            context.setAttribute("usuariosConectados", usuariosConectados);
        }
    }
}
package net.elpuig.Practica7a.m7.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.elpuig.Practica7a.m7.beans.Usuario;

import java.io.IOException;

public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String usuarioIntro = request.getParameter("txtUsuario");

		if (validarUsuario(usuarioIntro, request.getParameter("txtContrasenya")) != null) {
			request.getSession().setAttribute("usuario", new Usuario(usuarioIntro));
			response.sendRedirect("info");
		} else request.getRequestDispatcher("login").forward(request, response);
	}

	// Nos devuelve el nombre del usuario si la pareja
	// nombre-passw es correcta
	// Tendriamos que comparar con valores de la BD!
	private String validarUsuario(String usuarioIntro, String passwIntro) {
		String retorno = null;

		if (usuarioIntro.equals("daniel") && passwIntro.equals("daniel")) {
			retorno = usuarioIntro;
		}
		return retorno;
	}

}

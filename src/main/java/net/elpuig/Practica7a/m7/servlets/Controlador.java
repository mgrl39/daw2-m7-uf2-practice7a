package net.elpuig.Practica7a.m7.servlets;

import net.elpuig.Practica7a.m7.enums.Protocol;
import net.elpuig.Practica7a.m7.beans.Alumno;
import net.elpuig.Practica7a.m7.beans.Usuario;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet("/Controlador")
public class Controlador extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Set<String> ORDENES_VALIDAS = Set.of("ejecutar", "info", "desconectar", "informe");
	private PrintWriter out;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String operacion = request.getParameter("operacion");
		if (operacion != null)
			operacion = operacion.trim();
		if (!validarOrden(operacion)) {
			out.println(webFormatter("Operacion invalida.", Protocol.GET));
			out.println("<a href='home'>Ir a la pantalla inicial</a>");
			return;
		}

		// Si la orden es "desconectar", no creamos una nueva sesión
		if ("desconectar".equalsIgnoreCase(operacion)) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}
			response.sendRedirect("logout");
			return;
		}

		// Aquí solo obtenemos la sesión si el usuario no está desconectándose
		HttpSession session = request.getSession();
		session.setAttribute("idSesion", session.getId());
		session.setAttribute("fechaCreacion", new java.util.Date(session.getCreationTime()));
		session.setAttribute("ultimoAcceso", new java.util.Date(session.getLastAccessedTime()));
		incrementarContadorSesion(session);

		switch (operacion.toLowerCase()) {
		case "info":
			procesarInfo(request, response, session);
			break;
		case "ejecutar":
			procesarConsultaJPQL(request, response);
			break;
		case "informe":
			procesarInforme(request, response);
			break;
		default:
			out.println(webFormatter("operacion invalida", Protocol.GET));
			out.println("<a href='home'>Ir a la pantalla inicial</a>");
			break;
		}
	}

	private boolean validarOrden(String operacion) {
		return operacion != null && ORDENES_VALIDAS.contains(operacion.toLowerCase());
	}

	// 'infosesion.jsp' no hace nada extraño, sencillamente dibuja una tabla y
	// muestra los valores deseados de la sesión.
	private void procesarInfo(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		HttpSession sesion = request.getSession();

		sesion.setAttribute("idSesion", sesion.getId());
		sesion.setAttribute("fechaCreacion", new Date(sesion.getCreationTime()));
		sesion.setAttribute("ultimoAcceso", new Date(sesion.getLastAccessedTime()));

		Integer contadorAccessos = (Integer) sesion.getAttribute("contadorAccessos");
		sesion.setAttribute("contadorAccessos", contadorAccessos != null ? contadorAccessos : 0);

		ServletContext contexto = getServletContext();
		Integer usuariosConectados = (Integer) contexto.getAttribute("usuariosConectados");
		Integer usuariosValidados = (Integer) contexto.getAttribute("usuariosValidados");

		contexto.setAttribute("usuariosConectados", usuariosConectados != null ? usuariosConectados : 0);
		contexto.setAttribute("usuariosValidados", usuariosValidados != null ? usuariosValidados : 0);
		request.getRequestDispatcher("info").forward(request, response);
	}
	
	private void procesarInforme(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Obtener la lista de alumnos usando JPA
		List<Alumno> listaAlumnos = Alumno.load();
		
		// Colocar la lista en la request para que esté disponible para el servlet Report
		request.setAttribute("lista", listaAlumnos);
		
		// Redirigir al servlet Report para generar el informe
		request.getRequestDispatcher("/Report").forward(request, response);
	}

	private void procesarConsultaJPQL(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String jpql = request.getParameter("sql");
		if (isNullOrEmpty(jpql)) {
			out.println(webFormatter("<p style=\"color: red\">Error: No se proporcionó ninguna consulta JPQL</p>",
					Protocol.GET));
			out.println("<a href='home'>Ir a la pantalla inicial</a>");
			return;
		}
		try {
			// Usar directamente la consulta JPQL
			List<Map<String, String>> data = Alumno.executeJPQL(jpql);

			if ("true".equals(request.getParameter("jstl"))) {
				request.setAttribute("data", data);
				request.getRequestDispatcher("result").forward(request, response);
			} else
				printJPQL(request, response);
		} catch (RuntimeException e) {
			out.println(webFormatter("<p style=\"color: red\">Error al ejecutar la consulta: " + e.getMessage() + "</p>", Protocol.GET));
			out.println("<a href='home'>Ir a la pantalla inicial</a>");
		}
	}

	private void printJPQL(HttpServletRequest request, HttpServletResponse response) throws IOException {
		out = response.getWriter();
		String jpql = request.getParameter("sql");
		if (isNullOrEmpty(jpql)) {
			out.println(webFormatter(
					"<p style=\"color: #FF0000\">Error: <strong style=\"color: #FF0000\">No se proporcionó ninguna consulta JPQL</strong></p>",
					Protocol.GET));
			return;
		}
		try {
			List<Map<String, String>> data = Alumno.executeJPQL(jpql);
			String htmlTable = "";
			if (data.isEmpty()) {
				htmlTable += "<p style=\"color:#00007e\">No se encontraron resultados.</p>\n";
				out.println(webFormatter(htmlTable, Protocol.GET));
				return;
			}
			htmlTable += "<table style=\"color:#00007e\" border='1'><tr>";
			for (String columnName : data.get(0).keySet()) {
				htmlTable += "<th>" + columnName + "</th>";
			}
			htmlTable += "</tr>";
			for (Map<String, String> row : data) {
				htmlTable += "<tr>";
				for (String value : row.values())
					htmlTable += "<td>" + value + "</td>";
				htmlTable += "</tr>";
			}
			htmlTable += "</table>";
			out.println(webFormatter(htmlTable, Protocol.GET));
		} catch (RuntimeException e) {
			out.println(webFormatter(
					"<p style=\"color:#00007e\">Error al ejecutar la consulta: <strong style=\"color: #FF0000\">"
							+ e.getMessage() + "</strong></p>",
					Protocol.GET));
		}
	}

	private static String webFormatter(String msg, Protocol p) {
		String style;
		style = "<html><body style=\"background-color:#ffff9d\"><center><h1 style=\"color:#00007e\">";
		style += p == Protocol.GET ? "Usa JPA para recuperar registros de una tabla"
				: "Usa JPA para grabar un registro en una tabla";
		style += "</h1></center><hr style=\"color:#00007e\">";
		style += "<p style=\"color:#00007e\">" + msg + "</p></body></html>";
		return (style);
	}

	private boolean isNullOrEmpty(String value) {
		return (value == null || value.trim().isEmpty());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Creación de la sesión. Si ya existe la utiliza
		HttpSession session = request.getSession(true);
		response.setContentType("text/html");
		out = response.getWriter();

		String operacion = request.getParameter("operacion");
		if (operacion == null) {
			out.println(webFormatter("Error: No se ha recibido el parámetro 'operacion'", Protocol.POST));
			return;
		}

		if ("alta".equalsIgnoreCase(operacion)) {
			if (session.getAttribute("usuario") != null)
				postWithSession(request, response);
			else {
				session.setAttribute("sesAlumnoID", request.getParameter("id"));
				session.setAttribute("sesAlumnoNombre", request.getParameter("nombre"));
				session.setAttribute("sesAlumnoCurso", request.getParameter("curso"));
				request.getRequestDispatcher("login").forward(request, response);
			}
		} else if ("validar".equalsIgnoreCase(operacion)) {
			String user = validarUsuario(request.getParameter("txtUsuario"), request.getParameter("txtContrasenya"));
			if (user == null) {
				request.getRequestDispatcher("error").forward(request, response);
			} else {
				Usuario usuario = new Usuario(user);
				session.setAttribute("usuario", usuario);
				String sesAlumnoID = (String) session.getAttribute("sesAlumnoID");
				String sesAlumnoNombre = (String) session.getAttribute("sesAlumnoNombre");
				String sesAlumnoCurso = (String) session.getAttribute("sesAlumnoCurso");
				if (sesAlumnoID != null && sesAlumnoNombre != null && sesAlumnoCurso != null) {
					Alumno nuevoAlumno = new Alumno(Integer.parseInt(sesAlumnoID), sesAlumnoNombre, sesAlumnoCurso);
					response.setContentType("text/html");
					PrintWriter out = response.getWriter();
					out.println(webFormatter(nuevoAlumno.save() ? "Alumno añadido" : "Error al añadir el alumno",
							Protocol.POST));
					out.println("<a href='home'>Ir a la pantalla inicial</a>");

					// Limpiar los datos de la sesión después del alta
					session.removeAttribute("sesAlumnoID");
					session.removeAttribute("sesAlumnoNombre");
					session.removeAttribute("sesAlumnoCurso");
				} else {
					response.setContentType("text/html");
					PrintWriter out = response.getWriter();
					out.println(webFormatter("Error: No se encontraron datos del alumno en la sesión.", Protocol.POST));
				}
			}
		} else {
			out.println(webFormatter("Error: Valor de 'operacion' no válido.", Protocol.POST));
		}
	}

	private void postWithSession(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String idStr = request.getParameter("id");
		String nombre = request.getParameter("nombre");
		String curso = request.getParameter("curso");
		try {
			if ((isNullOrEmpty(idStr) || isNullOrEmpty(curso)) || isNullOrEmpty(nombre)) {
				out.println(webFormatter("Todos los campos son obligatorios", Protocol.POST));
				return;
			}
			Alumno nuevoAlumno = new Alumno(Integer.parseInt(idStr), curso, nombre);
			out.println(
					webFormatter(nuevoAlumno.save() ? "Alumno añadido" : "Error al añadir el alumno", Protocol.POST));
			out.println("<a href='home'>Ir a la pantalla inicial</a>");
		} catch (NumberFormatException e) {
			out.println(webFormatter("Error: El ID debe ser un número válido", Protocol.POST));
		}
	}

	/*
	 * Metodo para llevar el control del numero de veces que accede al servlet un
	 * usuario
	 */
	private void incrementarContadorSesion(HttpSession sesion) {
		// Variable para guardar las veces que accede el usuario
		Integer contadorAccesos = 0;

		// Si ya existe la sesion...
		if (!sesion.isNew()) {
			// Recuperar el contador
			Integer contadorActual = (Integer) sesion.getAttribute("contadorAccesos");
			// Incrementarlo
			if (contadorActual != null) {
				contadorAccesos = contadorActual + 1;
			}
		}
		// Guardar el nuevo valor del contador
		sesion.setAttribute("contadorAccesos", contadorAccesos);
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

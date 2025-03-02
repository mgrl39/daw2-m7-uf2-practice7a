<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>
<head>
<title>Sesión Finalizada</title>
<style>
a, h1 {
	color: white;
}

body {
	background-color: #663366;
}

button {
	background: none;
	border: none;
	color: white;
	text-decoration: underline;
	cursor: pointer;
	font-size: 16px;
	padding: 0;
}

button:hover {
	color: lightgray;
}
</style>
</head>
<body>
	<h1>Usuario o contraseña incorrecta</h1>
	<%
	HttpSession sesion = request.getSession();
	String id = (String) sesion.getAttribute("sesAlumnoID");
	String nombre = (String) sesion.getAttribute("sesAlumnoNombre");
	String curso = (String) sesion.getAttribute("sesAlumnoCurso");
	%>
	<!-- Formulario que hace un POST con operacion=alta -->
	<form action="Controlador" method="post">
		<input type="hidden" name="operacion" value="alta"> <input
			type="hidden" name="id" value="<%=id != null ? id : ""%>"> <input
			type="hidden" name="nombre" value="<%=nombre != null ? nombre : ""%>">
		<input type="hidden" name="curso"
			value="<%=curso != null ? curso : ""%>">
		<button type="submit">Reintentar</button>
	</form>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>Consulta de Alumnos</title>
</head>
<body>
	<h1>Resultados de la consulta</h1>
	<c:if test="${not empty error}">
		<p style="color: red;">${error}</p>
	</c:if>
	<table border="1">
		<tr>
			<th>ID</th>
			<th>Nombre</th>
			<th>Curso</th>
		</tr>
		<c:forEach items="${listaAlumnos}" var="alumno">
			<tr>
				<td>${alumno.id}</td>
				<td>${alumno.nombre}</td>
				<td>${alumno.curso}</td>
			</tr>
		</c:forEach>
	</table>
	<a href="home">Volver</a>
</body>
</html>
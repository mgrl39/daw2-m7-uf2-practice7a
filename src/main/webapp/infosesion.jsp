<%@ page contentType="text/html;charset=UTF-8" language="java"
	import="java.util.Date"%>
<html>
<head>
<title>Información de sesión</title>
<style>
body {
	background-color: #934993;
	color: white;
	font-family: Arial, sans-serif;
	text-align: center;
}

table {
	margin: auto;
	border-collapse: collapse;
	width: 60%;
	background-color: #ffff99;
	color: black;
}

th, td {
	border: 1px solid black;
	padding: 10px;
	text-align: left;
}

th {
	background-color: #FFD700;
}

a {
	text-decoration: none;
	display: block;
	margin-top: 20px;
	text-decoration: underline;
}

a:hover {
	
}
</style>
</head>
<body>
	<h1>Información de su sesión</h1>
	<table border="1">
		<tr>
			<td colspan="2"
				style="color: #934993; font-weight: bold; text-align: center;">
				Información
		</tr>

		<tr>
			<td style="color: #934993; font-weight: bold; text-align: center;">Atributo</td>
			<td style="color: #934993; font-weight: bold; text-align: center;">Valor</td>
		</tr>
		<tr>
			<td style="color: #934993; font-weight: bold;">Identificador</td>
			<td><%=session.getId()%></td>
		</tr>
		<tr>
			<td style="color: #934993; font-weight: bold;">Fecha/hora
				creación</td>
			<td><%=new Date(session.getCreationTime())%></td>
		</tr>
		<tr>
			<td style="color: #934993; font-weight: bold;">Hora último
				acceso</td>
			<td><%=new Date(session.getLastAccessedTime())%></td>
		</tr>
		<tr>
			<td style="color: #934993; font-weight: bold;">Número previo de
				accesos</td>
			<td><%=session.getAttribute("contadorAccesos") != null ? session.getAttribute("contadorAccesos") : 0%></td>
		</tr>
		<tr>
			<td style="color: #934993; font-weight: bold;">Usuario</td>
			<td><%=session.getAttribute("usuario") != null ? session.getAttribute("usuario") : "No validado"%></td>
		</tr>
		<tr>
			<td style="color: #934993; font-weight: bold;">Número de
				usuarios conectados</td>
			<td><%=application.getAttribute("usuariosConectados") != null ? application.getAttribute("usuariosConectados") : 0%></td>
		</tr>
		<tr>
			<td style="color: #934993; font-weight: bold;">Número de
				usuarios validados</td>
			<td><%=application.getAttribute("usuariosValidados") != null ? application.getAttribute("usuariosValidados") : 0%></td>
		</tr>
	</table>
	<a href="home">Ir a la pantalla inicial</a>
</body>
</html>

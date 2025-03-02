<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>
<head>
<title>Validación de Usuarios</title>
<style>
body {
	background-color: #663366;
	color: white;
	font-family: Arial, sans-serif;
	text-align: center;
}

h1 {
	margin-top: 50px;
}

.container {
	background-color: #ffff99;
	color: black;
	width: 400px;
	margin: auto;
	padding: 15px;
}

.container p {
	font-weight: bold;
	text-align: center;
	color: #663366;
}

table {
	width: 100%;
	margin-top: 10px;
}

td {
	padding: 5px;
	text-align: left;
	color: #663366;
}

input[type="text"], input[type="password"] {
	width: 100%;
	padding: 5px;
	border: 1px solid #000;
	background-color: #ffffcc;
}

input[type="submit"]:hover, input[type="reset"]:hover {
	background-color: #b3b3ff;
	cursor: pointer;
}
</style>
</head>
<body>
	<h1>Validación de Usuarios</h1>
	<div class="container">
		<p>
			La operación solicitada requiere validación<br> Por favor,
			introduzca sus credenciales
		</p>

		<c:if test="${not empty error}">
			<p style="color: red;">${error}</p>
		</c:if>

		<form action="Controlador" method="post">
			<input type="hidden" name="operacion" value="validar">
			<table>
				<tr>
					<td>Usuario</td>
					<td><input type="text" name="txtUsuario" required></td>
				</tr>
				<tr>
					<td>Contraseña</td>
					<td><input type="password" name="txtContrasenya" required></td>
				</tr>
				<tr>
					<td colspan="2" style="text-align: center;"><input
						type="submit" value="Aceptar"> <input type="reset"
						value="Borrar"></td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>

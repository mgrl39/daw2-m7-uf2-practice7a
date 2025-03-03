<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html lang="es">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Practica 7A Gestión de Alumnos</title>
<style>
body {
	background-color: #934993;
}

h1 {
	text-align: center;
}

h1, h2, hr, label, h3, span {
	color: #FFFFFF;
}

form label {
	font-weight: bold;
}

h3 {
	margin-bottom: 0;
}

.decisio {
	color: white;
}

.help-text {
	color: #FFFF99;
	font-style: italic;
	margin-top: 5px;
	display: block;
}
</style>
</head>
<body>
	<h1>Gestión de estudiantes</h1>
	<a href="Controlador?operacion=info">Informacion de sesion</a>
	<a href="javascript:desconectar()">Desconectar</a>
	<h2>Consultas</h2>
	<hr>
	<br>
	<form action="Controlador" method="get">
		<label for="sql">Consulta JPQL</label> <input id="sql" name="sql" required
			value="SELECT a FROM Alumno a">
		<span class="help-text">Use consultas JPQL, no SQL. Ejemplo: "SELECT a FROM Alumno a"</span>
		<input type="submit" name="operacion" value="Ejecutar">
		<h3>OPCIONES</h3>
		<br> <label>Técnica JSTL en resultados consulta</label> <input
			name="jstl" value="true" type="radio"> <span class="decisio">Si</span>
		<input name="jstl" value="false" type="radio" checked> <span
			class="decisio">No</span>
	</form>

	<h2>Modificaciones</h2>
	<hr>
	<br>
	<form action="Controlador" method="post">
		<label for="id">ID Alumno</label> <input type="number" name="id"
			required> <label for="curso">Curso:</label> <input
			type="text" name="curso" required> <label for="nombre">Nombre:</label>
		<input type="text" name="nombre" required><br> <br>
		<button type="submit" name="operacion" value="alta">Alta
			Alumno</button>
	</form>

	<h2>Informes</h2>
	<form action="informe" method="get">
		<input type="radio" id="optInformesPdf" name="optInformes"
			value="application/pdf" checked /> <label for="formatPdf">PDF</label>
		<input type="radio" id="optInformesExcel" name="optInformes"
			value="application/vnd.ms-excel" /> <label for="formatExcel">Excel</label>
		<input type="radio" id="optInformesWord" name="optInformes"
			value="application/msword" /> <label for="formatWord">Word</label> <input
			type="radio" id="optInformesHtml" name="optInformes" value="html" />
		<label for="formatHtml">HTML</label><br> <br> <input
			type="submit" value="Generar Informe" />
	</form>

</body>
<script>
	function desconectar() {
		var log_out = confirm("Con esta accion se finaliza la sesion actual ¿Esta seguro?");
		if (log_out)
			window.location = "Controlador?operacion=desconectar";
	}
</script>

</html>

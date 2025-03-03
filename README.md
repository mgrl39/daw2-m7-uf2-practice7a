# Práctica 07 (JPA)

En esta práctica vamos a cambiar el sistema de persistencia, por tanto se trata de una refactorización de la práctica anterior, puesto que no vamos a alterar ninguna funcionalidad de la aplicación. Actualmente trabajamos con JDBC puro y lo que debemos hacer en esta ocasión es sustituir esta implementación por JPA (Java Persistence API), concretamente, utilizaremos el framework de persistencia EclipseLink.

Para poder realizar lo que aquí se pide el docente de la asignatura debe haberos explicado los fundamentos de la tecnología JPA.

## Dependencias de Maven para trabajar con la implementación JPA de EclipseLink:

```xml
<dependency>
	<groupId>org.eclipse.persistence</groupId>
	<artifactId>org.eclipse.persistence.jpa</artifactId>
	<version>4.0.3</version>
</dependency>
<dependency>
	<groupId>org.eclipse.persistence</groupId>
	<artifactId>eclipselink</artifactId>
	<version>4.0.3</version>
</dependency>
```

## Sobre NO usar JDBC para los reports

Cuando utilizamos JPA es mejor no utilizar una conexión JDBC para los informes (bueno, en general es más efectivo siempre), ya que así no estaremos mezclando formas de trabajar. Lo tenemos fácil, ya que JasperReport nos permite pasarle en lugar de una conexión JDBC un objeto de la clase `JRBeanCollectionDataSource`, que viene a ser una `Collection`.

Para conseguir esto en la práctica 4c tenemos que hacer lo siguiente:

1) En el Servlet `Controlador`, cuando la petición es `'informe'`, obtenemos una lista de alumnos y la dejamos en la request.

2) En el Servlet `Report`, en el método `service()`, creamos un objeto `JRBeanCollectionDataSource`, recuperamos esa lista y se la proporcionamos como argumento al constructor de `JRBeanCollectionDataSource`:

```java
Collection<Alumno> lista =
(List<Alumno>) request.getAttribute("lista");

JRBeanCollectionDataSource dataSource =
new JRBeanCollectionDataSource(lista);
```

3) Al rellenar el report, en lugar de pasar la conexión JDBC pasamos la referencia de `JRBeanCollectionDataSource`:

```java
JasperPrint jasperPrint =
  JasperFillManager.fillReport(jsprRep, new HashMap<>(0), dataSource);
```

### Modificaciones en el Report `Alumnos.jrxml`

1) Modificamos el nombre de los campos para que coincidan exactamente con el nombre de los atributos de la clase `Alumno`.

2) Eliminamos la query, ya que ahora no hay que hacer ningún `SELECT`. En su lugar dejamos la definición de los campos tal y como se muestra a continuación:

```xml
<jasperReport ...>
    <field name="id" class="java.lang.Integer"/>        
    <field name="curso" class="java.lang.String"/>        
    <field name="nombre" class="java.lang.String"/>
</jasperReport>
```

---

## 📌 1. JPQL y el Uso de Alias
- JPQL (Java Persistence Query Language) es similar a SQL, pero funciona sobre **entidades de Java**, no sobre tablas de base de datos.
- En JPQL, **el alias distingue entre mayúsculas y minúsculas** según cómo se define.
  
```java
SELECT a FROM Alumno a  ✅ // Funciona
SELECT a FROM Alumno A  ❌ // Error: alias no definido
SELECT A FROM Alumno a  ❌ // Error: alias "A" no existe
SELECT alumno FROM Alumno alumno  ✅ // Funciona
SELECT alumno FROM Alumno a  ❌ // Error: alias "alumno" no existe
```

✅ **Regla:**  
**JPQL es case-sensitive con los alias** → Debes usar el alias exactamente como lo definiste en la consulta.

---

## 📌 2. Diferencia con SQL (Base de Datos)
- En SQL estándar, **los alias NO son case-sensitive**, a menos que uses comillas dobles (`"Alias"`):
  
```sql
SELECT id FROM alumnos AS a;  -- Funciona en SQL
SELECT id FROM alumnos AS A;  -- También funciona en SQL
```
- En cambio, en **JPQL**, el alias es sensible a mayúsculas/minúsculas porque **es un identificador de Java**.

---

## 📌 3. Relación con JPA y el ORM
- JPA utiliza **JPQL**, que sigue convenciones similares a Java.
- **Java es case-sensitive**, por lo que los alias también lo son en JPQL.
- Si defines **`a` minúscula**, tienes que usar `a` en toda la consulta. Si cambias a `A`, JPQL no lo reconocerá.

---

## 🔎 Conclusión
🔹 **JPQL es case-sensitive con alias porque sigue la convención de Java.**  
🔹 **Si defines `a` en `SELECT a FROM Alumno a`, debes usar `a` en toda la consulta.**  
🔹 **JPQL NO se comporta como SQL, donde los alias pueden no ser case-sensitive.**  

👉 **Solución:** Siempre usa el alias **tal como lo defines** en la consulta. 🚀

---

## 📌 1. JPQL Trabaja con Clases de Java, No con Tablas de la BD
- JPQL **no consulta directamente la base de datos** como SQL.
- En su lugar, **consulta las entidades de JPA**, que son clases de Java anotadas con `@Entity`.

```java
@Entity
@Table(name = "alumnos")  // ← Nombre de la tabla en BD
public class Alumno {
    @Id
    private int id;
    
    @Column(name = "curso")
    private String curso;
    
    @Column(name = "nombre")
    private String nombre;
}
```

- Aquí, la entidad en Java se llama **`Alumno`**, pero la tabla en la base de datos se llama **`alumnos`**.
- **JPQL usa el nombre de la clase (`Alumno`), no el nombre de la tabla (`alumnos`).**

---

## 📌 2. Diferencia con SQL
- En **SQL**, consultas directamente la tabla:
  ```sql
  SELECT * FROM alumnos;  -- SQL usa el nombre de la tabla
  ```
- En **JPQL**, consultas la entidad **definida en Java**, no la tabla:
  ```java
  SELECT a FROM Alumno a  -- JPQL usa el nombre de la clase, no la tabla
  ```

**Ejemplo incorrecto en JPQL:**
```java
SELECT a FROM Alumnos a  // ❌ Error: "Alumnos" no existe como entidad
```

💡 **Solución:** Siempre usa el nombre de la entidad de Java en JPQL. Si tu clase se llama `Alumno`, usa `Alumno`, no `alumnos` ni `Alumnos`. 🚀


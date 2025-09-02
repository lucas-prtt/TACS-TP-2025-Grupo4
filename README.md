
# TP TACS

## Links

### Alumnos/Grupos
[https://docs.google.com/spreadsheets/d/e/2PACX-1vSReiaYjc97RflWQ5IlHoeRb2lrDqMi3n9YwexC2KBf7_fqZ1Am_Et7CZ5V5K5b0vy_0n4DeWvXZzPD/pubhtml?gid=347228570&single=true
](https://docs.google.com/spreadsheets/d/e/2PACX-1vSReiaYjc97RflWQ5IlHoeRb2lrDqMi3n9YwexC2KBf7_fqZ1Am_Et7CZ5V5K5b0vy_0n4DeWvXZzPD/pubhtml?gid=347228570&single=true)

### Planificacion
[https://docs.google.com/spreadsheets/d/e/2PACX-1vSa2Emm5Rjl2zmFTbxOxp6nH19FbL80dM3T9-7Ae1EGK9laBI_4D-3FLcUuk9pmSOv9FiCEXfFt0he-/pubhtml?gid=0&single=true
](https://docs.google.com/spreadsheets/d/e/2PACX-1vSa2Emm5Rjl2zmFTbxOxp6nH19FbL80dM3T9-7Ae1EGK9laBI_4D-3FLcUuk9pmSOv9FiCEXfFt0he-/pubhtml?gid=0&single=true)

### Consigna
[https://docs.google.com/document/d/e/2PACX-1vRKgz7eEA1fIByKMtXKxA6-Vs1rSst8cwUeTkMnZyYrDPkzkUECyK7WXqXWFSh5jwnxJMdanffdyWzB/pub
](https://docs.google.com/document/d/e/2PACX-1vRKgz7eEA1fIByKMtXKxA6-Vs1rSst8cwUeTkMnZyYrDPkzkUECyK7WXqXWFSh5jwnxJMdanffdyWzB/pub)

### Pag general
[https://www.tacs-utn.com.ar/
](https://www.tacs-utn.com.ar/)

### Jira
[https://frba-team-hj2c1r3q.atlassian.net/jira/software/projects/MBA/boards/1](https://frba-team-hj2c1r3q.atlassian.net/jira/software/projects/MBA/boards/1)

## Instrucciones para levantar el servidor

### Desde la raiz del repositorio...

- Abrir Docker Desktop si no está abierto


- Compilar el código y crear un `.jar`:

  ```bash
  mvn clean package

- Crear contenedor:
  ```bash
  docker build -t servidor ./Servidor

- Ejecutar contenedor:
  ```bash
  docker run -p 8080:8080 servidor
  ```

Tras eso, el servidor estará escuchando peticiones en el puerto 8080.
Se puede comprobar haciendo alguna de las peticiones en la carpeta de ejemplos



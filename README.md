# Proyecto-SD

Sistema bibliotecario construido como sistema distribuido. Se utilizan patrones de comunicación cliente/servidor y publicador/suscriptor, los cuales se gestionan con la librería ZeroMQ. Próximamente se incluirá un gráfico que expliqué cómo está construído.

# Compilación

Se debe contar con un ambiente de Maven, esto será suficiente para la compilación. Tenga precaución con las direcciones IP que maneje. El cliente le solicitará que pase como argumento la dirección IP de la máquina en la que está el servidor al cual se conectará. En los archivos que tienen el rol de actores está la IP que se conecta a la máquina que tiene la base de datos con el nodo primario. Modifique lo que sea necesario para que la comunicación sea efectiva entre los programas.

La ejecución que se recomienda hacer de los programas es:

### 1. Suscriptor Devolución y Renovacion
Estos dos programas están dentro lo que en este proyecto se llama *actores*. Recuerde que al ser suscriptores puede ejecutarlos sin problema, ellos estarán a la espera de que un publicador envíe un tópico.

### 2. Actividad Préstamo
Este programa está dentro de lo que en este proyecto se llama *actores*. Es necesario que este programa se levante porque será el servidor de la comunión cliente/servidor que tendrá con el "Gestor Biblioteca"

### 3. Supervisor Server
Este es un programa que se encargará de supervisar que proceso de "Gestor Biblioteca" se encuentre en funcionamiento. En caso de que detecte que algo ha pasado en este proceso, entonces automáticamente pondrá en funcionamiento un proceso homólogo. 

***El orden de la ejecución de los tres items anteriores no afecta***

### 4. Gestor Biblioteca
Se encargará de gestionar las solucitudes del cliente y enviarlas a un actor correspondiente.

### 5. Cliente Biblioteca
Será quién hará las peticiones al gestor

# Recomendaciones

Se recomienda poner a correr las aplicaciones en el siguiente orden:
1. Suscriptor Devolución y Suscriptor Renovación
2. Actividad Préstamo
3. Supervisor Server
4. Gestor Biblioteca
5. Cliente Biblioteca

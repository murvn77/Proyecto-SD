# Proyecto-SD

Sistema bibliotecario construido como sistema distribuido. Se utilizan patrones de comunicación cliente/servidor y publicador/suscriptor, los cuales se gestionan con la librería ZeroMQ. Próximamente se incluirá un gráfico que expliqué cómo está construído.

# Compilación

Se debe contar con un ambiente de Maven, esto será suficiente para la compilación. Para la comunicación efectiva entre las aplicaciones se debe tener en consideración las direcciones IP y configurarlas en las aplicaciones dependiendo de la ubicación de las máquinas.

# Recomendaciones

Se recomienda poner a correr las aplicaciones en el siguiente orden:
1. Suscriptor Devolución
2. Suscriptor Renovación
3. Actividad Préstamo
4. Gestor Biblioteca
5. Cliente Biblioteca

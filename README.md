# tareaDistribuidos2
Integrantes:
Nombre: Felipe Monsalve Cantín
Rol: 201473512-k

Nombre: Felipe Vásquez Astudillo
Rol: 201473504-9

A grandes rasgos, el servidor (SemaforoImp) coordina todas las comunicaciones
entre los procesos. Se encarga de entregar las funciones RMI a estos y coordina
la dirección y entrega del token. Los requests emitidos por los procesos son
enviados al servidor, este lo difunde con multicast y luego de que el proceso
reciba el token y ejecute su zona crítica, el token actualiza su queue de
direccionamiento y es enviado al siguiente proceso.

Para correr el algoritmo se asume que se utilizan 5 procesos para probarlo. El
bearer por default es el proceso número 2 (desde 0 a 4).

1.- Correr en el terminal 'make' para compilar todos los archivos.
2.- Ejecutar 'java servidor/SemaforoImp 5', siendo 5 el número de procesos.
3.- Abrir un nuevo terminal en la carpeta conteniendo la tarea y ejecutar
    'make -j target'.
4.- El resultado de la ejecución se encontrará en el archivo 'log.txt'.
5.- Ejecutar 'make clean' para eliminar archivos basura y limpiar log.

En el log se indica el tiempo en que se registró el mensaje, el proceso que
generó el registro y los estados de los vectores RN y LN. RN corresponde al
vector de request del proceso, LN al vector de requests atendidos del token.

En caso de querer editar la cantidad de procesos se debe ir al archivo
GNUmakefile y agregar los procesos adicionales a la línea 'target:'. Cada
proceso debe ser declarado de la misma manera que todos los demás, cuidando
solamente cambiar la id.

Además cambiar la variable NRO para indicar el número de procesos corriendo y
BEARER en caso de querer cambiar el proceso con el token inicial.

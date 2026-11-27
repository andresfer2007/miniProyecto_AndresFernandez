# Programación Funcional y Reactiva

## Mini Proyecto: Higher Order Functions en Criptografía

Programaci´on Funcional y Reactiva Mini Proyecto: Higher Order Functions
en Criptograf´ıa Objetivos • Aplicar los conocimientos sobre funciones
de orden superior (Higher Order Func- tions). • Utilizar funciones como
par´ametros para definir estrategias de cifrado din´amicas. •
Implementar operaciones de transformaci´on sobre colecciones inmutables
(map). • Implementar un manejo adecuado de excepciones y validaci´on de
datos. Descripci´on del Trabajo El objetivo es crear un motor de cifrado
que reciba una funci´on de transformaci´on como par´ametro. Se deben
implementar dos m´etodos de cifrado espec´ıficos: 1. Cifrado C´esar
(Desplazamiento): Consiste en mover cada letra del alfabeto un n´umero
fijo de posiciones. E(x, k) = (pos(x) + k) mod 26 Donde x es el
car´acter y k es la clave (entero). 2. Cifrado XOR (L´ogico): Aplica la
operaci´on Exclusive OR a nivel de bits entre el c´odigo ASCII del
car´acter y una clave num´erica. E(x, k) = ASCII(x) ⊕k Nota de
implementaci´on: Para realizar esta operaci´on a nivel de bits en el
c´odigo, se debe utilizar el operador \^ (caret). Este m´etodo tiene la
propiedad de que aplicar la misma operaci´on dos veces devuelve el valor
original. Tambi´en se debe implementar un manejo adecuado de
excepciones, tales como: • Cadenas de texto vac´ıas o nulas. • Claves
inv´alidas. • Caracteres no soportados.

Actividades 1. Implementaci´on de Funciones Crear una funci´on de orden
superior llamada procesar mensaje (o similar) con los siguientes
par´ametros: • Mensaje (String). • Clave (Int). • Funci´on de cifrado
((Char, Int) -\> Char). La funci´on debe aplicar la transformaci´on a
cada car´acter del mensaje utilizando map e incluir bloques de manejo de
excepciones (Try/Catch o Try/Success/Failure). 2. Pruebas de Cifrado y
Descifrado Utilizar las funciones para resolver los siguientes casos: 1.
Caso C´esar: • Cifrar la palabra "FUNCIONAL" con clave k = 3. •
Descifrar el resultado obtenido (usando clave −3 o l´ogica inversa) para
re- cuperar el mensaje original. 2. Caso XOR: • Cifrar la palabra
"scala" con clave k = 5. • Utilizar el operador \^ dentro de la funci´on
lambda para transformar el car´acter. • Volver a aplicar la funci´on con
el resultado y la misma clave k = 5 para demostrar que se recupera
"scala". 3. Manejo de Errores Implementar pruebas donde se fuerce el
fallo para demostrar la robustez del c´odigo (ej. pasar una clave
excesivamente grande o caracteres especiales no manejados) y capturar el
error elegantemente. Ejemplos de Ejercicio A continuaci´on se detalla el
paso a paso l´ogico que el c´odigo debe realizar interna- mente.

Ejemplo 1: M´etodo C´esar Para el mensaje "AD" con clave k = 1: •
Convertir caracteres a ´ındices (A=0, B=1, ...). • Letra 'A' (0):
(0 + 1) mod 26 = 1 →'B' • Letra 'D' (3): (3 + 1) mod 26 = 4 →'E' •
Resultado: "BE" Ejemplo 2: M´etodo XOR Para el mensaje "scala" con clave
k = 5: Paso detallado para la primera letra 's': • C´odigo ASCII de 's':
115. • En c´odigo Scala: 115 \^ 5. • Representaci´on binaria 's': 0111
0011 • Representaci´on binaria 5: 0000 0101 • Operaci´on XOR (⊕): 0111
0110 (Decimal 118) • Car´acter ASCII 118: 'v' Tabla completa de
transformaci´on: Car´acter ASCII XOR 5 (C´alculo) Res. ASCII Cifrado s
115 115 ⊕5 118 v c 99 99 ⊕5 102 f a 97 97 ⊕5 100 d l 108 108 ⊕5 105 i a
97 97 ⊕5 100 d Resultado Final: "vfdid" Preguntas para Reflexionar •
¿Por qu´e el m´etodo XOR es su propia funci´on inversa utilizando el
mismo oper- ador \^, mientras que el C´esar requiere invertir el signo
de la clave? • ¿Qu´e ventajas ofrece el uso de map sobre un bucle for
tradicional en este con- texto? • ¿C´omo ayuda el tipado est´atico a
prevenir errores al pasar funciones como ar- gumentos?

Requisitos del Trabajo • C´odigo: Documentado en GitHub. •
Documentaci´on: Desarrollo y resultados en archivo README.md del
reposito- rio. Fecha de Entrega La fecha de entrega ser´a en la semana
8. Los estudiantes deber´an presentar el trabajo a su docente,
incluyendo la documentaci´on (README del repositorio) y el c´odigo
fuente utilizado. Todo debe estar alojado en un proyecto de GitHub.

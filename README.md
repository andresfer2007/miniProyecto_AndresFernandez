# Mini Proyecto: Higher Order Functions en Criptografía

**Asignatura:** Programación Funcional y Reactiva
**Lenguaje:** Scala 3
**Autor:** Andres Fernandez
**Entrega:** Semana 8

---

## 1. Objetivos

- Aplicar los conocimientos sobre funciones de orden superior (*Higher Order Functions*).
- Utilizar funciones como parámetros para definir estrategias de cifrado dinámicas.
- Implementar transformaciones sobre colecciones inmutables mediante `map`.
- Implementar un manejo adecuado de excepciones y validación de datos.

## 2. Descripción del trabajo

Se construye un **motor de cifrado** que recibe la función de transformación como parámetro. Se implementan dos métodos de cifrado:

### 2.1 Cifrado César (desplazamiento)

Mueve cada letra del alfabeto un número fijo de posiciones:

```
E(x, k) = (pos(x) + k) mod 26
```

donde `x` es el carácter y `k` la clave entera.

### 2.2 Cifrado XOR (lógico)

Aplica la operación *Exclusive OR* a nivel de bits entre el código ASCII del carácter y una clave numérica:

```
E(x, k) = ASCII(x) ⊕ k
```

En Scala se usa el operador `^`. Aplicar la misma operación dos veces devuelve el valor original.

### 2.3 Manejo de excepciones

El motor valida y captura:

- Cadenas de texto vacías o nulas.
- Claves inválidas.
- Caracteres no soportados.

## 3. Estructura del proyecto

```
funcioenss/
└── src/main/scala/miniProyecto/
    └── Main.scala      // procesarMensaje, cifradoCesar, cifradoXor, casos de prueba, errores y verificaciones
```

## 4. Implementación


```scala
package miniProyecto

import scala.util.{Try, Success, Failure}

//funcion para procesar mensaje
def procesarMensaje(mensaje: String, clave: Int, cifrado: (Char, Int) => Char): Try[String] =
  Try {
    if (mensaje == null) throw new IllegalArgumentException("El mensaje es nulo")
    if (mensaje.isEmpty) throw new IllegalArgumentException("El mensaje está vacío")
    mensaje.map(c => cifrado(c, clave))
  }
//metodo de cifrado 1
def cifradoCesar(c: Char, clave: Int): Char = {
  if (clave < -25 || clave > 25)
    throw new IllegalArgumentException(s"Clave inválida para César: $clave (rango -25 a 25)")
  c match {
    case l if l >= 'A' && l <= 'Z' => (Math.floorMod(l - 'A' + clave, 26) + 'A').toChar
    case l if l >= 'a' && l <= 'z' => (Math.floorMod(l - 'a' + clave, 26) + 'a').toChar
    case _ => throw new UnsupportedOperationException(s"Carácter no soportado: '$c'")
  }
}
//metodo de cifrado 2
def cifradoXor(c: Char, clave: Int): Char = {
  if (clave < 0 || clave > 127) throw new IllegalArgumentException(s"Clave inválida para XOR: $clave (rango 0-127)")
  if (c.toInt > 127) throw new UnsupportedOperationException(s"Carácter no ASCII: '$c'")
  (c.toInt ^ clave).toChar
}


object Main extends App {

  def mostrar(titulo: String, r: Try[String]): Unit = r match {
    case Success(t) => println(s"[OK]    $titulo -> $t")
    case Failure(e) => println(s"[ERROR] $titulo -> ${e.getClass.getSimpleName}: ${e.getMessage}")
  }

  // ---------- 1. CASO CÉSAR ----------
  println("=== CASO CÉSAR ===")
  val cesarCifrado = procesarMensaje("FUNCIONAL", 3, cifradoCesar)
  mostrar("Cifrar 'FUNCIONAL' k=3", cesarCifrado)

  val cesarDescifrado = cesarCifrado.flatMap(t => procesarMensaje(t, -3, cifradoCesar))
  mostrar("Descifrar con k=-3", cesarDescifrado)

  // Ejemplo del enunciado: "AD" con k=1 -> "BE"
  mostrar("Ejemplo 'AD' k=1", procesarMensaje("AD", 1, cifradoCesar))

  // ---------- 2. CASO XOR (con ^ dentro de la lambda) ----------
  println("\n=== CASO XOR ===")
  val xorLambda: (Char, Int) => Char = (c, k) => (c.toInt ^ k).toChar

  val xorCifrado = procesarMensaje("scala", 5, xorLambda)
  mostrar("Cifrar 'scala' k=5", xorCifrado)

  val xorDescifrado = xorCifrado.flatMap(t => procesarMensaje(t, 5, xorLambda))
  mostrar("Aplicar de nuevo k=5", xorDescifrado)

  // Detalle de la primera letra: 's' = 115 = 0111 0011, 5 = 0000 0101 -> 0111 0110 = 118 = 'v'
  val s = 's'
  println(f"\nDetalle 's': ${s.toInt}%d (${s.toInt.toBinaryString.reverse.padTo(8, '0').reverse}) " +
    f"^ 5 (${5.toBinaryString.reverse.padTo(8, '0').reverse}) = " +
    f"${s.toInt ^ 5}%d (${(s.toInt ^ 5).toBinaryString.reverse.padTo(8, '0').reverse}) = '${(s.toInt ^ 5).toChar}'")

  // También con la función cifradoXor validada
  mostrar("cifradoXor 'scala' k=5", procesarMensaje("scala", 5, cifradoXor))

  // ---------- 3. MANEJO DE ERRORES ----------
  println("\n=== MANEJO DE ERRORES ===")
  mostrar("Mensaje vacío",              procesarMensaje("", 3, cifradoCesar))
  mostrar("Mensaje nulo",               procesarMensaje(null, 3, cifradoCesar))
  mostrar("César clave enorme (9999)",  procesarMensaje("HOLA", 9999, cifradoCesar))
  mostrar("César caracteres especiales", procesarMensaje("HOLA 123!", 3, cifradoCesar))
  mostrar("XOR clave enorme (500)",     procesarMensaje("scala", 500, cifradoXor))
  mostrar("XOR clave negativa (-1)",    procesarMensaje("scala", -1, cifradoXor))
  mostrar("XOR carácter no ASCII",      procesarMensaje("scalá€", 5, cifradoXor))

  // ---------- VERIFICACIÓN AUTOMÁTICA ----------
  println("\n=== VERIFICACIÓN (assert) ===")
  assert(procesarMensaje("AD", 1, cifradoCesar) == Success("BE"))
  assert(cesarCifrado == Success("IXQFLRQDO"))
  assert(cesarDescifrado == Success("FUNCIONAL"))
  assert(xorCifrado == Success("vfdid"))
  assert(xorDescifrado == Success("scala"))
  assert((115 ^ 5) == 118 && (115 ^ 5).toChar == 'v')
  assert(procesarMensaje("", 3, cifradoCesar).isFailure)
  assert(procesarMensaje(null, 3, cifradoCesar).isFailure)
  assert(procesarMensaje("HOLA", 9999, cifradoCesar).isFailure)
  assert(procesarMensaje("HOLA 123", 3, cifradoCesar).isFailure)
  assert(procesarMensaje("scala", 500, cifradoXor).isFailure)
  println("Todas las verificaciones pasaron correctamente ✔")
}
```

## 5. Ejecución

"C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot\bin\java.exe" "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\lib\idea_rt.jar=57249" -Dfile.encoding=UTF-8 -classpath C:\Users\USER\IdeaProjects\funcioenss\target\out\jvm\scala-3.9.0\funcioenss\classes;C:\Users\USER\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala3-library_3\3.9.0\scala3-library_3-3.9.0.jar;C:\Users\USER\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\3.9.0\scala-library-3.9.0.jar miniProyecto.Main
=== CASO CÉSAR ===
[OK]    Cifrar 'FUNCIONAL' k=3 -> IXQFLRQDO
[OK]    Descifrar con k=-3 -> FUNCIONAL
[OK]    Ejemplo 'AD' k=1 -> BE

=== CASO XOR ===
[OK]    Cifrar 'scala' k=5 -> vfdid
[OK]    Aplicar de nuevo k=5 -> scala

Detalle 's': 115 (01110011) ^ 5 (00000101) = 118 (01110110) = 'v'
[OK]    cifradoXor 'scala' k=5 -> vfdid

=== MANEJO DE ERRORES ===
[ERROR] Mensaje vacío -> IllegalArgumentException: El mensaje está vacío
[ERROR] Mensaje nulo -> IllegalArgumentException: El mensaje es nulo
[ERROR] César clave enorme (9999) -> IllegalArgumentException: Clave inválida para César: 9999 (rango -25 a 25)
[ERROR] César caracteres especiales -> UnsupportedOperationException: Carácter no soportado: ' '
[ERROR] XOR clave enorme (500) -> IllegalArgumentException: Clave inválida para XOR: 500 (rango 0-127)
[ERROR] XOR clave negativa (-1) -> IllegalArgumentException: Clave inválida para XOR: -1 (rango 0-127)
[ERROR] XOR carácter no ASCII -> UnsupportedOperationException: Carácter no ASCII: 'á'

=== VERIFICACIÓN (assert) ===
Todas las verificaciones pasaron correctamente ✔

Process finished with exit code 0

## 6. Resultados

### 6.1 Caso César

| Operación | Entrada | Clave | Resultado |
|---|---|---|---|
| Cifrar | `FUNCIONAL` | 3 | `IXQFLRQDO` |
| Descifrar | `IXQFLRQDO` | -3 | `FUNCIONAL` |
| Ejemplo del enunciado | `AD` | 1 | `BE` |

Paso a paso del ejemplo `AD` con `k = 1`:

- `'A'` (0): (0 + 1) mod 26 = 1 → `'B'`
- `'D'` (3): (3 + 1) mod 26 = 4 → `'E'`
- Resultado: `BE`

### 6.2 Caso XOR

Mensaje `scala` con `k = 5`:

| Carácter | ASCII | Cálculo | Resultado ASCII | Cifrado |
|---|---|---|---|---|
| s | 115 | 115 ⊕ 5 | 118 | v |
| c | 99 | 99 ⊕ 5 | 102 | f |
| a | 97 | 97 ⊕ 5 | 100 | d |
| l | 108 | 108 ⊕ 5 | 105 | i |
| a | 97 | 97 ⊕ 5 | 100 | d |

Resultado: `vfdid`. Al aplicar de nuevo la misma función con `k = 5` se recupera `scala`.

Detalle de la primera letra:

```
's'  = 115 = 0111 0011
5    =       0000 0101
XOR  =       0111 0110 = 118 = 'v'
```

### 6.3 Manejo de errores

| Prueba | Excepción capturada | Mensaje |
|---|---|---|
| Mensaje vacío | `IllegalArgumentException` | El mensaje está vacío |
| Mensaje nulo | `IllegalArgumentException` | El mensaje es nulo |
| César, clave 9999 | `IllegalArgumentException` | Clave inválida para César: 9999 (rango -25 a 25) |
| César, `"HOLA 123!"` | `UnsupportedOperationException` | Carácter no soportado: ' ' |
| XOR, clave 500 | `IllegalArgumentException` | Clave inválida para XOR: 500 (rango 0-127) |
| XOR, clave -1 | `IllegalArgumentException` | Clave inválida para XOR: -1 (rango 0-127) |
| XOR, `"scalá€"` | `UnsupportedOperationException` | Carácter no ASCII: 'á' |

Ninguna de estas pruebas detiene el programa: `Try` captura la excepción y se devuelve un `Failure` que se muestra de forma controlada.

### 6.4 Salida esperada

```
=== CASO CÉSAR ===
[OK]    Cifrar 'FUNCIONAL' k=3 -> IXQFLRQDO
[OK]    Descifrar con k=-3 -> FUNCIONAL
[OK]    Ejemplo 'AD' k=1 -> BE

=== CASO XOR ===
[OK]    Cifrar 'scala' k=5 -> vfdid
[OK]    Aplicar de nuevo k=5 -> scala

Detalle 's': 115 (01110011) ^ 5 (00000101) = 118 (01110110) = 'v'
[OK]    cifradoXor 'scala' k=5 -> vfdid

=== MANEJO DE ERRORES ===
[ERROR] Mensaje vacío -> IllegalArgumentException: El mensaje está vacío
[ERROR] Mensaje nulo -> IllegalArgumentException: El mensaje es nulo
[ERROR] César clave enorme (9999) -> IllegalArgumentException: Clave inválida para César: 9999 (rango -25 a 25)
[ERROR] César caracteres especiales -> UnsupportedOperationException: Carácter no soportado: ' '
[ERROR] XOR clave enorme (500) -> IllegalArgumentException: Clave inválida para XOR: 500 (rango 0-127)
[ERROR] XOR clave negativa (-1) -> IllegalArgumentException: Clave inválida para XOR: -1 (rango 0-127)
[ERROR] XOR carácter no ASCII -> UnsupportedOperationException: Carácter no ASCII: 'á'

=== VERIFICACIÓN (assert) ===
Todas las verificaciones pasaron correctamente ✔
```

## 7. Preguntas para reflexionar

**¿Por qué el método XOR es su propia función inversa con el mismo operador `^`, mientras que el César requiere invertir el signo de la clave?**

Porque XOR es una operación que se anula a sí misma: `(a ⊕ k) ⊕ k = a ⊕ (k ⊕ k) = a ⊕ 0 = a`. Aplicar la misma clave dos veces devuelve el valor original. En cambio, el César es una suma módulo 26: para deshacer un desplazamiento de `+k` hay que desplazar `-k` (equivalente a `26 - k`), por lo que la clave debe invertirse.

**¿Qué ventajas ofrece el uso de `map` sobre un bucle `for` tradicional en este contexto?**

- Es declarativo: expresa *qué* se hace (transformar cada carácter) y no *cómo* iterar.
- No requiere variables mutables ni índices, lo que evita errores de límites y de estado.
- Devuelve una nueva colección sin modificar la original, respetando la inmutabilidad.
- Permite pasar la transformación como función, lo que facilita reutilizar el mismo motor con César, XOR u otros cifrados.

**¿Cómo ayuda el tipado estático a prevenir errores al pasar funciones como argumentos?**

La firma `(Char, Int) => Char` obliga a que cualquier función que se pase tenga exactamente esos tipos de entrada y salida. Si se intenta pasar una función con otra firma (por ejemplo `(String, Int) => String`), el compilador lo rechaza antes de ejecutar el programa. Así se detectan en compilación errores que en un lenguaje dinámico aparecerían en tiempo de ejecución.

## 8. Conclusiones

- Las funciones de orden superior permiten desacoplar el *motor* (`procesarMensaje`) de la *estrategia* de cifrado (`cifradoCesar`, `cifradoXor` o una lambda).
- `Try` con `Success`/`Failure` centraliza el manejo de errores y evita que el programa termine abruptamente.
- Las validaciones de clave y de caracteres hacen el código robusto ante entradas inválidas.
- Las pruebas con `assert` demuestran de forma automática que las funciones cumplen los casos del enunciado.

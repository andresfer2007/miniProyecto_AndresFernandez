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
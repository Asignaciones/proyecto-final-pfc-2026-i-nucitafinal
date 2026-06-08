package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._

@RunWith(classOf[JUnitRunner])
class AsignacionAulasTest extends AnyFunSuite {

  // Ejemplo 1 del enunciado
  val c1: Cursos    = Vector(("M01", 4, 8, 25), ("M02", 6, 10, 30), ("M03", 12, 16, 20))
  val a1: Aulas     = Vector(("E101", 30), ("E102", 40))
  val d1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val w: Pesos      = (1000, 100, 1, 2)

  // ===========================================================================
  // 1. PRUEBAS PARA LA FUNCIÓN: solapan
  // ===========================================================================
  test("solapan - Caso 1: Cursos se cruzan completamente (mismo horario)") {
    val c1 = ("C1", 4, 8, 20)  // 8:00 - 10:00
    val c2 = ("C2", 4, 8, 30)  // 8:00 - 10:00
    assert(solapan(c1, c2) === true)
  }

  test("solapan - Caso 2: Traslape parcial (el segundo inicia antes de que termine el primero)") {
    val c1 = ("C1", 4, 8, 20)  // 8:00 - 10:00
    val c2 = ("C2", 6, 10, 25) // 9:00 - 11:00
    assert(solapan(c1, c2) === true)
  }

  test("solapan - Caso 3: Cursos consecutivos exactos (no se solapan por ser intervalo abierto [ini, fin) )") {
    val c1 = ("C1", 4, 8, 20)  // 8:00 - 10:00
    val c2 = ("C2", 8, 12, 15) // 10:00 - 12:00
    assert(solapan(c1, c2) === false)
  }

  test("solapan - Caso 4: Completamente separados en el tiempo") {
    val c1 = ("C1", 0, 4, 20)   // 6:00 - 8:00
    val c2 = ("C2", 12, 16, 20) // 12:00 - 14:00
    assert(solapan(c1, c2) === false)
  }

  test("solapan - Caso 5: Un curso está contenido totalmente dentro de la duración del otro") {
    val c1 = ("C1", 2, 10, 40) // 7:00 - 11:00
    val c2 = ("C2", 4, 6, 15)  // 8:00 - 9:00
    assert(solapan(c1, c2) === true)
  }

  // ===========================================================================
  // 2. PRUEBAS PARA LA FUNCIÓN: choques
  // ===========================================================================
  test("choques - Caso 1: Sin choques porque están en aulas diferentes") {
    val cursos = Vector(("C0", 4, 8, 20), ("C1", 4, 8, 30))
    val asignacion = Vector(0, 1) // Aula 0 y Aula 1
    assert(choques(cursos, asignacion) === 0)
  }

  test("choques - Caso 2: Choque detectado (mismo horario y misma aula)") {
    val cursos = Vector(("C0", 4, 8, 20), ("C1", 6, 10, 30))
    val asignacion = Vector(0, 0) // Ambos en Aula 0
    assert(choques(cursos, asignacion) === 1)
  }

  test("choques - Caso 3: Cursos consecutvios en la misma aula (no debe contar como choque)") {
    val cursos = Vector(("C0", 4, 8, 20), ("C1", 8, 12, 30))
    val asignacion = Vector(1, 1) // Ambos en Aula 1 consecutivamente
    assert(choques(cursos, asignacion) === 0)
  }

  test("choques - Caso 4: Múltiples choques concurrentes combinados") {
    val cursos = Vector(
      ("C0", 4, 8, 20),
      ("C1", 5, 9, 25),
      ("C2", 6, 10, 30)
    )
    val asignacion = Vector(0, 0, 0) // Todos en la misma aula y todos se cruzan entre sí
    // Parejas en choque: (0,1), (0,2) y (1,2) -> Total 3
    assert(choques(cursos, asignacion) === 3)
  }

  test("choques - Caso 5: Cursos que se cruzan pero uno de ellos no está asignado (-1)") {
    val cursos = Vector(("C0", 4, 8, 20), ("C1", 4, 8, 30))
    val asignacion = Vector(0, -1) // El segundo no tiene aula asignada
    assert(choques(cursos, asignacion) === 0)
  }

  // ===========================================================================
  // 3. PRUEBAS PARA LA FUNCIÓN: capacidadFallida
  // ===========================================================================
  test("capacidadFallida - Caso 1: Todas las aulas tienen capacidad suficiente") {
    val anonymityCursos = Vector(("C0", 4, 8, 20), ("C1", 4, 8, 35))
    val aulas = Vector(("E0", 40), ("E1", 50))
    val asignacion = Vector(0, 1)
    assert(capacidadFallida(anonymityCursos, aulas, asignacion) === 0)
  }

  test("capacidadFallida - Caso 2: Un curso excede la capacidad exacta de su aula asignada") {
    val anonymityCursos = Vector(("C0", 4, 8, 45)) // 45 estudiantes
    val aulas = Vector(("E0", 40))                 // Capacidad 40
    val asignacion = Vector(0)
    assert(capacidadFallida(anonymityCursos, aulas, asignacion) === 1)
  }

  test("capacidadFallida - Caso 3: Capacidad exacta límite (no debe fallar)") {
    val anonymityCursos = Vector(("C0", 4, 8, 30))
    val aulas = Vector(("E0", 30))
    val asignacion = Vector(0)
    assert(capacidadFallida(anonymityCursos, aulas, asignacion) === 0)
  }

  test("capacidadFallida - Caso 4: Múltiples fallas simultáneas") {
    val anonymityCursos = Vector(("C0", 4, 8, 50), ("C1", 6, 10, 40), ("C2", 12, 16, 10))
    val aulas = Vector(("E0", 30)) // Solo una aula pequeña
    val asignacion = Vector(0, 0, 0) // C0 falla (50>30), C1 falla (40>30), C2 ok (10<=30)
    assert(capacidadFallida(anonymityCursos, aulas, asignacion) === 2)
  }

  test("capacidadFallida - Caso 5: El curso requiere más espacio pero no está asignado (-1)") {
    val anonymityCursos = Vector(("C0", 4, 8, 60))
    val aulas = Vector(("E0", 30))
    val asignacion = Vector(-1) // Sin asignar, no se cuenta el fallo
    assert(capacidadFallida(anonymityCursos, aulas, asignacion) === 0)
  }

  // ===========================================================================
  // 4. PRUEBAS PARA LA FUNCIÓN: desperdicio
  // ===========================================================================
  test("desperdicio - Caso 1: Desperdicio cero en ajuste exacto") {
    val anonymityCursos = Vector(("C0", 4, 8, 30))
    val aulas = Vector(("E0", 30))
    val asignacion = Vector(0)
    assert(desperdicio(anonymityCursos, aulas, asignacion) === 0)
  }

  test("desperdicio - Caso 2: Suma de desperdicio positivo en aulas holgadas") {
    val anonymityCursos = Vector(("C0", 4, 8, 25), ("C1", 12, 16, 15))
    val aulas = Vector(("E0", 40)) // Aula de 40 para ambos (en diferentes horarios)
    val asignacion = Vector(0, 0)
    // Desperdicio C0: 40 - 25 = 15
    // Desperdicio C1: 40 - 15 = 25 -> Total: 40
    assert(desperdicio(anonymityCursos, aulas, asignacion) === 40)
  }

  test("desperdicio - Caso 3: Curso inválido por capacidad (debe retornar 0 en desperdicio según la regla)") {
    val anonymityCursos = Vector(("C0", 4, 8, 50))
    val aulas = Vector(("E0", 30))
    val asignacion = Vector(0) // Asignación inválida, no aporta al desperdicio se penaliza en CF
    assert(desperdicio(anonymityCursos, aulas, asignacion) === 0)
  }

  test("desperdicio - Caso 4: Mezcla de curso óptimo, curso con desperdicio y curso sin asignar") {
    val anonymityCursos = Vector(
      ("C0", 4, 8, 20),  // Aula 0 (35) -> desp = 15
      ("C1", 4, 8, 35),  // Aula 1 (35) -> desp = 0
      ("C2", 12, 16, 10) // Sin asignar -> desp = 0
    )
    val aulas = Vector(("E0", 35), ("E1", 35))
    val asignacion = Vector(0, 1, -1)
    assert(desperdicio(anonymityCursos, aulas, asignacion) === 15)
  }

  test("desperdicio - Caso 5: Ningún curso asignado") {
    val anonymityCursos = Vector(("C0", 4, 8, 20), ("C1", 8, 12, 25))
    val aulas = Vector(("E0", 50))
    val asignacion = Vector(-1, -1)
    assert(desperdicio(anonymityCursos, aulas, asignacion) === 0)
  }

  // ===========================================================================
  // 5. PRUEBAS PARA LA FUNCIÓN: movilidad
  // ===========================================================================
  test("movilidad - Caso 1: Un solo curso asignado (no hay desplazamientos)") {
    val anonymityCursos = Vector(("C0", 4, 8, 20))
    val aulas = Vector(("E0", 30), ("E1", 40))
    val d = Vector(Vector(0, 5), Vector(5, 0))
    val asignacion = Vector(0)
    assert(movilidad(anonymityCursos, aulas, d, asignacion) === 0)
  }

  test("movilidad - Caso 2: Cursos ordenados cronológicamente") {
    val anonymityCursos = Vector(
      ("C0", 4, 8, 20),  // Inicia bloque 4 -> Aula 0
      ("C1", 10, 14, 20) // Inicia bloque 10 -> Aula 1
    )
    val aulas = Vector(("E0", 30), ("E1", 30))
    val d = Vector(
      Vector(0, 8),
      Vector(8, 0)
    )
    val asignacion = Vector(0, 1) // Trayecto de Aula 0 a Aula 1 = 8
    assert(movilidad(anonymityCursos, aulas, d, asignacion) === 8)
  }

  test("movilidad - Caso 3: Cursos desordenados en el vector de entrada (deben ser ordenados por hora de inicio)") {
    val anonymityCursos = Vector(
      ("C0", 12, 16, 20), // Inicia tarde (bloque 12) -> Aula 1
      ("C1", 4, 8, 20)    // Inicia temprano (bloque 4) -> Aula 0
    )
    val aulas = Vector(("E0", 30), ("E1", 30))
    val d = Vector(
      Vector(0, 12),
      Vector(12, 0)
    )
    val asignacion = Vector(1, 0)
    // El orden cronológico es C1 (Aula 0) y luego C0 (Aula 1).
    // Distancia de Aula 0 a Aula 1 = 12.
    assert(movilidad(anonymityCursos, aulas, d, asignacion) === 12)
  }

  test("movilidad - Caso 4: Tres cursos consecutivos pasando por diferentes aulas") {
    val anonymityCursos = Vector(
      ("C0", 4, 8, 20),  // Inicia 4 -> Aula 0
      ("C1", 8, 12, 20), // Inicia 8 -> Aula 1
      ("C2", 12, 16, 20) // Inicia 12 -> Aula 2
    )
    val aulas = Vector(("E0", 30), ("E1", 30), ("E2", 30))
    val d = Vector(
      Vector(0, 4, 9),
      Vector(4, 0, 3),
      Vector(9, 3, 0)
    )
    val asignacion = Vector(0, 1, 2)
    // De Aula 0 a 1 = 4. De Aula 1 a 2 = 3. Total = 7
    assert(movilidad(anonymityCursos, aulas, d, asignacion) === 7)
  }

  test("movilidad - Caso 5: Ignorar cursos que no están asignados") {
    val anonymityCursos = Vector(
      ("C0", 4, 8, 20),   // Inicia 4 -> Aula 0
      ("C1", 8, 12, 20),  // Inicia 8 -> Sin Asignar (-1)
      ("C2", 12, 16, 20)  // Inicia 12 -> Aula 1
    )
    val aulas = Vector(("E0", 30), ("E1", 30))
    val d = Vector(
      Vector(0, 15),
      Vector(15, 0)
    )
    val asignacion = Vector(0, -1, 1)
    // Al omitir C1, la transición directa es del curso C0 (Aula 0) al curso C2 (Aula 1) = 15
    assert(movilidad(anonymityCursos, aulas, d, asignacion) === 15)
  }

  // ===========================================================================
  // 6. PRUEBAS PARA LA FUNCIÓN: costoAsignacion
  // ===========================================================================
  test("costoAsignacion - Caso 1: Costo total balanceado usando pesos del PDF") {
    // Escenario simplificado del ejemplo 1 del PDF
    val anonymityCursos = Vector(
      ("M01", 4, 8, 25),
      ("M02", 6, 10, 30),
      ("M03", 12, 16, 20)
    )
    val aulas = Vector(("E101", 30), ("E102", 40))
    val d = Vector(Vector(0, 3), Vector(3, 0))
    val pesos = (1000, 100, 1, 2) // w_CH, w_CF, w_DE, w_MV

    val asignacion = Vector(0, 0, 1) // M01 y M02 chocan en Aula 0

    // CH = 1 (M01 y M02) -> 1 * 1000 = 1000
    // CF = 0 (Todos caben) -> 0 * 100 = 0
    // DE = (30-25) + (30-30) + (40-20) = 5 + 0 + 20 = 25 -> 25 * 1 = 25
    // MV = M01(A0) -> M02(A0) -> M03(A1) = d(0)(0) + d(0)(1) = 0 + 3 = 3 -> 3 * 2 = 6
    // Costo Total Esperado = 1000 + 0 + 25 + 6 = 1031
    assert(costoAsignacion(anonymityCursos, aulas, d, asignacion, pesos) === 1031)
  }

  test("costoAsignacion - Caso 2: Costo ideal de asignación óptima libre de penalizaciones") {
    val anonymityCursos = Vector(("C0", 4, 8, 30))
    val aulas = Vector(("E0", 30))
    val d = Vector(Vector(0))
    val pesos = (1000, 100, 1, 2)
    val asignacion = Vector(0) // Ajuste perfecto, sin choques, sin movilidad, sin desperdicio
    assert(costoAsignacion(anonymityCursos, aulas, d, asignacion, pesos) === 0)
  }

  test("costoAsignacion - Caso 3: Impacto severo por pesos de capacidad fallida") {
    val anonymityCursos = Vector(("C0", 4, 8, 50)) // 50 estudiantes
    val aulas = Vector(("E0", 30))                 // Capacidad 30
    val d = Vector(Vector(0))
    val pesos = (10, 500, 2, 1) // w_CF es 500
    val asignacion = Vector(0)
    // CH = 0, CF = 1 (*500), DE = 0, MV = 0 -> Total = 500
    assert(costoAsignacion(anonymityCursos, aulas, d, asignacion, pesos) === 500)
  }

  test("costoAsignacion - Caso 4: Multiplicadores en cero anulan los costos parciales") {
    val anonymityCursos = Vector(("C0", 4, 8, 20), ("C1", 4, 8, 20))
    val aulas = Vector(("E0", 40))
    val d = Vector(Vector(0))
    val pesos = (0, 0, 0, 0) // Todos los pesos valen cero
    val asignacion = Vector(0, 0) // Chocan y desperdician espacio, pero no cuesta nada
    assert(costoAsignacion(anonymityCursos, aulas, d, asignacion, pesos) === 0)
  }

  test("costoAsignacion - Caso 5: Sin cursos asignados da costo cero") {
    val anonymityCursos = Vector(("C0", 4, 8, 25), ("C1", 6, 10, 30))
    val aulas = Vector(("E0", 35))
    val d = Vector(Vector(0))
    val pesos = (1000, 100, 5, 2)
    val asignacion = Vector(-1, -1)
    assert(costoAsignacion(anonymityCursos, aulas, d, asignacion, pesos) === 0)
  }




  // test nuevos funcion generarAsignaciones

  test("generarAsignaciones: 1 curso y 2 aulas genera exactamente 2 asignaciones") {
    val resultado = generarAsignaciones(1, 2)
    assert(resultado.length == 2)
  }

  test("generarAsignaciones: 0 cursos devuelve una asignacion vacia") {
    val resultado = generarAsignaciones(0, 5)
    assert(resultado == Vector(Vector()))
  }

  test("generarAsignaciones: todas las asignaciones tienen longitud igual a n") {
    val resultado = generarAsignaciones(4, 2)
    assert(resultado.forall(asig => asig.length == 4))
  }

  test("generarAsignaciones: todas las aulas generadas estan entre 0 y m-1") {
    val resultado = generarAsignaciones(3, 4)
    assert(
      resultado.forall(asig =>
        asig.forall(aula => aula >= 0 && aula < 4)
      )
    )
  }

  test("generarAsignaciones: 2 cursos y 1 aula solo genera una asignacion") {
    val resultado = generarAsignaciones(2, 1)
    assert(resultado == Vector(Vector(0,0)))
  }


  // Test nuevos funcion asignacionOptima

  test("asignacionOptima: devuelve una asignacion del mismo tamaño que cursos") {
    val (asig, _) = asignacionOptima(c1, a1, d1, w)
    assert(asig.length == c1.length)
  }

  test("asignacionOptima: el costo calculado coincide con costoAsignacion") {
    val (asig, costo) = asignacionOptima(c1, a1, d1, w)
    assert(costo == costoAsignacion(c1, a1, d1, asig, w))
  }

  test("asignacionOptima: devuelve una asignacion valida") {
    val (asig, _) = asignacionOptima(c1, a1, d1, w)
    assert(asig.forall(aula => aula >= 0 && aula < a1.length))
  }

  test("asignacionOptima: encuentra el costo minimo global") {
    val todas = generarAsignaciones(c1.length, a1.length)
    val costos = todas.map(a =>
      costoAsignacion(c1, a1, d1, a, w)
    )
    val (_, costoOptimo) = asignacionOptima(c1, a1, d1, w)
    assert(costoOptimo == costos.min)
  }

  test("asignacionOptima: devuelve una asignacion perteneciente al conjunto generado") {
    val (asig, _) = asignacionOptima(c1, a1, d1, w)
    val todas = generarAsignaciones(c1.length, a1.length)
    assert(todas.contains(asig))
  }

}

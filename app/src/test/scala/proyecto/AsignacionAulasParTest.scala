package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._
import AsignacionAulasPar._

@RunWith(classOf[JUnitRunner])
class AsignacionAulasParTest extends AnyFunSuite {

  val c1: Cursos    = Vector(("M01", 4, 8, 25), ("M02", 6, 10, 30), ("M03", 12, 16, 20))
  val a1: Aulas     = Vector(("E101", 30), ("E102", 40))
  val d1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val w: Pesos      = (1000, 100, 1, 2)

  // ChoquesPar
  test("choquesPar sin choques en aulas distintas") {
    val cursos = Vector(
      ("A", 8, 10, 30),
      ("B", 9, 11, 20)
    )
    val a = Vector(0, 1)

    assert(choquesPar(cursos, a) == 0)
  }

  test("choquesPar detecta un choque") {
    val cursos = Vector(
      ("A", 8, 10, 30),
      ("B", 9, 11, 20)
    )
    val a = Vector(0, 0)

    assert(choquesPar(cursos, a) == 1)
  }

  test("choquesPar detecta multiples choques") {
    val cursos = Vector(
      ("A", 8, 12, 30),
      ("B", 9, 11, 20),
      ("C", 10, 13, 25)
    )
    val a = Vector(0, 0, 0)

    assert(choquesPar(cursos, a) == 3)
  }

  test("choquesPar ignora cursos sin asignar") {
    val cursos = Vector(
      ("A", 8, 10, 30),
      ("B", 9, 11, 20)
    )
    val a = Vector(-1, -1)

    assert(choquesPar(cursos, a) == 0)
  }

  test("choquesPar con vector vacio") {
    assert(choquesPar(Vector(), Vector()) == 0)
  }

  // DesperdicioPar

  test("desperdicioPar sin desperdicio") {
    val cursos = Vector(("A", 8, 10, 30))
    val aulas = Vector(("X", 30))
    val a = Vector(0)

    assert(desperdicioPar(cursos, aulas, a) == 0)
  }

  test("desperdicioPar calcula desperdicio") {
    val cursos = Vector(("A", 8, 10, 20))
    val aulas = Vector(("X", 40))
    val a = Vector(0)

    assert(desperdicioPar(cursos, aulas, a) == 20)
  }

  test("desperdicioPar suma desperdicios") {
    val cursos = Vector(
      ("A", 8, 10, 20),
      ("B", 10, 12, 30)
    )
    val aulas = Vector(
      ("X", 40),
      ("Y", 50)
    )
    val a = Vector(0, 1)

    assert(desperdicioPar(cursos, aulas, a) == 40)
  }

  test("desperdicioPar ignora cursos sin asignar") {
    val cursos = Vector(("A", 8, 10, 20))
    val aulas = Vector(("X", 40))
    val a = Vector(-1)

    assert(desperdicioPar(cursos, aulas, a) == 0)
  }

  test("desperdicioPar aula insuficiente") {
    val cursos = Vector(("A", 8, 10, 50))
    val aulas = Vector(("X", 40))
    val a = Vector(0)

    assert(desperdicioPar(cursos, aulas, a) == 0)
  }

  // MovilidadPar

  test("movilidadPar con un solo curso") {
    val cursos = Vector(
      ("A", 8, 10, 30)
    )

    val d = Vector(
      Vector(0)
    )

    val a = Vector(0)

    assert(
      movilidadPar(cursos, Vector(), d, a) == 0
    )
  }

  test("movilidadPar dos cursos") {
    val cursos = Vector(
      ("A", 8, 10, 30),
      ("B", 10, 12, 20)
    )

    val d = Vector(
      Vector(0, 5),
      Vector(5, 0)
    )

    val a = Vector(0, 1)

    assert(movilidadPar(cursos, Vector(), d, a) == 5)
  }

  test("movilidadPar suma desplazamientos") {
    val cursos = Vector(
      ("A", 8, 10, 30),
      ("B", 10, 12, 20),
      ("C", 12, 14, 25)
    )

    val d = Vector(
      Vector(0, 5, 8),
      Vector(5, 0, 3),
      Vector(8, 3, 0)
    )

    val a = Vector(0, 1, 2)

    assert(movilidadPar(cursos, Vector(), d, a) == 8)
  }

  test("movilidadPar ignora cursos no asignados") {
    val cursos = Vector(
      ("A", 8, 10, 30),
      ("B", 10, 12, 20)
    )

    val d = Vector(Vector(0))

    val a = Vector(0, -1)

    assert(movilidadPar(cursos, Vector(), d, a) == 0)
  }

  test("movilidadPar con cursos vacios") {
    assert(
      movilidadPar(
        Vector(),
        Vector(),
        Vector(),
        Vector()
      ) == 0
    )
  }

  // ===========================================================================
  // generarAsignacionesPar
  // ===========================================================================

  test("generarAsignacionesPar: n=0 retorna una única asignación vacía") {
    val resultado =
      generarAsignacionesPar(0, 3)
    assert(
      resultado ==
        Vector(Vector())
    )
  }

  test("generarAsignacionesPar: un curso y tres aulas genera todas las opciones posibles") {
    val esperado =
      Vector(
        Vector(0),
        Vector(1),
        Vector(2)
      )
    val resultado =
      generarAsignacionesPar(1, 3)

    assert(
      resultado.toSet ==
        esperado.toSet
    )
  }

  test("generarAsignacionesPar: dos cursos y dos aulas genera exactamente las cuatro combinaciones posibles") {
    val esperado =
      Vector(
        Vector(0, 0),
        Vector(0, 1),
        Vector(1, 0),
        Vector(1, 1)
      )
    val resultado =
      generarAsignacionesPar(2, 2)
    assert(
      resultado.toSet ==
        esperado.toSet
    )
  }

  test("generarAsignacionesPar: la cantidad total de asignaciones coincide con m elevado a n") {
    val n = 3
    val m = 2
    val resultado =
      generarAsignacionesPar(n, m)
    assert(
      resultado.length ==
        math.pow(m, n).toInt
    )
  }

  test("generarAsignacionesPar: todas las asignaciones tienen longitud correcta y aulas válidas") {
    val n = 4
    val m = 3
    val resultado =
      generarAsignacionesPar(n, m)
    assert(
      resultado.forall(asig =>
        asig.length == n &&
          asig.forall(aula =>
            aula >= 0 && aula < m
          )
      )
    )
  }

  // ===========================================================================
  // asignacionOptimaPar
  // ===========================================================================

  test("asignacionOptimaPar: encuentra una solución óptima conocida en un caso simple") {

    val cursos = Vector(
      ("A", 8, 10, 20)
    )

    val aulas = Vector(
      ("X", 30),
      ("Y", 40)
    )

    val d = Vector(
      Vector(0, 5),
      Vector(5, 0)
    )

    val pesos = (1000, 100, 1, 1)

    val resultado =
      asignacionOptimaPar(
        cursos,
        aulas,
        d,
        pesos
      )

    assert(
      resultado == (Vector(0), 10)
    )
  }

  test("asignacionOptimaPar: encuentra el costo mínimo global entre todas las asignaciones posibles") {

    val todas =
      generarAsignacionesPar(
        c1.length,
        a1.length
      )
    val costoMinimo =
      todas
        .map(a =>
          costoAsignacion(
            c1,
            a1,
            d1,
            a,
            w
          )
        )
        .min

    val (_, costoOptimo) =
      asignacionOptimaPar(
        c1,
        a1,
        d1,
        w
      )
    assert(
      costoOptimo == costoMinimo
    )
  }

  test("asignacionOptimaPar: la asignación encontrada pertenece al conjunto de soluciones generadas") {

    val (asig, _) =
      asignacionOptimaPar(
        c1,
        a1,
        d1,
        w
      )
    val todas =
      generarAsignacionesPar(
        c1.length,
        a1.length
      )
    assert(
      todas.contains(asig)
    )
  }

  test("asignacionOptimaPar coincide con la versión secuencial en un caso pequeño") {

    val cursos = Vector(
      ("A", 8, 10, 20),
      ("B", 10, 12, 15)
    )
    val aulas = Vector(
      ("X", 30),
      ("Y", 25)
    )
    val d = Vector(
      Vector(0, 5),
      Vector(5, 0)
    )
    val pesos = (1, 1, 1, 1)
    assert(
      asignacionOptimaPar(
        cursos,
        aulas,
        d,
        pesos
      ) ==
        asignacionOptima(
          cursos,
          aulas,
          d,
          pesos
        )
    )
  }

  test("asignacionOptimaPar coincide con la versión secuencial en el escenario principal de prueba") {
    val par =
      asignacionOptimaPar(
        c1,
        a1,
        d1,
        w
      )
    val sec =
      asignacionOptima(
        c1,
        a1,
        d1,
        w
      )
    assert(
      par == sec
    )
  }
}

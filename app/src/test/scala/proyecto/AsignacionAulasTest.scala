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

  // solapan
  test("solapan: M01[4,8) y M02[6,10) se solapan") {
    assert(solapan(("M01", 4, 8, 25), ("M02", 6, 10, 30)))
  }

  test("solapan: M01[4,8) y M03[12,16) no se solapan") {
    assert(!solapan(("M01", 4, 8, 25), ("M03", 12, 16, 20)))
  }

  test("solapan: cursos adyacentes [0,4) y [4,8) no se solapan") {
    assert(!solapan(("A", 0, 4, 10), ("B", 4, 8, 10)))
  }

  // choques
  test("choques: asignacion [0,0,1] tiene 1 choque (M01 y M02 en E101)") {
    assert(choques(c1, Vector(0, 0, 1)) == 1)
  }

  test("choques: asignacion [0,1,0] no tiene choques") {
    assert(choques(c1, Vector(0, 1, 0)) == 0)
  }

  // capacidadFallida
  test("capacidadFallida: asignacion [0,0,1] no falla capacidad") {
    assert(capacidadFallida(c1, a1, Vector(0, 0, 1)) == 0)
  }

  // desperdicio
  test("desperdicio: asignacion [0,0,1] tiene desperdicio 25") {
    // E101(30)-M01(25)=5, E101(30)-M02(30)=0, E102(40)-M03(20)=20 → 25
    assert(desperdicio(c1, a1, Vector(0, 0, 1)) == 25)
  }

  test("desperdicio: asignacion [0,1,0] tiene desperdicio 25") {
    // E101(30)-M01(25)=5, E102(40)-M02(30)=10, E101(30)-M03(20)=10 → 25
    assert(desperdicio(c1, a1, Vector(0, 1, 0)) == 25)
  }

  // costoAsignacion
  test("costoAsignacion: asignacion [0,0,1] cuesta 1031") {
    assert(costoAsignacion(c1, a1, d1, Vector(0, 0, 1), w) == 1031)
  }

  test("costoAsignacion: asignacion [0,1,0] cuesta 37") {
    assert(costoAsignacion(c1, a1, d1, Vector(0, 1, 0), w) == 37)
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

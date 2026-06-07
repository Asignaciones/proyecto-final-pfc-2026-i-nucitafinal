package proyecto

import common._
import AsignacionAulas._

object AsignacionAulasPar {

  /** Versión paralela de choques: divide el vector de cursos en dos mitades. */
  def choquesPar(cursos: Cursos, a: Asignacion): Int = {
    def contar(desde: Int, hasta: Int): Int =
      (desde until hasta).flatMap { i =>
        (i + 1 until cursos.length).map { j =>
          if (
            a(i) == a(j) &&
              a(i) >= 0 &&
              solapan(cursos(i), cursos(j))
          ) 1
          else 0
        }
      }.sum
    val mitad = cursos.length / 2
    val (izq, der) =
      parallel(
        contar(0, mitad),
        contar(mitad, cursos.length)
      )
    izq + der
  }

  /** Versión paralela de desperdicio: divide el vector de cursos en dos mitades. */
  def desperdicioPar(cursos: Cursos, aulas: Aulas, a: Asignacion): Int = {
    def calcular(rango: Range): Int =
      rango.map { i =>
        if (
          a(i) >= 0 &&
            capAula(aulas(a(i))) >= estCurso(cursos(i))
        )
          capAula(aulas(a(i))) - estCurso(cursos(i))
        else
          0
      }.sum
    val mitad = cursos.length / 2
    val (izq, der) =
      parallel(
        calcular(0 until mitad),
        calcular(mitad until cursos.length)
      )
    izq + der
  }

  /** Versión paralela de movilidad: divide el vector de cursos en dos mitades. */
  def movilidadPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                   a: Asignacion): Int = {
    val cursosAsignados =
      cursos.indices
        .filter(i => a(i) >= 0)
        .sortBy(i => iniCurso(cursos(i)))
    val pares =
      cursosAsignados
        .sliding(2)
        .toVector
        .collect {
          case Seq(i, j) => (i, j)
        }
    val mitad = pares.length / 2
    def suma(v: Vector[(Int, Int)]): Int =
      v.map { case (i, j) =>
        d(a(i))(a(j))
      }.sum
    val (izq, der) =
      parallel(
        suma(pares.take(mitad)),
        suma(pares.drop(mitad))
      )
    izq + der
  }

  /**
   * Versión paralela de generarAsignaciones:
   * paraleliza la construcción usando parallel sobre los valores del primer curso.
   */
  def generarAsignacionesPar(n: Int, m: Int): Vector[Asignacion] = {
    if (n == 0)
      Vector(Vector.empty[Int])
    else {
      val mitad = m / 2
      def construir(rango: Range): Vector[Asignacion] =
        rango.flatMap { aula =>
          generarAsignaciones(n - 1, m).map { asignacion =>
            asignacion :+ aula
          }
        }.toVector
      val (izq, der) =
        parallel(
          construir(0 until mitad),
          construir(mitad until m)
        )
      izq ++ der
    }
  }

  /**
   * Versión paralela de asignacionOptima:
   * divide el espacio de candidatos en dos mitades y combina los mínimos.
   */
  def asignacionOptimaPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                          w: Pesos): (Asignacion, Int) = {
    val todas =
      generarAsignacionesPar(
        cursos.length,
        aulas.length
      )
    def mejor(
               asignaciones: Vector[Asignacion]
             ): (Asignacion, Int) =
      asignaciones
        .map { asignacion =>
          (
            asignacion,
            costoAsignacion(
              cursos,
              aulas,
              d,
              asignacion,
              w
            )
          )
        }
        .minBy(_._2)
    val mitad = todas.length / 2
    val (izq, der) =
      parallel(
        mejor(todas.take(mitad)),
        mejor(todas.drop(mitad))
      )
    if (izq._2 <= der._2) izq
    else der
  }
}

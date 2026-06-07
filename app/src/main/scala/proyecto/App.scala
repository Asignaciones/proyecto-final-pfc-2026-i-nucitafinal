package proyecto
import org.scalameter._

object App {
  def main(args: Array[String]): Unit = {
    println("====================================")
    println(" PROYECTO FINAL - PARALELIZACIÓN")
    println("====================================")

    // --------------------------------------------------
    // choques
    // --------------------------------------------------

    val cursosChoques = AsignacionAulas.cursosAlAzar(4)
    val aulasChoques = AsignacionAulas.aulasAlAzar(3)


    val asignacionChoques =
      Vector.fill(4)(scala.util.Random.nextInt(3))

    println("\nPrueba choques(4,3)")

    val tSecChoques = measure {
      AsignacionAulas.choques(
        cursosChoques,
        asignacionChoques
      )
    }

    val tParChoques = measure {
      AsignacionAulasPar.choquesPar(
        cursosChoques,
        asignacionChoques
      )
    }

    val acelChoques =
      ((tSecChoques.value - tParChoques.value) /
        tSecChoques.value) * 100

    println(s"Secuencial: $tSecChoques")
    println(s"Paralela:   $tParChoques")
    println(f"Aceleración: $acelChoques%.2f %%")



    // --------------------------------------------------
    // desperdicio
    // --------------------------------------------------

    val cursosDes = AsignacionAulas.cursosAlAzar(8)
    val aulasDes = AsignacionAulas.aulasAlAzar(5)

    val asignacionDes =
      Vector.fill(8)(scala.util.Random.nextInt(5))

    println("\nPrueba desperdicio(8, 5)")

    val tSecDes = measure {
      AsignacionAulas.desperdicio(
        cursosDes,
        aulasDes,
        asignacionDes
      )
    }

    val tParDes = measure {
      AsignacionAulasPar.desperdicioPar(
        cursosDes,
        aulasDes,
        asignacionDes
      )
    }

    val acelDes =
      ((tSecDes.value - tParDes.value) /
        tSecDes.value) * 100

    println(s"Secuencial: $tSecDes")
    println(s"Paralela:   $tParDes")
    println(f"Aceleración: $acelDes%.2f %%")



    // --------------------------------------------------
    // movilidad
    // --------------------------------------------------

    val cursosMov = AsignacionAulas.cursosAlAzar(8)
    val aulasMov = AsignacionAulas.aulasAlAzar(5)
    val distMov = AsignacionAulas.distanciasAlAzar(5)

    val asignacionMov =
      Vector.fill(8)(scala.util.Random.nextInt(5))

    println("\nPrueba movilidad(8,5)")

    val tSecMov = measure {
      AsignacionAulas.movilidad(
        cursosMov,
        aulasMov,
        distMov,
        asignacionMov
      )
    }

    val tParMov = measure {
      AsignacionAulasPar.movilidadPar(
        cursosMov,
        aulasMov,
        distMov,
        asignacionMov
      )
    }

    val acelMov =
      ((tSecMov.value - tParMov.value) /
        tSecMov.value) * 100

    println(s"Secuencial: $tSecMov")
    println(s"Paralela:   $tParMov")
    println(f"Aceleración: $acelMov%.2f %%")

    // --------------------------------------------------
    //  generarAsignaciones
    // --------------------------------------------------

    println("\nPrueba generarAsignaciones(4,3)")

    val tSecGen = measure {
      AsignacionAulas.generarAsignaciones(4, 3)
    }

    val tParGen = measure {
      AsignacionAulasPar.generarAsignacionesPar(4, 3)
    }

    val acelGen =
      ((tSecGen.value - tParGen.value) / tSecGen.value) * 100

    println(s"Secuencial: $tSecGen")
    println(s"Paralela:   $tParGen")
    println(f"Aceleración: $acelGen%.2f %%")

    // --------------------------------------------------
    //  asignacionOptima
    // --------------------------------------------------

    val cursos = AsignacionAulas.cursosAlAzar(4)
    val aulas = AsignacionAulas.aulasAlAzar(3)
    val dist = AsignacionAulas.distanciasAlAzar(3)
    val pesos = (100, 50, 1, 1)

    println("\nPrueba asignacionOptima(4,3)")

    val tSecOpt = measure {
      AsignacionAulas.asignacionOptima(
        cursos,
        aulas,
        dist,
        pesos
      )
    }

    val tParOpt = measure {
      AsignacionAulasPar.asignacionOptimaPar(
        cursos,
        aulas,
        dist,
        pesos
      )
    }

    val acelOpt =
      ((tSecOpt.value - tParOpt.value) / tSecOpt.value) * 100

    println(s"Secuencial: $tSecOpt")
    println(s"Paralela:   $tParOpt")
    println(f"Aceleración: $acelOpt%.2f %%")

    println("\n====================================")
    println(" FIN DE LAS PRUEBAS")
    println("====================================")
  }
}

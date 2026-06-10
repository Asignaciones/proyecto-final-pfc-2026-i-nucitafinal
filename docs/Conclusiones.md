# Conclusiones

## Integrantes del grupo

- Adriana Milena Noscue Dagua
- Sebastián Cucalón Astorquiza
- Nicolle Camila Hoyos Puin
- Santiago Torres Rojas

---

## Conclusiones del proyecto

Presente aquí las conclusiones del proyecto. Como mínimo debe responder:

1. **Programación funcional:** ¿Qué ventajas y dificultades encontraron al implementar
   la solución usando recursión y funciones de alto orden en lugar de ciclos iterativos?

2. **Corrección:** ¿Cómo argumentaron formalmente que sus implementaciones son correctas?
   ¿Qué técnicas de inducción estructural o de invariantes aplicaron?

3. **Paralelismo:** ¿En qué escenarios resultó beneficioso paralelizar? ¿Cuándo la
   sobrecarga del sistema superó la ganancia esperada?

4. **Aprendizajes:** ¿Qué conceptos del curso les resultaron más útiles para resolver
   el problema? ¿Qué cambiarían en su diseño si volvieran a empezar?

---

## 1. Programación Funcional

La implementación usando recursión y funciones de alto orden presentó ventajas claras frente a los ciclos iterativos. El uso de combinadores como `map`, `flatMap`, `filter`, `sortBy` y `sliding` permitió expresar transformaciones complejas sobre colecciones de forma declarativa, reduciendo la brecha entre la especificación matemática del problema y el código. Por ejemplo, el cálculo del desperdicio se reduce directamente a un `map` sobre índices seguido de un `.sum`, lo cual refleja fielmente la sumatoria de la especificación.

La principal dificultad estuvo en la generación de asignaciones: razonar de forma recursiva sobre la construcción de $\{0,\ldots,m-1\}^n$ sin variables mutables ni acumuladores explícitos requirió internalizar el modelo de sustitución funcional. Asimismo, la prohibición de `return` obligó a estructurar cada función como una expresión única, lo que inicialmente resultó poco intuitivo pero produjo código más conciso y verificable.

## 2. Corrección

La corrección de las funciones se argumentó combinando dos técnicas. Para las funciones de conteo y suma (`choques`, `desperdicio`, `movilidad` y sus versiones paralelas) se usaron **invariantes combinatorios**: se demostró que los rangos recorridos cubren exhaustivamente el espacio de índices o pares requeridos por la especificación, sin duplicados, y que la función indicadora o el término de la sumatoria es idéntico al definido formalmente.

Para `generarAsignaciones` y `generarAsignacionesPar` se aplicó **inducción estructural sobre $n$**: el caso base $n = 0$ es trivialmente correcto, y el paso inductivo demuestra que si la función genera correctamente $\{0,\ldots,m-1\}^{n-1}$, entonces agregar cada valor de aula al final de cada asignación produce exactamente $\{0,\ldots,m-1\}^n$. Para `asignacionOptima` y su versión paralela bastó con demostrar que el espacio de búsqueda es completo y que el mínimo global pertenece necesariamente a una de las dos mitades en que se divide.

## 3. Paralelismo

El paralelismo resultó beneficioso en instancias con $n \ge 6$ cursos y $m \ge 3$ aulas, donde el costo de lanzar tareas concurrentes se amortiza frente al volumen de trabajo. En particular, `asignacionOptimaPar` y `generarAsignacionesPar` mostraron aceleraciones significativas a partir de esos tamaños, dado que el espacio de búsqueda crece como $m^n$ y la evaluación de cada asignación es independiente.

En instancias pequeñas ($n \le 4$, $m \le 2$) la sobrecarga de coordinación entre hilos superó la ganancia esperada, produciendo tiempos mayores que la versión secuencial. Esto es consistente con la ley de Amdahl: cuando la fracción paralelizable es pequeña en términos absolutos, el overhead de `parallel` domina. Las funciones `desperdicioPar` y `movilidadPar` fueron las menos beneficiadas, ya que su costo secuencial es $\mathcal{O}(n)$ y el trabajo por hilo es demasiado reducido para justificar la paralelización en entradas pequeñas.

## 4. Aprendizajes

Los conceptos más útiles del curso fueron las funciones de alto orden, la inducción estructural como herramienta de argumentación formal, y el modelo de paralelismo de tareas con `parallel` y `task`. Las funciones de alto orden simplificaron drásticamente la implementación al abstraer patrones de recorrido y reducción. La inducción estructural proporcionó un marco riguroso para razonar sobre la corrección sin necesidad de ejecutar el programa.

Si volviéramos a empezar, consideraríamos aplicar un umbral (`threshold`) en las versiones paralelas de `choquesPar` y `desperdicioPar`, de modo que para rangos pequeños se use directamente la versión secuencial, evitando la sobrecarga de `parallel` en instancias reducidas. También definiríamos desde el principio una función auxiliar genérica de división en mitades para evitar repetir el patrón `take`/`drop` en cada función paralela.
 
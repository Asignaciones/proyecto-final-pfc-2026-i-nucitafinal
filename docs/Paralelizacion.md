# Informe de paralelización

**Integrantes:**
- Adriana Milena Noscue Dagua
- Sebastián Cucalón Astorquiza
- Nicolle Camila Hoyos Puin
- Santiago Torres Rojas

---

## Estrategia de paralelización

### `choquesPar`


La función `choques` recorre todos los pares de cursos
\((i,j)\) con \(i<j\) para verificar si:

- Están asignados a la misma aula.
- Los intervalos horarios se traslapan.

La versión paralela divide el rango de índices de los cursos en dos mitades:

$$
[0,n) = [0,m) \cup [m,n)
$$

donde \(m = n/2\).

Cada mitad calcula de forma independiente la cantidad de choques parciales y posteriormente los resultados se combinan mediante una suma.

La implementación utiliza:

```scala
parallel(
  contar(0, mitad),
  contar(mitad, cursos.length)
)
```

## Resultados experimentales



| Cursos $n$ | Aulas $m$ | Secuencial (ms) |  Paralela (ms)  | Aceleración (%) |
|:----------:|:---------:|:--------------:|:---------------:|:----------:|
| 4          | 3         |   6.0606          |     7.5302      |     -24,25 |
| 6          | 4         |      15.1771   |     7.6144      |     49,83   |
| 7          | 5         |     5.8669       |       4.647     |   20,79 |
| 8          | 5         |     10.6809       |      11.1529           |  -4,42     |



## Análisis con la ley de Amdahl

La ley de Amdahl establece que la aceleración máxima de un programa paralelo depende de la fracción del algoritmo que puede ejecutarse concurrentemente.

En la función `choquesPar`, la mayor parte del trabajo consiste en recorrer pares de cursos y verificar si presentan conflictos de horario en una misma aula. Estas comparaciones son independientes entre sí, por lo que una parte importante del cálculo puede ejecutarse en paralelo.

Los resultados experimentales muestran que el paralelismo no siempre genera mejoras. Para el caso $(6,4)$ se obtuvo una aceleración de aproximadamente 49.83 %, mientras que para $(7,5)$ la aceleración fue de 20.79 %. En estos casos el trabajo realizado por cada tarea paralela logró compensar el costo de creación y sincronización de los hilos.

Sin embargo, para los casos $(4,3)$ y $(8,5)$ se observó una aceleración negativa. Esto ocurre porque el tiempo invertido en coordinar las tareas paralelas es comparable o incluso superior al tiempo necesario para realizar el cálculo secuencial.

Por lo tanto, la ley de Amdahl permite explicar que el beneficio del paralelismo depende tanto de la fracción paralelizable del algoritmo como del tamaño del problema. Cuando la carga de trabajo es pequeña, la sobrecarga de paralelización puede reducir o eliminar las ganancias esperadas.



## Conclusion

La paralelización de la función `choques` se realizó dividiendo el conjunto de cursos en dos mitades y calculando los choques parciales de manera concurrente.

Los experimentos muestran que el paralelismo puede reducir significativamente el tiempo de ejecución cuando existe suficiente trabajo para distribuir entre los procesadores, como ocurrió en el caso $(6,4)$. Sin embargo, también se observó que para tamaños pequeños la sobrecarga asociada a la creación y sincronización de tareas puede producir aceleraciones negativas.

En conclusión, la estrategia implementada es correcta y permite aprovechar el paralelismo disponible, aunque su efectividad depende del tamaño del problema y de la relación entre el trabajo útil y el costo de coordinación de las tareas paralelas.

---

## Estrategia de paralelización


### `desperdicioPar`

La función `desperdicio` calcula la suma del espacio desaprovechado en las aulas asignadas. Para cada curso asignado, si la capacidad del aula es suficiente, se agrega la diferencia entre la capacidad del aula y el número de estudiantes.

La versión paralela divide el conjunto de cursos en dos mitades:

$$
[0,n) = [0,m) \cup [m,n)
$$

donde \(m = n/2\).

Cada mitad calcula de forma independiente el desperdicio parcial correspondiente a su rango de índices. Finalmente, los resultados parciales se combinan mediante una suma.

La implementación utiliza:

```scala
parallel(
  calcular(0 until mitad),
  calcular(mitad until cursos.length)
)
```
## Tabla experimental 

| Cursos $n$ | Aulas $m$ | Secuencial (ms) | Paralela (ms) | Aceleración (%)  |
|:----------:|:---------:|:----------:|:----------:|:----------------:|
| 4          | 3         |   1.2164      |     2.6037     |       -114,05     |
| 6          | 4         |    0.9515     |      3.7463    |         -293,73     |
| 7          | 5         |     1.1569    |        2.8097   |   -142,86   |
| 8          | 5         |    1.8978     |      6.925   |    -264,90     |



## Análisis con la ley de Amdahl

La ley de Amdahl establece que la aceleración máxima de un programa paralelo depende de la fracción del algoritmo que puede ejecutarse concurrentemente y de la parte que necesariamente permanece secuencial.

En la función `desperdicioPar`, el trabajo se divide en dos mitades independientes del vector de cursos. Cada tarea calcula el desperdicio parcial correspondiente a un subconjunto de cursos y posteriormente ambos resultados se combinan mediante una suma.

Sin embargo, los resultados experimentales muestran aceleraciones negativas en todos los casos evaluados. Esto indica que el tiempo invertido en crear, coordinar y sincronizar las tareas paralelas es mayor que el tiempo requerido para realizar el cálculo secuencial.

La función `desperdicio` realiza operaciones muy simples sobre cada curso, consistentes principalmente en comparaciones y sumas. Debido a que el costo computacional de cada operación es pequeño, la carga de trabajo no es suficiente para compensar la sobrecarga introducida por el paralelismo.

Por esta razón, para los tamaños de entrada evaluados \((4,3)\), \((6,4)\), \((7,5)\) y \((8,5)\), la versión secuencial resulta más eficiente que la versión paralela. Esto coincide con la predicción de la ley de Amdahl, según la cual el paralelismo solo produce mejoras significativas cuando la parte paralelizable del programa representa una fracción importante del tiempo total de ejecución.

Los resultados muestran aceleraciones negativas. Esto ocurre porque el trabajo
realizado por la función es pequeño y el costo de crear tareas paralelas es mayor
que el beneficio obtenido.

## Conclusion

La función `desperdicioPar` fue paralelizada dividiendo el conjunto de cursos en dos mitades y calculando el desperdicio parcial de cada una de forma concurrente.

Sin embargo, los resultados experimentales muestran que la versión paralela fue más lenta que la secuencial en todos los casos evaluados. Esto se debe a que el costo de coordinación de las tareas paralelas supera el beneficio obtenido por el paralelismo para tamaños de entrada pequeños.

Por lo tanto, aunque la paralelización es correcta, no resulta eficiente para los casos analizados.



---

## Estrategia de paralelización

### `movilidadPar`

La función `movilidad` calcula la distancia total recorrida entre aulas de cursos consecutivos ordenados por hora de inicio.

Primero se construye la secuencia de cursos asignados y se ordena según su hora de inicio. Posteriormente se generan los pares consecutivos de cursos:

$$
(c_1,c_2), (c_2,c_3), \dots, (c_{k-1},c_k)
$$

Para cada par se consulta la distancia entre las aulas correspondientes y finalmente se suman todas las distancias obtenidas.

La versión paralela divide el conjunto de pares consecutivos en dos mitades:

$$
P = P_1 \cup P_2
$$

donde cada subconjunto contiene una parte de los pares generados.

Cada mitad calcula de forma independiente la suma parcial de las distancias y posteriormente ambos resultados se combinan mediante una suma.

La implementación utiliza:

```scala
parallel(
  suma(pares.take(mitad)),
  suma(pares.drop(mitad))
)
```

## Tabla experimental

| Cursos $n$ | Aulas $m$ | Secuencial (ms) | Paralela (ms) | Aceleración (%)  |
|:----------:|:---------:|:--------------:|:-------------:|:----------------:|
| 4          | 3         |         21.2419    |     6.3486         |   70,11         |
| 6          | 4         |    18.0698     |       6.6437    |       63,23       |
| 7          | 5         |        14.5276    |         4.1178    |  71,66    |
| 8          | 5         |        15.8309    |     4.459    | 71,83        |


## Análisis con la ley de Amdahl

La mayor parte del trabajo de la función `movilidad` corresponde al cálculo de las distancias entre pares consecutivos de cursos, una tarea que puede ejecutarse de manera independiente para distintos subconjuntos de pares.

Los resultados muestran aceleraciones superiores al 60% en todos los casos evaluados, alcanzando valores cercanos al 72% para los tamaños más grandes. Esto indica que una fracción importante del cómputo es paralelizable.

Según la ley de Amdahl, la aceleración está limitada por la parte secuencial del algoritmo, principalmente la construcción y ordenamiento inicial de los cursos asignados. Sin embargo, al aumentar el número de cursos, el costo de calcular las distancias domina la ejecución y el beneficio del paralelismo se vuelve más evidente.



## Conclusion

La paralelización de `movilidad` fue efectiva, ya que produjo mejoras de rendimiento en todos los experimentos realizados.

Los mejores resultados se obtuvieron para instancias de mayor tamaño, donde la distribución del cálculo de distancias entre varias tareas permitió reducir significativamente el tiempo de ejecución.

En consecuencia, la estrategia implementada aprovecha adecuadamente el paralelismo disponible y mejora el desempeño de la función respecto a su versión secuencial.

---

## Estrategia de paralelización

### `generarAsignacionesPar`

La función `generarAsignaciones` genera todas las asignaciones posibles de $n$ cursos en
$m$ aulas. El algoritmo recursivo construye las asignaciones de longitud $n$ a partir de
las asignaciones de longitud $n-1$.

Observamos que, para cada aula posible $a \in \{0,\ldots,m-1\}$, la construcción de las
asignaciones asociadas a dicha aula es independiente de las demás. Por esta razón, el
espacio de búsqueda puede dividirse en varias tareas que se ejecutan simultáneamente.

La estrategia de paralelización consiste en generar en paralelo los subconjuntos de
asignaciones correspondientes a diferentes valores de aula y posteriormente combinar los
resultados obtenidos.

Formalmente, el conjunto de asignaciones puede expresarse como:

$$
A(n,m) = \bigcup_{a=0}^{m-1} \left\{ [a] \mathbin{++} x \mid x \in A(n-1,m) \right\}
$$

donde cada subconjunto asociado a un valor de $a$ puede calcularse de forma independiente.

En la implementación se utiliza la función `parallel` para dividir el rango de aulas en
dos mitades. Cada mitad construye sus asignaciones de manera concurrente y finalmente
ambos resultados se concatenan mediante el operador `++`.




## Resultados experimentales



| Cursos $n$ | Aulas $m$ | Secuencial (ms) | Paralela (ms) | Aceleración (%)  |
|:----------:|:---------:|:--------------:|:-------------:|:----------------:|
| 4          | 3         |         10.6802       |   7.13            |      33.24       |
| 6          | 4         |          22.688      |       11.5189        |         49.2         |
| 7          | 5         |         59.9294       |    40.5332            |       32.4       |
| 8          | 5         |          103.829      |         139.9056      |      -34.8       |




## Análisis con la ley de Amdahl

La ley de Amdahl establece que la aceleración máxima obtenible mediante paralelización
depende de la fracción paralelizable del programa:

$$
S(p) = \frac{1}{(1 - \alpha) + \dfrac{\alpha}{p}}
$$

En `generarAsignacionesPar`, la mayor parte del trabajo corresponde a la construcción de
las asignaciones para cada aula posible. Estas tareas son independientes entre sí, por
lo que una fracción significativa del algoritmo puede ejecutarse en paralelo.

Los resultados muestran mejoras importantes para los casos $(4,3)$, $(6,4)$ y $(7,5)$,
donde se obtuvieron aceleraciones del 33.2 %, 49.2 % y 32.4 %, respectivamente. Esto
indica que el costo de coordinación entre tareas es menor que la ganancia obtenida al
distribuir el trabajo entre varios núcleos.

Sin embargo, para el caso $(8,5)$ se obtuvo una aceleración negativa de $-34.8\,\%$.
En este escenario, la sobrecarga asociada a la creación y sincronización de tareas
paralelas superó el beneficio de la paralelización. Como consecuencia, la versión
paralela tardó más tiempo que la versión secuencial.



## Conclusion

La paralelización de `generarAsignaciones` permitió aprovechar varios núcleos del
procesador para construir subconjuntos de asignaciones de forma concurrente. Los
resultados experimentales evidencian mejoras significativas para varios tamaños de
entrada, alcanzando una aceleración cercana al 50 % en algunos casos.

No obstante, los experimentos también muestran que el paralelismo no siempre garantiza
una reducción del tiempo de ejecución. Cuando el costo de coordinación entre tareas es
elevado respecto al trabajo realizado, puede producirse una aceleración negativa. Por
esta razón, la efectividad de la paralelización depende tanto del tamaño del problema
como de la relación entre trabajo útil y sobrecarga de gestión de tareas.

---

## Estrategia de paralelización

### `asignacionOptimaPar`

Busca la asignación de menor costo entre todas las posibles. La versión secuencial
genera todas las asignaciones, calcula el costo de cada una con `costoAsignacion` y
selecciona el mínimo con `minBy`.

La estrategia de paralelización divide el conjunto $A$ de asignaciones en dos mitades
independientes y busca el mínimo local de cada una de forma concurrente:

$$c(a) = \texttt{costoAsignacion}(\textit{cursos},\textit{aulas},d,a,w), \quad a \in A$$

$$\min(A) = \min\!\left(\min(A_1),\, \min(A_2)\right)$$

La implementación usa `parallel` sobre las dos mitades:

```scala
parallel(
  mejor(todas.take(mitad)),
  mejor(todas.drop(mitad))
)
```

Esto es seguro porque el costo de cada asignación es independiente de las demás,
por lo que los mínimos locales pueden calcularse sin comunicación entre tareas.




## Resultados experimentales



| Cursos $n$ | Aulas $m$ | Secuencial (ms) | Paralela (ms) | Aceleración (%) |
|:----------:|:---------:|:----------:|:-----:|:-------:|
| 4          | 3         |      41.3067 |    16.7873   |    59,36     |
| 6          | 4         |       140.9715    |  58.4703     |   58,52      |
| 7          | 5         |   731.464     |     274.6514  | 62,45        |
| 8          | 5         |     2721.4156      |   1390.6819 |      48,90 |



## Análisis con la ley de Amdahl

$$S(p) = \frac{1}{(1-\alpha)+\dfrac{\alpha}{p}}$$

En `asignacionOptimaPar`, el cálculo del costo de cada asignación y la búsqueda del
mínimo local son independientes entre sí, por lo que una fracción importante del
algoritmo se ejecuta en paralelo.

Los resultados muestran mejoras en todos los casos evaluados: aceleraciones superiores
al 58 % para $(4,3)$ y $(6,4)$, y cercanas al 62 % para $(7,5)$. A medida que crece
el espacio de búsqueda, el trabajo útil por tarea supera el costo de sincronización.
Para $(8,5)$ la aceleración baja al 48.90 %, pero sigue siendo significativa.


## Conclusion

La paralelización de `asignacionOptima` redujo los tiempos de ejecución en todos los
experimentos, con aceleraciones entre el 48.90 % y el 62.45 %. La estrategia de dividir
el espacio de asignaciones en dos subconjuntos independientes, calcular el mínimo local
de cada uno en paralelo y comparar al final resulta efectiva porque el costo de cada
asignación se evalúa sin comunicación entre tareas.

La versión paralela es especialmente beneficiosa cuando el espacio de búsqueda es
grande, ya que distribuye el trabajo entre múltiples núcleos y reduce el tiempo total
de manera consistente.


--- 


## Conclusión general

En este proyecto se implementaron versiones paralelas de las funciones
`choques`, `desperdicio`, `movilidad`, `generarAsignaciones` y
`asignacionOptima`.

Los experimentos muestran que el paralelismo no siempre garantiza una mejora
en el rendimiento. Funciones con poco trabajo computacional, como
`desperdicio`, presentaron aceleraciones negativas debido al costo de
coordinación de las tareas paralelas.

Por el contrario, funciones con mayor carga computacional, como
`movilidad`, `generarAsignaciones` y especialmente `asignacionOptima`,
obtuvieron reducciones importantes en el tiempo de ejecución, alcanzando
aceleraciones superiores al 60 % en algunos casos.

En conclusión, los resultados obtenidos coinciden con la ley de Amdahl y
evidencian que la efectividad del paralelismo depende del tamaño del
problema y de la proporción de trabajo que puede ejecutarse de manera
concurrente.
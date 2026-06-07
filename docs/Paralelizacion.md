# Informe de paralelización

**Integrantes:**
- Adriana Milena Noscue Dagua
- Sebastián Cucalón Astorquiza
- Nicolle Camila Hoyos Puin
- Santiago Torres Rojas

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


---

## Resultados experimentales



| Cursos $n$ | Aulas $m$ | Secuencial (ms) | Paralela (ms) | Aceleración (%)  |
|:----------:|:---------:|:--------------:|:-------------:|:----------------:|
| 4          | 3         |         10.6802       |   7.13            |      33.24       |
| 6          | 4         |          22.688      |       11.5189        |         49.2         |
| 7          | 5         |         59.9294       |    40.5332            |       32.4       |
| 8          | 5         |          103.829      |         139.9056      |      -34.8       |


---


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

---

## Conclusiones de paralelización

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

`AsignacionesOptimas` y `AsignacionesOPtimasPar`

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


---

## Resultados experimentales



| Cursos $n$ | Aulas $m$ | Secuencial (ms) | Paralela (ms) | Aceleración (%) |
|:----------:|:---------:|:----------:|:-----:|:-------:|
| 4          | 3         |      41.3067 |    16.7873   |    59,36     |
| 6          | 4         |       140.9715    |  58.4703     |   58,52      |
| 7          | 5         |   731.464     |     274.6514  | 62,45        |
| 8          | 5         |     2721.4156      |   1390.6819 |      48,90 |


---


## Análisis con la ley de Amdahl

$$S(p) = \frac{1}{(1-\alpha)+\dfrac{\alpha}{p}}$$

En `asignacionOptimaPar`, el cálculo del costo de cada asignación y la búsqueda del
mínimo local son independientes entre sí, por lo que una fracción importante del
algoritmo se ejecuta en paralelo.

Los resultados muestran mejoras en todos los casos evaluados: aceleraciones superiores
al 58 % para $(4,3)$ y $(6,4)$, y cercanas al 62 % para $(7,5)$. A medida que crece
el espacio de búsqueda, el trabajo útil por tarea supera el costo de sincronización.
Para $(8,5)$ la aceleración baja al 48.90 %, pero sigue siendo significativa.

---

## Conclusiones

La paralelización de `asignacionOptima` redujo los tiempos de ejecución en todos los
experimentos, con aceleraciones entre el 48.90 % y el 62.45 %. La estrategia de dividir
el espacio de asignaciones en dos subconjuntos independientes, calcular el mínimo local
de cada uno en paralelo y comparar al final resulta efectiva porque el costo de cada
asignación se evalúa sin comunicación entre tareas.

La versión paralela es especialmente beneficiosa cuando el espacio de búsqueda es
grande, ya que distribuye el trabajo entre múltiples núcleos y reduce el tiempo total
de manera consistente.
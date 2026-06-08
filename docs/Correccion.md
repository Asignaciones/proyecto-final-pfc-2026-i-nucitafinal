## Argumentación de corrección

A continuación, se presenta la verificación formal de las funciones de acceso, restricciones y cálculo de costos marginales de la aplicación de asignación de aulas. Se demuestra la validez de los algoritmos bajo el paradigma funcional puro mediante lógica proposicional, análisis combinatorio de alto orden e invariantes sobre colecciones indexadas.

---
## 1. Función `solapan`

### Código Evaluado

```scala
def solapan(c1: Curso, c2: Curso): Boolean = {
  iniCurso(c1) < finCurso(c2) && iniCurso(c2) < finCurso(c1)
}
```

### Demostración Teórica

Sean dos intervalos de tiempo semiabiertos en la línea real correspondientes a los cursos:

$$
I_1 = [ini_1, fin_1)
$$

$$
I_2 = [ini_2, fin_2)
$$

Por definición de asignación de franjas horarias contiguas, estos **no se intersecan (no solapan)** si y solo si uno de ellos ocurre completamente antes o en el instante exacto en que termina el otro.

La condición de **no solapamiento** se expresa formalmente como:

$$
\text{NoSolapan} \iff (fin_1 \le ini_2) \lor (fin_2 \le ini_1)
$$

Para encontrar cuándo los cursos **sí se solapan**, aplicamos la negación lógica a la expresión anterior utilizando las Leyes de De Morgan:

$$
\neg \text{NoSolapan} \iff \neg(fin_1 \le ini_2) \land \neg(fin_2 \le ini_1)
$$

$$
\neg \text{NoSolapan} \iff (fin_1 > ini_2) \land (fin_2 > ini_1)
$$

Reordenando los términos de las desigualdades de forma equivalente:

$$
\neg \text{NoSolapan} \iff (ini_2 < fin_1) \land (ini_1 < fin_2)
$$

Esta especificación lógica final es idéntica a la conjunción booleana `&&` implementada en el código:

```scala
iniCurso(c1) < finCurso(c2) && iniCurso(c2) < finCurso(c1)
```

Por lo tanto, la función es **correcta**.

---

## 2. Función `choques`

### Código Evaluado

```scala
def choques(cursos: Cursos, a: Asignacion): Int = {
  (0 until cursos.length).flatMap {
    i =>
      (i + 1 until cursos.length).map {
        j =>
          if (a(i) == a(j) && a(i) >= 0 &&
              solapan(cursos(i), cursos(j))) 1 else 0
      }
  }.sum
}
```

### Argumentación por Invariante Combinatorio

La especificación del problema exige contar la cantidad de colisiones en pares ordenados $(i,j)$ bajo la restricción estricta de ordenamiento $i < j$, evitando evaluar un curso consigo mismo o contar dos veces la misma pareja simétrica.

1. **Invariante de Orden:** El rango externo provee el índice $i$ en el intervalo $[0,\text{length})$. El rango interno inicializa el índice $j$ estrictamente en $i+1$. Esto garantiza que:

$$
\forall (i,j)\in\text{Combinaciones Evaluadas}: i < j
$$

Esto recorre exactamente el triángulo superior estricto de la matriz de combinaciones posibles.

2. **Mapeo y Reducción Funcional:** El condicional interno actúa como una función indicadora $\mathbb{I}(i,j)$ que devuelve $1$ si se cumplen concurrentemente las tres precondiciones de la especificación:

* Misma aula asignada.
* Asignación válida.
* Cruce de horarios.

y devuelve $0$ en caso contrario.

3. El uso de `flatMap` y `map` unifica las colecciones anidadas en un vector plano de enteros, y la operación terminal asociativa `.sum` acumula de forma lineal los valores.

Al cubrir exhaustivamente todo el espacio muestral restrictivo, la función es **correcta**.

---

## 3. Función `capacidadFallida`

### Código Evaluado

```scala
def capacidadFallida(
  cursos: Cursos,
  aulas: Aulas,
  a: Asignacion
): Int = {
  cursos.indices.count { i =>
    a(i) >= 0 &&
    capAula(aulas(a(i))) < estCurso(cursos(i))
  }
}
```

### Argumentación por Lógica de Predicados

Definiendo el conjunto de índices:

$$
I = {0,1,\dots,N-1}
$$

la especificación requiere calcular la cardinalidad del conjunto de cursos que violan la restricción física de cupos:

$$
\text{Resultado}
================

\Big|
{
i \in I
\mid
a(i) \ge 0
\land
\text{capacidad}(aula(a(i)))
<
\text{estudiantes}(curso(i))
}
\Big|
$$

La función `.count` aplica el predicado a cada elemento del dominio sin omitir ninguna posición.

Dado que incrementa el contador si y solo si el curso posee un aula válida y su capacidad es estrictamente menor al número de estudiantes registrados, la función computa exactamente la cardinalidad definida por la especificación.

Por lo tanto, la función es **correcta**.

---

## 4. Función `desperdicio`

### Código Evaluado

```scala
def desperdicio(
  cursos: Cursos,
  aulas: Aulas,
  a: Asignacion
): Int = {
  cursos.indices.map { i =>
    if (
      a(i) >= 0 &&
      capAula(aulas(a(i))) >= estCurso(cursos(i))
    )
      capAula(aulas(a(i))) - estCurso(cursos(i))
    else
      0
  }.sum
}
```

### Argumentación por Homomorfismo de Sumas

El desperdicio individual para un curso indexado por $i$ se modela mediante:

$$
g(i)=
\begin{cases}
\text{capAula}(a(i))-\text{estCurso}(i)
&
\text{si }
a(i)\ge0
\land
\text{capAula}(a(i))\ge\text{estCurso}(i)
[6pt]
0
&
\text{en otro caso}
\end{cases}
$$

El desperdicio global corresponde a:

$$
\sum_{i=0}^{N-1} g(i)
$$

El operador `.map` construye el vector de valores $g(i)$ para cada curso, mientras que `.sum` realiza la reducción asociativa de todos los elementos obtenidos.

Como esta transformación preserva exactamente la semántica de la sumatoria matemática, la función es **correcta**.

---

## 5. Función `movilidad`

### Código Evaluado

```scala
def movilidad(
  cursos: Cursos,
  aulas: Aulas,
  d: Distancias,
  a: Asignacion
): Int = {

  val cursosAsignados =
    cursos.indices
      .filter(i => a(i) >= 0)
      .sortBy(i => iniCurso(cursos(i)))

  cursosAsignados
    .sliding(2)
    .collect {
      case Vector(i, j) => d(a(i))(a(j))
    }
    .sum
}
```

### Argumentación por Invariantes de Estructuras de Datos

1. **Invariante de Filtrado y Validez**

La operación:

```scala
.filter(i => a(i) >= 0)
```

garantiza que todos los índices conservados corresponden a cursos con asignación válida.

2. **Invariante de Cronología**

La operación:

```scala
.sortBy(i => iniCurso(cursos(i)))
```

garantiza:

$$
\forall k:
\text{iniCurso}(cursos(cursosAsignados(k)))
\le
\text{iniCurso}(cursos(cursosAsignados(k+1)))
$$

3. **Invariante de Contigüidad por Ventanas**

El método `.sliding(2)` genera parejas consecutivas de cursos en orden temporal:

$$
(i_1,i_2),
(i_2,i_3),
\dots,
(i_{n-1},i_n)
$$

4. La operación `.collect` obtiene la distancia correspondiente para cada transición temporal:

$$
d(a(i),a(j))
$$

Finalmente, `.sum` acumula todos los costos de desplazamiento.

Por lo tanto, la función modela correctamente la movilidad total entre cursos consecutivos y es **correcta**.

---

### 6.Funcion `costoAsigacion`

Codigo evaluado:
```Scala
def costoAsignacion(cursos: Cursos, aulas: Aulas, d: Distancias,
a: Asignacion, w: Pesos): Int = {
val (wCH, wCF, wDE, wMV) = w

wCH * choques(cursos, a) +
wCF * capacidadFallida(cursos, aulas, a) +
wDE * desperdicio(cursos, aulas, a) +
wMV * movilidad(cursos, aulas, d, a)
}
```

Argumentación de Combinación Lineal

Sean $CH(a)$, $CF(a)$, $DE(a)$ y $MV(a)$ las funciones correspondientes a los costos de choques, capacidad fallida, desperdicio y movilidad evaluadas sobre una asignación $a$, y sea $w = (w_{CH}, w_{CF}, w_{DE}, w_{MV})$ el vector de pesos asignado.
La especificación define el costo total de la asignación como una combinación lineal ponderada:

$$CostoTotal(a) = w_{CH} \cdot CH(a) + w_{CF} \cdot CF(a) + w_{DE} \cdot DE(a) + w_{MV} \cdot MV(a)$$

1. El código realiza un desempaquetado de tuplas por coincidencia de patrones mediante val (wCH, wCF, wDE, wMV) = w, aislando correctamente cada escalar multiplicativo.

2.	Posteriormente, invoca las subfunciones validadas en las secciones anteriores pasando exactamente las estructuras de datos inmutables correspondientes (cursos, aulas, d, a).

4. Finalmente, computa la suma algebraica de los productos intermedios. Dado que cada término se evalúa individualmente de acuerdo con su costo marginal específico y se escala por su peso respectivo según el modelo matemático, la correspondencia del algoritmo con la combinación lineal es exacta. La función es correcta.


## Argumentación de corrección — Función `generarAsignaciones`
## Corrección de programa recursivo

La función `generarAsignaciones` es un programa recursivo cuyo parámetro recursivo es el número de cursos `n`.

Sea:

$$
A(n,m)=\{(a_1,a_2,\dots,a_n)\mid a_i\in\{0,1,\dots,m-1\}\}
$$

el conjunto de todas las asignaciones posibles de \(n\) cursos en \(m\) aulas.


La especificación de la función consiste en generar exactamente dicho conjunto.

Queremos demostrar que:

$$
\forall\, n \ge 0:
\texttt{generarAsignaciones}(n,m)=A(n,m)
$$

Como el conjunto de valores posibles para \(n\) está definido recursivamente sobre los números naturales, utilizaremos inducción estructural para argumentar la corrección del programa.


## Caso base

Si $n = 0$, la función ejecuta:

```scala
Vector(Vector.empty[Int])
```

y retorna el conjunto que contiene únicamente la asignación vacía:
$\{[]\}$.

Cuando no existen cursos por asignar, la única asignación posible es la asignación vacía. Por definición:

$$A(0,m) = \{[]\}$$

Por lo tanto:

$$\texttt{generarAsignaciones}(0,m) = A(0,m)$$



## Hipótesis de inducción

Supongamos que para algún $k \ge 0$:

$$\texttt{generarAsignaciones}(k,m) = A(k,m)$$

es decir, la función genera correctamente todas las asignaciones posibles de $k$ cursos.



## Paso inductivo

Debemos demostrar que:

$$\texttt{generarAsignaciones}(k+1,m) = A(k+1,m)$$

La función calcula primero:

```scala
val anteriores = generarAsignaciones(k, m)
```

Por hipótesis de inducción, $anteriores = A(k,m)$.

Luego ejecuta:

```scala
for {
  asignacion <- anteriores
  aula       <- 0 until m
} yield asignacion :+ aula
```


Para cada asignación de longitud $k$, se agrega cada posible aula $0, 1, \ldots, m-1$,
obteniendo todas las asignaciones de longitud $k+1$.

Por construcción:

- Ninguna asignación válida es omitida.
- Ninguna asignación inválida es generada.

Por lo tanto:

$$\texttt{generarAsignaciones}(k+1,m) = A(k+1,m)$$


## Conclusión

Se verificó el caso base y el paso inductivo. Por inducción estructural:

$$\forall\, n \ge 0: \texttt{generarAsignaciones}(n,m) = A(n,m)$$

Por lo tanto, la implementación de `generarAsignaciones` es correcta respecto a su
especificación, ya que genera exactamente todas las asignaciones posibles de $n$ cursos
en $m$ aulas.


---


# Argumentación de corrección — Función `asignacionOptima`

## Especificación

Sea $A$ el conjunto de todas las asignaciones posibles de cursos a aulas.

Sea además la función de costo:

$$c : A \rightarrow \mathbb{N}$$

donde:

$$c(a) = \texttt{costoAsignacion}(cursos, aulas, d, a, w)$$

La especificación de `asignacionOptima` consiste en retornar una asignación óptima $a^*$
y su costo asociado, tales que:

$$c(a^*) = \min_{a \in A}\, c(a)$$

Queremos demostrar que:

`asignacionOptima(cursos,aulas,d,w)` =  $$ (a^{*}, c(a^{*}))$$

donde $a^*$ es una asignación de costo mínimo.



## Paso 1: Generación de todas las asignaciones

La función ejecuta:

```scala
val todas = generarAsignaciones(
  cursos.length,
  aulas.length
)
```

Por la corrección demostrada previamente para `generarAsignaciones`:

$$\texttt{generarAsignaciones}(n, m) = A$$

Por lo tanto, la variable `todas` contiene exactamente todas las asignaciones posibles.



## Paso 2: Asociación de cada asignación con su costo

La función ejecuta:

```scala
todas.map { asignacion =>
  (
    asignacion,
    costoAsignacion(cursos, aulas, d, asignacion, w)
  )
}
```

Esto produce el conjunto:

$$T = \{\, (a,\, c(a)) \mid a \in A \,\}$$

Es decir, para cada asignación posible se construye una tupla formada por:

- La asignación.
- Su costo correspondiente.

Por lo tanto, ninguna asignación es omitida y cada costo es calculado correctamente
mediante `costoAsignacion`.



## Paso 3: Selección del mínimo

La función aplica:

```scala
.minBy(_._2)
```

La expresión `_._2` representa el segundo componente de cada tupla, es decir, el costo.

Por definición de `minBy`, el resultado es una tupla $(a^*, c(a^*))$ tal que:

$$c(a^*) \le c(a) \qquad \forall\, a \in A$$

Por lo tanto:

$$c(a^*) = \min_{a \in A}\, c(a)$$



## Conclusión

La función:

1. Genera todas las asignaciones posibles.
2. Calcula correctamente el costo de cada una.
3. Selecciona la asignación cuyo costo es mínimo.

Por lo tanto:

$$
\texttt{asignacionOptima}(cursos, aulas, d, w)
=
(a^{*}, c(a^{*}))
$$

donde:

$$c(a^*) = \min_{a \in A}\, c(a)$$

En consecuencia, la implementación de `asignacionOptima` es correcta respecto a su
especificación, ya que retorna exactamente una asignación óptima y su costo mínimo
asociado.

---
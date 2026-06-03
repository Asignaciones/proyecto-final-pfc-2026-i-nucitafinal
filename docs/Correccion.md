

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
\forall n \ge 0:
\texttt{generarAsignaciones}(n,m)=A(n,m)
$$

Como el conjunto de valores posibles para \(n\) está definido recursivamente sobre los números naturales, utilizaremos inducción estructural para argumentar la corrección del programa.


## Caso base

Si $n = 0$, la función ejecuta:

```scala
Vector(Vector.empty[Int])
```

y retorna $\{[]\}$.

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

$$\texttt{asignacionOptima}(cursos, aulas, d, w) = (a^*, c(a^*))$$

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

$$\texttt{asignacionOptima}(cursos, aulas, d, w) = (a^*, c(a^*))$$

donde:

$$c(a^*) = \min_{a \in A}\, c(a)$$

En consecuencia, la implementación de `asignacionOptima` es correcta respecto a su
especificación, ya que retorna exactamente una asignación óptima y su costo mínimo
asociado.

---
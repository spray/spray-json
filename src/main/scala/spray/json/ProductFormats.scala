/*
 * Copyright (C) 2011 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spray.json

import java.lang.reflect.Modifier

/**
 * Provides the helpers for constructing custom JsonFormat implementations for types implementing the Product trait
 * (especially case classes)
 */
trait ProductFormats {
  this: StandardFormats =>

  case class Field(name: String, getDefault: Option[() => Any])
  implicit def toFieldAndDefArg(s : String) = Field(s, None)
  implicit def toFieldAndDefArg(s : (String, () => Any)) = Field(s._1, Some(s._2))
    
  def jsonFormat0[T <: Product :ClassManifest](construct: () => T): RootJsonFormat[T] = {
    jsonFormat(construct)
  }
  def jsonFormat[T <: Product](construct: () => T): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject()
    def read(value: JsValue) = construct()
  }

  def jsonFormat1[A :JF, T <: Product :ClassManifest](construct: A => T): RootJsonFormat[T] = {
    val Array(a) = extractFieldNames(classManifest[T])
    jsonFormat(construct, a)
  }
  def jsonFormat[A :JF, T <: Product](construct: A => T, a : Field): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject(
      productElement2Field[A](a.name, p, 0)
    )
    def read(value: JsValue) = construct(
      fromField[A](value, a)
    )
  }

  def jsonFormat2[A :JF, B :JF, T <: Product :ClassManifest](construct: (A, B) => T): RootJsonFormat[T] = {
    val Array(a, b) = extractFieldNames(classManifest[T])
    jsonFormat(construct, a, b)
  }
  
  def jsonFormat[A :JF, B :JF, T <: Product](construct: (A, B) => T, a : Field, b: Field): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject(
      productElement2Field[A](a.name, p, 0,
      productElement2Field[B](b.name, p, 1))
    )
    def read(value: JsValue) = construct(
      fromField[A](value, a),
      fromField[B](value, b)
    )
  }

  def jsonFormat3[A :JF, B :JF, C :JF, T <: Product :ClassManifest](construct: (A, B, C) => T): RootJsonFormat[T] = {
    val Array(a, b, c) = extractFieldNames(classManifest[T])
    jsonFormat(construct, a, b, c)
  }
  def jsonFormat[A :JF, B :JF, C :JF, T <: Product](construct: (A, B, C) => T,
        a : Field, b: Field, c: Field): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject(
      productElement2Field[A](a.name, p, 0,
      productElement2Field[B](b.name, p, 1,
      productElement2Field[C](c.name, p, 2)))
    )
    def read(value: JsValue) = construct(
      fromField[A](value, a),
      fromField[B](value, b),
      fromField[C](value, c)
    )
  }

  def jsonFormat4[A :JF, B :JF, C :JF, D :JF, T <: Product :ClassManifest]
    (construct: (A, B, C, D) => T): RootJsonFormat[T] = {
    val Array(a, b, c, d) = extractFieldNames(classManifest[T])
    jsonFormat(construct, a, b, c, d)
  }
  def jsonFormat[A :JF, B :JF, C :JF, D :JF, T <: Product](construct: (A, B, C, D) => T,
        a : Field, b: Field, c: Field, d: Field): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject(
      productElement2Field[A](a.name, p, 0,
      productElement2Field[B](b.name, p, 1,
      productElement2Field[C](c.name, p, 2,
      productElement2Field[D](d.name, p, 3))))
    )
    def read(value: JsValue) = construct(
      fromField[A](value, a),
      fromField[B](value, b),
      fromField[C](value, c),
      fromField[D](value, d)
    )
  }

  def jsonFormat5[A :JF, B :JF, C :JF, D :JF, E :JF, T <: Product :ClassManifest]
    (construct: (A, B, C, D, E) => T): RootJsonFormat[T] = {
    val Array(a, b, c, d, e) = extractFieldNames(classManifest[T])
    jsonFormat(construct, a, b, c, d, e)
  }
  def jsonFormat[A :JF, B :JF, C :JF, D :JF, E :JF, T <: Product](construct: (A, B, C, D, E) => T,
        a : Field, b: Field, c: Field, d: Field, e: Field): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject(
      productElement2Field[A](a.name, p, 0,
      productElement2Field[B](b.name, p, 1,
      productElement2Field[C](c.name, p, 2,
      productElement2Field[D](d.name, p, 3,
      productElement2Field[E](e.name, p, 4)))))
    )
    def read(value: JsValue) = construct(
      fromField[A](value, a),
      fromField[B](value, b),
      fromField[C](value, c),
      fromField[D](value, d),
      fromField[E](value, e)
    )
  }

  def jsonFormat6[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, T <: Product :ClassManifest]
    (construct: (A, B, C, D, E, F) => T): RootJsonFormat[T] = {
    val Array(a, b, c, d, e, f) = extractFieldNames(classManifest[T])
    jsonFormat(construct, a, b, c, d, e, f)
  }
  def jsonFormat[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, T <: Product](construct: (A, B, C, D, E, F) => T,
        a : Field, b: Field, c: Field, d: Field, e: Field, f: Field): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject(
      productElement2Field[A](a.name, p, 0,
      productElement2Field[B](b.name, p, 1,
      productElement2Field[C](c.name, p, 2,
      productElement2Field[D](d.name, p, 3,
      productElement2Field[E](e.name, p, 4,
      productElement2Field[F](f.name, p, 5))))))
    )
    def read(value: JsValue) = construct(
      fromField[A](value, a),
      fromField[B](value, b),
      fromField[C](value, c),
      fromField[D](value, d),
      fromField[E](value, e),
      fromField[F](value, f)
    )
  }

  def jsonFormat7[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, T <: Product :ClassManifest]
    (construct: (A, B, C, D, E, F, G) => T): RootJsonFormat[T] = {
    val Array(a, b, c, d, e, f, g) = extractFieldNames(classManifest[T])
    jsonFormat(construct, a, b, c, d, e, f, g)
  }
  def jsonFormat[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, T <: Product](construct: (A, B, C, D, E, F, G) => T,
        a : Field, b: Field, c: Field, d: Field, e: Field, f: Field, g: Field): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject(
      productElement2Field[A](a.name, p, 0,
      productElement2Field[B](b.name, p, 1,
      productElement2Field[C](c.name, p, 2,
      productElement2Field[D](d.name, p, 3,
      productElement2Field[E](e.name, p, 4,
      productElement2Field[F](f.name, p, 5,
      productElement2Field[G](g.name, p, 6)))))))
    )
    def read(value: JsValue) = construct(
      fromField[A](value, a),
      fromField[B](value, b),
      fromField[C](value, c),
      fromField[D](value, d),
      fromField[E](value, e),
      fromField[F](value, f),
      fromField[G](value, g)
    )
  }

  def jsonFormat8[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, T <: Product :ClassManifest]
    (construct: (A, B, C, D, E, F, G, H) => T): RootJsonFormat[T] = {
    val Array(a, b, c, d, e, f, g, h) = extractFieldNames(classManifest[T])
    jsonFormat(construct, a, b, c, d, e, f, g, h)
  }
  def jsonFormat[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, T <: Product]
        (construct: (A, B, C, D, E, F, G, H) => T,
         a : Field, b: Field, c: Field, d: Field, e: Field, f: Field, g: Field, h: Field): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject(
      productElement2Field[A](a.name, p, 0,
      productElement2Field[B](b.name, p, 1,
      productElement2Field[C](c.name, p, 2,
      productElement2Field[D](d.name, p, 3,
      productElement2Field[E](e.name, p, 4,
      productElement2Field[F](f.name, p, 5,
      productElement2Field[G](g.name, p, 6,
      productElement2Field[H](h.name, p, 7))))))))
    )
    def read(value: JsValue) = construct(
      fromField[A](value, a),
      fromField[B](value, b),
      fromField[C](value, c),
      fromField[D](value, d),
      fromField[E](value, e),
      fromField[F](value, f),
      fromField[G](value, g),
      fromField[H](value, h)
    )
  }

  def jsonFormat9[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, I :JF, T <: Product :ClassManifest]
    (construct: (A, B, C, D, E, F, G, H, I) => T): RootJsonFormat[T] = {
    val Array(a, b, c, d, e, f, g, h, i) = extractFieldNames(classManifest[T])
    jsonFormat(construct, a, b, c, d, e, f, g, h, i)
  }
  def jsonFormat[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, I :JF, T <: Product]
        (construct: (A, B, C, D, E, F, G, H, I) => T, a : Field, b: Field, c: Field, d: Field, e: Field, f: Field,
         g: Field, h: Field, i: Field): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject(
      productElement2Field[A](a.name, p, 0,
      productElement2Field[B](b.name, p, 1,
      productElement2Field[C](c.name, p, 2,
      productElement2Field[D](d.name, p, 3,
      productElement2Field[E](e.name, p, 4,
      productElement2Field[F](f.name, p, 5,
      productElement2Field[G](g.name, p, 6,
      productElement2Field[H](h.name, p, 7,
      productElement2Field[I](i.name, p, 8)))))))))
    )
    def read(value: JsValue) = construct(
      fromField[A](value, a),
      fromField[B](value, b),
      fromField[C](value, c),
      fromField[D](value, d),
      fromField[E](value, e),
      fromField[F](value, f),
      fromField[G](value, g),
      fromField[H](value, h),
      fromField[I](value, i)
    )
  }

  def jsonFormat10[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, I :JF, J :JF, T <: Product :ClassManifest]
    (construct: (A, B, C, D, E, F, G, H, I, J) => T): RootJsonFormat[T] = {
    val Array(a, b, c, d, e, f, g, h, i, j) = extractFieldNames(classManifest[T])
    jsonFormat(construct, a, b, c, d, e, f, g, h, i, j)
  }
  def jsonFormat[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, I :JF, J :JF, T <: Product]
        (construct: (A, B, C, D, E, F, G, H, I, J) => T, a : Field, b: Field, c: Field, d: Field, e: Field,
         f: Field, g: Field, h: Field, i: Field, j: Field): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject(
      productElement2Field[A](a.name, p, 0,
      productElement2Field[B](b.name, p, 1,
      productElement2Field[C](c.name, p, 2,
      productElement2Field[D](d.name, p, 3,
      productElement2Field[E](e.name, p, 4,
      productElement2Field[F](f.name, p, 5,
      productElement2Field[G](g.name, p, 6,
      productElement2Field[H](h.name, p, 7,
      productElement2Field[I](i.name, p, 8,
      productElement2Field[J](j.name, p, 9))))))))))
    )
    def read(value: JsValue) = construct(
      fromField[A](value, a),
      fromField[B](value, b),
      fromField[C](value, c),
      fromField[D](value, d),
      fromField[E](value, e),
      fromField[F](value, f),
      fromField[G](value, g),
      fromField[H](value, h),
      fromField[I](value, i),
      fromField[J](value, j)
    )
  }

  def jsonFormat11[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, I :JF, J :JF, K :JF, T <: Product :ClassManifest]
    (construct: (A, B, C, D, E, F, G, H, I, J, K) => T): RootJsonFormat[T] = {
    val Array(a, b, c, d, e, f, g, h, i, j, k) = extractFieldNames(classManifest[T])
    jsonFormat(construct, a, b, c, d, e, f, g, h, i, j, k)
  }
  def jsonFormat[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, I :JF, J :JF, K :JF, T <: Product]
        (construct: (A, B, C, D, E, F, G, H, I, J, K) => T, a : Field, b: Field, c: Field, d: Field, e: Field,
         f: Field, g: Field, h: Field, i: Field, j: Field, k: Field): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject(
      productElement2Field[A](a.name, p, 0,
      productElement2Field[B](b.name, p, 1,
      productElement2Field[C](c.name, p, 2,
      productElement2Field[D](d.name, p, 3,
      productElement2Field[E](e.name, p, 4,
      productElement2Field[F](f.name, p, 5,
      productElement2Field[G](g.name, p, 6,
      productElement2Field[H](h.name, p, 7,
      productElement2Field[I](i.name, p, 8,
      productElement2Field[J](j.name, p, 9,
      productElement2Field[K](k.name, p, 10)))))))))))
    )
    def read(value: JsValue) = construct(
      fromField[A](value, a),
      fromField[B](value, b),
      fromField[C](value, c),
      fromField[D](value, d),
      fromField[E](value, e),
      fromField[F](value, f),
      fromField[G](value, g),
      fromField[H](value, h),
      fromField[I](value, i),
      fromField[J](value, j),
      fromField[K](value, k)
    )
  }

  def jsonFormat12[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, I :JF, J :JF, K :JF, L: JF, T <: Product :ClassManifest]
    (construct: (A, B, C, D, E, F, G, H, I, J, K, L) => T): RootJsonFormat[T] = {
    val Array(a, b, c, d, e, f, g, h, i, j, k, l) = extractFieldNames(classManifest[T])
    jsonFormat(construct, a, b, c, d, e, f, g, h, i, j, k, l)
  }
  def jsonFormat[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, I :JF, J :JF, K :JF, L :JF, T <: Product]
        (construct: (A, B, C, D, E, F, G, H, I, J, K, L) => T, a : Field, b: Field, c: Field, d: Field, e: Field,
         f: Field, g: Field, h: Field, i: Field, j: Field, k: Field, l: Field): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject(
      productElement2Field[A](a.name, p,  0,
      productElement2Field[B](b.name, p,  1,
      productElement2Field[C](c.name, p,  2,
      productElement2Field[D](d.name, p,  3,
      productElement2Field[E](e.name, p,  4,
      productElement2Field[F](f.name, p,  5,
      productElement2Field[G](g.name, p,  6,
      productElement2Field[H](h.name, p,  7,
      productElement2Field[I](i.name, p,  8,
      productElement2Field[J](j.name, p,  9,
      productElement2Field[K](k.name, p, 10,
      productElement2Field[L](l.name, p, 11))))))))))))
    )
    def read(value: JsValue) = construct(
      fromField[A](value, a),
      fromField[B](value, b),
      fromField[C](value, c),
      fromField[D](value, d),
      fromField[E](value, e),
      fromField[F](value, f),
      fromField[G](value, g),
      fromField[H](value, h),
      fromField[I](value, i),
      fromField[J](value, j),
      fromField[K](value, k),
      fromField[L](value, l)
    )
  }

  def jsonFormat13[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, I :JF, J :JF, K :JF, L: JF, M :JF, T <: Product :ClassManifest]
    (construct: (A, B, C, D, E, F, G, H, I, J, K, L, M) => T): RootJsonFormat[T] = {
    val Array(a, b, c, d, e, f, g, h, i, j, k, l, m) = extractFieldNames(classManifest[T])
    jsonFormat(construct, a, b, c, d, e, f, g, h, i, j, k, l, m)
  }
  def jsonFormat[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, I :JF, J :JF, K :JF, L :JF, M :JF, T <: Product]
        (construct: (A, B, C, D, E, F, G, H, I, J, K, L, M) => T, a : Field, b: Field, c: Field, d: Field, e: Field,
         f: Field, g: Field, h: Field, i: Field, j: Field, k: Field, l: Field, m: Field): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject(
      productElement2Field[A](a.name, p,  0,
      productElement2Field[B](b.name, p,  1,
      productElement2Field[C](c.name, p,  2,
      productElement2Field[D](d.name, p,  3,
      productElement2Field[E](e.name, p,  4,
      productElement2Field[F](f.name, p,  5,
      productElement2Field[G](g.name, p,  6,
      productElement2Field[H](h.name, p,  7,
      productElement2Field[I](i.name, p,  8,
      productElement2Field[J](j.name, p,  9,
      productElement2Field[K](k.name, p, 10,
      productElement2Field[L](l.name, p, 11,
      productElement2Field[M](m.name, p, 12)))))))))))))
    )
    def read(value: JsValue) = construct(
      fromField[A](value, a),
      fromField[B](value, b),
      fromField[C](value, c),
      fromField[D](value, d),
      fromField[E](value, e),
      fromField[F](value, f),
      fromField[G](value, g),
      fromField[H](value, h),
      fromField[I](value, i),
      fromField[J](value, j),
      fromField[K](value, k),
      fromField[L](value, l),
      fromField[M](value, m)
    )
  }

  def jsonFormat14[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, I :JF, J :JF, K :JF, L: JF, M :JF, N :JF, T <: Product :ClassManifest]
    (construct: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) => T): RootJsonFormat[T] = {
    val Array(a, b, c, d, e, f, g, h, i, j, k, l, m, n) = extractFieldNames(classManifest[T])
    jsonFormat(construct, a, b, c, d, e, f, g, h, i, j, k, l, m, n)
  }
  def jsonFormat[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, I :JF, J :JF, K :JF, L :JF, M :JF, N :JF, T <: Product]
        (construct: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) => T, a : Field, b: Field, c: Field, d: Field,
         e: Field, f: Field, g: Field, h: Field, i: Field, j: Field, k: Field, l: Field, m: Field,
         n: Field): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject(
      productElement2Field[A](a.name, p,  0,
      productElement2Field[B](b.name, p,  1,
      productElement2Field[C](c.name, p,  2,
      productElement2Field[D](d.name, p,  3,
      productElement2Field[E](e.name, p,  4,
      productElement2Field[F](f.name, p,  5,
      productElement2Field[G](g.name, p,  6,
      productElement2Field[H](h.name, p,  7,
      productElement2Field[I](i.name, p,  8,
      productElement2Field[J](j.name, p,  9,
      productElement2Field[K](k.name, p, 10,
      productElement2Field[L](l.name, p, 11,
      productElement2Field[M](m.name, p, 12,
      productElement2Field[N](n.name, p, 13))))))))))))))
    )
    def read(value: JsValue) = construct(
      fromField[A](value, a),
      fromField[B](value, b),
      fromField[C](value, c),
      fromField[D](value, d),
      fromField[E](value, e),
      fromField[F](value, f),
      fromField[G](value, g),
      fromField[H](value, h),
      fromField[I](value, i),
      fromField[J](value, j),
      fromField[K](value, k),
      fromField[L](value, l),
      fromField[M](value, m),
      fromField[N](value, n)
    )
  }

  def jsonFormat15[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, I :JF, J :JF, K :JF, L: JF, M :JF, N :JF, O :JF, T <: Product :ClassManifest]
    (construct: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) => T): RootJsonFormat[T] = {
    val Array(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) = extractFieldNames(classManifest[T])
    jsonFormat(construct, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
  }
  def jsonFormat[A :JF, B :JF, C :JF, D :JF, E :JF, F :JF, G :JF, H :JF, I :JF, J :JF, K :JF, L :JF, M :JF, N :JF, O :JF, T <: Product]
        (construct: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) => T, a : Field, b: Field, c: Field, d: Field,
         e: Field, f: Field, g: Field, h: Field, i: Field, j: Field, k: Field, l: Field, m: Field, n: Field,
         o: Field): RootJsonFormat[T] = new RootJsonFormat[T]{
    def write(p: T) = JsObject(
      productElement2Field[A](a.name, p,  0,
      productElement2Field[B](b.name, p,  1,
      productElement2Field[C](c.name, p,  2,
      productElement2Field[D](d.name, p,  3,
      productElement2Field[E](e.name, p,  4,
      productElement2Field[F](f.name, p,  5,
      productElement2Field[G](g.name, p,  6,
      productElement2Field[H](h.name, p,  7,
      productElement2Field[I](i.name, p,  8,
      productElement2Field[J](j.name, p,  9,
      productElement2Field[K](k.name, p, 10,
      productElement2Field[L](l.name, p, 11,
      productElement2Field[M](m.name, p, 12,
      productElement2Field[N](n.name, p, 13,
      productElement2Field[O](o.name, p, 14)))))))))))))))
    )
    def read(value: JsValue) = construct(
      fromField[A](value, a),
      fromField[B](value, b),
      fromField[C](value, c),
      fromField[D](value, d),
      fromField[E](value, e),
      fromField[F](value, f),
      fromField[G](value, g),
      fromField[H](value, h),
      fromField[I](value, i),
      fromField[J](value, j),
      fromField[K](value, k),
      fromField[L](value, l),
      fromField[M](value, m),
      fromField[N](value, n),
      fromField[O](value, o)
    )
  }

  // helpers
  
  protected def productElement2Field[T](fieldName: String, p: Product, ix: Int, rest: List[JsField] = Nil)
                                       (implicit writer: JsonWriter[T]): List[JsField] = {
    val value = p.productElement(ix).asInstanceOf[T]
    writer match {
      case _: OptionFormat[_] if (value == None) => rest
      case _ => (fieldName, writer.write(value)) :: rest
    }
  }

  private def fromField[T](value: JsValue, fieldAndDefault: Field)(implicit reader: JsonReader[T]) = {
    value match {
      case x: JsObject =>
        x.fields.get(fieldAndDefault.name) match {
          case Some(value) => 
            reader.read(value)
          case None => fieldAndDefault.getDefault match {
            case Some(defarg) =>
              defarg().asInstanceOf[T]
            case None =>
              if (reader.isInstanceOf[OptionFormat[_]]) None.asInstanceOf[T]
              else deserializationError("Object is missing required member '" + fieldAndDefault.name + "'")
          }
        }
      case _ => deserializationError("Object expected")
    }
  }

  protected def extractFieldNames(classManifest: ClassManifest[_]): Array[Field] = {
    val clazz = classManifest.erasure
    try {
      // Need companion class for default arguments.
      lazy val companionClass = Class.forName(clazz.getName + "$")
      lazy val moduleField = 
        try { companionClass.getField("MODULE$") }
        catch { case e : Throwable => throw new RuntimeException("Can't deserialize default arguments of nested case classes", e) }
      lazy val companionObj = moduleField.get(null)
      // copy methods have the form copy$default$N(), we need to sort them in order, but must account for the fact
      // that lexical sorting of ...8(), ...9(), ...10() is not correct, so we extract N and sort by N.toInt
      val copyDefaultMethods = clazz.getMethods.filter(_.getName.startsWith("copy$default$")).sortBy(
        _.getName.drop("copy$default$".length).takeWhile(_ != '(').toInt)
      val fields = clazz.getDeclaredFields.filterNot(f => f.getName.startsWith("$") || Modifier.isTransient(f.getModifiers))
      if (copyDefaultMethods.length != fields.length)
        sys.error("Case class " + clazz.getName + " declares additional fields")
      val applyDefaultMethods = copyDefaultMethods.map { method => 
        try { 
          val defmeth = companionClass.getMethod("apply" + method.getName.drop("copy".size))
          Some(() => defmeth.invoke(companionObj))}
        catch { case e : Throwable => None }
      }
      if (fields.zip(copyDefaultMethods).exists { case (f, m) => f.getType != m.getReturnType })
        sys.error("Cannot determine field order of case class " + clazz.getName)
      fields.zip(applyDefaultMethods).map { case (f, m) => Field(f.getName, m) }
    } catch {
      case ex : Throwable => throw new RuntimeException("Cannot automatically determine case class field names and order " +
        "for '" + clazz.getName + "', please use the 'jsonFormat' overload with explicit field name specification", ex)
    }
  }
}

/**
 * This trait supplies an alternative rendering mode for optional case class members.
 * Normally optional members that are undefined (`None`) are not rendered at all.
 * By mixing in this trait into your custom JsonProtocol you can enforce the rendering of undefined members as `null`.
 * (Note that this only affect JSON writing, spray-json will always read missing optional members as well as `null`
 * optional members as `None`.)
 */
trait NullOptions extends ProductFormats {
  this: StandardFormats =>

  override protected def productElement2Field[T](fieldName: String, p: Product, ix: Int, rest: List[JsField])
                                                (implicit writer: JsonWriter[T]) = {
    val value = p.productElement(ix).asInstanceOf[T]
    (fieldName, writer.write(value)) :: rest
  }
}

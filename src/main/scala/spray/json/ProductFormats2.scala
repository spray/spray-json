/*
 * Copyright (C) 2011 Ruud Diterwich, BusyMachines
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

import scala.reflect.ClassTag

trait ProductFormats2 {
  type Fields = Seq[ProductField]
  type JFmt[F] = ProductFieldFormat[F]
  private[spray] trait FNull
  private[spray] implicit val fNull = new NullProductFieldFormat[FNull]

  def format1[P <: Product :ClassTag, F0 :JFmt](construct: (F0) => P) = new ProductFormatImpl[P, F0, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        Nil)
    )
    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value)
    )
  }

  def format2[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt](construct: (F0, F1) => P) = new ProductFormatImpl[P, F0, F1, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          Nil))
    )
    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value)
    )
  }

  def format3[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt](construct: (F0, F1, F2) => P) = new ProductFormatImpl[P, F0, F1, F2, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            Nil)))
    )
    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value)
    )
  }

  def format4[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt, F3 :JFmt](construct: (F0, F1, F2, F3) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              Nil))))
    )
    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value)
    )
  }

  def format5[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt, F3 :JFmt, F4 :JFmt](construct: (F0, F1, F2, F3, F4) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                Nil)))))
    )
    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value)
    )
  }

  def format6[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt, F3 :JFmt, F4 :JFmt, F5 :JFmt](construct: (F0, F1, F2, F3, F4, F5) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  Nil))))))
    )
    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value)
    )
  }

  def format7[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt, F3 :JFmt, F4 :JFmt, F5 :JFmt, F6 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    Nil)))))))
    )
    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value)
    )
  }

  def format8[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt, F3 :JFmt, F4 :JFmt, F5 :JFmt, F6 :JFmt, F7 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6, F7) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, F7, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    write[F7](fields(7), p, 7,
                      Nil))))))))
    )
    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value),
      read[F7](fields(7), value)
    )
  }

  def format9[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt, F3 :JFmt, F4 :JFmt, F5 :JFmt, F6 :JFmt, F7 :JFmt, F8 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6, F7, F8) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, F7, F8, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    write[F7](fields(7), p, 7,
                      write[F8](fields(8), p, 8,
                        Nil)))))))))
    )
    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value),
      read[F7](fields(7), value),
      read[F8](fields(8), value)
    )
  }

  def format10[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt, F3 :JFmt, F4 :JFmt, F5 :JFmt, F6 :JFmt, F7 :JFmt, F8 :JFmt, F9 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6, F7, F8, F9) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    write[F7](fields(7), p, 7,
                      write[F8](fields(8), p, 8,
                        write[F9](fields(9), p, 9,
                          Nil))))))))))
    )
    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value),
      read[F7](fields(7), value),
      read[F8](fields(8), value),
      read[F9](fields(9), value)
    )
  }

  def format11[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt, F3 :JFmt, F4 :JFmt, F5 :JFmt, F6 :JFmt, F7 :JFmt, F8 :JFmt, F9 :JFmt, F10 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    write[F7](fields(7), p, 7,
                      write[F8](fields(8), p, 8,
                        write[F9](fields(9), p, 9,
                          write[F10](fields(10), p, 10,
                            Nil)))))))))))
    )
    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value),
      read[F7](fields(7), value),
      read[F8](fields(8), value),
      read[F9](fields(9), value),
      read[F10](fields(10), value)
    )
  }

  def format12[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt,F3 :JFmt,F4 :JFmt,F5 :JFmt,F6 :JFmt,F7 :JFmt,F8 :JFmt,F9 :JFmt,F10 :JFmt,F11 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    write[F7](fields(7), p, 7,
                      write[F8](fields(8), p, 8,
                        write[F9](fields(9), p, 9,
                          write[F10](fields(10), p, 10,
                            write[F11](fields(11), p, 11,
                              Nil))))))))))))
    )

    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value),
      read[F7](fields(7), value),
      read[F8](fields(8), value),
      read[F9](fields(9), value),
      read[F10](fields(10), value),
      read[F11](fields(11), value)
    )
  }

  def format13[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt,F3 :JFmt,F4 :JFmt,F5 :JFmt,F6 :JFmt,F7 :JFmt,F8 :JFmt,F9 :JFmt,F10 :JFmt, F11 :JFmt, F12 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    write[F7](fields(7), p, 7,
                      write[F8](fields(8), p, 8,
                        write[F9](fields(9), p, 9,
                          write[F10](fields(10), p, 10,
                            write[F11](fields(11), p, 11,
                              write[F12](fields(12), p, 12,
                                Nil)))))))))))))
    )

    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value),
      read[F7](fields(7), value),
      read[F8](fields(8), value),
      read[F9](fields(9), value),
      read[F10](fields(10), value),
      read[F11](fields(11), value),
      read[F12](fields(12), value)
    )
  }

  def format14[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt,F3 :JFmt,F4 :JFmt,F5 :JFmt,F6 :JFmt,F7 :JFmt,F8 :JFmt,F9 :JFmt,F10 :JFmt, F11 :JFmt, F12 :JFmt, F13 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, FNull, FNull, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    write[F7](fields(7), p, 7,
                      write[F8](fields(8), p, 8,
                        write[F9](fields(9), p, 9,
                          write[F10](fields(10), p, 10,
                            write[F11](fields(11), p, 11,
                              write[F12](fields(12), p, 12,
                                write[F13](fields(13), p, 13,
                                  Nil))))))))))))))
    )

    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value),
      read[F7](fields(7), value),
      read[F8](fields(8), value),
      read[F9](fields(9), value),
      read[F10](fields(10), value),
      read[F11](fields(11), value),
      read[F12](fields(12), value),
      read[F13](fields(13), value)
    )
  }

  def format15[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt,F3 :JFmt,F4 :JFmt,F5 :JFmt,F6 :JFmt,F7 :JFmt,F8 :JFmt,F9 :JFmt,F10 :JFmt, F11 :JFmt, F12 :JFmt, F13 :JFmt, F14 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, FNull, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    write[F7](fields(7), p, 7,
                      write[F8](fields(8), p, 8,
                        write[F9](fields(9), p, 9,
                          write[F10](fields(10), p, 10,
                            write[F11](fields(11), p, 11,
                              write[F12](fields(12), p, 12,
                                write[F13](fields(13), p, 13,
                                  write[F14](fields(14), p, 14,
                                    Nil)))))))))))))))
    )

    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value),
      read[F7](fields(7), value),
      read[F8](fields(8), value),
      read[F9](fields(9), value),
      read[F10](fields(10), value),
      read[F11](fields(11), value),
      read[F12](fields(12), value),
      read[F13](fields(13), value),
      read[F14](fields(14), value)
    )
  }

  def format16[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt,F3 :JFmt,F4 :JFmt,F5 :JFmt,F6 :JFmt,F7 :JFmt,F8 :JFmt,F9 :JFmt,F10 :JFmt, F11 :JFmt, F12 :JFmt, F13 :JFmt, F14 :JFmt, F15 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, FNull, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    write[F7](fields(7), p, 7,
                      write[F8](fields(8), p, 8,
                        write[F9](fields(9), p, 9,
                          write[F10](fields(10), p, 10,
                            write[F11](fields(11), p, 11,
                              write[F12](fields(12), p, 12,
                                write[F13](fields(13), p, 13,
                                  write[F14](fields(14), p, 14,
                                    write[F15](fields(15), p, 15,
                                      Nil))))))))))))))))
    )

    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value),
      read[F7](fields(7), value),
      read[F8](fields(8), value),
      read[F9](fields(9), value),
      read[F10](fields(10), value),
      read[F11](fields(11), value),
      read[F12](fields(12), value),
      read[F13](fields(13), value),
      read[F14](fields(14), value),
      read[F15](fields(15), value)
    )
  }

  def format17[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt,F3 :JFmt,F4 :JFmt,F5 :JFmt,F6 :JFmt,F7 :JFmt,F8 :JFmt,F9 :JFmt,F10 :JFmt, F11 :JFmt, F12 :JFmt, F13 :JFmt, F14 :JFmt, F15 :JFmt, F16 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, FNull, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    write[F7](fields(7), p, 7,
                      write[F8](fields(8), p, 8,
                        write[F9](fields(9), p, 9,
                          write[F10](fields(10), p, 10,
                            write[F11](fields(11), p, 11,
                              write[F12](fields(12), p, 12,
                                write[F13](fields(13), p, 13,
                                  write[F14](fields(14), p, 14,
                                    write[F15](fields(15), p, 15,
                                      write[F16](fields(16), p, 16,
                                        Nil)))))))))))))))))
    )

    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value),
      read[F7](fields(7), value),
      read[F8](fields(8), value),
      read[F9](fields(9), value),
      read[F10](fields(10), value),
      read[F11](fields(11), value),
      read[F12](fields(12), value),
      read[F13](fields(13), value),
      read[F14](fields(14), value),
      read[F15](fields(15), value),
      read[F16](fields(16), value)
    )
  }

  def format18[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt,F3 :JFmt,F4 :JFmt,F5 :JFmt,F6 :JFmt,F7 :JFmt,F8 :JFmt,F9 :JFmt,F10 :JFmt, F11 :JFmt, F12 :JFmt, F13 :JFmt, F14 :JFmt, F15 :JFmt, F16 :JFmt, F17 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, F17) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, F17, FNull, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    write[F7](fields(7), p, 7,
                      write[F8](fields(8), p, 8,
                        write[F9](fields(9), p, 9,
                          write[F10](fields(10), p, 10,
                            write[F11](fields(11), p, 11,
                              write[F12](fields(12), p, 12,
                                write[F13](fields(13), p, 13,
                                  write[F14](fields(14), p, 14,
                                    write[F15](fields(15), p, 15,
                                      write[F16](fields(16), p, 16,
                                        write[F17](fields(17), p, 17,
                                          Nil))))))))))))))))))
    )

    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value),
      read[F7](fields(7), value),
      read[F8](fields(8), value),
      read[F9](fields(9), value),
      read[F10](fields(10), value),
      read[F11](fields(11), value),
      read[F12](fields(12), value),
      read[F13](fields(13), value),
      read[F14](fields(14), value),
      read[F15](fields(15), value),
      read[F16](fields(16), value),
      read[F17](fields(17), value)
    )
  }

  def format19[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt,F3 :JFmt,F4 :JFmt,F5 :JFmt,F6 :JFmt,F7 :JFmt,F8 :JFmt,F9 :JFmt,F10 :JFmt, F11 :JFmt, F12 :JFmt, F13 :JFmt, F14 :JFmt, F15 :JFmt, F16 :JFmt, F17 :JFmt, F18 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, F17, F18) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, F17, F18, FNull, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    write[F7](fields(7), p, 7,
                      write[F8](fields(8), p, 8,
                        write[F9](fields(9), p, 9,
                          write[F10](fields(10), p, 10,
                            write[F11](fields(11), p, 11,
                              write[F12](fields(12), p, 12,
                                write[F13](fields(13), p, 13,
                                  write[F14](fields(14), p, 14,
                                    write[F15](fields(15), p, 15,
                                      write[F16](fields(16), p, 16,
                                        write[F17](fields(17), p, 17,
                                          write[F18](fields(18), p, 18,
                                            Nil)))))))))))))))))))
    )

    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value),
      read[F7](fields(7), value),
      read[F8](fields(8), value),
      read[F9](fields(9), value),
      read[F10](fields(10), value),
      read[F11](fields(11), value),
      read[F12](fields(12), value),
      read[F13](fields(13), value),
      read[F14](fields(14), value),
      read[F15](fields(15), value),
      read[F16](fields(16), value),
      read[F17](fields(17), value),
      read[F18](fields(18), value)
    )
  }

  def format20[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt,F3 :JFmt,F4 :JFmt,F5 :JFmt,F6 :JFmt,F7 :JFmt,F8 :JFmt,F9 :JFmt,F10 :JFmt, F11 :JFmt, F12 :JFmt, F13 :JFmt, F14 :JFmt, F15 :JFmt, F16 :JFmt, F17 :JFmt, F18 :JFmt, F19 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, F17, F18, F19) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, F17, F18, F19, FNull, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    write[F7](fields(7), p, 7,
                      write[F8](fields(8), p, 8,
                        write[F9](fields(9), p, 9,
                          write[F10](fields(10), p, 10,
                            write[F11](fields(11), p, 11,
                              write[F12](fields(12), p, 12,
                                write[F13](fields(13), p, 13,
                                  write[F14](fields(14), p, 14,
                                    write[F15](fields(15), p, 15,
                                      write[F16](fields(16), p, 16,
                                        write[F17](fields(17), p, 17,
                                          write[F18](fields(18), p, 18,
                                            write[F19](fields(19), p, 19,
                                              Nil))))))))))))))))))))
    )

    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value),
      read[F7](fields(7), value),
      read[F8](fields(8), value),
      read[F9](fields(9), value),
      read[F10](fields(10), value),
      read[F11](fields(11), value),
      read[F12](fields(12), value),
      read[F13](fields(13), value),
      read[F14](fields(14), value),
      read[F15](fields(15), value),
      read[F16](fields(16), value),
      read[F17](fields(17), value),
      read[F18](fields(18), value),
      read[F19](fields(19), value)
    )
  }

  def format21[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt,F3 :JFmt,F4 :JFmt,F5 :JFmt,F6 :JFmt,F7 :JFmt,F8 :JFmt,F9 :JFmt,F10 :JFmt, F11 :JFmt, F12 :JFmt, F13 :JFmt, F14 :JFmt, F15 :JFmt, F16 :JFmt, F17 :JFmt, F18 :JFmt, F19 :JFmt, F20 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, F17, F18, F19, F20) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, F17, F18, F19, F20, FNull] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    write[F7](fields(7), p, 7,
                      write[F8](fields(8), p, 8,
                        write[F9](fields(9), p, 9,
                          write[F10](fields(10), p, 10,
                            write[F11](fields(11), p, 11,
                              write[F12](fields(12), p, 12,
                                write[F13](fields(13), p, 13,
                                  write[F14](fields(14), p, 14,
                                    write[F15](fields(15), p, 15,
                                      write[F16](fields(16), p, 16,
                                        write[F17](fields(17), p, 17,
                                          write[F18](fields(18), p, 18,
                                            write[F19](fields(19), p, 19,
                                              write[F20](fields(20), p, 20,
                                                Nil)))))))))))))))))))))
    )

    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value),
      read[F7](fields(7), value),
      read[F8](fields(8), value),
      read[F9](fields(9), value),
      read[F10](fields(10), value),
      read[F11](fields(11), value),
      read[F12](fields(12), value),
      read[F13](fields(13), value),
      read[F14](fields(14), value),
      read[F15](fields(15), value),
      read[F16](fields(16), value),
      read[F17](fields(17), value),
      read[F18](fields(18), value),
      read[F19](fields(19), value),
      read[F20](fields(20), value)
    )
  }

  def format22[P <: Product :ClassTag, F0 :JFmt, F1 :JFmt, F2 :JFmt,F3 :JFmt,F4 :JFmt,F5 :JFmt,F6 :JFmt,F7 :JFmt,F8 :JFmt,F9 :JFmt,F10 :JFmt, F11 :JFmt, F12 :JFmt, F13 :JFmt, F14 :JFmt, F15 :JFmt, F16 :JFmt, F17 :JFmt, F18 :JFmt, F19 :JFmt, F20 :JFmt, F21 :JFmt](construct: (F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, F17, F18, F19, F20, F21) => P) = new ProductFormatImpl[P, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, F17, F18, F19, F20, F21] {
    def write(fields: Fields, p: P) = jsObject(
      write[F0](fields(0), p, 0,
        write[F1](fields(1), p, 1,
          write[F2](fields(2), p, 2,
            write[F3](fields(3), p, 3,
              write[F4](fields(4), p, 4,
                write[F5](fields(5), p, 5,
                  write[F6](fields(6), p, 6,
                    write[F7](fields(7), p, 7,
                      write[F8](fields(8), p, 8,
                        write[F9](fields(9), p, 9,
                          write[F10](fields(10), p, 10,
                            write[F11](fields(11), p, 11,
                              write[F12](fields(12), p, 12,
                                write[F13](fields(13), p, 13,
                                  write[F14](fields(14), p, 14,
                                    write[F15](fields(15), p, 15,
                                      write[F16](fields(16), p, 16,
                                        write[F17](fields(17), p, 17,
                                          write[F18](fields(18), p, 18,
                                            write[F19](fields(19), p, 19,
                                              write[F20](fields(20), p, 20,
                                                write[F21](fields(21), p, 21,
                                                  Nil))))))))))))))))))))))
    )

    def read(fields: Fields, value: JsValue) = construct(
      read[F0](fields(0), value),
      read[F1](fields(1), value),
      read[F2](fields(2), value),
      read[F3](fields(3), value),
      read[F4](fields(4), value),
      read[F5](fields(5), value),
      read[F6](fields(6), value),
      read[F7](fields(7), value),
      read[F8](fields(8), value),
      read[F9](fields(9), value),
      read[F10](fields(10), value),
      read[F11](fields(11), value),
      read[F12](fields(12), value),
      read[F13](fields(13), value),
      read[F14](fields(14), value),
      read[F15](fields(15), value),
      read[F16](fields(16), value),
      read[F17](fields(17), value),
      read[F18](fields(18), value),
      read[F19](fields(19), value),
      read[F20](fields(20), value),
      read[F21](fields(21), value)
    )
  }
}

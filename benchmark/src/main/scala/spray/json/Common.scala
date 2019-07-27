package spray.json

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

// taken from https://github.com/plokhotnyuk/jsoniter-scala/blob/23984b3555a06d1ae27d4e3646832172e8ae27e0/jsoniter-scala-benchmark/src/main/scala/com/github/plokhotnyuk/jsoniter_scala/macros/CommonParams.scala#L7-L24

@State(Scope.Thread)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = Array(
  "-server",
  "-Xms1g",
  "-Xmx1g",
  "-XX:NewSize=500m",
  "-XX:MaxNewSize=500m",
  "-XX:InitialCodeCacheSize=512m",
  "-XX:ReservedCodeCacheSize=512m",
  "-XX:+UseParallelGC",
  "-XX:-UseBiasedLocking",
  "-XX:+AlwaysPreTouch"
))
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
abstract class Common

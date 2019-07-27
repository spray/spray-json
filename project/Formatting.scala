import sbt._
import com.typesafe.sbt.SbtScalariform
import SbtScalariform.autoImport._

object Formatting extends AutoPlugin {
  import scalariform.formatter.preferences._

  override def trigger = PluginTrigger.AllRequirements
  override def requires = SbtScalariform

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalariformPreferences := setPreferences(scalariformPreferences.value)
  )

  def setPreferences(preferences: IFormattingPreferences) = preferences
    .setPreference(AlignParameters, true)
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(DoubleIndentConstructorArguments, false)
    .setPreference(DoubleIndentMethodDeclaration, false)
    .setPreference(DanglingCloseParenthesis, Preserve)
    .setPreference(NewlineAtEndOfFile, true)
    .setPreference(RewriteArrowSymbols, true)
    .setPreference(UseUnicodeArrows, false)
}

package io.github.rosemoe.sora.langs.textmate;

import android.os.Bundle
import com.raredev.vcspace.utils.PreferencesUtils
import io.github.rosemoe.sora.lang.completion.CompletionHelper
import io.github.rosemoe.sora.lang.completion.CompletionPublisher
import io.github.rosemoe.sora.lang.format.Formatter
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.ContentReference
import io.github.rosemoe.sora.util.MyCharacter
import org.eclipse.tm4e.core.grammar.IGrammar
import org.eclipse.tm4e.languageconfiguration.model.LanguageConfiguration

class VCSpaceTMLanguage(
  iGrammar: IGrammar,
  languageConfiguration: LanguageConfiguration?
): TextMateLanguage(
  iGrammar,
  languageConfiguration,
  GrammarRegistry.getInstance()
) {

  init {
    tabSize = PreferencesUtils.tabSize
    useTab(PreferencesUtils.useTab)
  }

  companion object {

    fun create(scope: String): VCSpaceTMLanguage {
      val grammarRegistry = GrammarRegistry.getInstance()
      val iGrammar = grammarRegistry.findGrammar(scope)

      if (iGrammar == null) {
        throw IllegalArgumentException("Language with $scope scope name not found")
      }
      return VCSpaceTMLanguage(iGrammar, grammarRegistry.findLanguageConfiguration(scope))
    }
  }
}
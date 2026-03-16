package eu.kanade.tachiyomi.extension.fr.mugi

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource

class CrunchyScan : ParsedHttpSource() {

    override val name = "CrunchyScan"
    override val baseUrl = "https://crunchyscan.fr"
    override val lang = "fr"
    override val supportsLatest = true

    // 1. Comment trouver la liste des mangas (Page Populaire)
    override fun popularMangaSelector() = ".manga-list__card"

    // 2. Comment passer à la page suivante
    override fun popularMangaNextPageSelector() = "a.next"

    // 3. Comment extraire les infos d'un manga dans la liste
    override fun popularMangaFromElement(element: Element): SManga {
        val manga = SManga.create()
        manga.title = element.select(".manga-list__card-title").text()
        manga.thumbnail_url = element.select(".manga-list__card-image img").attr("absUrl:src")
        manga.setUrlWithoutDomain(element.select("a").first()!!.attr("href"))
        return manga
    }

    // --- Les fonctions ci-dessous sont obligatoires mais on les remplira plus tard ---

    override fun latestUpdatesSelector() = popularMangaSelector()
    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()
    override fun latestUpdatesFromElement(element: Element) = popularMangaFromElement(element)

    override fun mangaDetailsParse(document: Document): SManga {
        val manga = SManga.create()
        manga.description = document.select(".manga-summary").text()
        return manga
    }

    override fun chapterListSelector() = ".chapitre-lien" // Ce qu'on a vu ensemble !
    override fun chapterFromElement(element: Element) = throw Exception("Pas encore codé")
    override fun pageListParse(document: Document) = throw Exception("Pas encore codé")
    override fun searchMangaSelector() = throw Exception("Pas encore codé")
    override fun searchMangaFromElement(element: Element) = throw Exception("Pas encore codé")
    override fun searchMangaNextPageSelector() = throw Exception("Pas encore codé")
}
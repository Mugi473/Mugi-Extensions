package eu.kanade.tachiyomi.extension.fr.mugi

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import eu.kanade.tachiyomi.network.GET
import okhttp3.Request

class CrunchyScan : ParsedHttpSource() {

    override val name = "CrunchyScan"
    override val baseUrl = "https://crunchyscan.fr"
    override val lang = "fr"
    override val supportsLatest = true

    // --- POPULAR / LATEST ---
    override fun popularMangaSelector() = ".manga-list__card"
    override fun popularMangaNextPageSelector() = "a.next"

    override fun popularMangaFromElement(element: Element): SManga {
        val manga = SManga.create()
        manga.title = element.select(".manga-list__card-title").text()
        manga.thumbnail_url = element.select(".manga-list__card-image img").attr("absUrl:src")
        manga.setUrlWithoutDomain(element.select("a").first()!!.attr("href"))
        return manga
    }

    override fun latestUpdatesSelector() = popularMangaSelector()
    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()
    override fun latestUpdatesFromElement(element: Element) = popularMangaFromElement(element)

    // --- SEARCH ---
    override fun searchMangaSelector() = popularMangaSelector()
    override fun searchMangaNextPageSelector() = popularMangaNextPageSelector()
    override fun searchMangaFromElement(element: Element) = popularMangaFromElement(element)
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        return GET("$baseUrl/search?query=$query&page=$page")
    }

    // --- DETAILS ---
    override fun mangaDetailsParse(document: Document): SManga {
        val manga = SManga.create()
        manga.description = document.select(".manga-summary").text()
        manga.author = document.select(".manga-info__author").text()
        manga.genre = document.select(".manga-info__genres a").joinToString { it.text() }
        return manga
    }

    // --- CHAPTERS ---
    override fun chapterListSelector() = ".manga-chapters__item a"

    override fun chapterFromElement(element: Element): SChapter {
        val chapter = SChapter.create()
        chapter.setUrlWithoutDomain(element.attr("href"))
        chapter.name = element.text()
        return chapter
    }

    // --- PAGES ---
    override fun pageListParse(document: Document): List<Page> {
        return document.select("div.read-content img, .vung-doc img").mapIndexed { i, element ->
            Page(i, "", element.attr("absUrl:src"))
        }
    }

    override fun imageUrlParse(document: Document) = ""
}
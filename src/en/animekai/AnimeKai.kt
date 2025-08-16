package eu.kanade.tachiyomi.extension.en.animekai

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class AnimeKai : ParsedHttpSource() {
    override val name = "AnimeKai"
    override val baseUrl = "https://animekai.example" // TODO: replace with real site
    override val lang = "en"
    override val supportsLatest = true

    // Popular anime
    override fun popularAnimeRequest(page: Int): Request =
        GET("$baseUrl/popular?page=$page", headers)

    override fun popularAnimeSelector() = "div.anime-item"

    override fun popularAnimeFromElement(element: Element): SAnime {
        val anime = SAnime.create()
        anime.setUrlWithoutDomain(element.select("a").attr("href"))
        anime.title = element.select("a > img").attr("alt")
        anime.thumbnail_url = element.select("a > img").attr("src")
        return anime
    }

    override fun popularAnimeNextPageSelector() = "a.next"

    // Episodes
    override fun episodeListSelector() = "ul.episode-list > li"

    override fun episodeFromElement(element: Element): SEpisode {
        val episode = SEpisode.create()
        episode.setUrlWithoutDomain(element.select("a").attr("href"))
        episode.name = element.text()
        return episode
    }

    // Video links
    override fun videoListSelector() = "div.player > source"

    override fun videoFromElement(element: Element): Video {
        val videoUrl = element.attr("src")
        return Video(videoUrl, "HD", videoUrl)
    }

    // Details
    override fun animeDetailsParse(document: Document): SAnime {
        val anime = SAnime.create()
        anime.title = document.select("h1.title").text()
        anime.description = document.select("div.synopsis").text()
        anime.genre = document.select("div.genres > a").joinToString(", ") { it.text() }
        anime.status = SAnime.COMPLETED
        anime.thumbnail_url = document.select("div.cover > img").attr("src")
        return anime
    }

    // Latest
    override fun latestUpdatesRequest(page: Int): Request =
        GET("$baseUrl/latest?page=$page", headers)

    override fun latestUpdatesSelector() = popularAnimeSelector()
    override fun latestUpdatesFromElement(element: Element) = popularAnimeFromElement(element)
    override fun latestUpdatesNextPageSelector() = popularAnimeNextPageSelector()
}

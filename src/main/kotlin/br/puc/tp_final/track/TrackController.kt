package br.puc.tp_final.track

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/track-ms/rest/status")
class TrackController(
    val trackService: TrackService
) {
    @GetMapping
    fun status(): String {
        return trackService.status()
    }
}
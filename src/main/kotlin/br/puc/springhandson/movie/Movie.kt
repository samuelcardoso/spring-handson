package br.puc.springhandson.movie

import jakarta.persistence.*

@Entity
data class Movie(
    var director: String?,
    var title: String?,
    var genre: String?,
    var rating: Int?,
    var year: Int?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
}

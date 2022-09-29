package br.puc.springhandson.movie;

import javax.persistence.*

@Entity
data class Movie(
    var director: String? = null,
    var title: String? = null,
    var genre: String? = null,
    var rating: Int? = null,
    var year: Int? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
}
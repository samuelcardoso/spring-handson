package br.puc.springhandson.movie

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/samplejeecrud/rest/movies")
class MovieController(
    val movieService: MovieService
) {
    @GetMapping("{id}")
    fun find(@PathVariable("id") id: Long?): Movie {
        return movieService.find(id)
    }

    @GetMapping
    fun getMovies(
        @RequestParam("first") first: Int?,
        @RequestParam("max") max: Int?,
        @RequestParam("field") field: String?,
        @RequestParam("searchTerm") searchTerm: String?
    ): List<Movie> {
        return movieService.getMovies(first, max, field, searchTerm)
    }

    @PostMapping
    fun addMovie(movie: Movie): Movie {
        movieService.addMovie(movie)
        return movie
    }

    @PutMapping("{id}")
    fun editMovie(movie: Movie): Movie {
        movieService.editMovie(movie)
        return movie
    }

    @DeleteMapping("{id}")
    fun deleteMovie(@PathVariable("id") id: Long) {
        movieService.deleteMovie(id)
    }

    @GetMapping("count")
    fun count(@RequestParam("field") field: String?, @RequestParam("searchTerm") searchTerm: String?): Int {
        return movieService.count(field, searchTerm)
    }

    @PostMapping("load")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun load() {
        movieService.addMovie(Movie("Wedding Crashers", "David Dobkin", "Comedy", 7, 2005))
        movieService.addMovie(Movie("Starsky & Hutch", "Todd Phillips", "Action", 6, 2004))
        movieService.addMovie(Movie("Shanghai Knights", "David Dobkin", "Action", 6, 2003))
        movieService.addMovie(Movie("I-Spy", "Betty Thomas", "Adventure", 5, 2002))
        movieService.addMovie(Movie("The Royal Tenenbaums", "Wes Anderson", "Comedy", 8, 2001))
        movieService.addMovie(Movie("Zoolander", "Ben Stiller", "Comedy", 6, 2001))
        movieService.addMovie(Movie("Shanghai Noon", "Tom Dey", "Comedy", 7, 2000))
    }
}
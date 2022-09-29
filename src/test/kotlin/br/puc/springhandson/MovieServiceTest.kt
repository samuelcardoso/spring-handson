package br.puc.springhandson

import br.puc.springhandson.movie.Movie
import br.puc.springhandson.movie.MovieService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MovieServiceTest {
	@Autowired
	private lateinit var movieService: MovieService

	@BeforeEach
	@AfterEach
	fun clean() {
		movieService.clean()
	}

	@Test
	fun testShouldAddAMovie() {
		val movie = Movie("Michael Bay", "Bad Boys", "Action", 9, 1995)
		movieService.addMovie(movie)
		assertEquals(1, movieService.count("title", "a"))
		val moviesFound: List<Movie> = movieService.getMovies(0, 100, "title", "Bad Boys")
		assertEquals(1, moviesFound.size)
		assertEquals("Michael Bay", moviesFound[0].director)
		assertEquals("Action", moviesFound[0].genre)
		assertEquals(9, moviesFound[0].rating)
		assertEquals("Bad Boys", moviesFound[0].title)
		assertEquals(1995, moviesFound[0].year)
	}
}

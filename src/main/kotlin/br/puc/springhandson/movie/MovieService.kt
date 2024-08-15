package br.puc.springhandson.movie

import org.springframework.stereotype.Service
import jakarta.persistence.EntityManager
import jakarta.persistence.TypedQuery
import jakarta.persistence.criteria.*
import jakarta.persistence.metamodel.EntityType
import org.springframework.transaction.annotation.Transactional

@Service
class MovieService(
    val entityManager: EntityManager
) {
    fun find(id: Long?): Movie = entityManager.find(Movie::class.java, id)

    @Transactional
    fun addMovie(movie: Movie?): Movie? {
        entityManager.persist(movie)
        return movie
    }

    @Transactional
    fun editMovie(id: Long?, updatedMovie: Movie?): Movie? {
        val existingMovie: Movie = entityManager.find(Movie::class.java, id)
        if (updatedMovie != null) {
            existingMovie.title = updatedMovie.title
            existingMovie.director = updatedMovie.director
            existingMovie.genre = updatedMovie.genre
            existingMovie.rating = updatedMovie.rating
            existingMovie.year = updatedMovie.year
            entityManager.merge(existingMovie)
        }
        return existingMovie
    }

    @Transactional
    fun deleteMovie(id: Long) {
        val movie: Movie = entityManager.find(Movie::class.java, id)
        return entityManager.remove(movie)
    }

    fun getMovies(firstResult: Int?, maxResults: Int?, field: String?, searchTerm: String?): List<Movie> {
        val qb: CriteriaBuilder = entityManager.criteriaBuilder
        val cq: CriteriaQuery<Movie> = qb.createQuery(Movie::class.java)
        val root: Root<Movie> = cq.from(Movie::class.java)
        val type: EntityType<Movie> = entityManager.metamodel.entity(Movie::class.java)

        if (field != null && searchTerm != null && field.trim().isNotEmpty() && searchTerm.trim().isNotEmpty()) {
            val path: Path<String> =
                root.get(type.getDeclaredSingularAttribute(field.trim(), String::class.java))
            val condition: Predicate = qb.like(path, "%" + searchTerm.trim() + "%")
            cq.where(condition)
        }

        cq.orderBy(qb.asc(root.get<Long>("id")))

        val q: TypedQuery<Movie> = entityManager.createQuery(cq)
        if (maxResults != null) {
            q.maxResults = maxResults
        }
        if (firstResult != null) {
            q.firstResult = firstResult
        }
        return q.resultList
    }


    fun count(field: String?, searchTerm: String?): Int {
        val qb: CriteriaBuilder = entityManager.criteriaBuilder
        val cq: CriteriaQuery<Long> = qb.createQuery(Long::class.java)
        val root: Root<Movie> = cq.from(Movie::class.java)
        val type: EntityType<Movie> = entityManager.metamodel.entity(Movie::class.java)
        cq.select(qb.count(root))
        if (field != null && searchTerm != null && "" != field.trim { it <= ' ' } && "" != searchTerm.trim { it <= ' ' }) {
            val path: Path<String> =
                root.get(type.getDeclaredSingularAttribute(field.trim { it <= ' ' }, String::class.java))
            val condition: Predicate = qb.like(path, "%" + searchTerm.trim { it <= ' ' } + "%")
            cq.where(condition)
        }
        return entityManager.createQuery(cq).singleResult.toInt()
    }

    @Transactional
    fun clean() {
        entityManager.createQuery("delete from Movie").executeUpdate()
    }
}

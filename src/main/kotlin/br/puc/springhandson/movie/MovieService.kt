package br.puc.springhandson.movie

import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.TypedQuery
import javax.persistence.criteria.*
import javax.persistence.metamodel.EntityType
import javax.transaction.Transactional

@Service
class MovieService(
    val entityManager: EntityManager
) {
    fun find(id: Long?): Movie = entityManager.find(Movie::class.java, id)

    @Transactional
    fun addMovie(movie: Movie?) = entityManager.persist(movie)

    @Transactional
    fun editMovie(movie: Movie?) = entityManager.merge(movie)

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
        if (field != null && searchTerm != null && "" != field.trim { it <= ' ' } && "" != searchTerm.trim { it <= ' ' }) {
            val path: Path<String> =
                root.get(type.getDeclaredSingularAttribute(field.trim { it <= ' ' }, String::class.java))
            val condition: Predicate = qb.like(path, "%" + searchTerm.trim { it <= ' ' } + "%")
            cq.where(condition)
        }
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
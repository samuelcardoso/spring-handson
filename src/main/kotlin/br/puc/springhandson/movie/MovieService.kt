package br.puc.springhandson.movie

import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.completion.chat.ChatMessageRole
import com.theokanning.openai.service.OpenAiService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import javax.persistence.EntityManager
import javax.persistence.TypedQuery
import javax.persistence.criteria.*
import javax.persistence.metamodel.EntityType
import javax.transaction.Transactional

@Service
class MovieService(
    val entityManager: EntityManager,
    @Value("\${OPENAI_KEY}")
    val keyOpenAI: String
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

    fun generateScript(title: String): String? {
        val service = OpenAiService(keyOpenAI, Duration.ofSeconds(60))
        val messages: MutableList<ChatMessage> = ArrayList()
        messages.add(ChatMessage(ChatMessageRole.USER.value(),
                "Gere um poema com no máximo 10 estrofes sobre $title"))
        val chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .stream(false)
                .build()
        val completionChoice = service.createChatCompletion(chatCompletionRequest).choices[0]
        return completionChoice.message.content
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
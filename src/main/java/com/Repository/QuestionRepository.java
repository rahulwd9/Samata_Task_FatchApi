package com.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long>{
	@Query(value = "SELECT * FROM question ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Question> getRandomQuestion();
	 @Query(value = "SELECT * FROM question WHERE id <> :currentQuestionId ORDER BY RAND() LIMIT 1", nativeQuery = true)
	    Optional<Question> findNextRandomQuestion(@Param("currentQuestionId") Long currentQuestionId);
	

}

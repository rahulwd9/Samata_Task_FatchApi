package com.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.Entity.Question;
import com.Repository.QuestionRepository;
import com.Utility.AnswerRequest;
import com.Utility.NextQuestionResponse;
import com.Utility.QuestionDTO;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    private static final String API_URL = "https://jservice.io/api/random?count=5";
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }
    @GetMapping
    public ResponseEntity<List<Question>> getRandomQuestions() {
        RestTemplate restTemplate = new RestTemplate();
        Question[] questions = restTemplate.getForObject(API_URL, Question[].class);
       
        if (questions != null) {
            List<Question> savedQuestions = new ArrayList<>();
            for (Question questionResponse : questions) {
                Question question = new Question();
                question.setQuestion(questionResponse.getQuestion());
                question.setAnswer(questionResponse.getAnswer());
                savedQuestions.add(questionRepository.save(question));
            }
            return ResponseEntity.ok(savedQuestions);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
    
    @GetMapping("/play")
    public ResponseEntity<QuestionDTO> getQuestionToPlay() {
        Optional<Question> randomQuestion = questionRepository.getRandomQuestion();

        if (randomQuestion.isPresent()) {
            Question question = randomQuestion.get();
            QuestionDTO questionDTO = new QuestionDTO(question.getId(), question.getQuestion());
            return ResponseEntity.ok(questionDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/next")
    public ResponseEntity<NextQuestionResponse> getNextQuestion(@RequestBody AnswerRequest answerRequest) {
        Optional<Question> currentQuestion = questionRepository.findById(answerRequest.getQuestionId());

        if (currentQuestion.isPresent()) {
            Question question = currentQuestion.get();
            String correctAnswer = question.getAnswer();
            QuestionDTO nextQuestion = getNextRandomQuestion(question.getId());
            NextQuestionResponse response = new NextQuestionResponse(correctAnswer, nextQuestion);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private QuestionDTO getNextRandomQuestion(Long currentQuestionId) {
        Optional<Question> nextQuestion = questionRepository.findNextRandomQuestion(currentQuestionId);

        if (nextQuestion.isPresent()) {
            Question question = nextQuestion.get();
            return new QuestionDTO(question.getId(), question.getQuestion());
        } else {
            return null;
        }
    }
    
    }
    


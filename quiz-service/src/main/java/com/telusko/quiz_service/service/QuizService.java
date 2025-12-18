package com.telusko.quiz_service.service;

import com.telusko.quiz_service.dao.QuizDao;
import com.telusko.quiz_service.feign.QuizInterface;
import com.telusko.quiz_service.model.QuestionWrapper;
import com.telusko.quiz_service.model.Quiz;
import com.telusko.quiz_service.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {
    @Autowired
    QuizDao quizDao;
    
    @Autowired
    QuizInterface quizInterface;

//    @Autowired
//    QuestionDao questionDao;

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {
        //List<Integer> questionList = //MONOLITHIC WAY-->questionDao.findRandomQuestionsByCategory(category, numQ);
                                     //this way won't work in microservices architecture
        //as we need to call http://localhost:8080/question/generate url using RestTemplate or okhttpclient or anything else
        //but this is problematic and binds me to specific ip/domain name and port number
        //here comes the feign which enables us to not hardcode ip/domain name and port number-->will see in details later
        //also eureka server enables us to discover methods in other services directly without needing to directly hit the above url
        //eureka server is made by Netflix!!

//        System.out.println("QUESTION LIST:"+questionList);
//        Quiz quiz = new Quiz();
//        quiz.setTitle(title);
//        quiz.setQuestions(questionList);
//        quizDao.save(quiz);


        List<Integer> questionList = quizInterface.getQuestionsForQuiz(category,numQ).getBody();
        System.out.println("QUESTION LIST:"+questionList);
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questionList);
        quizDao.save(quiz);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(int id) {
        Optional<Quiz> quiz=quizDao.findById(id);
        List<Integer> questionIds = quiz.get().getQuestionIds();
        ResponseEntity<List<QuestionWrapper>> questionWrapperList = quizInterface.getQuestionsFromId(questionIds);
        return questionWrapperList;
    }

    public ResponseEntity<Integer> calculateResult(int id, List<Response> responses) {
        ResponseEntity<Integer>score=quizInterface.getScore(responses);
        return score;
    }
}

package com.telusko.question_service.controller;


import com.telusko.question_service.model.Question;
import com.telusko.question_service.model.QuestionWrapper;
import com.telusko.question_service.model.Response;
import com.telusko.question_service.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("question")
public class QuestionController {
    @Autowired
    QuestionService questionService;

    @Autowired
    Environment environment;

    @GetMapping("allQuestions")
    public ResponseEntity<List<Question>> getAllQuestions() {
//    public String getAllQuestions(){

//        return "Hi...These are all your questions";

        return questionService.getAllQuestions();
    }

    @GetMapping("category/{category}")
    public ResponseEntity<List<Question>> getQuestionsByCategory(@PathVariable String category) {
        return questionService.getQuestionsByCategory(category);
    }

    @PostMapping("add")
    public ResponseEntity<String> addQuestion(@RequestBody Question question) {
        return questionService.addQuestion(question);
    }

    //1.find a way to generate quiz
    //earlier in quizapp QuizController endpoints were calling QuizService apis which had
    //following tables in questiondb: 1.question 2.quiz 3.quiz_questions
    //but now we are creating microservices and when we will create seperate quiz-service it wont have access to questiondb
    //we will need to create endpoint of  createQuiz and submitQuiz and getQuizQuestions(i/p:quiz id) in that quiz service
    //in which createQuiz will need data from question-service
    //hence we need to create getQuestionsForCreatingQuiz endpoint inside question-service


    //    //find a way to generate the quiz-->supply questions to generate quiz
    @PostMapping("generate")
    public ResponseEntity<List<Integer>> getQuestionsForQuiz(@RequestParam String categoryName, @RequestParam Integer numQuestions) {

        return questionService.getQuestionsForQuiz(categoryName, numQuestions);//return list of question-id
    }

    //2. getQuestions(i/p:list of question ids) in question-service
    //as quiz-service will have similar endpoint getQuizQuestions(i/p:quiz id) but it wont have access to questiondb
    //it will only have quizdb containing following tables: 1.quiz and 2.quiz-question
    //[a]quiz table contains quiz-name and its id
    //[b]quiz-question table contains quiz-id and questions-id (not actual question)
    //hence we need to create endpoint that will serve questions based on given question-ids
    //i.e.getQuizQuestions(i/p:list of question ids) in question-service
    @PostMapping("getQuestions")
    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(@RequestBody List<Integer> questionIds) {
        System.out.println("PORT NO. OF INSTANCE USED: " + environment.getProperty("server.port"));
        return questionService.getQuestionsListFromQuestionsIdList(questionIds);
    }

    //3.getScore
    //as quiz-service wont have actual questions and its corresponding right answer
    //which is essential to create result
    //this method will be called from submitQuiz from quiz-service
    @PostMapping("getScore")
    public ResponseEntity<Integer> getScore(@RequestBody List<Response> responseList) {
        return questionService.getScore(responseList);
    }

    //get questions based on question id

    //getscore
}

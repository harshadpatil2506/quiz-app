package com.telusko.question_service.service;


import com.telusko.question_service.dao.QuestionDao;
import com.telusko.question_service.model.Question;
import com.telusko.question_service.model.QuestionWrapper;
import com.telusko.question_service.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDao questionDao;


    public ResponseEntity<List<Question>> getAllQuestions() {
        try {
            System.out.println("questionDao.findAll():" + questionDao.findAll());
            return new ResponseEntity<>(questionDao.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
        try {
            return new ResponseEntity<>(questionDao.findByCategory(category), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> addQuestion(Question question) {
        try {
            questionDao.save(question);
            return new ResponseEntity<>("success", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("failed", HttpStatus.BAD_REQUEST);
    }

    //1.find a way to generate quiz
    //earlier in quizapp QuizController endpoints were calling QuizService apis which had
    //following tables in questiondb: 1.question 2.quiz 3.quiz_questions
    //but now we are creating microservices and when we will create seperate quiz-service it wont have access to questiondb
    //we will need to create endpoint of  createQuiz and submitQuiz and getQuizQuestions(i/p:quiz id) in that quiz service
    //in which createQuiz will need data from question-service
    //hence we need to create getQuestionsForCreatingQuiz endpoint inside question-service


    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String categoryName, Integer numQuestions) {
        List<Integer> questionList = questionDao.findRandomQuestionsByCategory(categoryName, numQuestions);
        return new ResponseEntity<>(questionList, HttpStatus.OK);
    }


    //2. getQuizQuestions(i/p:quiz id) in question-service
    //as quiz-service will have similar endpoint getQuizQuestions(i/p:quiz id) but it wont have access to questiondb
    //it will only have quizdb containing following tables: 1.quiz and 2.quiz-question
    //[a]quiz table contains quiz-name and its id
    //[b]quiz-question table contains quiz-id and questions-id (not actual question)
    //hence we need to create endpoint that will serve questions based on given question-id
    //i.e.getQuizQuestions(i/p:quiz id) in question-service

    public ResponseEntity<List<QuestionWrapper>> getQuestionsListFromQuestionsIdList(List<Integer> questionIds) {
        List<QuestionWrapper> questionWrapperList = new ArrayList<>();
        List<Question> questionList = new ArrayList<>();
        for (Integer questionId : questionIds) {
            questionList.add(questionDao.findById(questionId).get());
        }

        for (Question question : questionList) {
            QuestionWrapper questionWrapper = new QuestionWrapper();
            questionWrapper.setId(question.getId());
            questionWrapper.setQuestionTitle(question.getQuestionTitle());
            questionWrapper.setCategory(question.getCategory());
            questionWrapper.setOption1(question.getOption1());
            questionWrapper.setOption2(question.getOption2());
            questionWrapper.setOption3(question.getOption3());
            questionWrapper.setOption4(question.getOption4());
            questionWrapper.setDifficultylevel(question.getDifficultylevel());
            questionWrapperList.add(questionWrapper);
        }
        return new ResponseEntity<>(questionWrapperList, HttpStatus.OK);
    }

    public ResponseEntity<Integer> getScore(List<Response> responseList) {

        int right = 0;

        for (Response response : responseList) {
            Question question = questionDao.findById(response.getId()).get();
            if (response.getResponse().equals(question.getRightAnswer())) {
                right++;
            }
        }
        return new ResponseEntity<>(right, HttpStatus.OK);
    }

    //3.getScore
    //as quiz-service wont have actual questions and its corresponding right answer
    //which is essential to create result
    //this method will be called from submitQuiz from quiz-service


}

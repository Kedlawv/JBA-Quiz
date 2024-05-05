package engine.controller;

import engine.model.*;
import engine.repo.AppUserRepo;
import engine.repo.CompletedQuizRepo;
import engine.repo.QuizQuestionRepo;
import engine.service.AppUserDetailsServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RequestMapping("/api")
@RestController
public class WebQuizController {

    private final QuizQuestionRepo questionRepo;
    private final AppUserRepo appUserRepo;
    private final CompletedQuizRepo completedQuizRepo;
    private final PasswordEncoder passwordEncoder;

    private final AppUserDetailsServiceImpl appUserDetailsService;

    public WebQuizController(QuizQuestionRepo questionRepo, AppUserRepo appUserRepo, CompletedQuizRepo completedQuizRepo, PasswordEncoder passwordEncoder, AppUserDetailsServiceImpl appUserDetailsService) {
        this.questionRepo = questionRepo;
        this.appUserRepo = appUserRepo;
        this.completedQuizRepo = completedQuizRepo;
        this.passwordEncoder = passwordEncoder;
        this.appUserDetailsService = appUserDetailsService;
    }

    @GetMapping("/quizzes")
    public ResponseEntity<Page<QuizQuestion>> getQuizQuestions(@RequestParam int page,
                                                               @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(questionRepo.findAll(pageable));
    }

    @GetMapping("/quizzes/{id}")
    public ResponseEntity<QuizQuestion> getQuizQuestionById(@PathVariable() int id) {
        QuizQuestion quizQuestion = questionRepo.findById(id);

        if (quizQuestion != null) {
            return ResponseEntity.ok(quizQuestion);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/quizzes")
    public QuizQuestion addQuizQuestion(@AuthenticationPrincipal UserDetails authedUser,
                                        @RequestBody @Valid QuizQuestion quizQuestion) {
        quizQuestion.setOwner(authedUser.getUsername());
        QuizQuestion addedQuestion = questionRepo.save(quizQuestion);
        return addedQuestion;
    }

    @PostMapping("/quizzes/{id}/solve")
    public ResponseEntity<QuizGuessResponseObject> getQuizResult(
            @PathVariable int id,
            @RequestBody AnswerRequestObject answer,
            @AuthenticationPrincipal UserDetails authedUser) {

        QuizQuestion quizQuestion = questionRepo.findById(id);
        AppUser appUser = appUserRepo.findAppUserByUsername(authedUser.getUsername()).get();

        if (answer.getAnswer() == null && quizQuestion.getAnswer().isEmpty()) {
            completedQuizRepo.save(new CompletedQuiz(quizQuestion.getId(), appUser, LocalDateTime.now()));
            return ResponseEntity.ok(new QuizGuessResponseObject(true, "Congratulations, you're right!"));
        }

        if (answer.getAnswer() == null && !quizQuestion.getAnswer().isEmpty()) {
            return ResponseEntity.ok(new QuizGuessResponseObject(false, "Wrong answer! Please, try again."));
        }

        Collections.sort(answer.getAnswer());
        Collections.sort(quizQuestion.getAnswer());

        if (answer.getAnswer().equals(quizQuestion.getAnswer())) {
            completedQuizRepo.save(new CompletedQuiz(quizQuestion.getId(), appUser, LocalDateTime.now()));
            return ResponseEntity.ok(new QuizGuessResponseObject(true, "Congratulations, you're right!"));
        } else {
            return ResponseEntity.ok(new QuizGuessResponseObject(false, "Wrong answer! Please, try again."));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRequest userCredentials) {
        if (appUserRepo.findAppUserByUsername(userCredentials.getEmail()).isPresent()) {
           return ResponseEntity.badRequest().body("User already exists");
        }

        AppUser newAppUser = new AppUser();
        newAppUser.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
        newAppUser.setUsername(userCredentials.getEmail());
        appUserRepo.save(newAppUser);

        return ResponseEntity.ok("New user created");
    }

    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getAllUsers() {
        return ResponseEntity.ok(appUserRepo.findAll());
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<AppUser> getUserByUsername(@PathVariable String username) {
        AppUser appUser = appUserRepo.findAppUserByUsername(username).get();

        return ResponseEntity.ok(appUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        appUserRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/quizzes/{quizId}")
    public ResponseEntity<String> deleteQuiz(
            @AuthenticationPrincipal UserDetails authedUser,
            @PathVariable int quizId) {

        String reqUserName = authedUser.getUsername();
        QuizQuestion quizToBeDeleted = questionRepo.findById(quizId);

        if (quizToBeDeleted == null) {
            return ResponseEntity.notFound().build();
        }

        if (quizToBeDeleted.getOwner().equals(reqUserName)) {
            questionRepo.deleteById(quizId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/quizzes/completed")
    public ResponseEntity<Page<CompletedQuiz>> getCompletedQuizzesForUser(
            @RequestParam int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails authedUser) {

        int userId = appUserRepo.findAppUserByUsername(authedUser.getUsername()).get().getId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"completedAt"));
        return ResponseEntity.ok(completedQuizRepo.findByUserId(userId, pageable));

    }

}

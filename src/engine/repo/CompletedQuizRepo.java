package engine.repo;

import engine.model.CompletedQuiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CompletedQuizRepo extends PagingAndSortingRepository<CompletedQuiz,Long> {

    Page<CompletedQuiz> findByUserId(int userId, Pageable pageable);
}

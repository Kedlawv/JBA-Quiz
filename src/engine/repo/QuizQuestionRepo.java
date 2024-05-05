package engine.repo;

import engine.model.QuizQuestion;
import net.bytebuddy.implementation.bind.annotation.Default;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface QuizQuestionRepo extends PagingAndSortingRepository<QuizQuestion, Integer> {

    QuizQuestion findById(int id);


    default List<QuizQuestion> findAllAsList(){
        Iterable<QuizQuestion> iterable = this.findAll();
        List<QuizQuestion> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

}

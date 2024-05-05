package engine.repo;

import engine.model.AppUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepo extends CrudRepository<AppUser, Integer> {

    Optional<AppUser> findAppUserByUsername(String username);
    List<AppUser> findAll();

}

package engine.service;

import engine.model.AppUser;
import engine.model.AppUserAdapter;
import engine.repo.AppUserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class AppUserDetailsServiceImpl implements UserDetailsService {

    Logger logger = LoggerFactory.getLogger(AppUserDetailsServiceImpl.class);
    private final AppUserRepo appUserRepo;

    public AppUserDetailsServiceImpl(AppUserRepo appUserRepo) {
        this.appUserRepo = appUserRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepo
                .findAppUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));

        logger.info("username=" + user.getUsername());

        return new AppUserAdapter(user);
    }
}

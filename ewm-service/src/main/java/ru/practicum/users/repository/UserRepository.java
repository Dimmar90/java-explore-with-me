package ru.practicum.users.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.users.model.User;


import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE (:ids IS NULL OR u.id IN :ids)")
    List<User> findAllByIds(@Param("ids") List<Long> ids, Pageable pageable);

    boolean existsUserByName(String name);

    boolean existsUserByEmail(String email);
}

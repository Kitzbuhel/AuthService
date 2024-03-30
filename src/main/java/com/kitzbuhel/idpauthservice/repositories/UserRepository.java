package com.kitzbuhel.idpauthservice.repositories;

import com.kitzbuhel.idpauthservice.identities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}


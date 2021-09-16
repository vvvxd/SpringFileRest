package com.example.springfilerest.repository;


import com.example.springfilerest.model.File;
import com.example.springfilerest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findAllByUser(User user);

    File findByName(String filename);


}

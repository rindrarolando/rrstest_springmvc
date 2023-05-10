package com.royalresearch.services.repositories;

import com.royalresearch.services.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {
}

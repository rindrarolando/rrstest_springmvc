package com.royalresearch.services.services;

import com.royalresearch.services.entities.Task;
import com.royalresearch.services.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {
    final private TaskRepository taskRepo;

    @Autowired
    public TaskService(TaskRepository taskRepo) {
        this.taskRepo = taskRepo;
    }

    public void createTask(Task task){
        taskRepo.save(task);
    }

    public List<Task> getAllTasks(){
        return taskRepo.findAll();
    }

    public Optional<Task> getTaskById(Long id){
        return taskRepo.findById(id);
    }
}

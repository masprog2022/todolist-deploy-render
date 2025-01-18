package com.masprogtech.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {
    List<TaskModel> findByUserId(UUID userId);
    TaskModel findByIdAndUserId(UUID id, UUID userId);
}

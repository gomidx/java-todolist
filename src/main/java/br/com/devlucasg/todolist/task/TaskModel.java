package br.com.devlucasg.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tasks")
public class TaskModel {
    
    @Id
    @GeneratedValue
    private UUID taskId;

    private UUID userId;

    @Column(length = 50)
    private String title;
    
    private String description;
    private String priority;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

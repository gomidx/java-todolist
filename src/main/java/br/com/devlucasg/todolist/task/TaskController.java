package br.com.devlucasg.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.devlucasg.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;
    
    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        taskModel.setUserId((UUID) request.getAttribute("userId"));

        if (LocalDateTime.now().isAfter(taskModel.getStartAt()) || LocalDateTime.now().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não é possível criar uma tarefa com a data de início ou final posterior à data atual.");
        }
        
        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não é possível criar uma tarefa com a data de início posterior à data final.");
        }

        var task = this.taskRepository.save(taskModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping("/")
    public ResponseEntity list(HttpServletRequest request) {
        var list = this.taskRepository.findByUserId((UUID) request.getAttribute("userId"));

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID taskId) {
        var task = this.taskRepository.findById(taskId).orElse(null);

        Utils.copyNonNullProperties(taskModel, task);

        return ResponseEntity.status(HttpStatus.OK).body(this.taskRepository.save(task));
    }
}

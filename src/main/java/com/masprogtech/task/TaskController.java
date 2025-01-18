package com.masprogtech.task;

import com.masprogtech.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final ITaskRepository taskRepository;

    public TaskController(ITaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        System.out.println("Chegou no controller " + request.getAttribute("userId"));
        var idUser = request.getAttribute("userId");
        taskModel.setUserId((UUID) idUser);

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio e de termino deve ser maior do que a data actual");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio deve ser menor do que a data de termino");
        }
        return ResponseEntity.status(HttpStatus.OK).body(taskRepository.save(taskModel));
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {
        var userId = request.getAttribute("userId");
        return this.taskRepository.findByUserId((UUID) userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        var task = this.taskRepository.findById(id).orElse(null);

        if(task == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tarefa não encontrada");
        }
        var userId = request.getAttribute("userId");

        if(!task.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuário não tem permissão");
        }
       // System.out.println("IdUser: " + userId);
        Utils.copyNonNullProperties(taskModel, task);

       // taskModel.setUserId((UUID) userId);
       // taskModel.setId(id);
     //   taskModel.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.OK).body(taskRepository.save(task));
    }
}

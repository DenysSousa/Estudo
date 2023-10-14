package br.com.denyssousa.todolist.task;

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

import br.com.denyssousa.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskControllers {

	private String validaNewTask(TaskModel taskModel) {
		var dataAtual = LocalDateTime.now();

		if ((dataAtual.isAfter(taskModel.getStartAt())) || (dataAtual.isAfter(taskModel.getEndAt()))) {
			return "A data de inicio / fim não pode ser menor do que a data atual";
		} else if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
			return "A data de inicio deve ser menor do que a data final";
		}

		return "";
	}

	private String validaUpdateTask(TaskModel taskModel, UUID id) {
		if (taskModel == null) {
			return "Tarefa não encontrada!";
		} else if (!taskModel.getIdUser().equals(id)) {
			return "A tarefa informada não pertence a este usuário!";
		}

		return "";
	}

	@Autowired
	private ITaskRepository taskRepository;

	@PostMapping("/new")
	public ResponseEntity<Object> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
		taskModel.setIdUser((UUID) request.getAttribute("idUser"));

		var erroValidacao = validaNewTask(taskModel);

		if (!erroValidacao.equals("")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroValidacao);
		}

		var task = this.taskRepository.save(taskModel);
		return ResponseEntity.status(HttpStatus.CREATED).body(task);
	}

	@GetMapping("/list")
	public List<TaskModel> list(HttpServletRequest request) {
		return taskRepository.findAllByIdUser((UUID) request.getAttribute("idUser"));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> update(@RequestBody TaskModel taskModel, @PathVariable UUID id,
			HttpServletRequest request) {
		
		taskModel.setIdUser((UUID) request.getAttribute("idUser"));
		var task = this.taskRepository.findById(id).orElse(null);

		var erroValidacao = validaUpdateTask(task, taskModel.getIdUser());

		if (!erroValidacao.equals("")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroValidacao);
		}

		Utils.copyNonNullProperties(taskModel, task);
		var taskUpdate = this.taskRepository.save(task);

		return ResponseEntity.status(HttpStatus.OK).body(taskUpdate);

	}

}

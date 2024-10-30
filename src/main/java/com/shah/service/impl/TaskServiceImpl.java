package com.shah.service.impl;

import com.shah.modal.Task;
import com.shah.modal.TaskStatus;
import com.shah.repository.TaskRepository;
import com.shah.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Task createTask(Task task, String requesterRole) throws Exception {
        if (!Objects.equals(requesterRole, "ROLE_ADMIN")) {
            throw new Exception("Only Admin can create Task");
        }
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Override
    public Task getTaskById(Long id) throws Exception {
        return taskRepository.findById(id)
                .orElseThrow(() -> new Exception("Task not found with id" + id));
    }

    @Override
    public List<Task> getAllTasks(TaskStatus status) {
        List<Task> allTask = taskRepository.findAll();
        return allTask.stream().filter(
                task -> status == null || task.getStatus().name().equalsIgnoreCase(status.toString())
        ).toList();
    }

    //    public Task updateTask(Long id, Task updatedTask, Long userID) throws Exception {
//        Task existingTask = getTaskById(id);
//        if (updatedTask.getTitle() != null) {
//            existingTask.setTitle(updatedTask.getTitle());
//        }
//        if (updatedTask.getImage() != null) {
//            existingTask.setImage(updatedTask.getImage());
//        }
//        if (updatedTask.getStatus() != null) {
//            existingTask.setStatus(updatedTask.getStatus());
//        }
//        if (updatedTask.getDescription() != null) {
//            existingTask.setDescription(updatedTask.getDescription());
//        }
//        if (updatedTask.getDeadline() != null) {
//            existingTask.setDeadline(updatedTask.getDeadline());
//        }
//        return taskRepository.save(existingTask);
//    }
    @Override
    public Task updateTask(Long id, Task updatedTask, Long userID) throws Exception {
        Task existingTask = getTaskById(id);
        BeanUtils.copyProperties(updatedTask, existingTask, getNullPropertyNames(updatedTask));
        return taskRepository.save(existingTask);
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    @Override
    public void deleteTask(Long id) throws Exception {
        getTaskById(id);
        taskRepository.deleteById(id);
    }

    @Override
    public Task assignedToUser(Long userId, Long taskId) throws Exception {
        Task task = getTaskById(taskId);
        task.setAssignedUserId(userId);
        task.setStatus(TaskStatus.DONE);
        return taskRepository.save(task);
    }

    @Override
    public List<Task> assignedUsersTask(Long userId, TaskStatus status) {
        List<Task> allTask = taskRepository.findByAssignedUserId(userId);
        return allTask.stream().filter(
                task -> status == null || task.getStatus().name().equalsIgnoreCase(status.toString())
        ).toList();
    }

    @Override
    public Task completeTask(Long taskId) throws Exception {
        Task task = getTaskById(taskId);
        task.setStatus(TaskStatus.DONE);
        return taskRepository.save(task);
    }
}

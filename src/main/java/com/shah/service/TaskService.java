package com.shah.service;

import com.shah.modal.Task;
import com.shah.modal.TaskStatus;

import java.util.List;

public interface TaskService {
    public Task createTask(Task task, String requesterRole) throws Exception;

    public Task getTaskById(Long id) throws Exception;

    public List<Task> getAllTasks(TaskStatus status);

    public Task updateTask(Long id, Task updatedTask, Long userID) throws Exception;

    public void deleteTask(Long id) throws Exception;

    public Task assignedToUser(Long userId, Long taskId) throws Exception;

    public List<Task> assignedUsersTask(Long userId, TaskStatus status);

    public Task completeTask(Long taskId) throws Exception;
}

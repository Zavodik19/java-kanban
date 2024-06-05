package manager;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {
    Task addTask(Task task);

    Task getTask(int id);

    void deleteTask(int id);

    void updateTask(Task task);

    List<Task> getAllTask();

    void deleteAllTask();

    Epic addEpic(Epic epic);

    Epic getEpic(int id);

    void deleteEpic(int id);

    void updateEpicStatus(int epicId);

    List<Epic> getAllEpics();

    void deleteAllEpic();

    SubTask addSubTask(SubTask subTask);

    SubTask getSubTask(int id);

    void updateSubTask(SubTask subTask);

    void deleteSubTask(int id);

    List<SubTask> getAllSubTasks();

    void deleteAllSubTask();

    List<Task> getHistory();

}

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {

    private final HashMap<Integer, SubTask> subtasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Task> tasks;
    private int taskId = 1;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.taskId = 1;
    }

    private int generateId() {   // Генерация ID задачи
        return taskId++;
    }



    // -------- ТАСКИ ----------

    public Task addTask(Task task) {     // Добавление задачи и присваивание статуса NEW
        task.setId(generateId());
        task.setStatus(TaskStatus.NEW);
        tasks.put(task.getId(), task);
        return task;
    }

    public Task getTask(int id) {  // Получение задачи по ID
        return tasks.get(id);
    }

    public void deleteTask(int id) {  // Удаление задачи по ID
        tasks.remove(id);
    }

    public void updateTask(Task task) {  // Обноваление задачи
        task.setId(taskId);
        tasks.put(task.getId(), task);
    }

    public List<Task> getAllTask() {  // Получение всех задач
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTask() {   // Удаление всех задач (тасок)
        tasks.clear();
    }


// -------- ЭПИКИ ----------

    public Epic addEpic(Epic epic) {   // Добавление эпика и присваивание статуса NEW
        epic.setId(generateId());
        epic.setStatus(TaskStatus.NEW);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Epic getEpic(int id) {   // Получение эпика по ID
        return epics.get(id);
    }

    public void deleteEpic(int id) {   // Удаление эпика по ID
        Epic removedEpic = epics.remove(id);
        if (removedEpic == null) {
            return;
        }
        List<Integer> subTaskIds = removedEpic.getSubTasks(); // Удаление связанных с эпиком подзадач
        for (int subTaskId : subTaskIds) {
            subtasks.remove(subTaskId);
        }
    }

    public void updateEpicStatus(int epicId) {  // Изменение статуса эпика
        Epic epic = epics.get(epicId);
        if (epic != null) {
            boolean allDone = true;
            boolean allNew = true;
            for (int subTaskId : epic.getSubTasks()) {
                SubTask subTask = subtasks.get(subTaskId);
                if (subTask != null) {
                    if (subTask.getStatus() != TaskStatus.DONE) {
                        allDone = false;
                    }
                    if (subTask.getStatus() != TaskStatus.NEW) {
                        allNew = false;
                    }
                }
            }
            if (allDone) {
                epic.setStatus(TaskStatus.DONE);
            } else if (allNew) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    public List<Epic> getAllEpics() {  // Получение всех эпиков
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpic() {   //Удаление всех эпиков
        epics.clear();
    }



    // -------- САБТАСКИ ----------

    public SubTask addSubTask(SubTask subTask) {  // Добавление подзаадчи
        subTask.setId(generateId());
        subtasks.put(subTask.getId(), subTask);
        // Добавляем идентификатор подзадачи в список подзадач эпика
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.getSubTasks().add(subTask.getId());
        }
        return subTask;
    }

    public SubTask getSubTask(int id) {  // Получение подзадачи
        return subtasks.get(id);
    }

    public void updateSubTask(SubTask subTask) {  // Обновление подзадачи
        subtasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.updateStatus(this);
        }
    }

    public void deleteSubTask(int id) {  // Удаление подзадачи по ID
        subtasks.remove(id);
    }

    public List<SubTask> getAllSubTasks() {  // Получение всех подзадач
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubTask() {  // Удаление всех подзадач
        subtasks.clear();
    }


    public void deleteAll() {  // Удаление задач всех типов
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

}

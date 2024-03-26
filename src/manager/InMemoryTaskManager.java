package manager;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, SubTask> subtasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Task> tasks;
    private int taskId = 1;
    private List<Task> history;
    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.taskId = 1;
        this.historyManager = Managers.getDefaultHistory();
    }

    private int generateId() {   // Генерация ID задачи
        return taskId++;
    }



    // -------- ТАСКИ ----------

    @Override
    public Task addTask(Task task) {     // Добавление задачи (Убран принудительно установленный статус)
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task getTask(int id) {  // Получение задачи по ID
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public void deleteTask(int id) {  // Удаление задачи по ID
        tasks.remove(id);
    }

    @Override
    public void updateTask(Task task) {  // Обновление задачи
        task.setId(task.getId());
        tasks.put(task.getId(), task);
    }

    @Override
    public List<Task> getAllTask() {  // Получение всех задач
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTask() {   // Удаление всех задач (тасок)
        tasks.clear();
    }


// -------- ЭПИКИ ----------

    @Override
    public Epic addEpic(Epic epic) {   // Добавление эпика
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic getEpic(int id) {   // Получение эпика по ID
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
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

    @Override
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

    @Override
    public List<Epic> getAllEpics() {  // Получение всех эпиков
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpic() {   //Удаление всех эпиков
        epics.clear();
        subtasks.clear();
    }



    // -------- САБТАСКИ ----------

    @Override
    public SubTask addSubTask(SubTask subTask) {  // Добавление подзадачи
        subTask.setId(generateId());
        subtasks.put(subTask.getId(), subTask);
        // Добавляем идентификатор подзадачи в список подзадач эпика
        if (epics.containsKey(subTask.getEpicId())) {
            Epic epic = epics.get(subTask.getEpicId());
            epic.getSubTasks().add(subTask.getId());
            updateEpicStatus(epic.getId());
            return subTask;
        }
        return null;
    }

    @Override
    public SubTask getSubTask(int id) {  // Получение подзадачи
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    @Override
    public void updateSubTask(SubTask subTask) {  // Обновление подзадачи
        subtasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.getStatus();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void deleteSubTask(int id) {  // Удаление подзадачи по ID
        subtasks.remove(id);
        // Пересчитываем статус эпика
        for (Epic epic : epics.values()) {
            List<Integer> subTaskIds = epic.getSubTasks();
            if (subTaskIds.contains(id)) {
                updateEpicStatus(epic.getId());
                break;
            }
        }
    }

    @Override
    public List<SubTask> getAllSubTasks() {  // Получение всех подзадач
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubTask() {  // Удаление всех подзадач
        subtasks.clear();
        // Пересчитываем статусы эпиков
        for (Epic epic : epics.values()) {
            updateEpicStatus(epic.getId());
        }
    }


    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }




}

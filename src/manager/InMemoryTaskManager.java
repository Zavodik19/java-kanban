package manager;

import exception.ErrorSavingTasksException;
import exception.TaskCrossingTimeException;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, SubTask> subtasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Task> tasks;
    private int taskId = 1;
    private List<Task> history;
    private final HistoryManager historyManager;

    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));


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
    public Task addTask(Task task) {
        task.setId(generateId());
        saveTaskInTreeSet(task);   // Теперь проверяется пересечение добавляемой задачи по времени
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void updateTask(Task task) {  // Обновленная версия обновления задачи с учетом prioritizedTasks.
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            // Если задача имеет время начала и окончания, обновляем её в prioritizedTasks
            if (task.getStartTime() != null && task.getEndTime() != null) {
                prioritizedTasks.remove(tasks.get(task.getId())); // Удаляем старую версию задачи
                saveTaskInTreeSet(task); // Добавляем обновленную версию задачи
            }
        } else {
            throw new NoSuchElementException("Задача с ID " + task.getId() + " не найдена.");
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTask() {
        tasks.clear();
    }


// -------- ЭПИКИ ----------

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic removedEpic = epics.remove(id);
        if (removedEpic != null) {
            removedEpic.getSubTasks().stream()
                    .forEach(subtasks::remove);
        }
    }

    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = getEpic(epicId);
        Set<TaskStatus> statuses = getSubTasksByEpic(epic).stream()
                .map(SubTask::getStatus)
                .collect(Collectors.toSet());

        if (statuses.equals(Collections.singleton(TaskStatus.DONE))) {
            epic.setStatus(TaskStatus.DONE);
        } else if (statuses.equals(Collections.singleton(TaskStatus.NEW))) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpic() {
        epics.clear();
        subtasks.clear();
    }


    // -------- САБТАСКИ ----------

    @Override
    public SubTask addSubTask(SubTask subTask) {
        subTask.setId(generateId());
        saveTaskInTreeSet(subTask); // Проверяем пересечение времени перед обновлением
        subtasks.put(subTask.getId(), subTask);
        System.out.println("Добавлена подзадача с ID: " + subTask.getId() + " и статусом: " + subTask.getStatus());
        if (epics.containsKey(subTask.getEpicId())) {
            Epic epic = epics.get(subTask.getEpicId());
            epic.getSubTasks().add(subTask);
            updateEpicStatus(epic.getId());
            return subTask;
        }
        return null;
    }

    @Override
    public SubTask getSubTask(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    public List<SubTask> getSubTasksByEpic(Epic epic) {
        return subtasks.values().stream()
                .filter(subTask -> subTask.getEpicId() == epic.getId())
                .collect(Collectors.toList());
    }

    @Override
    public void updateSubTask(SubTask subTask) {  // Изменен метод по обновлению подзадачи с учетом проверки на пересечение
        if (subtasks.containsKey(subTask.getId())) {
            saveTaskInTreeSet(subTask); // Проверяем пересечение времени перед обновлением
            subtasks.put(subTask.getId(), subTask);
            updateEpicStatus(subTask.getEpicId());
        } else {
            throw new NoSuchElementException("Подзадача с таким ID не найдена.");
        }
    }

    @Override
    public void deleteSubTask(int id) {
        subtasks.remove(id);
        // Пересчитываем статус эпика
        for (Epic epic : epics.values()) {
            List<SubTask> subTaskIds = epic.getSubTasks();
            if (subTaskIds.contains(id)) {
                updateEpicStatus(epic.getId());
                break;
            }
        }
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubTask() {
        subtasks.clear();
        // Пересчитываем статусы эпиков
        for (Epic epic : epics.values()) {
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public boolean isOverlapping(Task task1, Task task2) {
        return task2.getStartTime().isBefore(task1.getEndTime()) &&
                task2.getStartTime().isAfter(task1.getStartTime());
    }

    public void saveTaskInTreeSet(Task task) {  // проверка на пересечение добавляемой задачи с другими задачами в этом списке.
        if (task == null) {
            throw new ErrorSavingTasksException("Передана пустая задача.");
        }

        Task prev = prioritizedTasks.lower(task);
        Task next = prioritizedTasks.higher(task);

        if ((prev != null && isOverlapping(prev, task)) ||
                (next != null && isOverlapping(task, next))) {
            throw new TaskCrossingTimeException("Задача пересекается по времени с другими.");
        }

        prioritizedTasks.add(task);

    }


}

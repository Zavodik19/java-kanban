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
    public List<Task> getAllTasks() {  // Получение всех задач
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
    public void deleteEpic(int id) {
        Epic removedEpic = epics.remove(id);
        if (removedEpic != null) {
            removedEpic.getSubTasks().stream()
                    .forEach(subtasks::remove);
        }
    }

    @Override
    public void updateEpicStatus(int epicId) {  // Новая версия обновления статуса эпика с использованием Stream API
        Epic epic = epics.get(epicId);
        if (epic != null) {
            List<SubTask> subTasks = epic.getSubTasks().stream()
                    .map(subtasks::get)
                    .collect(Collectors.toList());

            boolean allDone = subTasks.stream()
                    .allMatch(subTask -> subTask.getStatus() == TaskStatus.DONE);
            boolean allNew = subTasks.stream()
                    .allMatch(subTask -> subTask.getStatus() == TaskStatus.NEW);

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
    public void updateSubTask(SubTask subTask) {
        if (!subtasks.containsKey(subTask.getId())) {
            throw new NoSuchElementException("Подзадача с ID " + subTask.getId() + " не найдена.");
        }

        subtasks.put(subTask.getId(), subTask); // Обновляем подзадачу
        updateEpicStatus(subTask.getEpicId()); // Обновляем статус эпика
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
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public boolean isOverlapping(Task task1, Task task2) {
        return !task1.getEndTime().isBefore(task2.getStartTime()) &&
                !task1.getStartTime().isAfter(task2.getEndTime());
    }

    public void saveTaskInTreeSet(Task task) {
        if (task == null) {
            throw new ErrorSavingTasksException("Передана пустая задача.");
        }

        prioritizedTasks.add(task);
        Task prev = prioritizedTasks.lower(task);
        Task next = prioritizedTasks.higher(task);

        if ((prev != null && isOverlapping(prev, task)) ||
                (next != null && isOverlapping(task, next))) {
            prioritizedTasks.remove(task);
            throw new TaskCrossingTimeException("Задача пересекается по времени с другими.");
        }
    }


}

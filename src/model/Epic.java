package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subTasks;

    public Epic(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.subTasks = new ArrayList<>();

    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask subTask) {
        if (subTask != null && subTask.getId() != getId()) {
            subTasks.add(subTask);
        }
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask);
    }

    // Добавен метод вычисления начального времени Epic
    public LocalDateTime calculateStartTime() {
        return subTasks.stream()
                .map(SubTask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(getStartTime()); // Если подзадач нет, возвращаем время начала Epic
    }

    // Добавен метод вычисления конечного времени Epic
    public LocalDateTime calculateEndTime() {
        return subTasks.stream()
                .map(subTask -> subTask.getStartTime().plus(subTask.getDuration()))
                .max(LocalDateTime::compareTo)
                .orElse(getStartTime().plus(getDuration())); // Если подзадач нет,
        // возвращаем время начала плюс продолжительность Epic
    }

    // Добавен метод вычисления продолжительности Epic
    public Duration calculateDuration() {
        return subTasks.stream()
                .map(SubTask::getDuration)
                .reduce(Duration::plus)
                .orElse(getDuration()); // Если подзадач нет, возвращаем продолжительность Epic
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "model.Epic{" +
                "subTasks=" + subTasks +
                '}';
    }

}
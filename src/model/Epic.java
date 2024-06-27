package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasks;
    private LocalDateTime endTime;

    public Epic(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.subTasks = new ArrayList<>();

    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(int subTaskId) {
        if (subTaskId != getId()) {
            subTasks.add(subTaskId);
        }
    }

    public void removeSubTask(int subTaskId) {
        subTasks.remove(Integer.valueOf(subTaskId));
    }

    public LocalDateTime getEndTimeEpic() {
        return endTime;
    }

    public void setEndTimeEpic(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setDurationEpic(Duration duration) {
        this.duration = duration;
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
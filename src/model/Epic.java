package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasks;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
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
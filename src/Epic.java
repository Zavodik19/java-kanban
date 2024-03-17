import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTasks;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        this.subTasks = new ArrayList<>();
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(int subTaskId) {
        subTasks.add(subTaskId);
    }

    public void removeSubTask(int subTaskId) {
        subTasks.remove(Integer.valueOf(subTaskId));
    }

    public void updateStatus(TaskManager taskManager) {
        boolean allDone = true;
        boolean allNew = true;
        for (Integer subTaskId : subTasks) {
            SubTask subTask = (SubTask) taskManager.getTask(subTaskId);
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
            setStatus(TaskStatus.DONE);
        } else if (allNew) {
            setStatus(TaskStatus.NEW);
        } else {
            setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                '}';
    }

}
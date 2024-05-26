package test.model;

import model.Epic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {

    @Test
    public void testEpicCannotBeSubTaskOfItself() {
        Epic epic = new Epic("Epic 1", "Description 1");
        epic.setId(1);

        epic.addSubTask(1);

        assertEquals(0, epic.getSubTasks().size());
    }
}
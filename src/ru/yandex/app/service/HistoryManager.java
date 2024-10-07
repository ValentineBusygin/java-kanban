package ru.yandex.app.service;
import ru.yandex.app.model.*;

import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);

    ArrayList<Task> getHistory();
}

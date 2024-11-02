package ru.yandex.app.service;

import ru.yandex.app.model.*;
import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}

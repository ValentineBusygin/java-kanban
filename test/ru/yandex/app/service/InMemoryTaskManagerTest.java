package ru.yandex.app.service;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    @Override
    public void initTest() {
        taskManager = new InMemoryTaskManager();
    }

}
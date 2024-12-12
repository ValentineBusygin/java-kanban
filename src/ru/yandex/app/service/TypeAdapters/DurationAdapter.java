package ru.yandex.app.service.TypeAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.value("");
        } else {
            jsonWriter.value(String.valueOf(duration.getSeconds()));
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        String nextString = jsonReader.nextString();
        if (nextString == null || nextString.isEmpty()) {
            return null;
        } else {
            return Duration.ofSeconds(Long.parseLong(nextString));
        }
    }
}

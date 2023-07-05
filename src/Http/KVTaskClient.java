package Http;

import Http.KVServerExceptions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class KVTaskClient {
    private String APIToken = "?API_TOKEN=";
    private final HttpClient client;
    private static final String START_URI = "http://localhost:8081/"; //  адрес KV сервера (для простоты)

    public KVTaskClient(URI uri) {
        client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException();
            }

            try {
                Long.parseLong(response.body());
            } catch (NumberFormatException exception) {
                throw new KVServerRegisterException(); // Проверка на то что при регистрации должно выдаваться число - токен
            }
            APIToken += response.body();

        } catch (IOException | InterruptedException exception) {
            throw new KVServerRegisterException();
        }
    }

    public void put(String key, String json) {
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        URI uri = URI.create(START_URI + "save/" + key + APIToken);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(uri).build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new KVServerSaveException("Код ответа не совпадает с ожидаемым - " + response.statusCode());
            }

        } catch (IOException | InterruptedException exception) {
            throw new KVServerSaveException("При сохранении произошла ошибка");
        }
    }

    public String load(String key) {
        URI uri = URI.create(START_URI + "load/" + key + APIToken);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException();
            }

            return response.body();

        } catch (IOException | InterruptedException exception) {
            throw new KVServerLoadException();
        }
    }
}

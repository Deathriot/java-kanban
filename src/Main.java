import Tasks.*;
import Managers.*;
import com.google.gson.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import Http.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Main {

    public static void main(String[] args) throws Exception{
        Gson gson = new Gson();
        new KVServer().start();
        HttpTaskServer server = new HttpTaskServer();
        server.start();

        EpicTask epic = new EpicTask("i am correct!", "and YOU are broken!");
        System.out.println(gson.toJson(epic));
    }
}


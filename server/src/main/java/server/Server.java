package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.InvalidRequest;
import service.UnauthorizedException;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private Handler handler;

    public Server(){
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        try {
            handler = new Handler(new DatabaseAuthDAO(), new DatabaseGameDAO(), new DatabaseUserDAO());
        } catch (DataAccessException e) {
            System.exit(1);
        }
        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.post("/user", this::register)
                .post("/session", this::login)
                .delete("session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .delete("/db", this::clear)
                .exception(JsonSyntaxException.class, this::badRequest)
                .exception(InvalidRequest.class, this::badRequest)
                .exception(UnauthorizedException.class, this::unauthorized)
                .exception(AlreadyTakenException.class, this::alreadyTaken)
                .exception(DataAccessException.class, this::dataAccess)
                .exception(Exception.class, this::internalError)
                .start(desiredPort);
        return javalin.port();
    }

    private void register(Context ctx) throws JsonSyntaxException, AlreadyTakenException, InvalidRequest, DataAccessException {
        String body = handler.register(ctx.body());
        ctx.status(200);
        ctx.json(body);
    }

    private void login(Context ctx) throws JsonSyntaxException, UnauthorizedException, InvalidRequest, DataAccessException {
        String body = handler.login(ctx.body());
        ctx.status(200);
        ctx.json(body);
    }

    private void logout(Context ctx) throws UnauthorizedException, DataAccessException {
        handler.logout(ctx.header("Authorization"));
        ctx.status(200);
    }

    private void listGames(Context ctx) throws UnauthorizedException, DataAccessException {
        String body = handler.listGames(ctx.header("Authorization"));
        ctx.status(200);
        ctx.json(body);
    }

    private void createGame(Context ctx) throws UnauthorizedException, InvalidRequest, DataAccessException {
        String body = handler.createGame(ctx.header("Authorization"), ctx.body());
        ctx.status(200);
        ctx.json(body);
    }

    private void joinGame(Context ctx) throws UnauthorizedException, DataAccessException, AlreadyTakenException, InvalidRequest {
        handler.joinGame(ctx.header("Authorization"), ctx.body());
        ctx.status(200);
    }

    private void clear(Context ctx) throws DataAccessException {
        handler.clear();
        ctx.status(200);
    }

    private void badRequest(Exception e, Context ctx) {
        var body = new Gson().toJson(Map.of("message", "Error: bad request"));
        ctx.status(400);
        ctx.json(body);
    }

    private void unauthorized(Exception e, Context ctx) {
        var body = new Gson().toJson(Map.of("message", "Error: unauthorized"));
        ctx.status(401);
        ctx.json(body);
    }

    private void alreadyTaken(Exception e, Context ctx) {
        var body = new Gson().toJson(Map.of("message", "Error: already taken"));
        ctx.status(403);
        ctx.json(body);
    }

    private void dataAccess(Exception e, Context ctx) {
        var body = new Gson().toJson(Map.of("message", "Error: Data access internal error"));
        ctx.status(500);
        ctx.json(body);
    }

    private void internalError(Exception e, Context ctx) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        ctx.status(500);
        ctx.json(body);
    }

    public void stop() {
        javalin.stop();
    }
}

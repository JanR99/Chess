package game;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.input.Input;
import graphics.ChessBoardRenderer;
import input.MouseInputHandler;
import model.Board;

public class ChessApp {

    public static void main(String[] args) {
        Game.init(args);
        Game.window().setTitle("Chess");

        ChessGame chessGame = ChessGame.init();
        Board board = chessGame.board;

        Game.window().getRenderComponent().onRendered(g -> new ChessBoardRenderer(board, chessGame).render(g));

        MouseInputHandler inputHandler = new MouseInputHandler(chessGame);
        Input.mouse().onClicked(inputHandler::onMouseClicked);
        Input.mouse().onDragged(inputHandler::onMouseClicked);

        Game.start();
    }
}
package game;

import de.gurkenlabs.litiengine.Game;
import graphics.ChessBoardRenderer;
import model.Board;

public class ChessApp {

    public static void main(String[] args) {
        Game.init(args);
        Game.window().setTitle("Chess");
        ChessGame chessGame = ChessGame.init();
        chessGame.start();
        Board board = chessGame.board;
        Game.start();
        Game.window().getRenderComponent().onRendered(g -> new ChessBoardRenderer(board).render(g));
    }
}
package chess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
	}
	
	
	public ChessPiece[][] getPieces(){ // Converte a matriz do tipo Piece em tipo ChessPiece
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0;  j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i,j);
			}
		}
		return mat;
	}
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition){
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) { //Mover a peça de um lugar para outro
		Position source = sourcePosition.toPosition(); // Presisa converter as posições do xadrez p/ o de matriz
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target); //Move a peça e guarda a peça capturada para guardar na lista ou eventual desfazimento de movimento

//		Testa check após movimentar a peça (jogador não pode se colocar em check, então deve se desfazer o movimento anterior) 
		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}
		
//		Testa se o oponente ficou em check após o movimento do jogador e atualiza "check" para true se ocorrer o check
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
//		Testa se o oponente ficou em checkmate e o jogo acaba
		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		}
		else {
			nextTurn(); // muda turno se não estiver em checkmate
		}
		return (ChessPiece) capturedPiece;
	}
	
	private Piece makeMove(Position source, Position target) {
		Piece p = board.removePiece(source); //Guarda a peça que será movida do tabuleiro (tira da origem) 
		Piece capturedPiece = board.removePiece(target); //Guarda a peça que foi capturada e que estava na posição de destino
		board.placePiece(p, target); //Coloca a peça que foi tirada da origem 'p' e coloca na posição da peça que foi capturada 
		
		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		return capturedPiece; 
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		Piece p = board.removePiece(target);
		board.placePiece(p, source);
		
		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
	}
	
	private void validateSourcePosition(Position position) {
		if(!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
		if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece is not yours");
		}
		if(!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There is no possible moves for the chosen piece");
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if(!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can't move to target position");
		}
	}
	
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE)? Color.BLACK : Color.WHITE;
	}
	
	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) { //Pega apenas a peça do Rei da referida cor passado no parâmetro
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list){
			if (p instanceof King) {
				return (ChessPiece)p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the board");
	}
	
	private boolean testCheck(Color color) {	//Testa se o rei está em check
		Position kingPosition = king(color).getChessPosition().toPosition();	//Pega a posição do Rei
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		for (Piece p : opponentPieces) {	//Laço para comparar a posição do Rei com as possíveis posições de movimento das peças do oponente
			boolean[][] mat = p.possibleMoves();	//Gera matriz de posições possível da peça do oponente
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {	 //Se tiver alguma posição true, o Rei está em posição de check
				return true;
			}
		}
		return false;
	}
	
//	Testa o checkmate percorrendo cada elemento da matriz "mat" verificando se algum movimento possível da peça (de mesma cor) 
//	pode tirar o Rei do check. É realizado mudando, provisioriamente, a posição da peça 'p' para a posição de cada elemento de 
//	movimento possível, conforme a matriz, apenas para o teste
	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {	//Testa inicialmente se a peça não está em check, então também não está em checkmate
			return false;
		}
		//Verificar se alguma peça da mesma cor pode tirar o Rei do check, se não tiver, o Rei está em checkmate
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {	 //Laço pega cada peça da lista filtrada
			boolean[][] mat = p.possibleMoves();	//Gera matriz com as posições possíveis da peça e percorrida no for
			for (int i = 0; i < board.getRows(); i++) {
				for (int j = 0; j < board.getColumns(); j++) {
					if (mat[i][j]) {	//Essa posição da matriz contém um movimento possível (true) e tira do check?
						Position source = ((ChessPiece)p).getChessPosition().toPosition();//Pega posição da peça 'p'
						Position target = new Position(i, j);	//Pega a posição de mat[i][j] que é um movimento possível
						Piece capturedPiece = makeMove(source, target);	//Move a peça 'p' para a posição possível mat[i][j]
						boolean testCheck = testCheck(color);	//Testa de o Rei ainda está em check
						undoMove(source, target, capturedPiece);	//Desfaz o movimento
						if(!testCheck) {	//Se o teste der falso o movimento tirou o Rei do check
							return false;	//Não está em check
						}
					}
				}
			}
		}
		return true;	//Está em checkmate
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {
		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));

        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));
	}
}

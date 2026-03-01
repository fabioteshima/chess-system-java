package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Rook extends ChessPiece {

	public Rook(Board board, Color color) {
		super(board, color);
	}
	
	@Override 
	public String toString() {
		return "R";
	}
	
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		
		Position p = new Position(0, 0);
		
		//above
		p.setValues(position.getRow() - 1, position.getColumn());	//Seta "p" auxiliar na casa/posição acima da peça 
		while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) { 
			mat[p.getRow()][p.getColumn()] = true;	  //Coloca true nas posições da matriz onde a casa existe e se está vago
			p.setRow(p.getRow() - 1);	 //Passa para a casa mais acima 
		}
		if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {	 //Verifica se a última casa tem peça adversária ou não
			mat[p.getRow()][p.getColumn()] = true;	  //Se tiver marca true nessa posição também (pode "comer" a peça adversária")		
		}
		
		//left
		p.setValues(position.getRow(), position.getColumn() - 1);  
		while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) { 
			mat[p.getRow()][p.getColumn()] = true; 
			p.setColumn(p.getColumn() - 1); 
		}
		if(getBoard().positionExists(p) && isThereOpponentPiece(p)) { 
			mat[p.getRow()][p.getColumn()] = true; 		
		}
		
		//right
		p.setValues(position.getRow(), position.getColumn() + 1);  
		while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) { 
			mat[p.getRow()][p.getColumn()] = true; 
			p.setColumn(p.getColumn() + 1);  
		}
		if(getBoard().positionExists(p) && isThereOpponentPiece(p)) { 
			mat[p.getRow()][p.getColumn()] = true; 		
		}
		
		//below
		p.setValues(position.getRow() + 1, position.getColumn()); 
		while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) { 
			mat[p.getRow()][p.getColumn()] = true;
			p.setRow(p.getRow() + 1); 
		}
		if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true; 		
		}
		
		return mat;
	}
}

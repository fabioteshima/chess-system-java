package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Bishop extends ChessPiece {

	public Bishop(Board board, Color color) {
		super(board, color);
	}
	
	@Override 
	public String toString() {
		return "B";
	}
	
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		
		Position p = new Position(0, 0);
		
//		Bispo move para as diagonais
		
		//nw
		p.setValues(position.getRow() - 1, position.getColumn() - 1);	//Seta p na diagonal noroeste 
		while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) { 
			mat[p.getRow()][p.getColumn()] = true;	  //Coloca true nas posições da matriz onde a casa existe e se está vago
			p.setValues(p.getRow() - 1, p.getColumn() - 1);;	 //Passa para essa casa 
		}
		if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {	 //Verifica se a última casa tem peça adversária ou não
			mat[p.getRow()][p.getColumn()] = true;	  //Se tiver marca true nessa posição também (pode "comer" a peça adversária")		
		}
		
		//ne
		p.setValues(position.getRow() - 1 , position.getColumn() + 1);  
		while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) { 
			mat[p.getRow()][p.getColumn()] = true; 
			p.setValues(p.getRow() - 1, p.getColumn() + 1);; 
		}
		if(getBoard().positionExists(p) && isThereOpponentPiece(p)) { 
			mat[p.getRow()][p.getColumn()] = true; 		
		}
		
		//se
		p.setValues(position.getRow() + 1, position.getColumn() + 1);  
		while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) { 
			mat[p.getRow()][p.getColumn()] = true; 
			p.setValues(p.getRow() + 1, p.getColumn() + 1);;  
		}
		if(getBoard().positionExists(p) && isThereOpponentPiece(p)) { 
			mat[p.getRow()][p.getColumn()] = true; 		
		}
		
		//sw
		p.setValues(position.getRow() + 1, position.getColumn() - 1); 
		while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) { 
			mat[p.getRow()][p.getColumn()] = true;
			p.setValues(p.getRow() + 1, p.getColumn() - 1); 
		}
		if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true; 		
		}
		
		return mat;
	}
}
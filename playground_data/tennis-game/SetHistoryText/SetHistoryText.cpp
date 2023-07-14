#include "SetHistoryText.h"

SetHistoryText::SetHistoryText( GameState* gameState ) : _gameState( gameState ) {}
SetHistoryText::~SetHistoryText() { std::cout << "destructing SetHistoryText..." << std::endl; }

std::string SetHistoryText::getSetHistoryText( int player ) {  
    std::string setHistoryText = _buildString( player );
    return setHistoryText; }

std::string SetHistoryText::_buildString( int player ) {
    std::map< int, int > setHistory = player == PLAYER_1_INITIALIZED ? 
        _gameState->getPlayer1SetHistory() : _gameState->getPlayer2SetHistory();
    std::string setHistoryText = ""; 
    for ( long unsigned int i = 0; i < setHistory.size()-1; i++ ) {
        setHistoryText += std::to_string( setHistory[ i ] );
        setHistoryText += " "; }
    return setHistoryText; }
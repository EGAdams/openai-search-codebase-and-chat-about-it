#include "Player.h"

Player::Player( int playerNumber ) : _playerNumber( playerNumber ) { 
        _points = 0; _games = 0; _sets = 0; _matches = 0; _mode = 0; _setting = 0; 
        _game_history[ 1 ] = 0; _game_history[ 2 ] = 0; _game_history[ 3 ] = 0;
        _set_history[  1 ] = 0;  _set_history[ 2 ] = 0; _set_history[  3 ] = 0; }
Player::~Player() {}

void Player::setOpponent( Player* opponent ) { _opponent = opponent; }                                  
Player* Player::getOpponent() { return _opponent; }
void Player::setSets( GameState* gameState, int sets ) {
    std::cout << "Player::setSets()  passed in sets var: " << sets << std::endl;
    std::cout << "setting history for player " << _playerNumber << " set " << gameState->getCurrentSet() << " to " << _games << std::endl; 
    this->setSetHistory(      gameState->getCurrentSet(), _games );
    gameState->setCurrentSet( gameState->getCurrentSet() + 1     );
    std::cout << "end of setSets method.  setting sets to: " << sets << std::endl;
    _sets = sets; }                                          int Player::getSets() {    return _sets;    }
void Player::setPoints(int points) { _points = points; }     int Player::getPoints() {  return _points;  }
void Player::setGames(int games) { _games = games; }         int Player::getGames() {   return _games;   }
void Player::setMatches(int matches) { _matches = matches; } int Player::getMatches() { return _matches; }
void Player::setMode(int mode) { _mode = mode; }             int Player::getMode() {    return _mode;    }
void Player::setSetting(int setting) { _setting = setting; } int Player::getSetting() { return _setting; }
int Player::incrementSetting() { return ++_setting; }
int Player::number() { return _playerNumber; }

void Player::setSetHistory( int set, int games ) {
    std::cout << "*** Player::setSetHistory() player: " << number() << " set: " << set << "  games: " << games << " ***" << std::endl;
    _set_history[  set  ] = games; } 
std::map< int, int> Player::getSetHistory() {  return _set_history; }

void Player::setGameHistory( int game, int score ) { _game_history[ game ] = score; } std::map< int, int> Player::getGameHistory() { return _game_history;}
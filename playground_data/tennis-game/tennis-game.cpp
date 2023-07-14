// -*- mode: c++; c-basic-offset: 2; indent-tabs-mode: nil; -*-
// The main tennis game entry point.
// This code is public domain
// (but note, that the led-matrix library this depends on is GPL v2)

#include "../include/led-matrix.h"
#include "../include/graphics.h"
#include <getopt.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <csignal>
#include "FontLoader/FontLoader.h"
#include "TextDrawer/TextDrawer.h"
#include "NumberDrawer/NumberDrawer.h"
#include "ScoreBoard/ScoreBoard.h"
#include "GameObject/GameObject.h"

using namespace rgb_matrix;

int main( int argc, char *argv[]) {
    bool game_running = true;
    int loop_count = 0;
    #define MAX_LOOP_COUNT 200
    #define A_SPACE        13
    #define FOUR_SPACE     14
    #define THREE_SPACE    15
    #define SCORE_DELAY    .15
    
    std::cout << "creating GameObject..." << std::endl;
    GameObject* gameObject = new GameObject();
    std::cout << "done creating game object.  sleeping...\n\n\n\n\n" << std::endl;
    sleep( 1 );
    std::cout << "done sleeping.  calling gameObject->loopGame()..." << std::endl;
    gameObject->loopGame();

    std::cout << "done calling loopGame().  sleeping...\n\n\n\n\n" << std::endl;
    sleep( 1 );

    int randomPlayer = 1;
    std::signal( SIGINT, GameObject::_signalHandler );
    /*/// Begin Game Loop ///*/ while ( game_running && GameObject::gSignalStatus != SIGINT ) { 
        if ( loop_count >  MAX_LOOP_COUNT ) { game_running = false; }
        sleep( SCORE_DELAY );
        randomPlayer = rand() % 2 + 1; // generate random player between 1 and 2
        std::cout << "\n\n\n*** Player " << randomPlayer << " scored ***\n" << std::endl;
        gameObject->playerScore( randomPlayer );  // flip the player score flag
        sleep( SCORE_DELAY );
        gameObject->loopGame();  // handle the player score flag
        loop_count++; 
    } ///////// End Game Loop /////////
    std::cout << "game loop exited.  loop_count: " << loop_count << std::endl;
    if ( loop_count > MAX_LOOP_COUNT ) {
        std::cout << "MAX_LOOP_COUNT reached.  Exiting...\n\n\n\n\n" << std::endl; }
    return 0; }
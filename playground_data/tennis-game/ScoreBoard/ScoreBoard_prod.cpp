#include "ScoreBoard.h"

ScoreBoard::ScoreBoard( Player* player1, Player* player2, GameState* gameState ) 
: _player1( player1 ), _player2( player2 ), _gameState( gameState ) {
    printf( "Constructing ScoreBoard...\n" );
    Color pipe_color( 255, 255, 0 ); // yellow
    Color background_color( 0, 0, 0 );
    Color big_number_color( 0, 255, 0 ); // green
    Color outline_color( 0, 0, 0 );
    RGBMatrix::Options matrix_options; // seems like the only logical place to create the canvas
    matrix_options.hardware_mapping = "regular";  // or e.g. "adafruit-hat"
    matrix_options.pixel_mapper_config = "Rotate:180;264-Mapper"; // or e.g. "U-mapper"
    matrix_options.rows = 64;
    matrix_options.cols = 64;
    matrix_options.chain_length = 1;
    matrix_options.parallel = 2;
    matrix_options.show_refresh_rate = false;
    matrix_options.disable_hardware_pulsing = true; // --led-no-hardware-pulse
    matrix_options.brightness = 35; // best for demo videos in largo
    matrix_options.pwm_bits = 11;
    matrix_options.multiplexing = 1;
    rgb_matrix::RuntimeOptions runtime_opt;
    runtime_opt.drop_privileges = 0;
    runtime_opt.gpio_slowdown = 2;
    runtime_opt.daemon = 0;
    runtime_opt.do_gpio_init = 1;

    printf( "Matrix options:\n" );
    printf( "  rows: %d\n", matrix_options.rows );
    printf( "  chain_length: %d\n", matrix_options.chain_length );
    printf( "  parallel: %d\n", matrix_options.parallel );
    printf( "  pwm_bits: %d\n", matrix_options.pwm_bits );
    printf( "  pwm_lsb_nanoseconds: %d\n", matrix_options.pwm_lsb_nanoseconds );

    printf( "Runtime options:\n" );
    printf( "  daemon: %d\n", runtime_opt.daemon );
    printf( "  do_gpio_init: %d\n", runtime_opt.do_gpio_init );
    printf( "  drop_privileges: %d\n", runtime_opt.drop_privileges );
    printf( "  gpio_slowdown: %d\n", runtime_opt.gpio_slowdown );
        
    CanvasCreator canvasCreator(matrix_options, runtime_opt);
    _canvas = canvasCreator.CreateCanvas();
    FontLoader fontLoader("fonts/mspgothic_042623.bdf"); // Load Fonts
    rgb_matrix::Font font;
    fontLoader.LoadFont(font);
    FontLoader bigNumberFontLoader("fonts/fgm_27_ee.bdf");
    rgb_matrix::Font bigNumberFont;
    bigNumberFontLoader.LoadFont(bigNumberFont);
    if (!_big_number_font.LoadFont( BIG_NUMBER_FONT )) { 
        fprintf( stderr, "Couldn't load font '%s'\n", BIG_NUMBER_FONT );
        exit( 1 ); }
    Color color( 255, 255, 0 );
    Color bg_color( 0, 0, 0 );
    _bigNumberDrawer = new NumberDrawer( _canvas, &_big_number_font, NumberDrawer::BIG, color, bg_color);
    _pipeDrawer      = new NumberDrawer( _canvas, &_big_number_font, NumberDrawer::BIG, color, bg_color); 
    update(); }

ScoreBoard::~ScoreBoard() {
    std::cout << "destructing ScoreBoard..." << std::endl;
    delete _bigNumberDrawer;
    delete _pipeDrawer;
    delete _canvas; }

void ScoreBoard::update() {
    std::cout << "inside ScoreBoard::update(), calling _drawPlayerScore..." << std::endl;
    // std::cout << "checking if player2 is null..." << std::endl;
    if ( _player2 == NULL ) {
        std::cout << "player1 is null" << std::endl; 
        return; }
    // std::cout << "checking if player1 is null..." << std::endl;
    if ( _player1 == NULL ) {
        std::cout << "player1 is null" << std::endl; 
        return; }
    std::cout << "player1 points: " << _player1->getPoints() << std::endl;
    std::cout << "player2 points: " << _player2->getPoints() << std::endl;
    _drawPlayerScore( _player1 );
    _drawPlayerScore( _player2 ); }

void ScoreBoard::clearScreen() {
    Color flood_color( 0, 0, 0 ); _canvas->Fill (flood_color.r, flood_color.g, flood_color.b ); }  // clear screen

void ScoreBoard::_drawPlayerScore(Player* player) {
    std::cout << "determining vertical offset..." << std::endl;
    int vertical_offset = player->number() == 1 ? 0 : _big_number_font.height();
    std::cout << "determining serve bar..." << std::endl;
    std::string serve_bar = _gameState->getServe() == PLAYER_2_SERVE ? " " : "I"; // or use p1sv and swap
    std::cout << "actually drawing serve bar..." << std::endl;
    _pipeDrawer->DrawNumber(serve_bar, 1, _big_number_font.baseline());
    std::cout << "translating score..." << std::endl;
    std::string score = _translate(player->getPoints());
    std::cout << "drawing score with bigNumberDrawer objects..." << std::endl;
    _bigNumberDrawer->DrawNumber(score.substr( 0, 1), 16, _big_number_font.baseline() + vertical_offset);
    _bigNumberDrawer->DrawNumber(score.substr(1, 1), 38, _big_number_font.baseline() + vertical_offset); }


std::string ScoreBoard::_translate( int raw_score ) {
    switch ( raw_score ) {
    case 0: return "00";
    case 1: return "10";
    case 2: return "15";
    case 3: return "30";
    case 4: return "40";
    case 5: return "Ad";
    case 99: return "Ad";
    default: return "00"; }}

void _showLittleNumbers( rgb_matrix::Canvas *canvas ) {
    #define LITTLE_NUMBER_FONT "fonts/little_numbers.bdf"
    #define SPACE_BEFORE_SMALL_NUMBER   7
    #define SPACE_BETWEEN_SMALL_NUMBERS 17
    #define START_ROW                   86
    Color background_color( 0, 0, 0 );
    int letter_spacing = 0;
    rgb_matrix::Font little_number_font;
    if (!little_number_font.LoadFont( LITTLE_NUMBER_FONT )) {
        fprintf( stderr, "Couldn't load font '%s'\n", LITTLE_NUMBER_FONT );
        exit( 1 ); }
    int x = 0;
    int y = START_ROW;
    rgb_matrix::Font *outline_font = NULL;
    Color thirdRowColor( 0, 255, 0 );
    rgb_matrix::DrawText( canvas, little_number_font, x + SPACE_BEFORE_SMALL_NUMBER, y + little_number_font.baseline(), thirdRowColor,  outline_font ? NULL : &background_color, "0 0 0", letter_spacing );
    y += little_number_font.height() - 5;
    Color fourthRowColor( 255, 0, 0 );
    rgb_matrix::DrawText( canvas, little_number_font, x + SPACE_BEFORE_SMALL_NUMBER, y + little_number_font.baseline(), fourthRowColor, outline_font ? NULL : &background_color, "0", letter_spacing );
    rgb_matrix::DrawText( canvas, little_number_font, x + SPACE_BEFORE_SMALL_NUMBER + SPACE_BETWEEN_SMALL_NUMBERS, y + little_number_font.baseline(), fourthRowColor, outline_font ? NULL : &background_color, "0", letter_spacing );
    rgb_matrix::DrawText( canvas, little_number_font, x + SPACE_BEFORE_SMALL_NUMBER + (( 2 * SPACE_BETWEEN_SMALL_NUMBERS )), y + little_number_font.baseline(), fourthRowColor, outline_font ? NULL : &background_color, "0", letter_spacing ); }
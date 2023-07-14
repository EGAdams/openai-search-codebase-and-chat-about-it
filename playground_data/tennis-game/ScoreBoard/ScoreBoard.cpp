#include "ScoreBoard.h"

ScoreBoard::ScoreBoard( Player* player1, Player* player2, GameState* gameState ): 
    _player1( player1 ), _player2( player2 ), _gameState( gameState ) {
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
    matrix_options.brightness = 35; // 35 is best for demo videos in largo
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
    CanvasCreator canvasCreator( matrix_options, runtime_opt );
    _canvas = std::unique_ptr<rgb_matrix::RGBMatrix>( canvasCreator.createCanvas());
    FontLoader fontLoader( "fonts/mspgothic_042623.bdf" ); // Load Fonts
    rgb_matrix::Font font;
    fontLoader.LoadFont( font );
    FontLoader bigNumberFontLoader( "fonts/fgm_27_ee.bdf" );
    rgb_matrix::Font bigNumberFont;
    bigNumberFontLoader.LoadFont( bigNumberFont );
    if (!_big_number_font.LoadFont( BIG_NUMBER_FONT )) { 
        fprintf( stderr, "Couldn't load font '%s'\n", BIG_NUMBER_FONT ); exit( 1 );}
    Color color( 255, 255, 0 );
    Color bg_color( 0, 0, 0 );
    _bigNumberDrawer   = std::make_unique<NumberDrawer>( _canvas.get(), &_big_number_font, NumberDrawer::BIG, color, bg_color );
    _smallNumberDrawer = std::make_unique<NumberDrawer>( _canvas.get(), &_big_number_font, NumberDrawer::BIG, color, bg_color );
    _pipeDrawer        = std::make_unique<NumberDrawer>( _canvas.get(), &_big_number_font, NumberDrawer::BIG, color, bg_color ); 
    _setDrawer         = std::make_unique<SetDrawer>(    _canvas.get(), _gameState                                            );
    update(); }

ScoreBoard::~ScoreBoard() {
    std::cout << "destroying ScoreBoard..." << std::endl;
    delete _canvas.get(); }

void ScoreBoard::drawGames() {  std::cout << "inside ScoreBoard::drawGames()" << std::endl; }

bool ScoreBoard::hasCanvas() { 
    if ( _canvas != NULL ) { return true;
    } else { std::cout << "*** WARNING: canvas is NULL ***" << std::endl; return false; }}

void ScoreBoard::update() {
    std::cout << "inside ScoreBoard::update(), calling clearScreen()..." << std::endl;
    clearScreen();
    std::cout << "getting player points..." << std::endl;
    std::cout << "player1 points: " << _player1->getPoints() << std::endl;
    std::cout << "player2 points: " << _player2->getPoints() << std::endl;
    std::cout << "inside ScoreBoard::update(), calling _drawPlayerScore..." << std::endl;
    _drawPlayerScore( _player1 );
    _drawPlayerScore( _player2 );
    _setDrawer->drawSets(); }

void ScoreBoard::clearScreen() { 
    // std::cout << "inside ScoreBoard::clearScreen()" << std::endl;
    // std::cout << "*** the clear screen delay... ***" << std::endl;
    // GameTimer::gameDelay( 500 );
    // std::cout << "*** done with the clear screen delay... ***" << std::endl;
    // GameTimer::gameDelay( 500 );

    // if _canvas is null, exit
    if ( !hasCanvas()) { std::cout << "*** ERROR: canvas == NULL.  exiting... ***" << std::endl; exit( 1 ); }
    Color flood_color( 0, 0, 0 ); _canvas->Fill( flood_color.r, flood_color.g, flood_color.b );
    std::cout << "screen cleared." << std::endl; }

void ScoreBoard::_drawPlayerScore(Player* player) {
    int vertical_offset = player->number() == 0 ? 0 : _big_number_font.height();
    std::string serve_bar = _gameState->getServe() == player->number() ? "I" : " "; // or use p1sv and swap
    _pipeDrawer->DrawNumber(serve_bar, 1, _big_number_font.baseline() + vertical_offset );
    std::string score = _translate(player->getPoints());
    int baseline = _big_number_font.baseline();
    int first_offset  = _characterOffset( score.substr( 0, 1 ));
    int second_offset = _characterOffset( score.substr( 1, 1 ));
    _bigNumberDrawer->DrawNumber( score.substr( 0, 1 ), first_offset  + 16, baseline + vertical_offset );
    _bigNumberDrawer->DrawNumber( score.substr( 1, 1 ), second_offset + 38, baseline + vertical_offset ); }

void ScoreBoard::_drawPlayerSets( Player* player ) { 
    std::cout << "inside ScoreBoard::_drawPlayerSets()" << std::endl; }

int ScoreBoard::_characterOffset( std::string character ) {
    int char_offset = 0;
    if ( character == "A" ) {
        return -4;
    } else if ( character == "d" ) {
        return 0;
    } else { char_offset = std::stoi( character ); }

    switch ( char_offset ) {
    case 3: return -1;
    case 4: return -4;
    case 5: return -1;
    default: return 0; }}

std::string ScoreBoard::_translate( int raw_score ) {
    switch ( raw_score ) {
    case 0: return "00";
    case 1: return "15";
    case 2: return "30";
    case 3: return "40";
    case 4: return "Ad";
    case 5: return "Ad";
    case 99: return "Ad";
    default: return "00"; }}